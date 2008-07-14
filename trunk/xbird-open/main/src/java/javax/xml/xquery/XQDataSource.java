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
 * An XQDataSource is a factory for XQConnection objects. The datasource may be
 * obtained from a JNDI source or through other means. The XQuery connection
 * (XQConnection) objects may be created using an existing JDBC connection. This
 * is an optional feature that may not be supported by all implementations. If
 * the implementation supports this mechanism, then the XQuery connection may
 * inherit some of the JDBC connection's properties, such as login timeout, log
 * writer etc.
 */
public interface XQDataSource {
    /**
     * Attempts to create a connection to an XML datasource.
     */
    XQConnection getConnection() throws XQException;

    /**
     * Attempts to create a connection to an XML datasource using an existing
     * JDBC connection. An XQJ implementation is not required to support this
     * method. If it is not supported, then an exception (XQException) is
     * thrown. The XQJ and JDBC connections will operate under the same
     * transaction context.
     */
    XQConnection getConnection(java.sql.Connection con) throws XQException;

    /**
     * Attempts to establish a connection to an XML datasource using the
     * supplied username and password.
     */
    XQConnection getConnection(String username, String passwd) throws XQException;

    /**
     * Gets the maximum time in seconds that this datasource can wait while
     * attempting to connect to a database. A value of zero means that the
     * timeout is the default system timeout if there is one; otherwise, it
     * means that there is no timeout. When a XQDataSource object is created,
     * the login timeout is initially zero. It is implementation-defined whether
     * the returned login timeout is actually used by the data source
     * implementation.
     */
    int getLoginTimeout() throws XQException;

    /**
     * Retrieves the log writer for this XQDataSource object. The log writer is
     * a character output stream to which all logging and tracing messages for
     * this datasource will be printed. This includes messages printed by the
     * methods of this object, messages printed by methods of other objects
     * manufactured by this object, and so on. When a XQDataSource object is
     * created, the log writer is initially null; in other words, the default is
     * for logging to be disabled.
     */
    java.io.PrintWriter getLogWriter() throws XQException;

    /**
     * Returns the current value of the named property if set, else null. If the
     * implementation does not support the given property then an exception is
     * raised.
     */
    String getProperty(String name) throws XQException;

    /**
     * Returns an array containing the property names supported by this
     * XQDataSource. Implementations that support user name and password must
     * recognize the user name and password properties listed below. Any
     * additional properties are implementation-defined.
     */
    String[] getSupportedPropertyNames();

    /**
     * Sets the maximum time in seconds that this datasource will wait while
     * attempting to connect to a database. A value of zero specifies that the
     * timeout is the default system timeout if there is one; otherwise, it
     * specifies that there is no timeout. When a XQDataSource object is
     * created, the login timeout is initially zero. It is
     * implementation-defined whether the specified login timeout is actually
     * used by the data source implementation. If the connection is created over
     * an existing JDBC connection, then the login timeout value from the
     * underlying JDBC connection may be used.
     */
    void setLoginTimeout(int seconds) throws XQException;

    /**
     * Sets the log writer for this XQDataSource object to the given
     * java.io.PrintWriter object. The log writer is a character output stream
     * to which all logging and tracing messages for this datasource will be
     * printed. This includes messages printed by the methods of this object,
     * messages printed by methods of other objects manufactured by this object,
     * and so on. When a XQDataSource object is created the log writer is
     * initially null; in other words, the default is for logging to be
     * disabled.
     */
    void setLogWriter(java.io.PrintWriter out) throws XQException;

    /**
     * Sets the data source properties from the specified Properties instance.
     * Properties set before this call will still apply but those with the same
     * name as any of these properties will be replaced. Properties set after
     * this call also apply and may replace properties set during this call. If
     * the implementation does not support one or more of the given property
     * names, or if it can determine that the value given for a specific
     * property is invalid, then an exception is thrown. If an exception is
     * thrown, then no previous value is overwritten. is invalid, then an
     * exception is raised.
     */
    void setProperties(java.util.Properties props) throws XQException;

    /**
     * Sets the named property to the specified value. If a property with the
     * same name was already set, then this method will override the old value
     * for that property with the new value. If the implementation does not
     * support the given property or if it can determine that the value given
     * for this property is invalid, then an exception is thrown. If an
     * exception is thrown, then no previous value is overwritten.
     */
    void setProperty(String name, String value) throws XQException;

}
