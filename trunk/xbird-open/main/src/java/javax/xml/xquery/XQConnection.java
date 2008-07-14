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
 * A connection (session) with a specific XQuery engine. Connections are
 * obtained through an XQDataSource object. XQuery expressions are executed and
 * results are returned within the context of a connection. They are either
 * executed through XQExpression or XQPreparedExpression objects. XQDataSource
 * ds;// obtain the XQuery datasource ... XQConnection conn =
 * ds.getConnection(); XQPreparedExpression expr = conn.prepareExpression("for
 * $i in ..."); XQResultSequence result = expr.executeQuery(); // - or -
 * XQExpression expr = conn.createExpression(); XQSequence result =
 * expr.executeQuery("for $i in.."); // The sequence can now be iterated while
 * (result.next()) { String str = result.getItemAsString(); System.out.println("
 * output "+ str); } result.close(); expr.close(); conn.close(); // close the
 * connection and free all resources.. A connection holds also default values
 * for XQExpression and XQPreparedExpression properties. An application can
 * override these defaults by passing an XQStaticContext object to the
 * setStaticContext() method. By default a connection operates in auto-commit
 * mode, which means that each xquery is executed and committed in an individual
 * transaction. If auto-commit mode is disabled, a transaction must be ended
 * explicitly by the application calling commit() or rollback(). An XQConnection
 * object can be created on top of an existing JDBC connection. If an
 * XQConnection is created on top of the JDBC connection, it inherits the
 * transaction context from the JDBC connection. Also, in this case, if the
 * auto-commit mode is changed, or a transaction is ended using commit or
 * rollback, it also changes the underlying JDBC connection. An XQJ driver is
 * not required to provide finalizer methods for the connection and other
 * objects. Hence it is strongly recommended that users call close method
 * explicitly to free any resources. It is also recommended that they do so
 * under a final block to ensure that the object is closed even when there are
 * exceptions. Not closing this object explicitly might result in serious memory
 * leaks. When the XQConnection is closed any XQExpression and
 * XQPreparedExpression objects obtained from it are also implicitly closed.
 */
public interface XQConnection extends XQDataFactory {
    /**
     * Closes the connection. This also closes any XQExpression and
     * XQPreparedExpression obtained from this connection. Once the connection
     * is closed, no method other than close or the isClosed method may be
     * called on the connection object. Calling close on an XQConnection object
     * that is already closed has no effect. Note that an XQJ driver is not
     * required to provide finalizer methods for the connection and other
     * objects. Hence it is strongly recommended that users call this method
     * explicitly to free any resources. It is also recommended that they do so
     * under a final block to ensure that the object is closed even when there
     * are exceptions.
     */
    abstract void close() throws XQException;

    /**
     * Makes all changes made in the current transaction permanent and releases
     * any locks held by the datasource. This method should be used only when
     * auto-commit mode is disabled. Any XQResultSequence, or XQResultItem may
     * be implicitly closed upon commit, if the holdability property of the
     * sequence is set to XQConstants.HOLDTYPE_CLOSE_CURSORS_AT_COMMIT.
     */
    void commit() throws XQException;

    /**
     * Creates a new XQExpression object that can be used to perform execute
     * immediate operations with XQuery expressions. The properties of the
     * connection's default XQStaticContext are copied to the returned
     * XQExpression.
     */
    XQExpression createExpression() throws XQException;

    /**
     * Creates a new XQExpression object that can be used to perform execute
     * immediate operations with XQuery expressions. The properties of the
     * specified XQStaticContext values are copied to the returned XQExpression.
     */
    XQExpression createExpression(XQStaticContext properties) throws XQException;

    /**
     * Gets the auto-commit attribute of this connection
     */
    boolean getAutoCommit() throws XQException;

    /**
     * Gets the metadata for this connection.
     */
    XQMetaData getMetaData() throws XQException;

    /**
     * Gets an XQStaticContext representing the default values for all
     * expression properties. In order to modify the defaults, it is not
     * sufficient to modify the values in the returned XQStaticContext object;
     * in addition setStaticContext should be called to make those new values
     * effective.
     */
    XQStaticContext getStaticContext() throws XQException;

    /**
     * Checks if the connection is closed.
     */
    boolean isClosed();

    /**
     * Prepares an expression for execution. The properties of the connection's
     * default XQStaticContext are copied to the returned XQPreparedExpression.
     */
    XQPreparedExpression prepareExpression(java.io.InputStream xquery) throws XQException;

    /**
     * Prepares an expression for execution. The properties of the specified
     * XQStaticContext values are copied to the returned XQPreparedExpression.
     */
    XQPreparedExpression prepareExpression(java.io.InputStream xquery, XQStaticContext properties)
            throws XQException;

    /**
     * Prepares an expression for execution. The properties of the connection's
     * default XQStaticContext are copied to the returned XQPreparedExpression.
     */
    XQPreparedExpression prepareExpression(java.io.Reader xquery) throws XQException;

    /**
     * Prepares an expression for execution. The properties of the specified
     * XQStaticContext values are copied to the returned XQPreparedExpression.
     */
    XQPreparedExpression prepareExpression(java.io.Reader xquery, XQStaticContext properties)
            throws XQException;

    /**
     * Prepares an expression for execution. The properties of the connection's
     * default XQStaticContext are copied to the returned XQPreparedExpression.
     */
    XQPreparedExpression prepareExpression(String xquery) throws XQException;

    /**
     * Prepares an expression for execution. The properties of the specified
     * XQStaticContext values are copied to the returned XQPreparedExpression.
     */
    XQPreparedExpression prepareExpression(String xquery, XQStaticContext properties)
            throws XQException;

    /**
     * Undoes all changes made in the current transaction and releases any locks
     * held by the datasource. This method should be used only when auto-commit
     * mode is disabled.
     */
    void rollback() throws XQException;

    /**
     * Sets the auto-commit attribute to the given state. If a connection is in
     * auto-commit mode, each xquery is executed and committed in an individual
     * transaction. When auto-commit mode is disabled, xqueries are grouped in a
     * transaction that must be ended explicitly by the application calling
     * commit() or rollback(). By default, new connections are in auto-commit
     * mode. If the value of auto-commit is changed in the middle of a
     * transaction, the transaction is committed. If setAutoCommit is called and
     * the auto-commit attribute is not changed from its current value, the
     * request is treated as a no-op.
     */
    void setAutoCommit(boolean autoCommit) throws XQException;

    /**
     * Sets the default values for all expression properties. The implementation
     * will read out all expression properties from the specified
     * XQStaticContext and update its private copy.
     */
    void setStaticContext(XQStaticContext properties) throws XQException;

}
