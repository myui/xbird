/*
 * @(#)$Id$
 * 
 * eaio: StringSearch - high-performance pattern matching algorithms in Java
 * Copyright (c) 2003, 2004 Johann Burkard (jb@eaio.com) http://eaio.com
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * --
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
 *     Makoto YUI - porting
 */
package xbird.util.string;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class FastStringSearch {

    public static int quickSearch(final byte[] text, final byte[] pattern) {
        return quickSearch(text, 0, text.length, pattern);
    }

    /**
     * An implementation of Sunday's simplified "Quick Finder" version of the
     * Boyer-Moore algorithm. See "A very fast substring search algorithm" (appeared
     * in <em>Communications of the ACM. 33 (8):132-142</em>).
     * <pre>
     * Preprocessing: O(m + &sum;) time
     * Processing   : O(mn) worst case
     * </pre>
     * 
     * @author <a href="mailto:jb@eaio.com">Johann Burkard</a>
     */
    public static int quickSearch(final byte[] text, final int textStart, final int textEnd, final byte[] pattern) {
        final int[] skip = processBytes(pattern);
        final int ptnlen = pattern.length;
        int from = textStart;
        int p;
        while(from + ptnlen <= textEnd) {
            p = 0;
            while(p < ptnlen && pattern[p] == text[from + p]) {
                ++p;
            }
            if(p == ptnlen) {
                return from;
            }
            if(from + ptnlen >= textEnd) {
                return -1;
            }
            from += skip[index(text[from + ptnlen])];
        }
        return -1;
    }

    /**
     * Returns a <code>int</code> array.
     */
    private static int[] processBytes(final byte[] pattern) {
        final int[] skip = new int[256];
        final int ptnlen = pattern.length;
        for(int i = 0; i < 256; ++i) {
            skip[i] = ptnlen + 1;
        }
        for(int i = 0; i < ptnlen; ++i) {
            skip[index(pattern[i])] = ptnlen - i;
        }
        return skip;
    }

    /**
     * Converts the given <code>byte</code> to an <code>int</code>.
     */
    private static int index(final byte idx) {
        return (idx < 0) ? 256 + idx : idx;
    }
}
