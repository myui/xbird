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
 * XQCancelledException is an exception to indicate that the current XQuery
 * processing is cancelled by the application through a cancel() request. This
 * exception allows an application to easily differentiate between a user's
 * cancellation of the query from a general execution failure.
 */
public class XQCancelledException extends XQQueryException {
    private static final long serialVersionUID = 5116493965104786724L;

    /**
     * Constructs an XQCancelledException object with a given message, vendor
     * code, error code, line number, column number, position, module URI, error
     * object, and stack trace.
     * 
     * @param message the description of the error. null indicates that the
     *        message string is non existant
     * @param vendorCode a vendor-specific string identifying the error. null
     *        indicates there is no vendor code or it is unknown
     * @param errorCode QName which identifies the error according to the
     *        standard as described in Appendix F, XQuery 1.0: An XML Query
     *        language, Appendix C, XQuery 1.0 and XPath 2.0 Functions and
     *        Operators, and its associated specifications;
     *        implementation-defined errors may be raised
     * @param line the line number in the query string where the error occured.
     *        Line numbering starts at 1. -1 if unknown
     * @param column the column number in the query string where the error
     *        occured. Column numbering starts at 1. -1 if unknown
     * @param position the position in the query string where the error occured.
     *        This is a 0 based position. -1 if unknown
     * @param moduleURI the module URI of the module in which the error
     *        occurred. null when it is the main module or when the module is
     *        unknown
     * @param errorObject an XQSequence representing the error object passed to
     *        fn:error(). null if this error was not triggered by fn:error() or
     *        when the error object is not available
     * @param stackTrace the XQuery stack trace where the error occurred. null
     *        if not available
     */
    public XQCancelledException(String message, String vendorCode, javax.xml.namespace.QName errorCode, int line, int column, int position, String moduleURI, XQSequence errorObject, XQStackTraceElement[] stackTrace) {
        super(message, vendorCode, errorCode, line, column, position, moduleURI, errorObject, stackTrace);
    }

}
