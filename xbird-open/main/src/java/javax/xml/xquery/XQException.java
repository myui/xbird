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
 * An exception that provides information on XQJ, XQuery or other errors
 * reported by an XQJ implementation. Each XQException provides several kinds of
 * information: a string describing the error. This is used as the Java
 * Exception message, available via the method getMessage. the cause of the
 * error. This is used as the Java Exception cause, available via the method
 * getCause. the vendor code identifying the error. Available via the method
 * getVendorCode. Refer to the vendor documentation which specific codes can be
 * returned. a chain of XQException objects. If more than one error occurred the
 * exceptions are referenced via this chain. Note that XQException has a
 * subclass XQQueryException providing more detailed information about errors
 * that occurred during the processing of a query. An implementation throws a
 * base XQException when an error occurs in the XQJ implementation. Further,
 * implementations are encouraged to use the more detailed XQQueryException in
 * case of an error reported by the XQuery engine. It is possible that during
 * the processing of a query that one or more errors could occur, each with
 * their own potential causal relationship. This means that when an XQJ
 * application catches an XQException, there is a possibility that there may be
 * additional XQException objects chained to the original thrown XQException. To
 * access the additional chained XQException objects, an application would
 * recursively invoke getNextException until a null value is returned. An
 * XQException may have a causal relationship, which consists of one or more
 * Throwable instances which caused the XQException to be thrown. The
 * application may recursively call the method getCause, until a null value is
 * returned, to navigate the chain of causes.
 */
public class XQException extends Exception {
    private static final long serialVersionUID = -640407944608160489L;

    private final String vendorCode;
    private XQException nextException;

    /**
     * Constructs an XQException object with a given message. An optional chain
     * of additional XQException objects may be set subsequently using
     * setNextException.
     * 
     * @param message - the description of the error. null indicates that the
     *        message string is non existant
     */
    public XQException(String message) {
        this(message, null);
    }

    /**
     * Constructs an XQException object with a given message and vendor code. An
     * optional chain of additional XQException objects may be set subsequently
     * using setNextException.
     * 
     * @param message - the description of the error. null indicates that the
     *        message string is non existantvendorCode - a vendor-specific
     *        string identifying the error. null indicates there is no vendor
     *        code or it is unknown
     */
    public XQException(String message, String vendorCode) {
        super(message);
        this.vendorCode = vendorCode;
    }

    /**
     * Returns the next XQException in the chain or null if none.
     */
    public XQException getNextException() {
        return nextException;
    }

    /**
     * Gets the vendor code associated with this exception or null if none. A
     * vendor code is a vendor-specific string identifying the failure in a
     * computer-comparable manner. For example, "NOCONNECT" if unable to connect
     * or "DIVBYZERO" if division by zero occurred within the XQuery.
     */
    public String getVendorCode() {
        return vendorCode;
    }

    /**
     * Adds an XQException to the chain of exceptions.
     */
    public void setNextException(XQException next) {
        this.nextException = next;
    }

}
