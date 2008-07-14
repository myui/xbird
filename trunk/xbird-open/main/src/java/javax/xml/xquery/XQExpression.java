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
 * This interface describes the execute immediate functionality for expressions.
 * This object can be created from the XQConnection and the execution can be
 * done using the executeQuery() or executeCommand() method, passing in the
 * XQuery expression. All external variables defined in the prolog of the
 * expression to be executed must be set in the dynamic context of this
 * expression using the bind methods. Also, variables bound in this expression
 * but not defined as external in the prolog of the expression to be executed,
 * are simply ignored. For example, if variables $var1 and $var2 are bound, but
 * the query only defines $var1 as external, no error will be reported for the
 * binding of $var2. It will simply be ignored. When the expression is executed
 * using the executeQuery method, if the execution is successful, then an
 * XQResultSequence object is returned. The XQResultSequence object is tied to
 * the XQExpression from which it was prepared and is closed implicitly if that
 * XQExpression is either closed or re-executed. The XQExpression object is
 * dependent on the XQConnection object from which it was created and is only
 * valid for the duration of that object. Thus, if the XQConnection object is
 * closed then this XQExpression object will be implicitly closed and it can no
 * longer be used. An XQJ driver is not required to provide finalizer methods
 * for the connection and other objects. Hence it is strongly recommended that
 * users call close method explicitly to free any resources. It is also
 * recommended that they do so under a final block to ensure that the object is
 * closed even when there are exceptions. Not closing this object implicitly or
 * explicitly might result in serious memory leaks. When the XQExpression is
 * closed any XQResultSequence object obtained from it is also implicitly
 * closed. Example - XQConnection conn = XQDatasource.getConnection();
 * XQExpression expr = conn.createExpression(); expr.bindInt(new QName("x"), 21,
 * null); XQSequence result = expr.executeQuery( "declare variable $x as
 * xs:integer external; for $i in $x return $i"); while (result.next()) { //
 * process results ... } // Execute some other expression on the same object
 * XQSequence result = expr.executeQuery("for $i in doc('foo.xml') return $i");
 * ... result.close(); // close the result expr.close(); conn.close();
 */
public interface XQExpression extends XQDynamicContext {
    /**
     * Attempts to cancel the execution if both the XQuery engine and XQJ driver
     * support aborting the execution of an XQExpression. This method can be
     * used by one thread to cancel an XQExpression, that is being executed in
     * another thread. If cancellation is not supported or the attempt to cancel
     * the execution was not successful, the method returns without any error.
     * If the cancellation is successful, an XQException is thrown, to indicate
     * that it has been aborted, by executeQuery, executeCommand or any method
     * accessing the XQResultSequence returned by executeQuery. If applicable,
     * any open XQResultSequence and XQResultItem objects will also be
     * implicitly closed in this case.
     */
    void cancel() throws XQException;

    /**
     * Closes the expression object and release associated resources. Once the
     * expression is closed, all methods on this object other than the close or
     * isClosed will raise exceptions. This also closes any result sequences
     * obtained from this expression. Calling close on an XQExpression object
     * that is already closed has no effect.
     */
    void close() throws XQException;

    /**
     * Executes an implementation-defined command. Calling this method
     * implicitly closes any previous result sequence obtained from this
     * expression.
     */
    void executeCommand(java.io.Reader cmd) throws XQException;

    /**
     * Executes an implementation-defined command. Calling this method
     * implicitly closes any previous result sequence obtained from this
     * expression.
     */
    void executeCommand(String cmd) throws XQException;

    /**
     * Executes a query expression. This implicitly closes any previous result
     * sequences obtained from this expression. If the query specifies a version
     * declaration including an encoding, the XQJ implementation may try use
     * this information to parse the query. In absence of the version
     * declaration, the assumed encoding is implementation dependent.
     */
    XQResultSequence executeQuery(java.io.InputStream query) throws XQException;

    /**
     * Executes a query expression. This implicitly closes any previous result
     * sequences obtained from this expression.
     */
    XQResultSequence executeQuery(java.io.Reader query) throws XQException;

    /**
     * Executes a query expression. This implicitly closes any previous result
     * sequences obtained from this expression.
     */
    XQResultSequence executeQuery(String query) throws XQException;

    /**
     * Gets an XQStaticContext representing the values for all expression
     * properties. Note that these properties cannot be changed; in order to
     * change, a new XQExpression needs to be created.
     */
    XQStaticContext getStaticContext() throws XQException;

    /**
     * Checks if the expression is in a closed state.
     */
    boolean isClosed();

}
