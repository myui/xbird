/*
 * @(#)$Id$
 *
 * Copyright 2008 Makoto YUI
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
 *     Makoto YUI - reversely generated from XQJ javadoc using codavaj
 */
package javax.xml.xquery;

/**
 * This interface represents a sequence of items as defined in the XDM. The
 * sequence may be materialized or non-materialized. The next method is useful
 * to position the XQSequence over the next item in the sequence. If the
 * scrollability is XQConstants.SCROLLTYPE_SCROLLABLE, then the previous method
 * can be called to move backwards. In the case of a forward only sequence, the
 * get methods may be only called once per item. To perform multiple gets on an
 * item, extract the item first from the sequence using the getItem method and
 * then operate on the XQItem object. XQPreparedExpression expr =
 * conn.prepareExpression("for $i .."); XQSequence result = expr.executeQuery(); //
 * create the ItemTypes for string and integer XQItemType strType =
 * conn.createAtomicType(XQItemType.XQBASETYPE_STRING); XQItemType intType =
 * conn.createAtomicType(XQItemType.XQBASETYPE_INT); // positioned before the
 * first item while (result.next()) { XQItemType type = result.getItemType(); //
 * If string, then get the string value out if (type.equals(strType)) String str =
 * result.getAtomicValue(); else if (type.equals(intType)) // if it is an
 * integer.. int intval = result.getInt(); ... } In a sequence, the cursor may
 * be positioned on an item, after the last item or before the first item. The
 * getPosition method returns the current position number. A value of 0
 * indicates that it is positioned before the first item, a value of count() + 1
 * indicates that it is positioned after the last item, and any other value
 * indicates that it is positioned on the item at that position. For example, a
 * position value of 1 indicates that it is positioned on the item at position
 * 1. The isOnItem method may be used to find out if the cursor is positioned on
 * the item. When the cursor is positioned on an item, the next method call will
 * move the cursor to be on the next item. See also: Section 12 Serialization,
 * XQuery API for Java (XQJ) 1.0, which describes some general information
 * applicable to various XQJ serialization methods.
 */
public interface XQSequence extends XQItemAccessor {
    /**
     * Moves the XQSequence's position to the given item number in this object.
     * If the item number is positive, the XQSequence moves to the given item
     * number with respect to the beginning of the XQSequence. The first item is
     * item 1, the second is item 2, and so on. If the given item number is
     * negative, the XQSequence positions itself on an absolute item position
     * with respect to the end of the sequence. For example, calling the method
     * absolute(-1) positions the XQSequence on the last item; calling the
     * method absolute(-2) moves the XQSequence to the next-to-last item, and so
     * on. absolute(0) will position the sequence before the first item. An
     * attempt to position the sequence beyond the first/last item set leaves
     * the current position to be before the first item or after the last item.
     * Calling this method on an empty sequence will return false.
     */
    boolean absolute(int itempos) throws XQException;

    /**
     * Move to the position after the last item.
     */
    void afterLast() throws XQException;

    /**
     * Moves to the position before the first item.
     */
    void beforeFirst() throws XQException;

    /**
     * Closes the sequence and frees all resources associated with this
     * sequence. Closing an XQSequence object also implicitly closes all XQItem
     * objects obtained from it. All methods other than the isClosed or close
     * method will raise exceptions when invoked after closing the sequence.
     * Calling close on an XQSequence object that is already closed has no
     * effect.
     */
    void close() throws XQException;

    /**
     * Returns a number indicating the number of items in the sequence.
     */
    int count() throws XQException;

    /**
     * Moves to the first item in the sequence. The method returns true, if it
     * was able to move to the first item in the sequence false, otherwise.
     * Calling this method on an empty sequence will return false.
     */
    boolean first() throws XQException;

    /**
     * Get the current item as an immutable XQItem object. In case of an
     * XQResultSequence, the item is an XQResultItem. In the case of forward
     * only sequences, this method or any other get or write method may only be
     * called once on the curent item. The XQItem object is dependent on the
     * sequence from which it was created and is only valid for the duration of
     * XQSequence lifetime. Thus, the XQSequence is closed, this XQItem object
     * will be implicitly closed and it can no longer be used.
     */
    XQItem getItem() throws XQException;

    /**
     * Gets the current cursor position. 0 indicates that the position is before
     * the first item and count() + 1 indicates position after the last item. A
     * specific position indicates that the cursor is positioned on the item at
     * that position. Use the isOnItem method to verify if the cursor is
     * positioned on the item. Calling this method on an empty sequence will
     * return 0.
     */
    int getPosition() throws XQException;

    /**
     * Read the entire sequence starting from the current position as an
     * XMLStreamReader object, as described in . Note that the serialization
     * process might fail, in which case a XQException is thrown. While the
     * stream is being read, the application MUST NOT do any other concurrent
     * operations on the sequence. The operation on the stream is undefined if
     * the underlying sequence position or state is changed by concurrent
     * operations. After all items are written to the stream, the current
     * position of the cursor is set to point after the last item. Also, in the
     * case of forward only sequences, this method may only be called if the
     * current item has not yet been read through any of the get or write
     * methods.
     */
    javax.xml.stream.XMLStreamReader getSequenceAsStream() throws XQException;

    /**
     * Serializes the sequence starting from the current position to a String
     * according to the . Serialization parameters, which influence how
     * serialization is performed, can be specified. Refer to the and for more
     * information. Reading the sequence during the serialization process
     * performs implicit next operations to read the items. After all items are
     * written to the stream, the current position of the cursor is set to point
     * after the last item. Also, in the case of forward only sequences, this
     * method may only be called if the current item has not yet been read
     * through any of the get or write methods.
     */
    String getSequenceAsString(java.util.Properties props) throws XQException;

    /**
     * Checks if the current position is after the last item in the sequence.
     * Calling this method on an empty sequence will return false.
     */
    boolean isAfterLast() throws XQException;

    /**
     * Checks if the current position before the first item in the sequence.
     * Calling this method on an empty sequence will return false.
     */
    boolean isBeforeFirst() throws XQException;

    /**
     * Checks if the sequence is closed.
     */
    boolean isClosed();

    /**
     * Checks if the current position at the first item in the sequence. Calling
     * this method on an empty sequence will return false.
     */
    boolean isFirst() throws XQException;

    /**
     * Checks if the current position at the last item in the sequence. Calling
     * this method on an empty sequence will return false.
     */
    boolean isLast() throws XQException;

    /**
     * Check if the sequence is positioned on an item or not. Calling this
     * method on an empty sequence will return false.
     */
    boolean isOnItem() throws XQException;

    /**
     * Checks if the sequence is scrollable.
     */
    boolean isScrollable() throws XQException;

    /**
     * Moves to the last item in the sequence. This method returns true, if it
     * was able to move to the last item in the sequence false, otherwise.
     * Calling this method on an empty sequence will return false.
     */
    boolean last() throws XQException;

    /**
     * Moves to the next item in the sequence. Calling this method on an empty
     * sequence will return false.
     */
    boolean next() throws XQException;

    /**
     * Moves to the previous item in the sequence. Calling this method on an
     * empty sequence will return false.
     */
    boolean previous() throws XQException;

    /**
     * Moves the cursor a relative number of items, either positive or negative.
     * Attempting to move beyond the first/last item in the sequence positions
     * the sequence before/after the the first/last item. Calling relative(0) is
     * valid, but does not change the cursor position. Note: Calling the method
     * relative(1) is identical to calling the method next and calling the
     * method relative(-1) is identical to calling the method previous().
     * Calling this method on an empty sequence will return false.
     */
    boolean relative(int itempos) throws XQException;

    /**
     * Serializes the sequence starting from the current position to an
     * OutputStream according to the . Serialization parameters, which influence
     * how serialization is performed, can be specified. Refer to the and for
     * more information. Reading the sequence during the serialization process
     * performs implicit next operations to read the items. After all items are
     * written to the stream, the current position of the cursor is set to point
     * after the last item. Also, in the case of forward only sequences, this
     * method may only be called if the current item has not yet been read
     * through any of the get or write methods.
     */
    void writeSequence(java.io.OutputStream os, java.util.Properties props) throws XQException;

    /**
     * Serializes the sequence starting from the current position to a Writer
     * according to the . Serialization parameters, which influence how
     * serialization is performed, can be specified. Refer to the and for more
     * information. Warning: When outputting to a Writer, make sure the writer's
     * encoding matches the encoding parameter if specified as a property or the
     * default encoding. Reading the sequence during the serialization process
     * performs implicit next operations to read the items. After all items are
     * written to the stream, the current position of the cursor is set to point
     * after the last item. Also, in the case of forward only sequences, this
     * method may only be called if the current item has not yet been read
     * through any of the get or write methods.
     */
    void writeSequence(java.io.Writer ow, java.util.Properties props) throws XQException;

    /**
     * Writes the entire sequence starting from the current position to a
     * Result. First the sequence is normalized as described in . Subsequently
     * it is serialized to the Result object. Note that the normalization
     * process can fail, in which case an XQException is thrown. An XQJ
     * implementation must at least support the following implementations:
     * javax.xml.transform.dom.DOMResult javax.xml.transform.sax.SAXResult
     * javax.xml.transform.stream.StreamResult
     */
    void writeSequenceToResult(javax.xml.transform.Result result) throws XQException;

    /**
     * Writes the entire sequence starting from the current position to a SAX
     * handler, as described in . Note that the serialization process might
     * fail, in which case a XQException is thrown. After all items are written
     * to the stream, the current position of the cursor is set to point after
     * the last item. Also, in the case of forward only sequences, this method
     * may only be called if the current item has not yet been read through any
     * of the get or write methods. The specified org.xml.sax.ContentHandler can
     * optionally implement the org.xml.sax.LexicalHandler interface. An
     * implementation must check if the specified ContentHandler implements
     * LexicalHandler. If the handler is a LexicalHandler comment nodes are
     * reported, otherwise they will be silently ignored.
     */
    void writeSequenceToSAX(org.xml.sax.ContentHandler saxhdlr) throws XQException;

}
