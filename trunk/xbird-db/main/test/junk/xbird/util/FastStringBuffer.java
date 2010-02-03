/*
 * @(#)$Id: FastStringBuffer.java 3619 2008-03-26 07:23:03Z yui $
 *
 * Copyright 2006-2008 Makoto YUI
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Contributors:
 *     Makoto YUI - ported from apache xalan
 */
/*
 * Copyright 1999-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xbird.util;

import java.io.Serializable;

/**
 * Fast String Buffer implementation rent from Xalan.
 * Bare-bones, unsafe, fast string buffer. No thread-safety, no
 * parameter range checking, exposed fields. Note that in typical
 * applications, thread-safety of a StringBuffer is a somewhat
 * dubious concept in any case.
 * <p>
 * FastStringBuffer operates as a "chunked buffer". Doing so
 * reduces the need to recopy existing information when an append
 * exceeds the space available; we just allocate another chunk and
 * flow across to it. (The array of chunks may need to grow,
 * admittedly, but that's a much smaller object.) Some excess
 * recopying may arise when we extract Strings which cross chunk
 * boundaries; larger chunks make that less frequent.
 * </p>
 * <p>
 * The size values are parameterized, to allow tuning this code. In
 * theory, Result Tree Fragments might want to be tuned differently 
 * from the main document's text. 
 * </p>
 * 
 * @link http://xml.apache.org/xalan-j/apidocs/org/apache/xml/utils/FastStringBuffer.html
 */
public final class FastStringBuffer implements Serializable, CharSequence {

    // %BUG% %REVIEW% *****PROBLEM SUSPECTED: If data from an FSB is being copied
    // back into the same FSB (variable set from previous variable, for example) 
    // and blocksize changes in mid-copy... there's risk of severe malfunction in 
    // the read process, due to how the resizing code re-jiggers storage. Arggh. 
    // If we want to retain the variable-size-block feature, we need to reconsider 
    // that issue. For now, I have forced us into fixed-size mode.
    static final boolean DEBUG_FORCE_FIXED_CHUNKSIZE = true;

    // If nonzero, forces the inial chunk size.
    /**/static final int DEBUG_FORCE_INIT_BITS = 0;

    /**
     * Field m_array holds the string buffer's text contents, using an
     * array-of-arrays. Note that this array, and the arrays it contains, may be
     * reallocated when necessary in order to allow the buffer to grow;
     * references to them should be considered to be invalidated after any
     * append. However, the only time these arrays are directly exposed
     * is in the sendSAXcharacters call.
     */
    char[][] m_array;

    /**
     * Field m_chunkBits sets our chunking strategy, by saying how many
     * bits of index can be used within a single chunk before flowing over
     * to the next chunk. For example, if m_chunkbits is set to 15, each
     * chunk can contain up to 2^15 (32K) characters  
     */
    int m_chunkBits = 15;

    /**
     * Field m_chunkMask is m_chunkSize-1 -- in other words, m_chunkBits
     * worth of low-order '1' bits, useful for shift-and-mask addressing
     * within the chunks. 
     */
    int m_chunkMask; // =m_chunkSize-1;

    /**
     * Field m_chunkSize establishes the maximum size of one chunk of the array
     * as 2**chunkbits characters.
     * (Which may also be the minimum size if we aren't tuning for storage) 
     */
    int m_chunkSize; // =1<<(m_chunkBits-1);

    /**
     * Field m_firstFree is an index into m_array[m_lastChunk][], pointing to
     * the first character in the Chunked Array which is not part of the
     * FastStringBuffer's current content. Since m_array[][] is zero-based,
     * the length of that content can be calculated as
     * (m_lastChunk<<m_chunkBits) + m_firstFree 
     */
    int m_firstFree = 0;

    /**
     * Field m_innerFSB, when non-null, is a FastStringBuffer whose total
     * length equals m_chunkSize, and which replaces m_array[0]. This allows
     * building a hierarchy of FastStringBuffers, where early appends use
     * a smaller chunkSize (for less wasted memory overhead) but later
     * ones use a larger chunkSize (for less heap activity overhead).
     */
    FastStringBuffer m_innerFSB = null;

    /**
     * Field m_lastChunk is an index into m_array[], pointing to the last
     * chunk of the Chunked Array currently in use. Note that additional
     * chunks may actually be allocated, eg if the FastStringBuffer had
     * previously been truncated or if someone issued an ensureSpace request.
     * <p>
     * The insertion point for append operations is addressed by the combination
     * of m_lastChunk and m_firstFree.
     */
    int m_lastChunk = 0;

    /**
     * Field m_maxChunkBits affects our chunk-growth strategy, by saying what
     * the largest permissible chunk size is in this particular FastStringBuffer
     * hierarchy. 
     */
    int m_maxChunkBits = 15;

    /**
     * Field m_rechunkBits affects our chunk-growth strategy, by saying how
     * many chunks should be allocated at one size before we encapsulate them
     * into the first chunk of the next size up. For example, if m_rechunkBits
     * is set to 3, then after 8 chunks at a given size we will rebundle
     * them as the first element of a FastStringBuffer using a chunk size
     * 8 times larger (chunkBits shifted left three bits).
     */
    int m_rebundleBits = 2;

    /**
     * Construct a FastStringBuffer, using a default allocation policy.
     */
    public FastStringBuffer() {

        // 10 bits is 1K. 15 bits is 32K. Remember that these are character
        // counts, so actual memory allocation unit is doubled for UTF-16 chars.
        //
        // For reference: In the original FastStringBuffer, we simply
        // overallocated by blocksize (default 1KB) on each buffer-growth.
        this(10, 15, 2);
    }

    /**
     * Construct a FastStringBuffer, using default maxChunkBits and
     * rebundleBits values.
     * <p>
     * ISSUE: Should this call assert initial size, or fixed size?
     * Now configured as initial, with a default for fixed.
     *
     * NEEDSDOC @param initChunkBits
     */
    public FastStringBuffer(int initChunkBits) {
        this(initChunkBits, 15, 2);
    }

    /**
     * Construct a FastStringBuffer, using a default rebundleBits value.
     *
     * NEEDSDOC @param initChunkBits
     * NEEDSDOC @param maxChunkBits
     */
    public FastStringBuffer(int initChunkBits, int maxChunkBits) {
        this(initChunkBits, maxChunkBits, 2);
    }

    /**
     * Construct a FastStringBuffer, with allocation policy as per parameters.
     * <p>
     * For coding convenience, I've expressed both allocation sizes in terms of
     * a number of bits. That's needed for the final size of a chunk,
     * to permit fast and efficient shift-and-mask addressing. It's less critical
     * for the inital size, and may be reconsidered.
     * <p>
     * An alternative would be to accept integer sizes and round to powers of two;
     * that really doesn't seem to buy us much, if anything.
     *
     * @param initChunkBits Length in characters of the initial allocation
     * of a chunk, expressed in log-base-2. (That is, 10 means allocate 1024
     * characters.) Later chunks will use larger allocation units, to trade off
     * allocation speed of large document against storage efficiency of small
     * ones.
     * @param maxChunkBits Number of character-offset bits that should be used for
     * addressing within a chunk. Maximum length of a chunk is 2^chunkBits
     * characters.
     * @param rebundleBits Number of character-offset bits that addressing should
     * advance before we attempt to take a step from initChunkBits to maxChunkBits
     */
    public FastStringBuffer(int initChunkBits, int maxChunkBits, int rebundleBits) {
        if (DEBUG_FORCE_INIT_BITS != 0)
            initChunkBits = DEBUG_FORCE_INIT_BITS;

        // %REVIEW%
        // Should this force to larger value, or smaller? Smaller less efficient, but if
        // someone requested variable mode it's because they care about storage space.
        // On the other hand, given the other changes I'm making, odds are that we should
        // adopt the larger size. Dither, dither, dither... This is just stopgap workaround
        // anyway; we need a permanant solution.
        //
        if (DEBUG_FORCE_FIXED_CHUNKSIZE)
            maxChunkBits = initChunkBits;
        //if(DEBUG_FORCE_FIXED_CHUNKSIZE) initChunkBits=maxChunkBits;

        m_array = new char[16][];

        // Don't bite off more than we're prepared to swallow!
        if (initChunkBits > maxChunkBits)
            initChunkBits = maxChunkBits;

        m_chunkBits = initChunkBits;
        m_maxChunkBits = maxChunkBits;
        m_rebundleBits = rebundleBits;
        m_chunkSize = 1 << (initChunkBits);
        m_chunkMask = m_chunkSize - 1;
        m_array[0] = new char[m_chunkSize];
    }

    /**
     * Encapsulation c'tor. After this is called, the source FastStringBuffer
     * will be reset to use the new object as its m_innerFSB, and will have
     * had its chunk size reset appropriately. IT SHOULD NEVER BE CALLED
     * EXCEPT WHEN source.length()==1<<(source.m_chunkBits+source.m_rebundleBits)
     *
     * NEEDSDOC @param source
     */
    private FastStringBuffer(FastStringBuffer source) {

        // Copy existing information into new encapsulation
        m_chunkBits = source.m_chunkBits;
        m_maxChunkBits = source.m_maxChunkBits;
        m_rebundleBits = source.m_rebundleBits;
        m_chunkSize = source.m_chunkSize;
        m_chunkMask = source.m_chunkMask;
        m_array = source.m_array;
        m_innerFSB = source.m_innerFSB;

        // These have to be adjusted because we're calling just at the time
        // when we would be about to allocate another chunk
        m_lastChunk = source.m_lastChunk - 1;
        m_firstFree = source.m_chunkSize;

        // Establish capsule as the Inner FSB, reset chunk sizes/addressing
        source.m_array = new char[16][];
        source.m_innerFSB = this;

        // Since we encapsulated just as we were about to append another
        // chunk, return ready to create the chunk after the innerFSB
        // -- 1, not 0.
        source.m_lastChunk = 1;
        source.m_firstFree = 0;
        source.m_chunkBits += m_rebundleBits;
        source.m_chunkSize = 1 << (source.m_chunkBits);
        source.m_chunkMask = source.m_chunkSize - 1;
    }

    /**
     * Append a single character onto the FastStringBuffer, growing the
     * storage if necessary.
     * <p>
     * NOTE THAT after calling append(), previously obtained
     * references to m_array[][] may no longer be valid....
     * though in fact they should be in this instance.
     *
     * @param value character to be appended.
     */
    public final void append(char value) {

        char[] chunk;

        if (m_firstFree < m_chunkSize) // Simplified test single-character-fits
            chunk = m_array[m_lastChunk];
        else {

            // Extend array?
            int i = m_array.length;

            if (m_lastChunk + 1 == i) {
                char[][] newarray = new char[i + 16][];

                System.arraycopy(m_array, 0, newarray, 0, i);

                m_array = newarray;
            }

            // Advance one chunk
            chunk = m_array[++m_lastChunk];

            if (chunk == null) {

                // Hierarchical encapsulation
                if (m_lastChunk == 1 << m_rebundleBits && m_chunkBits < m_maxChunkBits) {

                    // Should do all the work of both encapsulating
                    // existing data and establishing new sizes/offsets
                    m_innerFSB = new FastStringBuffer(this);
                }

                // Add a chunk.
                chunk = m_array[m_lastChunk] = new char[m_chunkSize];
            }

            m_firstFree = 0;
        }

        // Space exists in the chunk. Append the character.
        chunk[m_firstFree++] = value;
    }

    /**
     * Append part of the contents of a Character Array onto the
     * FastStringBuffer,  growing the storage if necessary.
     * <p>
     * NOTE THAT after calling append(), previously obtained
     * references to m_array[] may no longer be valid.
     *
     * @param chars character array from which data is to be copied
     * @param start offset in chars of first character to be copied,
     * zero-based.
     * @param length number of characters to be copied
     */
    public final void append(char[] chars, int start, int length) {

        int strlen = length;

        if (0 == strlen)
            return;

        int copyfrom = start;
        char[] chunk = m_array[m_lastChunk];
        int available = m_chunkSize - m_firstFree;

        // Repeat while data remains to be copied
        while (strlen > 0) {

            // Copy what fits
            if (available > strlen)
                available = strlen;

            System.arraycopy(chars, copyfrom, m_array[m_lastChunk], m_firstFree, available);

            strlen -= available;
            copyfrom += available;

            // If there's more left, allocate another chunk and continue
            if (strlen > 0) {

                // Extend array?
                int i = m_array.length;

                if (m_lastChunk + 1 == i) {
                    char[][] newarray = new char[i + 16][];

                    System.arraycopy(m_array, 0, newarray, 0, i);

                    m_array = newarray;
                }

                // Advance one chunk
                chunk = m_array[++m_lastChunk];

                if (chunk == null) {

                    // Hierarchical encapsulation
                    if (m_lastChunk == 1 << m_rebundleBits && m_chunkBits < m_maxChunkBits) {

                        // Should do all the work of both encapsulating
                        // existing data and establishing new sizes/offsets
                        m_innerFSB = new FastStringBuffer(this);
                    }

                    // Add a chunk.
                    chunk = m_array[m_lastChunk] = new char[m_chunkSize];
                }

                available = m_chunkSize;
                m_firstFree = 0;
            }
        }

        // Adjust the insert point in the last chunk, when we've reached it.
        m_firstFree += available;
    }
    
    public final void append(CharSequence chars) {

        int strlen = chars.length();

        if (0 == strlen)
            return;

        int copyfrom = 0;
        char[] chunk = m_array[m_lastChunk];
        int available = m_chunkSize - m_firstFree;

        // Repeat while data remains to be copied
        while (strlen > 0) {

            // Copy what fits
            if (available > strlen)
                available = strlen;

            System.arraycopy(chars, copyfrom, m_array[m_lastChunk], m_firstFree, available);

            strlen -= available;
            copyfrom += available;

            // If there's more left, allocate another chunk and continue
            if (strlen > 0) {

                // Extend array?
                int i = m_array.length;

                if (m_lastChunk + 1 == i) {
                    char[][] newarray = new char[i + 16][];

                    System.arraycopy(m_array, 0, newarray, 0, i);

                    m_array = newarray;
                }

                // Advance one chunk
                chunk = m_array[++m_lastChunk];

                if (chunk == null) {

                    // Hierarchical encapsulation
                    if (m_lastChunk == 1 << m_rebundleBits && m_chunkBits < m_maxChunkBits) {

                        // Should do all the work of both encapsulating
                        // existing data and establishing new sizes/offsets
                        m_innerFSB = new FastStringBuffer(this);
                    }

                    // Add a chunk.
                    chunk = m_array[m_lastChunk] = new char[m_chunkSize];
                }

                available = m_chunkSize;
                m_firstFree = 0;
            }
        }

        // Adjust the insert point in the last chunk, when we've reached it.
        m_firstFree += available;
    }

    /**
     * Append the contents of a String onto the FastStringBuffer,
     * growing the storage if necessary.
     * <p>
     * NOTE THAT after calling append(), previously obtained
     * references to m_array[] may no longer be valid.
     *
     * @param value String whose contents are to be appended.
     */
    public final void append(String value) {

        if (value == null)
            return;
        int strlen = value.length();

        if (0 == strlen)
            return;

        int copyfrom = 0;
        char[] chunk = m_array[m_lastChunk];
        int available = m_chunkSize - m_firstFree;

        // Repeat while data remains to be copied
        while (strlen > 0) {

            // Copy what fits
            if (available > strlen)
                available = strlen;

            value.getChars(copyfrom, copyfrom + available, m_array[m_lastChunk], m_firstFree);

            strlen -= available;
            copyfrom += available;

            // If there's more left, allocate another chunk and continue
            if (strlen > 0) {

                // Extend array?
                int i = m_array.length;

                if (m_lastChunk + 1 == i) {
                    char[][] newarray = new char[i + 16][];

                    System.arraycopy(m_array, 0, newarray, 0, i);

                    m_array = newarray;
                }

                // Advance one chunk
                chunk = m_array[++m_lastChunk];

                if (chunk == null) {

                    // Hierarchical encapsulation
                    if (m_lastChunk == 1 << m_rebundleBits && m_chunkBits < m_maxChunkBits) {

                        // Should do all the work of both encapsulating
                        // existing data and establishing new sizes/offsets
                        m_innerFSB = new FastStringBuffer(this);
                    }

                    // Add a chunk. 
                    chunk = m_array[m_lastChunk] = new char[m_chunkSize];
                }

                available = m_chunkSize;
                m_firstFree = 0;
            }
        }

        // Adjust the insert point in the last chunk, when we've reached it.
        m_firstFree += available;
    }

    /**
     * Append the contents of a StringBuffer onto the FastStringBuffer,
     * growing the storage if necessary.
     * <p>
     * NOTE THAT after calling append(), previously obtained
     * references to m_array[] may no longer be valid.
     *
     * @param value StringBuffer whose contents are to be appended.
     */
    public final void append(StringBuilder value) {

        if (value == null)
            return;
        int strlen = value.length();

        if (0 == strlen)
            return;

        int copyfrom = 0;
        char[] chunk = m_array[m_lastChunk];
        int available = m_chunkSize - m_firstFree;

        // Repeat while data remains to be copied
        while (strlen > 0) {

            // Copy what fits
            if (available > strlen)
                available = strlen;

            value.getChars(copyfrom, copyfrom + available, m_array[m_lastChunk], m_firstFree);

            strlen -= available;
            copyfrom += available;

            // If there's more left, allocate another chunk and continue
            if (strlen > 0) {

                // Extend array?
                int i = m_array.length;

                if (m_lastChunk + 1 == i) {
                    char[][] newarray = new char[i + 16][];

                    System.arraycopy(m_array, 0, newarray, 0, i);

                    m_array = newarray;
                }

                // Advance one chunk
                chunk = m_array[++m_lastChunk];

                if (chunk == null) {

                    // Hierarchical encapsulation
                    if (m_lastChunk == 1 << m_rebundleBits && m_chunkBits < m_maxChunkBits) {

                        // Should do all the work of both encapsulating
                        // existing data and establishing new sizes/offsets
                        m_innerFSB = new FastStringBuffer(this);
                    }

                    // Add a chunk.
                    chunk = m_array[m_lastChunk] = new char[m_chunkSize];
                }

                available = m_chunkSize;
                m_firstFree = 0;
            }
        }

        // Adjust the insert point in the last chunk, when we've reached it.
        m_firstFree += available;
    }

    /**
     * Get a single character from the string buffer.
     *
     *
     * @param pos character position requested.
     * @return A character from the requested position.
     */
    public char charAt(int pos) {
        int startChunk = pos >>> m_chunkBits;

        if (startChunk == 0 && m_innerFSB != null)
            return m_innerFSB.charAt(pos & m_chunkMask);
        else
            return m_array[startChunk][pos & m_chunkMask];
    }

    /**
     * @param start Offset of first character in the range.
     * @param length Number of characters to send.
     * @return a new String object initialized from the specified range of
     * characters.
     */
    public String getString(int start, int length) {
        int startColumn = start & m_chunkMask;
        int startChunk = start >>> m_chunkBits;
        if (startColumn + length < m_chunkMask && m_innerFSB == null) {
            return getOneChunkString(startChunk, startColumn, length);
        }
        return getString(new StringBuilder(length), startChunk, startColumn, length).toString();
    }

    /**
     * Get the length of the list. Synonym for size().
     *
     * @return the number of characters in the FastStringBuffer's content.
     */
    public final int length() {
        return (m_lastChunk << m_chunkBits) + m_firstFree;
    }

    /**
     * Discard the content of the FastStringBuffer, and most of the memory
     * that was allocated by it, restoring the initial state. Note that this
     * may eventually be different from setLength(0), which see.
     */
    public final void reset() {

        m_lastChunk = 0;
        m_firstFree = 0;

        // Recover the original chunk size
        FastStringBuffer innermost = this;

        while (innermost.m_innerFSB != null) {
            innermost = innermost.m_innerFSB;
        }

        m_chunkBits = innermost.m_chunkBits;
        m_chunkSize = innermost.m_chunkSize;
        m_chunkMask = innermost.m_chunkMask;

        // Discard the hierarchy
        m_innerFSB = null;
        m_array = new char[16][0];
        m_array[0] = new char[m_chunkSize];
    }

    /**
     * Directly set how much of the FastStringBuffer's storage is to be
     * considered part of its content. This is a fast but hazardous
     * operation. It is not protected against negative values, or values
     * greater than the amount of storage currently available... and even
     * if additional storage does exist, its contents are unpredictable.
     * The only safe use for our setLength() is to truncate the FastStringBuffer
     * to a shorter string.
     *
     * @param l New length. If l<0 or l>=getLength(), this operation will
     * not report an error but future operations will almost certainly fail.
     */
    public final void setLength(int l) {
        m_lastChunk = l >>> m_chunkBits;

        if (m_lastChunk == 0 && m_innerFSB != null) {
            // Replace this FSB with the appropriate inner FSB, truncated
            m_innerFSB.setLength(l, this);
        } else {
            m_firstFree = l & m_chunkMask;

            // There's an edge case if l is an exact multiple of m_chunkBits, which risks leaving
            // us pointing at the start of a chunk which has not yet been allocated. Rather than 
            // pay the cost of dealing with that in the append loops (more scattered and more
            // inner-loop), we correct it here by moving to the safe side of that
            // line -- as we would have left the indexes had we appended up to that point.
            if (m_firstFree == 0 && m_lastChunk > 0) {
                --m_lastChunk;
                m_firstFree = m_chunkSize;
            }
        }
    }

    /**
     * Get the length of the list. Synonym for length().
     *
     * @return the number of characters in the FastStringBuffer's content.
     */
    public final int size() {
        return (m_lastChunk << m_chunkBits) + m_firstFree;
    }

    /**
     * Note that this operation has been somewhat deoptimized by the shift to a
     * chunked array, as there is no factory method to produce a String object
     * directly from an array of arrays and hence a double copy is needed.
     * By using ensureCapacity we hope to minimize the heap overhead of building
     * the intermediate StringBuffer.
     * <p>
     * (It really is a pity that Java didn't design String as a final subclass
     * of MutableString, rather than having StringBuffer be a separate hierarchy.
     * We'd avoid a <strong>lot</strong> of double-buffering.)
     *
     * @return the contents of the FastStringBuffer as a standard Java string.
     */
    public final String toString() {

        int length = (m_lastChunk << m_chunkBits) + m_firstFree;

        return getString(new StringBuilder(length), 0, 0, length).toString();
    }

    /**
     * Subroutine for the public setLength() method. Deals with the fact
     * that truncation may require restoring one of the innerFSBs
     *
     * NEEDSDOC @param l
     * NEEDSDOC @param rootFSB
     */
    private final void setLength(int l, FastStringBuffer rootFSB) {

        m_lastChunk = l >>> m_chunkBits;

        if (m_lastChunk == 0 && m_innerFSB != null) {
            m_innerFSB.setLength(l, rootFSB);
        } else {

            // Undo encapsulation -- pop the innerFSB data back up to root.
            // Inefficient, but attempts to keep the code simple.
            rootFSB.m_chunkBits = m_chunkBits;
            rootFSB.m_maxChunkBits = m_maxChunkBits;
            rootFSB.m_rebundleBits = m_rebundleBits;
            rootFSB.m_chunkSize = m_chunkSize;
            rootFSB.m_chunkMask = m_chunkMask;
            rootFSB.m_array = m_array;
            rootFSB.m_innerFSB = m_innerFSB;
            rootFSB.m_lastChunk = m_lastChunk;

            // Finally, truncate this sucker.
            rootFSB.m_firstFree = l & m_chunkMask;
        }
    }

    protected String getOneChunkString(int startChunk, int startColumn, int length) {
        return new String(m_array[startChunk], startColumn, length);
    }

    /**
     * @param sb StringBuffer to be appended to
     * @param start Offset of first character in the range.
     * @param length Number of characters to send.
     * @return sb with the requested text appended to it
     */
    StringBuilder getString(StringBuilder sb, int start, int length) {
        return getString(sb, start >>> m_chunkBits, start & m_chunkMask, length);
    }

    /**
     * Internal support for toString() and getString().
     * PLEASE NOTE SIGNATURE CHANGE from earlier versions; it now appends into
     * and returns a StringBuffer supplied by the caller. This simplifies
     * m_innerFSB support.
     * <p>
     * Note that this operation has been somewhat deoptimized by the shift to a
     * chunked array, as there is no factory method to produce a String object
     * directly from an array of arrays and hence a double copy is needed.
     * By presetting length we hope to minimize the heap overhead of building
     * the intermediate StringBuffer.
     * <p>
     * (It really is a pity that Java didn't design String as a final subclass
     * of MutableString, rather than having StringBuffer be a separate hierarchy.
     * We'd avoid a <strong>lot</strong> of double-buffering.)
     *
     *
     * @param sb
     * @param startChunk
     * @param startColumn
     * @param length
     * 
     * @return the contents of the FastStringBuffer as a standard Java string.
     */
    StringBuilder getString(StringBuilder sb, int startChunk, int startColumn, int length) {

        int stop = (startChunk << m_chunkBits) + startColumn + length;
        int stopChunk = stop >>> m_chunkBits;
        int stopColumn = stop & m_chunkMask;

        // Factored out
        //StringBuffer sb=new StringBuffer(length);
        for (int i = startChunk; i < stopChunk; ++i) {
            if (i == 0 && m_innerFSB != null)
                m_innerFSB.getString(sb, startColumn, m_chunkSize - startColumn);
            else
                sb.append(m_array[i], startColumn, m_chunkSize - startColumn);

            startColumn = 0; // after first chunk
        }

        if (stopChunk == 0 && m_innerFSB != null)
            m_innerFSB.getString(sb, startColumn, stopColumn - startColumn);
        else if (stopColumn > startColumn)
            sb.append(m_array[stopChunk], startColumn, stopColumn - startColumn);

        return sb;
    }

    public CharSequence subSequence(int start, int end) {
        return getString(start, end - start);
    }
}
