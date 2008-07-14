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
 * This interface represents a factory to obtain sequences, item objects and
 * types. The items, sequences and types obtained through this interface are
 * independent of any connection. The items and sequences created are immutable.
 * The close method can be called to close the item or sequence and release all
 * resources associated with this item or sequence.
 */
public interface XQDataFactory {
    /**
     * Creates a new XQItemType object representing an XQuery atomic type. The
     * item kind of the item type will be XQItemType.XQITEMKIND_ATOMIC. Example -
     * XQConnection conn = ...; // to create xs:integer item type
     * conn.createAtomicType(XQItemType.XQBASETYPE_INTEGER);
     */
    XQItemType createAtomicType(int basetype) throws XQException;

    /**
     * Creates a new XQItemType object representing an XQuery atomic type. The
     * item kind of the item type will be XQItemType.XQITEMKIND_ATOMIC. Example -
     * XQConnection conn = ...; // to create po:hatsize atomic item type
     * conn.createAtomicType(XQItemType.XQBASETYPE_INTEGER, new
     * QName("http://www.hatsizes.com", "hatsize","po"), new
     * URI("http://hatschema.com"));
     */
    XQItemType createAtomicType(int basetype, javax.xml.namespace.QName typename, java.net.URI schemaURI)
            throws XQException;

    /**
     * Creates a new XQItemType object representing the XQuery attribute( , )
     * type with the given node name and base type. This method can be used to
     * create item type for attributes with a pre-defined schema type. Example -
     * XQConnection conn = ..; // An XQuery connection - attribute() // no node
     * name, pass null for the node name conn.createAttributeType(null,
     * XQItemType.XQBASETYPE_ANYSIMPLETYPE); - attribute (*) // equivalent to
     * attribute() conn.createAttributeType(null,
     * XQItemType.XQBASETYPE_ANYSIMPLETYPE); - attribute (person) // attribute
     * of name person and any simple type. conn.createAttributeType(new
     * QName("person"), XQItemType.XQBASETYPE_ANYSIMPLETYPE); -
     * attribute(foo:bar) // node name foo:bar, type is any simple type
     * conn.createAttributeType(new QName("http://www.foo.com", "bar","foo"),
     * XQItemType.XQBASETYPE_ANYSIMPLETYPE); - attribute(foo:bar, xs:integer) //
     * node name foo:bar, type is xs:integer conn.createAttributeType(new
     * QName("http://www.foo.com", "bar","foo"), XQItemType.XQBASETYPE_INTEGER);
     */
    XQItemType createAttributeType(javax.xml.namespace.QName nodename, int basetype)
            throws XQException;

    /**
     * Creates a new XQItemType object representing the XQuery attribute( , , , )
     * type, with the given node name, base type, schema type name and schema
     * URI. The type name can reference either pre-defined simple types or
     * user-defined simple types. Example - XQConnection conn = ..; // An XQuery
     * connection - attribute (name, employeename) // attribute name of type
     * employeename conn.createAttributeType(new QName("name"),
     * XQItemType.XQBASETYPE_ANYSIMPLETYPE, new QName("employeename"), null); -
     * attribute (foo:bar, po:city) where the prefix foo refers to the namespace
     * http://www.foo.com and the prefix po refers to the namespace
     * "http://www.address.com" conn.createAttributeType(new
     * QName("http://www.foo.com", "bar","foo"),
     * XQItemType.XQBASETYPE_ANYSIMPLETYPE, new QName("http://address.com",
     * "address","po"), null); - attribute (zip, zipcode) // attribute zip of
     * type zipchode which derives from // xs:string
     * conn.createAttributeType(new QName("zip"), XQItemType.XQBASETYPE_STRING,
     * new QName("zipcode"), null); - attribute(foo:bar, po:hatsize) where the
     * prefix foo refers to the namespace http://www.foo.com and the prefix po
     * refers to the namespace "http://www.hatsizes.com" with schema URI
     * "http://hatschema.com" conn.createAttributeType(new
     * QName("http://www.foo.com", "bar","foo"), XQItemType.XQBASETYPE_INTEGER,
     * new QName("http://www.hatsizes.com", "hatsize","po"), new
     * QName("http://hatschema.com"));
     */
    XQItemType createAttributeType(javax.xml.namespace.QName nodename, int basetype, javax.xml.namespace.QName typename, java.net.URI schemaURI)
            throws XQException;

    /**
     * Creates a new XQItemType object representing the XQuery comment() type.
     * The XQItemType object will have the item kind set to
     * XQItemType.XQITEMKIND_COMMENT. Example - XQConnection conn = ..; // An
     * XQuery connection XQItemType cmttype = conn.createCommentType(); int
     * itemkind = cmttype.getItemKind(); // will be
     * XQItemType.XQITEMKIND_COMMENT XQExpression expr =
     * conn.createExpression(); XQSequence result = expr.executeQuery(" !--
     * comments -- "); result.next(); boolean pi = result.instanceOf(cmttype); //
     * will be true
     */
    XQItemType createCommentType() throws XQException;

    /**
     * Creates a new XQItemType object representing the XQuery document-node( )
     * type containing a single element. The XQItemType object will have the
     * item kind set to XQItemType.XQITEMKIND_DOCUMENT_ELEMENT and the base type
     * set to the item type of the input elementType.
     */
    XQItemType createDocumentElementType(XQItemType elementType) throws XQException;

    /**
     * Creates a new XQItemType object representing the XQuery document-node( )
     * type containing a single schema-element(...). The XQItemType object will
     * have the item kind set to XQItemType.XQITEMKIND_DOCUMENT_SCHEMA_ELEMENT
     * and the base type set to the item type of the input elementType.
     */
    XQItemType createDocumentSchemaElementType(XQItemType elementType) throws XQException;

    /**
     * Creates a new XQItemType object representing the XQuery document-node()
     * type. The XQItemType object will have the item kind set to
     * XQItemType.XQITEMKIND_DOCUMENT.
     */
    XQItemType createDocumentType() throws XQException;

    /**
     * Creates a new XQItemType object representing the XQuery element( , )
     * type, with the given node name and base type. This method can be used to
     * create item type for elements with a pre-defined schema type. Example -
     * XQConnection conn = ..; // An XQuery connection - element() // no node
     * name, pass null for the node name conn.createElementType(null,
     * XQItemType.XQBASETYPE_ANYTYPE); - element (*) // equivalent to element()
     * conn.createElementType(null, XQItemType.XQBASETYPE_ANYTYPE); -
     * element(person) // element of name person and any type.
     * conn.createElementType(new QName("person"),
     * XQItemType.XQBASETYPE_ANYTYPE); - element(foo:bar) // node name foo:bar,
     * type is anytype conn.createElementType(new QName("http://www.foo.com",
     * "bar","foo"), XQItemType.XQBASETYPE_ANYTYPE); - element(foo:bar,
     * xs:integer) // node name foo:bar, type is xs:integer
     * conn.createElementType(new QName("http://www.foo.com", "bar","foo"),
     * XQItemType.XQBASETYPE_INTEGER);
     */
    XQItemType createElementType(javax.xml.namespace.QName nodename, int basetype)
            throws XQException;

    /**
     * Creates a new XQItemType object representing the XQuery element( , , , , )
     * type, given the node name, base type, schema type name, schema URI, and
     * nilled check. The type name can reference either pre-defined schema types
     * or user-defined types. Example - XQConnection conn = ..; // An XQuery
     * connection - element (person, employee) // element person of type
     * employee conn.createElementType(new QName("person"),
     * XQItemType.XQBASETYPE_ANYTYPE, new QName("employee"), null ,false); -
     * element(person, employee ? ) // element person of type employee, whose
     * nilled // property may be true or false. conn.createElementType(new
     * QName("person"), XQItemType.XQBASETYPE_ANYTYPE, new QName("employee"),
     * null ,true); - element(foo:bar, po:address) where the prefix foo refers
     * to the namespace http://www.foo.com and the prefix po refers to the
     * namespace "http://www.address.com" conn.createElementType(new
     * QName("http://www.foo.com", "bar","foo"), XQItemType.XQBASETYPE_ANYTYPE,
     * new QName("http://address.com", "address","po"), null, false); - element
     * (zip, zipcode) // element zip of type zipchode which derives from //
     * xs:string conn.createElementType(new QName("zip"),
     * XQItemType.XQBASETYPE_STRING, new QName("zipcode"), null, false); -
     * element (*, xs:anyType ?) conn.createElementType(null,
     * XQItemType.XQBASETYPE_ANYTYPE, null, null, true); - element(foo:bar,
     * po:hatsize) where the prefix foo refers to the namespace
     * http://www.foo.com and the prefix po refers to the namespace
     * "http://www.hatsizes.com" with schema URI "http://hatschema.com"
     * conn.createElementType(new QName("http://www.foo.com", "bar","foo"),
     * XQItemType.XQBASETYPE_INTEGER, new QName("http://www.hatsizes.com",
     * "hatsize","po"), new QName("http://hatschema.com"), false);
     */
    XQItemType createElementType(javax.xml.namespace.QName nodename, int basetype, javax.xml.namespace.QName typename, java.net.URI schemaURI, boolean allowNill)
            throws XQException;

    /**
     * Creates a copy of the specified XQItem. This method can be used, for
     * example, to copy an XQResultItem object so that the new item is not
     * dependant on the connection.
     */
    XQItem createItem(XQItem item) throws XQException;

    /**
     * Creates an item from a given value. The value is converted into an
     * instance of the specified type according to the casting from xs:string
     * rules outlined in . If the cast fails an XQException is thrown.
     */
    XQItem createItemFromAtomicValue(String value, XQItemType type) throws XQException;

    /**
     * Creates an item from a given value. The value is converted into an
     * instance of the specified type according to the rule defined in . If the
     * converstion fails, an XQException will be thrown.
     */
    XQItem createItemFromBoolean(boolean value, XQItemType type) throws XQException;

    /**
     * Creates an item from a given value. The value is converted into an
     * instance of the specified type according to the rule defined in . If the
     * converstion fails, an XQException will be thrown.
     */
    XQItem createItemFromByte(byte value, XQItemType type) throws XQException;

    /**
     * Creates an item from the given value. If the value represents a
     * well-formed XML document, it will be parsed and results in a document
     * node. The kind of the input type must be null,
     * XQITEMKIND_DOCUMENT_ELEMENT, or XQITEMKIND_DOCUMENT_SCHEMA_ELEMENT. The
     * value is converted into an instance of the specified type according to
     * the rules defined in . If the value is not well formed, or if a kind of
     * the input type other than the values list above is specified, behavior is
     * implementation defined and may raise an exception.
     */
    XQItem createItemFromDocument(java.io.InputStream value, String baseURI, XQItemType type)
            throws XQException;

    /**
     * Creates an item from the given value. If the value represents a
     * well-formed XML document, it will be parsed and results in a document
     * node. The kind of the input type must be null,
     * XQITEMKIND_DOCUMENT_ELEMENT, or XQITEMKIND_DOCUMENT_SCHEMA_ELEMENT. The
     * value is converted into an instance of the specified type according to
     * the rules defined in . If the value is not well formed, or if a kind of
     * the input type other than the values list above is specified, behavior is
     * implementation defined and may raise an exception.
     */
    XQItem createItemFromDocument(java.io.Reader value, String baseURI, XQItemType type)
            throws XQException;

    /**
     * Creates an item from the given Source. An XQJ implementation must at
     * least support the following implementations:
     * javax.xml.transform.dom.DOMSource javax.xml.transform.sax.SAXSource
     * javax.xml.transform.stream.StreamSource If the value represents a
     * well-formed XML document, it will result in a document node. The kind of
     * the input type must be null, XQITEMKIND_DOCUMENT_ELEMENT, or
     * XQITEMKIND_DOCUMENT_SCHEMA_ELEMENT. The value is converted into an
     * instance of the specified type according to the rules defined in . If the
     * value is not well formed, or if a kind of the input type other than the
     * values list above is specified, behavior is implementation defined and
     * may raise an exception.
     */
    XQItem createItemFromDocument(javax.xml.transform.Source value, XQItemType type)
            throws XQException;

    /**
     * Creates an item from the given value. If the value represents a
     * well-formed XML document, it will be parsed and results in a document
     * node. The kind of the input type must be null,
     * XQITEMKIND_DOCUMENT_ELEMENT, or XQITEMKIND_DOCUMENT_SCHEMA_ELEMENT. The
     * value is converted into an instance of the specified type according to
     * the rules defined in . If the value is not well formed, or if a kind of
     * the input type other than the values list above is specified, behavior is
     * implementation defined and may raise an exception.
     */
    XQItem createItemFromDocument(String value, String baseURI, XQItemType type) throws XQException;

    /**
     * Creates an item from the given value. If the XMLReader produces a
     * well-formed XML document, it results in a document node. The kind of the
     * input type must be null, XQITEMKIND_DOCUMENT_ELEMENT or
     * XQITEMKIND_DOCUMENT_SCHEMA_ELEMENT. The value is converted into an
     * instance of the specified type according to the rules defined in . If the
     * value is not well formed, or if a kind of the input type other than the
     * values list above is specified, behavior is implementation defined and
     * may raise an exception.
     */
    XQItem createItemFromDocument(org.xml.sax.XMLReader value, XQItemType type) throws XQException;

    /**
     * Creates an item from the given value. If the value represents a
     * well-formed XML document, it results in a document node. The kind of the
     * input type must be null, XQITEMKIND_DOCUMENT_ELEMENT or
     * XQITEMKIND_DOCUMENT_SCHEMA_ELEMENT. The value is converted into an
     * instance of the specified type according to the rules defined in . If the
     * value is not well formed, or if a kind of the input type other than the
     * values list above is specified, behavior is implementation defined and
     * may raise an exception.
     */
    XQItem createItemFromDocument(javax.xml.stream.XMLStreamReader value, XQItemType type)
            throws XQException;

    /**
     * Creates an item from a given value. The value is converted into an
     * instance of the specified type according to the rule defined in . If the
     * converstion fails, an XQException will be thrown.
     */
    XQItem createItemFromDouble(double value, XQItemType type) throws XQException;

    /**
     * Creates an item from a given value. The value is converted into an
     * instance of the specified type according to the rule defined in . If the
     * converstion fails, an XQException will be thrown.
     */
    XQItem createItemFromFloat(float value, XQItemType type) throws XQException;

    /**
     * Creates an item from a given value. The value is converted into an
     * instance of the specified type according to the rule defined in . If the
     * converstion fails, an XQException will be thrown.
     */
    XQItem createItemFromInt(int value, XQItemType type) throws XQException;

    /**
     * Creates an item from a given value. The value is converted into an
     * instance of the specified type according to the rule defined in . If the
     * converstion fails, an XQException will be thrown.
     */
    XQItem createItemFromLong(long value, XQItemType type) throws XQException;

    /**
     * Creates an item from a given value. The value is converted into an
     * instance of the specified type according to the rule defined in . If the
     * converstion fails, an XQException will be thrown.
     */
    XQItem createItemFromNode(org.w3c.dom.Node value, XQItemType type) throws XQException;

    /**
     * Creates an item from a given value. The value is converted into an
     * instance of the specified type according to the rule defined in . If the
     * converstion fails, an XQException will be thrown.
     */
    XQItem createItemFromObject(Object value, XQItemType type) throws XQException;

    /**
     * Creates an item from a given value. The value is converted into an
     * instance of the specified type according to the rule defined in . If the
     * converstion fails, an XQException will be thrown.
     */
    XQItem createItemFromShort(short value, XQItemType type) throws XQException;

    /**
     * Creates an item from a given value. The value is converted into an
     * instance of the specified type, which must represent an xs:string or a
     * type derived by restriction from xs:string. If the specified type is
     * null, it defaults to xs:string. Subsequently the value is converted into
     * an instance of the specified type according to the rule defined in . If
     * the conversion fails, an XQException will be thrown.
     */
    XQItem createItemFromString(String value, XQItemType type) throws XQException;

    /**
     * Creates a new XQItemType object representing the XQuery item type. The
     * XQItemType object will have the item kind set to
     * XQItemType.XQITEMKIND_ITEM. Example - XQConnection conn = ..; // An
     * XQuery connection XQItemType typ = conn.createItemType(); // represents
     * the XQuery item type "item()"
     */
    XQItemType createItemType() throws XQException;

    /**
     * Creates a new XQItemType object representing the XQuery node() type. The
     * XQItemType object will have the item kind set to
     * XQItemType.XQITEMKIND_NODE.
     */
    XQItemType createNodeType() throws XQException;

    /**
     * Creates a new XQItemType object representing the XQuery
     * processing-instruction( ) type. The XQItemType object will have the item
     * kind set to XQItemType.XQITEMKIND_PI. A string literal can be passed to
     * match the PITarget of the processing instruction as described in .
     * Example - XQConnection conn = ..; // An XQuery connection XQItemType
     * anypi = conn.createProcessingInstructionType(); XQItemType foopi =
     * conn.createProcessingInstructionType("foo-format"); XQExpression expr =
     * conn.createExpression(); XQSequence result = expr.executeQuery(" ?format
     * role="output" ? "); result.next(); boolean pi = result.instanceOf(anypi); //
     * will be true pi = result.instanceOf(foopi); // will be false XQExpression
     * expr = conn.createExpression(); XQSequence result = expr.executeQuery("
     * ?foo-format role="output" ? "); result.next(); boolean pi =
     * result.instanceOf(anypi); // will be true pi = result.instanceOf(foopi); //
     * will be true
     */
    XQItemType createProcessingInstructionType(String piTarget) throws XQException;

    /**
     * Creates a new XQItemType object representing the XQuery schema-attribute( , , )
     * type, with the given node name, base type, and schema URI. Example -
     * XQConnection conn = ..; // An XQuery connection - schema-attribute (name) //
     * schema-attribute name, found in the schema // available at
     * http://customerschema.com conn.createSchemaAttributeType(new
     * QName("name"), XQItemType.XQBASETYPE_STRING, new
     * URI(http://customerschema.com));
     */
    XQItemType createSchemaAttributeType(javax.xml.namespace.QName nodename, int basetype, java.net.URI schemaURI)
            throws XQException;

    /**
     * Creates a new XQItemType object representing the XQuery schema-element( , , )
     * type, given the node name, base type, and the schema URI. Example -
     * XQConnection conn = ..; // An XQuery connection - schema-element
     * (customer) // schema-element person, found in // the schema available at
     * http://customerschema.com conn.createElementType(new QName("customer"),
     * XQItemType.XQBASETYPE_ANYTYPE, new URI("http://customerschema.com"));
     */
    XQItemType createSchemaElementType(javax.xml.namespace.QName nodename, int basetype, java.net.URI schemaURI)
            throws XQException;

    /**
     * Creates an XQSequence, containing all the items from the iterator. The
     * newly created XQSequence is scrollable and independent of any underlying
     * XQConnection. If the iterator returns an XQItem, it is added to the
     * sequence. If the iterator returns any other object, an item is added to
     * the sequence following the rules from . If the iterator does not return
     * any items, then an empty sequence is created.
     */
    XQSequence createSequence(java.util.Iterator i) throws XQException;

    /**
     * Creates a copy of the specified XQSequence. The newly created XQSequence
     * is scrollable and independent of any underlying XQConnection. The new
     * XQSequence will contain all items from the specified XQSequence starting
     * from its current position. The copy process will implicitly perform next
     * operations on the specified sequence to read the items. All items are
     * consumed, the current position of the cursor is set to point after the
     * last item.
     */
    XQSequence createSequence(XQSequence s) throws XQException;

    /**
     * Creates a new sequence type from an item type and occurence indicator.
     */
    XQSequenceType createSequenceType(XQItemType item, int occurence) throws XQException;

    /**
     * Creates a new XQItemType object representing the XQuery text() type. The
     * XQItemType object will have the item kind set to
     * XQItemType.XQITEMKIND_TEXT.
     */
    XQItemType createTextType() throws XQException;

}
