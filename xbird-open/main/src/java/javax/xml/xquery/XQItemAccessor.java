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
 * This interface represents a common interface for accessing the values of an
 * XQuery item. All the get functions raise an exception if the underlying
 * sequence object is not positioned on an item (e.g. if the sequence is
 * positioned before the first item or after the last item). Example -
 * XQPreparedExpression expr = conn.prepareExpression("for $i .."); XQSequence
 * result = expr.executeQuery(); // create the ItemTypes for string and integer
 * XQItemType strType = conn.createAtomicType(XQItemType.XQBASETYPE_STRING);
 * XQItemType intType = conn.createAtomicType(XQItemType.XQBASETYPE_INTEGER); //
 * posititioned before the first item while (result.next()) { // If string or
 * any of its subtypes, then get the string value out if
 * (result.instanceOf(strType)) String str = result.getAtomicValue(); else if
 * (result.instanceOf(intType)) // if it is exactly an int int intval =
 * result.getInt(); ... // Alternatively, you can get the exact type out.
 * XQItemType type = result.getItemType(); // Now perform the comparison.. if
 * (type.equals(intType)) { ... }; } See also: Table 6 - XQuery Atomic Types and
 * Corresponding Java Object Types, XQuery API for Java (XQJ) 1.0, for mapping
 * of XQuery atomic types to Java object types. For example, if the XQuery value
 * returned is of type xs:unsignedByte, then calling the getObject() method will
 * return a Java object of type Short. Table 7 - XQuery Node Types and
 * Corresponding Java Object Types XQuery API for Java (XQJ) 1.0, for the
 * mapping of XQuery node types to the corresponding Java object types. For
 * example, if the XQuery value returned is an element node, then calling the
 * getObject() or getNode() method will return a Java object of type
 * org.w3.dom.Element.
 */
public interface XQItemAccessor {
    /**
     * Gets the current item as a Java String. The current item must be an
     * atomic value. This function casts the current item to an xs:string value
     * according to the casting rules defined in , and then returns the value as
     * a Java String.
     */
    String getAtomicValue() throws XQException;

    /**
     * Gets the current item as a boolean. The current item must be an atomic
     * value of type xs:boolean or a subtype.
     */
    boolean getBoolean() throws XQException;

    /**
     * Gets the current item as a byte. The current item must be an atomic value
     * of type xs:decimal or a subtype, and its value must be in the value space
     * of byte.
     */
    byte getByte() throws XQException;

    /**
     * Gets the current item as a double. The current item must be an atomic
     * value of type xs:double or a subtype.
     */
    double getDouble() throws XQException;

    /**
     * Gets the current item as a float. The current item must be an atomic
     * value of type xs:float or a subtype.
     */
    float getFloat() throws XQException;

    /**
     * Gets the current item as an int. The current item must be an atomic value
     * of type xs:decimal or a subtype, and its value must be in the value space
     * of int.
     */
    int getInt() throws XQException;

    /**
     * Read the current item as an XMLStreamReader object, as described in .
     * Note that the serialization process might fail, in which case a
     * XQException is thrown. While the stream is being read, the application
     * MUST NOT do any other concurrent operations on the underlying item or
     * sequence. The operation on the stream is undefined if the underlying
     * sequence is repositioned or the state of the underlying item or sequence
     * is changed by concurrent operations.
     */
    javax.xml.stream.XMLStreamReader getItemAsStream() throws XQException;

    /**
     * Serializes the current item according to the . Serialization parameters,
     * which influence how serialization is performed, can be specified. Refer
     * to the and for more information.
     */
    String getItemAsString(java.util.Properties props) throws XQException;

    /**
     * Gets the type of the item. On a forward only sequence this method can be
     * called independent of any other get or write method. It will not raise an
     * error if such method has been called already, nor will it affect
     * subsequent invocations of any other get or write method.
     */
    XQItemType getItemType() throws XQException;

    /**
     * Gets the current item as a long. The current item must be an atomic value
     * of type xs:decimal or a subtype, and its value must be in the value space
     * of long.
     */
    long getLong() throws XQException;

    /**
     * Gets the item as a DOM node. The current item must be a node. The type of
     * the returned DOM node is governed by The instance of the returned node is
     * implementation dependent. The node may be a reference or a copy of the
     * internal state of the item. It is advisable to make a copy of the node if
     * the application plans to modify it.
     */
    org.w3c.dom.Node getNode() throws XQException;

    /**
     * Returns the URI for this item. If the item is a document node, then this
     * method returns the absolute URI of the resource from which the document
     * node was constructed. If the document URI is not available, then the
     * empty string is returned. If the document URI is available, the returned
     * value is the same as if fn:document-uri were evaluated on this document
     * node. If the item is of a node kind other than document node, then the
     * returned URI is implementation-defined. On a forward only sequence this
     * method can be called independent of any other get or write method. It
     * will not raise an error if such method has been called already, nor will
     * it affect subsequent invocations of any other get or write method on the
     * current item.
     */
    java.net.URI getNodeUri() throws XQException;

    /**
     * Gets the current item as an Object. The data type of the returned object
     * will be the Java Object type as specified in .
     */
    Object getObject() throws XQException;

    /**
     * Gets the current item as a short. The current item must be an atomic
     * value of type xs:decimal or a subtype, and its value must be in the value
     * space of short.
     */
    short getShort() throws XQException;

    /**
     * Checks if the item "matches" an item type, as defined in . You can use
     * this method to first check the type of the result before calling the
     * specific get methods. Example - ... XQItemType strType =
     * conn.createAtomicType(XQItemType.XQBASETYPE_STRING); XQItemType nodeType =
     * conn.createNodeType(); XQSequence result = preparedExpr.executeQuery();
     * while (result.next()) { // Generic check for node.. if
     * (result.instanceOf(nodeType)) org.w3.dom.Node node = result.getNode();
     * else if (result.instanceOf(strType)) String str =
     * result.getAtomicValue(); } If either the type of the XQItemAccessor or
     * the input XQItemType is not a built-in type, then this method is allowed
     * to raise exception if it can NOT determine the instanceOf relationship
     * due to the lack of the access of the XML schema that defines the user
     * defined schema types if the
     * XQMetaData.isUserDefinedXMLSchemaTypeSupported() method returns false.
     * Otherwise, this method must determine if the type of the XQItemAccessor
     * is an instance of the input XQItemType. Note even if
     * isUserDefinedXMLSchemaTypeSupported() returns false, an XQJ
     * implementation may still be able to determine the instanceOf relationship
     * for certain cases involving user defined schema type. For example, if the
     * type of an XQItemAccessor is of mySchema:hatSize sequence type and the
     * input parameter XQItemType is of item() sequence type, the return value
     * for instanceOf relationship should always be true even though the XQJ
     * implementation does not know the precise type information of
     * mySchema:hatSize type defined in XML schema 'mySchema'.
     */
    boolean instanceOf(XQItemType type) throws XQException;

    /**
     * Serializes the current item to a Writer according to . Serialization
     * parameters, which influence how serialization is performed, can be
     * specified. Refer to the and for more information.
     */
    void writeItem(java.io.OutputStream os, java.util.Properties props) throws XQException;

    /**
     * Serializes the current item to a Writer according to . Serialization
     * parameters, which influence how serialization is performed, can be
     * specified. Refer to the and for more information. Warning: When
     * outputting to a Writer, make sure the writer's encoding matches the
     * encoding parameter if specified as a property or the default encoding.
     */
    void writeItem(java.io.Writer ow, java.util.Properties props) throws XQException;

    /**
     * Writes the current item to a Result. First the item is normalized as
     * described in . Subsequently it is serialized to the Result object. Note
     * that the normalization process can fail, in which case an XQException is
     * thrown. An XQJ implementation must at least support the following
     * implementations: javax.xml.transform.dom.DOMResult
     * javax.xml.transform.sax.SAXResult javax.xml.transform.stream.StreamResult
     */
    void writeItemToResult(javax.xml.transform.Result result) throws XQException;

    /**
     * Writes the current item to a SAX handler, as described in in . Note that
     * the serialization process might fail, in which case a XQException is
     * thrown. The specified org.xml.sax.ContentHandler can optionally implement
     * the org.xml.sax.LexicalHandler interface. An implementation must check if
     * the specified ContentHandler implements LexicalHandler. If the handler is
     * a LexicalHandler comment nodes are reported, otherwise they will be
     * silently ignored.
     */
    void writeItemToSAX(org.xml.sax.ContentHandler saxhdlr) throws XQException;

}
