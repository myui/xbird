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
 * A factory for PooledXQConnection objects. An object that implements this
 * interface will typically be registered with a JNDI based naming service.
 */
public interface ConnectionPoolXQDataSource {
    /**
     * Gets the maximum time in seconds that this datasource can wait while
     * attempting to connect to a database. A value of zero means that the
     * timeout is the default system timeout if there is one; otherwise, it
     * means that there is no timeout. When a datasource object is created, the
     * login timeout is initially zero. It is implementation-defined whether the
     * returned login timeout is actually used by the data source
     * implementation.
     */
    int getLoginTimeout() throws XQException;

    /**
     * Retrieves the log writer for this datasource object. The log writer is a
     * character output stream to which all logging and tracing messages for
     * this datasource will be printed. This includes messages printed by the
     * methods of this object, messages printed by methods of other objects
     * manufactured by this object, and so on. When a datasource object is
     * created, the log writer is initially null; in other words, the default is
     * for logging to be disabled.
     */
    java.io.PrintWriter getLogWriter() throws XQException;

    /**
     * Attempts to establish a physical connection to an XML datasource that can
     * be used as a pooled connection.
     */
    PooledXQConnection getPooledConnection() throws XQException;

    /**
     * Attempts to establish a physical connection to an XML datasource using
     * the supplied username and password, that can be used as a pooled
     * connection.
     */
    PooledXQConnection getPooledConnection(String user, String password) throws XQException;

    /**
     * Returns the current value of the named property if set, else null. If the
     * implementation does not support the given property then an exception is
     * raised.
     */
    String getProperty(String name) throws XQException;

    /**
     * Returns an array containing the property names supported by this
     * datasource.
     */
    String[] getSupportedPropertyNames();

    /**
     * Sets the maximum time in seconds that this datasource will wait while
     * attempting to connect to a database. A value of zero specifies that the
     * timeout is the default system timeout if there is one; otherwise, it
     * specifies that there is no timeout. When a datasource object is created,
     * the login timeout is initially zero. It is implementation-defined whether
     * the specified login timeout is actually used by the data source
     * implementation.
     */
    void setLoginTimeout(int seconds) throws XQException;

    /**
     * Sets the log writer for this datasource object to the given
     * java.io.PrintWriter object. The log writer is a character output stream
     * to which all logging and tracing messages for this datasource will be
     * printed. This includes messages printed by the methods of this object,
     * messages printed by methods of other objects manufactured by this object,
     * and so on. When a datasource object is created the log writer is
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
