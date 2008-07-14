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
 * XQMetaData interface provides information about the data source, in various
 * aspects, such as the product name and version identification, supported
 * features, specific behaviors, user information, product limits and so forth.
 * An object implementing this interface is obtained from the connection object
 * by calling the getMetaData() method, for example: XQMetaData metaData =
 * connection.getMetaData(); String productVersion =
 * metaData.getProductVersion(); ... Since the metadata object depends on the
 * connection, all its methods would raise an exception if the connection it is
 * created from is no longer valid.
 * 
 * @see XQConnection
 */
public interface XQMetaData {
    /**
     * Gets the maximum number of characters allowed in an expression in this
     * data source.
     */
    int getMaxExpressionLength() throws XQException;

    /**
     * Gets the maximum number of characters allowed in a user name.
     */
    int getMaxUserNameLength() throws XQException;

    /**
     * Gets the major version of this product.
     */
    int getProductMajorVersion() throws XQException;

    /**
     * Gets the minor version of this product.
     */
    int getProductMinorVersion() throws XQException;

    /**
     * Gets the name of this product. The value of string returned by this
     * method is implementation-defined.
     */
    String getProductName() throws XQException;

    /**
     * Gets the full version of this product. The format and value of the string
     * returned by this method is implementation-defined.
     */
    String getProductVersion() throws XQException;

    /**
     * Returns a set of String, each of which specifies a character encoding
     * method the XQJ implmentation supports to parse the XQuery query text. For
     * an example, for an XQJ impmentation which is able to parse the XQuery
     * encoded in "UTF-8" or "UTF-16", it returns a java.util.Set of "UTF-8" and
     * "UTF-16". If the implemetation is not able to generate a list of
     * encodings supported, an empty set is returned. If a non-empty set is
     * returned, the encodings returned in this set are guaranteed to be
     * supported. Note that encodings not in the returned set might also be
     * supported. For example, if the set has two encoding methods: 'UTF-8' and
     * 'UTF-16', they are supported by the implementation. However, this does
     * not mean 'Shift-Js' is not supported. It might be supported.
     */
    java.util.Set getSupportedXQueryEncodings() throws XQException;

    /**
     * Gets the user name associated with this connection.
     */
    String getUserName() throws XQException;

    /**
     * Gets the major version number of XQJ specification supported by this
     * implementation.
     */
    int getXQJMajorVersion() throws XQException;

    /**
     * Gets the minor version number of XQJ specification supported by this
     * implementation.
     */
    int getXQJMinorVersion() throws XQException;

    /**
     * Gets the full version of XQJ specification supported by this
     * implementation.
     */
    String getXQJVersion() throws XQException;

    /**
     * Query if XQuery full axis feature is supported in this connection.
     */
    boolean isFullAxisFeatureSupported() throws XQException;

    /**
     * Query if XQuery module feature is supported in this connection.
     */
    boolean isModuleFeatureSupported() throws XQException;

    /**
     * Query if the associated conection is restricted for read only use.
     */
    boolean isReadOnly() throws XQException;

    /**
     * Query if XQuery schema import feature is supported in this connection.
     */
    boolean isSchemaImportFeatureSupported() throws XQException;

    /**
     * Query if XQuery schema validation feature is supported in this
     * connection.
     */
    boolean isSchemaValidationFeatureSupported() throws XQException;

    /**
     * Query if XQuery serialization feature is supported in this connection.
     */
    boolean isSerializationFeatureSupported() throws XQException;

    /**
     * Query if XQuery static typing extensions are supported in this
     * connection.
     */
    boolean isStaticTypingExtensionsSupported() throws XQException;

    /**
     * Query if XQuery static typing feature is supported in this data source.
     */
    boolean isStaticTypingFeatureSupported() throws XQException;

    /**
     * Query if transaction is supported in this data source.
     */
    boolean isTransactionSupported() throws XQException;

    /**
     * Check if the user defined XML schema type is supported in this
     * connection. If this method returns true, then
     * XQItemAccessor.instanceOf(XQItemType) must be able to determine if the
     * type of an XQItemAccessor is an instance of the XQItemType even if either
     * of them is a user defined XML schema type which is defined by the
     * non-predefined XML schema. The pre-defined XML Schema refers to the XML
     * schema whose schema URL is "http://www.w3.org/2001/XMLSchema"
     */
    boolean isUserDefinedXMLSchemaTypeSupported() throws XQException;

    /**
     * Query if the XQuery encoding declaration is supported by the XQJ
     * implementation.
     */
    boolean isXQueryEncodingDeclSupported() throws XQException;

    /**
     * Query if a character encoding method of the XQuery query text is
     * supported by the XQJ implmentation.
     */
    boolean isXQueryEncodingSupported(String encoding) throws XQException;

    /**
     * Query if XQueryX format is supported in this data source.
     */
    boolean isXQueryXSupported() throws XQException;

    /**
     * Query if this connection was created from a JDBC connection.
     */
    boolean wasCreatedFromJDBCConnection() throws XQException;

}
