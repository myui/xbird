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
 * An object that provides hooks for connection pool management. A
 * PooledXQConnection object represents a physical connection to a data source.
 * The connection can be recycled rather than being closed when an application
 * is finished with it, thus reducing the number of connections that need to be
 * made. An application programmer does not use the PooledXQConnection interface
 * directly; rather, it is used by a middle tier infrastructure that manages the
 * pooling of connections. When an application calls the method
 * XQDataSource.getConnection, it gets back an XQConnection object. If
 * connection pooling is being done, that XQConnection object is actually a
 * handle to a PooledXQConnection object, which is a physical connection. The
 * connection pool manager, typically the application server, maintains a pool
 * of PooledXQConnection objects. If there is a PooledXQConnection object
 * available in the pool, the connection pool manager returns an XQConnection
 * object that is a handle to that physical connection. If no PooledXQConnection
 * object is available, the connection pool manager calls the
 * ConnectionPoolXQDataSource method getPooledConnection to create a new
 * physical connection and returns a handle to it. When an application closes a
 * connection, it calls the XQConnection method close. When connection pooling
 * is being done, the connection pool manager is notified because it has
 * registered itself as an XQConnectionEventListener object using the
 * PooledXQConnection method addConnectionEventListener. The connection pool
 * manager deactivates the handle to the PooledXQConnection object and returns
 * the PooledXQConnection object to the pool of connections so that it can be
 * used again. Thus, when an application closes its connection, the underlying
 * physical connection is recycled rather than being closed. The physical
 * connection is not closed until the connection pool manager calls the
 * PooledXQConnection method close. This method is generally called to have an
 * orderly shutdown of the server or if a fatal error has made the physical
 * connection unusable.
 */
public interface PooledXQConnection {
    /**
     * Registers the given event listener so that it will be notified when an
     * event occurs on this PooledXQConnection object.
     */
    void addConnectionEventListener(XQConnectionEventListener listener);

    /**
     * Closes the physical connection that this PooledXQConnection object
     * represents. An application never calls this method directly; it is called
     * by the connection pool manager.
     */
    void close() throws XQException;

    /**
     * Creates and returns an XQConnection object that is a handle for the
     * physical connection that this PooledXQConnection object represents. The
     * connection pool manager calls this method when an application has called
     * the XQDataSource method getConnection and there are no PooledXQConnection
     * objects available.
     */
    XQConnection getConnection() throws XQException;

    /**
     * Removes the given event listener from the list of components that will be
     * notified when an event occurs on this PooledXQConnection object.
     */
    void removeConnectionEventListener(XQConnectionEventListener listener);

}
