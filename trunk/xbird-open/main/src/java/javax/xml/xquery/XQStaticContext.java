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
 * An XQStaticContext represents default values for various XQuery Static
 * Context Components. Further it includes the static XQJ properties for an
 * XQExpression or XQPreparedExpression object. The following XQuery Static
 * Context Components are supported through the XQStaticContext interface:
 * Statically known namespaces Default element/type namespace Default function
 * namespace Context item static type Default collation Construction mode
 * Ordering mode Default order for empty sequences Boundary-space policy
 * Copy-namespaces mode Base URI As described in the XQuery specification, each
 * of these default values can be overridden or augmented in the query prolog.
 * In addition XQStaticContext includes the static XQJ properties for an
 * XQExpression or XQPreparedExpression object: Binding mode Holdability of the
 * result sequences Scrollability of the result sequences Query language Query
 * timeout Note that XQStaticContext is a value object, changing attributes in
 * such object doesn't affect any existing XQExpression or XQPreparedExpression
 * object. In order to take effect, the application needs to explicitly change
 * the XQConnection default values, or specify an XQStaticContext object when
 * creating an XQExpression or XQPreparedExpression. XQConnection conn =
 * XQDatasource.getConnection(); // get the default values from the
 * implementation XQStaticContext cntxt = conn.getStaticContext(); // change the
 * base uri cntxt.setBaseURI("http://www.foo.com/xml/"); // change the
 * implementation defaults conn.setStaticContext(cntxt); // create an
 * XQExpression using the new defaults XQExpression expr1 =
 * conn.createExpression(); // creat an XQExpression, using BaseURI
 * "file:///root/user/john/" cntxt.setBaseURI("file:///root/user/john/");
 * XQExpression expr2 = conn.createExpression(cntxt); ...
 */
public interface XQStaticContext {
    /**
     * Declares a namespace prefix and associates it with a namespace URI. If
     * the namespace URI is the empty string, the prefix is removed from the
     * in-scope namespace definitions.
     */
    void declareNamespace(String prefix, String uri) throws XQException;

    /**
     * Gets the Base URI, if set in the static context, else the empty string.
     */
    String getBaseURI();

    /**
     * Gets the value of the binding mode property. By default an XQJ
     * implementation operates in immediate binding mode.
     */
    int getBindingMode();

    /**
     * Gets the boundary-space policy defined in the static context.
     */
    int getBoundarySpacePolicy();

    /**
     * Gets the construction mode defined in the static context.
     */
    int getConstructionMode();

    /**
     * Gets the static type of the context item. null if unspecified.
     */
    XQItemType getContextItemStaticType();

    /**
     * Gets the inherit part of the copy-namespaces mode defined in the static
     * context.
     */
    int getCopyNamespacesModeInherit();

    /**
     * Gets the preserve part of the copy-namespaces mode defined in the static
     * context.
     */
    int getCopyNamespacesModePreserve();

    /**
     * Gets the URI of the default collation.
     */
    String getDefaultCollation();

    /**
     * Gets the URI of the default element/type namespace, the empty string if
     * not set.
     */
    String getDefaultElementTypeNamespace();

    /**
     * Gets the URI of the default function namespace, the empty string if not
     * set.
     */
    String getDefaultFunctionNamespace();

    /**
     * Gets the default order for empty sequences defined in the static context.
     */
    int getDefaultOrderForEmptySequences();

    /**
     * Gets the value of the holdability property.
     */
    int getHoldability();

    /**
     * Returns the prefixes of all the statically known namespaces. Use the
     * getNamespaceURI method to look up the namespace URI corresponding to a
     * specific prefix.
     */
    String[] getNamespacePrefixes();

    /**
     * Retrieves the namespace URI associated with a prefix. An XQException is
     * thrown if an unknown prefix is specified, i.e. a prefix not returned by
     * the getInScopeNamespacePrefixes method.
     */
    String getNamespaceURI(String prefix) throws XQException;

    /**
     * Gets the ordering mode defined in the static context.
     */
    int getOrderingMode();

    /**
     * Gets the value of the language type and version property. By default an
     * XQJ implementation's default is XQConstants.LANGTYPE_XQUERY.
     */
    int getQueryLanguageTypeAndVersion();

    /**
     * Retrieves the number of seconds an implementation will wait for a query
     * to execute.
     */
    int getQueryTimeout();

    /**
     * Gets the value of the scrollability property. By default query results
     * are forward only.
     */
    int getScrollability();

    /**
     * Sets the Base URI in the static context, specify the empty string to make
     * it undefined.
     */
    void setBaseURI(String baseUri) throws XQException;

    /**
     * Sets the binding mode property. By default an XQJ implementation operates
     * in immediate binding mode.
     */
    void setBindingMode(int bindingMode) throws XQException;

    /**
     * Sets the boundary-space policy in the static context.
     */
    void setBoundarySpacePolicy(int policy) throws XQException;

    /**
     * Sets the construction mode in the static context.
     */
    void setConstructionMode(int mode) throws XQException;

    /**
     * Sets the static type of the context item, specify null to make it
     * unspecified.
     */
    void setContextItemStaticType(XQItemType contextItemType);

    /**
     * Sets the inherit part of the copy-namespaces mode in the static context.
     */
    void setCopyNamespacesModeInherit(int mode) throws XQException;

    /**
     * Sets the preserve part of the copy-namespaces mode in the static context.
     */
    void setCopyNamespacesModePreserve(int mode) throws XQException;

    /**
     * Sets the URI of the default collation.
     */
    void setDefaultCollation(String uri) throws XQException;

    /**
     * Sets the URI of the default element/type namespace, the empty string to
     * make it unspecified.
     */
    void setDefaultElementTypeNamespace(String uri) throws XQException;

    /**
     * Sets the URI of the default function namespace, the empty string to make
     * it unspecified.
     */
    void setDefaultFunctionNamespace(String uri) throws XQException;

    /**
     * Sets the default order for empty sequences in the static context.
     */
    void setDefaultOrderForEmptySequences(int order) throws XQException;

    /**
     * Sets the holdability property.
     */
    void setHoldability(int holdability) throws XQException;

    /**
     * Sets the ordering mode in the static context.
     */
    void setOrderingMode(int mode) throws XQException;

    /**
     * Sets the input query language type and version. When this is set to a
     * particular language type and version, then the query is assumed to be in
     * that language and version.
     */
    void setQueryLanguageTypeAndVersion(int langType) throws XQException;

    /**
     * Sets the number of seconds an implementation will wait for a query to
     * execute. If the implementation does not support query timeout it can
     * ignore the specified timeout value. It the limit is exceeded, the behavor
     * of the query is the same as an execution of a cancel by another thread.
     */
    void setQueryTimeout(int seconds) throws XQException;

    /**
     * Sets the scrollability of the result sequence. By default query results
     * are forward only.
     */
    void setScrollability(int scrollability) throws XQException;

}
