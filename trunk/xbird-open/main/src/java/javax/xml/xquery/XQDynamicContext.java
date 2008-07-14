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
 * XQDynamicContext provides access to the dynamic context as defined in 2.1.2
 * Dynamic Context, XQuery 1.0: An XML Query Language. The following components
 * can be accessed: The context item can be set The variable values can be bound
 * The implicit time zone can be retrieved or specified Where the prolog of the
 * expression specifies the static type of external variables, this interface
 * allows the dynamic type and value of the variable to be specified. Note that
 * in case of an XQPreparedExpression, values may only be bound to those
 * variables that are defined in the prolog of the expression. Example -
 * XQConnection conn = XQDataSource.getConnection(); // create an
 * XQPreparedExpression with external variable XQPreparedExpression e1 =
 * conn.prepareExpression("declare variable $i as xs:int external; $i + 10"); //
 * bind an int to the external variable e1.bindInt(new QName("i"), 200, null); //
 * this will fail as the expression has no external variable $foo e1.bindInt(new
 * QName("foo"), 200, null); // this will fail as xs:double is not compatible
 * with an external variable declared as xs:int e1.bindDouble(new QName("i"),
 * 2e2, null); // create an XQExpression with external variable XQExpression e2 =
 * conn.createExpression(); // bind a value to $i and $foo e2.bindInt(new
 * QName("i"), 200, null); e2.bindInt(new QName("foo"), 200, null); // the value
 * bound to $foo is ignored as the expression doesn't // declare $foo as
 * external variable e2.executeQuery("declare variable $i as xs:int external; $i +
 * 10"); Binding a value to the context item is achieved in the same way as
 * binding a value to an external variable. However, instead of specifying the
 * variable's name as first argument of the bindXXX() method, use
 * XQConstants.CONTEXT_ITEM as the first argument. Binding mode The default
 * binding mode is immediate. In other words, the external variable value
 * specified by the application is consumed during the bindXXX() method. An
 * application has the ability to set the binding mode to deferred. In deferred
 * mode an application cannot assume that the bound value will be consumed
 * during the invocation of the bindXXX method. In such scenario the order in
 * which the bindings are evaluated is implementation-dependent, and an
 * implementation doesn't necessarily need to consume a binding if it can
 * evaluate the query without requiring the external variable. The XQJ
 * implementation is also free to read the bound value either at bind time or
 * during the subsequent evaluation and processing of the query results. Also
 * note that in deferred binding mode, bindings are only active for a single
 * execution cycle. The application is required to explicitly re-bind values to
 * every external variable before each execution. Failing to do so will result
 * in an XQException, as the implementation will assume during the next
 * execution that none of the external variables are bound. Finally, note that
 * in deferred binding mode, any error condition specified to throw an exception
 * during the bindXXX() methods, may as well be thrown later during the query's
 * evaluation. Example - in case of an immediate binding mode, bindings stay
 * active over executions // BINDING_MODE_IMMEDIATE is the default, no need to
 * change it QName v = new QName(v); XQPreparedExpression e =
 * c.prepareExpression("declare variable $v external; $v"); e.bindInt(v, 1) //
 * successful execution e.executeQuery(); // successful execution
 * e.executeQuery(); Example - in case of a deferred binding mode, bindings are
 * only valid for a single execution // BINDING_MODE_IMMEDIATE is the default,
 * change it to // BINDING_MODE_DEFERRED XQStaticContext cntxt =
 * c.getStaticContext();
 * cntxt.setBindingMode(XQConstants.BINDING_MODE_DEFERRED);
 * c.setStaticContext(cntxt); QName v = new QName(v); XQPreparedExpression e =
 * c.prepareExpression("declare variable $v external; $v"); e.bindInt(v, 1) //
 * successful execution XQSequence s = e.executeQuery(); while (s.next())
 * System.out.println(s.getInt()); // an error is reported during the next query //
 * evaluation as not all external variables are bound s = e.executeQuery();
 * while (s.next()) System.out.println(s.getInt());
 */
public interface XQDynamicContext {
    /**
     * Binds a value to the given external variable or the context item. The
     * value is converted into an instance of the specified type according to
     * the casting from xs:string rules outlined in . If the cast fails, or if
     * there is a mismatch between the static and dynamic types, an XQException
     * is thrown either by this method or during query evaluation.
     */
    void bindAtomicValue(javax.xml.namespace.QName varName, String value, XQItemType type)
            throws XQException;

    /**
     * Binds a value to the given external variable or the context item. The
     * value is converted into an instance of the specified type according to
     * the rule defined in . If the conversion fails, or if there is a mismatch
     * between the static and dynamic types, an XQException is raised either by
     * this method, or during query evaluation.
     */
    void bindBoolean(javax.xml.namespace.QName varName, boolean value, XQItemType type)
            throws XQException;

    /**
     * Binds a value to the given external variable or the context item. The
     * value is converted into an instance of the specified type according to
     * the rule defined in . If the conversion fails, or if there is a mismatch
     * between the static and dynamic types, an XQException is raised either by
     * this method, or during query evaluation.
     */
    void bindByte(javax.xml.namespace.QName varName, byte value, XQItemType type)
            throws XQException;

    /**
     * Binds a value to the given external variable or the context item. If the
     * value represents a well-formed XML document, it will be parsed and
     * results in a document node. The kind of the input type must be null,
     * XQITEMKIND_DOCUMENT_ELEMENT, or XQITEMKIND_DOCUMENT_SCHEMA_ELEMENT. The
     * value is converted into an instance of the specified type according to
     * the rules defined in . If the conversion fails, or if there is a mismatch
     * between the static and dynamic types, an XQException is raised either by
     * this method, or during query evaluation. If the value is not well formed,
     * or if a kind of the input type other than the values list above is
     * specified, behavior is implementation defined and may raise an exception.
     */
    void bindDocument(javax.xml.namespace.QName varName, java.io.InputStream value, String baseURI, XQItemType type)
            throws XQException;

    /**
     * Binds a value to the given external variable or the context item. If the
     * value represents a well-formed XML document, it will be parsed and
     * results in a document node. The kind of the input type must be null,
     * XQITEMKIND_DOCUMENT_ELEMENT, or XQITEMKIND_DOCUMENT_SCHEMA_ELEMENT. The
     * value is converted into an instance of the specified type according to
     * the rules defined in . If the conversion fails, or if there is a mismatch
     * between the static and dynamic types, an XQException is raised either by
     * this method, or during query evaluation. If the value is not well formed,
     * or if a kind of the input type other than the values list above is
     * specified, behavior is implementation defined and may raise an exception.
     */
    void bindDocument(javax.xml.namespace.QName varName, java.io.Reader value, String baseURI, XQItemType type)
            throws XQException;

    /**
     * Binds a value to the given external variable or the context item from the
     * given Source. An XQJ implementation must at least support the following
     * implementations: javax.xml.transform.dom.DOMSource
     * javax.xml.transform.sax.SAXSource javax.xml.transform.stream.StreamSource
     * If the value represents a well-formed XML document, it will result in a
     * document node. The kind of the input type must be null,
     * XQITEMKIND_DOCUMENT_ELEMENT, or XQITEMKIND_DOCUMENT_SCHEMA_ELEMENT. The
     * value is converted into an instance of the specified type according to
     * the rules defined in . If the value is not well formed, or if a kind of
     * the input type other than the values list above is specified, behavior is
     * implementation defined and may raise an exception. If the conversion
     * fails, or if there is a mismatch between the static and dynamic types, an
     * XQException is raised either by this method, or during query evaluation.
     */
    void bindDocument(javax.xml.namespace.QName varName, javax.xml.transform.Source value, XQItemType type)
            throws XQException;

    /**
     * Binds a value to the given external variable or the context item. If the
     * value represents a well-formed XML document, it will be parsed and
     * results in a document node. The kind of the input type must be null,
     * XQITEMKIND_DOCUMENT_ELEMENT, or XQITEMKIND_DOCUMENT_SCHEMA_ELEMENT. The
     * value is converted into an instance of the specified type according to
     * the rules defined in . If the conversion fails, or if there is a mismatch
     * between the static and dynamic types, an XQException is raised either by
     * this method, or during query evaluation. If the value is not well formed,
     * or if a kind of the input type other than the values list above is
     * specified, behavior is implementation defined and may raise an exception.
     */
    void bindDocument(javax.xml.namespace.QName varName, String value, String baseURI, XQItemType type)
            throws XQException;

    /**
     * Binds a value to the given external variable. If the XMLReader produces a
     * well-formed XML document, it results in a document node. The kind of the
     * input type must be null, XQITEMKIND_DOCUMENT_ELEMENT or
     * XQITEMKIND_DOCUMENT_SCHEMA_ELEMENT. The value is converted into an
     * instance of the specified type according to the rules defined in . If the
     * value is not well formed, or if a kind of the input type other than the
     * values list above is specified, behavior is implementation defined and
     * may raise an exception. If the conversion fails, or if there is a
     * mismatch between the static and dynamic types, an XQException is raised
     * either by this method, or the execute method. The contract of this method
     * is as follows. First a ContentHandler is passed to the XML XMLReader; and
     * optionally an implementation can specify additional handlers like a
     * LexicalHandler. Subsequently parse(String systemId) will be invoked; and
     * as such the XMLReader will pass the SAX event representing the document
     * to bind. The systemId argument identifies the external variable, a QName
     * formatted into a String using the James Clark representation. Example -
     * the application loads an XML document (foo.xml) using a SAX parser, this
     * document is bound to an external variable. The XQuery returns all foo
     * elements in the document, which are written to stdout. Assume there is an
     * XQConnection connection... ... // Create an XMLReader, which will pass
     * the SAX events to the XQJ // implementation XMLFilter xmlReader = new
     * XMLFilterImpl() { public void parse(String systemId) throws IOException,
     * SAXException { // foo.xml is the XML document to bind
     * super.parse("foo.xml"); } }; // The parent XML Reader is a SAX parser,
     * which will do the // actual work of parsing the XML document
     * xmlReader.setParent(org.xml.sax.helpers.XMLReaderFactory.createXMLReader()); //
     * Create an XQPreparedExpression XQPreparedExpression e =
     * connection.prepareExpression("declare variable $doc as
     * document-node(element(*, xs:untyped)) external; $doc//foo));
     * e.bindDocument(new QName("doc"), xmlReader); XQResultSequence result =
     * preparedExpression.executeQuery(); result.writeSequence(System.out); ...
     */
    void bindDocument(javax.xml.namespace.QName varName, org.xml.sax.XMLReader value, XQItemType type)
            throws XQException;

    /**
     * Binds a value to the given external variable or the context item. If the
     * value represents a well-formed XML document, it results in a document
     * node. The kind of the input type must be null,
     * XQITEMKIND_DOCUMENT_ELEMENT or XQITEMKIND_DOCUMENT_SCHEMA_ELEMENT. The
     * value is converted into an instance of the specified type according to
     * the rules defined in . If the value is not well formed, or if a kind of
     * the input type other than the values list above is specified, behavior is
     * implementation defined and may raise an exception. If the conversion
     * fails, or if there is a mismatch between the static and dynamic types, an
     * XQException is raised either by this method, or during query evaluation.
     */
    void bindDocument(javax.xml.namespace.QName varName, javax.xml.stream.XMLStreamReader value, XQItemType type)
            throws XQException;

    /**
     * Binds a value to the given external variable or the context item. The
     * value is converted into an instance of the specified type according to
     * the rule defined in . If the conversion fails, or if there is a mismatch
     * between the static and dynamic types, an XQException is raised either by
     * this method, or during query evaluations.
     */
    void bindDouble(javax.xml.namespace.QName varName, double value, XQItemType type)
            throws XQException;

    /**
     * Binds a value to the given external variable or the context item. The
     * value is converted into an instance of the specified type according to
     * the rule defined in . If the conversion fails, or if there is a mismatch
     * between the static and dynamic types, an XQException is raised either by
     * this method, or during query evaluations.
     */
    void bindFloat(javax.xml.namespace.QName varName, float value, XQItemType type)
            throws XQException;

    /**
     * Binds a value to the given external variable or the context item. The
     * value is converted into an instance of the specified type according to
     * the rule defined in . If the conversion fails, or if there is a mismatch
     * between the static and dynamic types, an XQException is raised either by
     * this method, or during query evaluations.
     */
    void bindInt(javax.xml.namespace.QName varName, int value, XQItemType type) throws XQException;

    /**
     * Binds a value to the given external variable. The dynamic type of the
     * value is derived from the XQItem. In case of a mismatch between the
     * static and dynamic types, an XQException is raised either by this method,
     * or during query evaluation.
     */
    void bindItem(javax.xml.namespace.QName varName, XQItem value) throws XQException;

    /**
     * Binds a value to the given external variable or the context item. The
     * value is converted into an instance of the specified type according to
     * the rule defined in . If the conversion fails, or if there is a mismatch
     * between the static and dynamic types, an XQException is raised either by
     * this method, or during query evaluation.
     */
    void bindLong(javax.xml.namespace.QName varName, long value, XQItemType type)
            throws XQException;

    /**
     * Binds a value to the given external variable or the context item. The
     * value is converted into an instance of the specified type according to
     * the rule defined in . If the conversion fails, or if there is a mismatch
     * between the static and dynamic types, an XQException is raised either by
     * this method, or during query evaluation.
     */
    void bindNode(javax.xml.namespace.QName varName, org.w3c.dom.Node value, XQItemType type)
            throws XQException;

    /**
     * Binds a value to the given external variable or the context item. The
     * value is converted into an instance of the specified type according to
     * the rule defined in . If the conversion fails, or if there is a mismatch
     * between the static and dynamic types, an XQException is raised either by
     * this method, or during query evaluation.
     */
    void bindObject(javax.xml.namespace.QName varName, Object value, XQItemType type)
            throws XQException;

    /**
     * Binds a value to the given external variable or the context item. The
     * input sequence is consumed from its current position to the end, after
     * which the input sequence's position will be set to point after the last
     * item. The dynamic type of the value is derived from the items in the
     * sequence. In case of a mismatch between the static and dynamic types, an
     * XQException is be raised either by this method, or during query
     * evaluation.
     */
    void bindSequence(javax.xml.namespace.QName varName, XQSequence value) throws XQException;

    /**
     * Binds a value to the given external variable or the context item. The
     * value is converted into an instance of the specified type according to
     * the rule defined in . If the conversion fails, or if there is a mismatch
     * between the static and dynamic types, an XQException is raised either by
     * this method, or during query evaluation.
     */
    void bindShort(javax.xml.namespace.QName varName, short value, XQItemType type)
            throws XQException;

    /**
     * Binds a value to the given external variable or the context item. The
     * value is converted into an instance of the specified type, which must
     * represent an xs:string or a type derived by restriction from xs:string.
     * If the specified type is null, it defaults to xs:string. Subsequently the
     * value is converted into an instance of the specified type according to
     * the rule defined in . If the conversion fails, or if there is a mismatch
     * between the static and dynamic types, an XQException is raised either by
     * this method, or during query evaluation.
     */
    void bindString(javax.xml.namespace.QName varName, String value, XQItemType type)
            throws XQException;

    /**
     * Gets the implicit timezone
     */
    java.util.TimeZone getImplicitTimeZone() throws XQException;

    /**
     * Sets the implicit timezone
     */
    void setImplicitTimeZone(java.util.TimeZone implicitTimeZone) throws XQException;

}
