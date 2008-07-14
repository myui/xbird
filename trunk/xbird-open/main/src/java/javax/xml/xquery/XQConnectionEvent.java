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
 * An event object that provides information about the source of a
 * connection-related event. XQConnectionEvent objects are generated when an
 * application closes a pooled connection and when an error occurs. The
 * XQConnectionEvent object contains the folowing information: The pooled
 * connection closed by the application In the case of an error, the XQException
 * to be thrown to the application
 */
public class XQConnectionEvent extends java.util.EventObject {
    private static final long serialVersionUID = -5435981994299714124L;

    private final XQException ex;

    /**
     * Constructs an XQConnectionEvent object initialized with the given
     * PooledXQConnection object. XQException defaults to null.
     * 
     * @param con the pooled connection that is the source of the event
     */
    public XQConnectionEvent(PooledXQConnection con) {
        this(con, null);
    }

    /**
     * Constructs an XQConnectionEvent object initialized with the given
     * PooledXQConnection object and XQException object.
     * 
     * @param con the pooled connection that is the source of the eventex - the
     *        XQException to be thrown to the application
     */
    public XQConnectionEvent(PooledXQConnection con, XQException ex) {
        super(con);
        this.ex = ex;
    }

    /**
     * Retrieves the XQException for this XQConnectionEvent object.
     */
    public XQException getXQException() {
        return ex;
    }

}
