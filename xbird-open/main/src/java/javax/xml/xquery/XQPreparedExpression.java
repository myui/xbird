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
 * This interface describes an expression that can be prepared for multiple
 * subsequent executions. A prepared expression can be created from the
 * connection. The preparation of the expression does the static analysis of the
 * expression using the static context information. The dynamic context
 * information, such as values for bind variables, can then be set using the
 * setter methods. When setting values for bind variables, these variables
 * should be present as external variables in the prolog of the prepared
 * expression. The static type information of the query can also be retrieved if
 * the XQuery implementation provides it using the getStaticResultType method.
 * When the expression is executed using the executeQuery method, if the
 * execution is successful, then an XQResultSequence object is returned. The
 * XQResultSequence object is tied to the XQPreparedExpression from which it was
 * prepared and is closed implicitly if that expression is either closed or if
 * re-executed. The XQPreparedExpression object is dependent on the XQConnection
 * object from which it was created and is only valid for the duration of that
 * object. Thus, if the XQConnection object is closed then this
 * XQPreparedExpression object will be implicitly closed and it can no longer be
 * used. An XQJ driver is not required to provide finalizer methods for the
 * connection and other objects. Hence it is strongly recommended that users
 * call close method explicitly to free any resources. It is also recommended
 * that they do so under a final block to ensure that the object is closed even
 * when there are exceptions. Not closing this object implicitly or explicitly
 * might result in serious memory leaks. When the XQPreparedExpression is closed
 * any XQResultSequence object obtained from it is also implicitly closed.
 * Example - XQConnection conn = XQDataSource.getconnection();
 * XQPreparedExpression expr = conn.prepareExpression ("for $i in (1) return
 * 'abc' "); // get the sequence type out.. This would be something like
 * xs:string * XQSequenceType type = expr.getStaticResultType(); XQSequence
 * result1 = expr.executeQuery(); // process the result.. result1.next();
 * System.out.println(" First result1 "+ result1.getAtomicValue());
 * XQResultSequence result2 = expr.executeQuery(); // result1 is implicitly
 * closed // recommended to close the result sequences explicitly. // process
 * the result.. while (result2.next()) System.out.println(" result is "+
 * result2.getAtomicValue()); result2.close(); expr.close(); // closing
 * expression implicitly closes all result sequence or // items obtained from
 * this expression. conn.close(); // closing connections will close expressions
 * and results.
 */
public interface XQPreparedExpression extends XQDynamicContext {
    /**
     * Attempts to cancel the execution if both the XQuery engine and XQJ driver
     * support aborting the execution of an XQPreparedExpression. This method
     * can be used by one thread to cancel an XQPreparedExpression, that is
     * being executed in another thread. If cancellation is not supported or the
     * attempt to cancel the execution was not successful, the method returns
     * without any error. If the cancellation is successful, an XQException is
     * thrown, to indicate that it has been aborted, by executeQuery,
     * executeCommand or any method accessing the XQResultSequence returned by
     * executeQuery. If applicable, any open XQResultSequence and XQResultItem
     * objects will also be implicitly closed in this case.
     */
    abstract void cancel() throws XQException;

    /**
     * Closes the expression object and release all resources associated with
     * this prepared expression. This also closes any result sequences obtained
     * from this expression. Once the expression is closed, all methods on this
     * object other than the close or isClosed will raise exceptions. Calling
     * close on an XQExpression object that is already closed has no effect.
     */
    void close() throws XQException;

    /**
     * Executes the prepared query expression. Calling this method implicitly
     * closes any previous result sequence obtained from this expression.
     */
    XQResultSequence executeQuery() throws XQException;

    /**
     * Retrieves all the external variables defined in the prolog of the
     * prepared expression.
     */
    javax.xml.namespace.QName[] getAllExternalVariables() throws XQException;

    /**
     * Retrieves the names of all unbound external variables.
     */
    javax.xml.namespace.QName[] getAllUnboundExternalVariables() throws XQException;

    /**
     * Gets an XQStaticContext representing the values for all expression
     * properties. Note that these properties cannot be changed; in order to
     * change, a new XQPreparedExpression needs to be created.
     */
    XQStaticContext getStaticContext() throws XQException;

    /**
     * Gets the static type information of the result sequence. If an
     * implementation does not do static typing of the query, then this method
     * must return an XQSequenceType object corresponding to the XQuery sequence
     * type item()*.
     */
    XQSequenceType getStaticResultType() throws XQException;

    /**
     * Retrieves the static type of a given external variable.
     */
    XQSequenceType getStaticVariableType(javax.xml.namespace.QName name) throws XQException;

    /**
     * Checks if the prepared expression in a closed state.
     */
    boolean isClosed();

}
