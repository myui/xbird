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

import javax.xml.namespace.QName;

/**
 * An exception that provides information on errors occurring during the
 * evaluation of an xquery. Each XQQueryException provides several kinds of
 * optional information, in addition to the properties inherited from
 * XQException: an error code. This QName identifies the error according to the
 * standard as described in Appendix F, XQuery 1.0: An XML Query language,
 * Appendix C, XQuery 1.0 and XPath 2.0 Functions and Operators, and and its
 * associated specifications; implementation-defined errors may be raised. a
 * position. This identifies the character position of the failing expression in
 * the query text. This is a 0 based position. -1 if unknown. the line number in
 * the query string where the error occured. Line numbering starts at 1. -1 if
 * unknown the column number in the query string where the error occured. Column
 * numbering starts at 1. -1 if unknown the module uri. This identifies the
 * module in which the error occurred, null when the error is located in the
 * main module. the XQuery error object of this exception. This is the
 * $error-object argument specified through the fn:error() function. May be null
 * if not specified. the XQuery stack trace. This provides additional dynamic
 * information where the exception occurred in the XQuery expression.
 */
public class XQQueryException extends XQException {
    private static final long serialVersionUID = 4710645203004284359L;

    private final QName errorCode;
    private final int line, column, position;
    private final String moduleURI;
    private final XQSequence errorObject;
    private final XQStackTraceElement[] stackTrace;

    /**
     * Constructs an XQQueryException object with a given message.
     * 
     * @param message - the description of the error. null indicates that the
     *        message string is non existant
     */
    public XQQueryException(String message) {
        this(message, null);
    }

    /**
     * Constructs an XQQueryException object with a given message, and error
     * code.
     * 
     * @param message - the description of the error. null indicates that the
     *        message string is non existanterrorCode - QName which identifies
     *        the error according to the standard as described in Appendix F,
     *        XQuery 1.0: An XML Query language, Appendix C, XQuery 1.0 and
     *        XPath 2.0 Functions and Operators, and its associated
     *        specifications; implementation-defined errors may be raised.
     */
    public XQQueryException(String message, QName errorCode) {
        this(message, errorCode, -1, -1, -1);
    }

    /**
     * Constructs an XQQueryException object with a given message, error code,
     * line number, column number, and position.
     * 
     * @param message - the description of the error. null indicates that the
     *        message string is non existanterrorCode - QName which identifies
     *        the error according to the standard as described in Appendix F,
     *        XQuery 1.0: An XML Query language, Appendix C, XQuery 1.0 and
     *        XPath 2.0 Functions and Operators, and its associated
     *        specifications; implementation-defined errors may be raisedline -
     *        the line number in the query string where the error occured. Line
     *        numbering starts at 1. -1 if unknowncolumn - the column number in
     *        the query string where the error occured. Column numbering starts
     *        at 1. -1 if unknownposition - the position in the query string
     *        where the error occured. This is a 0 based position. -1 if unknown
     */
    public XQQueryException(String message, QName errorCode, int line, int column, int position) {
        this(message, null, errorCode, line, column, position, null, null, null);
    }

    /**
     * Constructs an XQQueryException object with a given message, vendor code,
     * error code, line number, column number, and position.
     * 
     * @param message - the description of the error. null indicates that the
     *        message string is non existantvendorCode - a vendor-specific
     *        string identifying the error. null indicates there is no vendor
     *        code or it is unknownerrorCode - QName which identifies the error
     *        according to the standard as described in Appendix F, XQuery 1.0:
     *        An XML Query language, Appendix C, XQuery 1.0 and XPath 2.0
     *        Functions and Operators, and its associated specifications;
     *        implementation-defined errors may be raisedline - the line number
     *        in the query string where the error occured. Line numbering starts
     *        at 1. -1 if unknowncolumn - the column number in the query string
     *        where the error occured. Column numbering starts at 1. -1 if
     *        unknownposition - the position in the query string where the error
     *        occured. This is a 0 based position. -1 if unknown
     */
    public XQQueryException(String message, String vendorCode, QName errorCode, int line, int column, int position) {
        this(message, vendorCode, errorCode, line, column, position, null, null, null);
    }

    /**
     * Constructs an XQQueryException object with a given message, vendor code,
     * error code, line number, column number, position, module URI, error
     * object, and stack trace.
     * 
     * @param message - the description of the error. null indicates that the
     *        message string is non existantvendorCode - a vendor-specific
     *        string identifying the error. null indicates there is no vendor
     *        code or it is unknownerrorCode - QName which identifies the error
     *        according to the standard as described in Appendix F, XQuery 1.0:
     *        An XML Query language, Appendix C, XQuery 1.0 and XPath 2.0
     *        Functions and Operators, and its associated specifications;
     *        implementation-defined errors may be raisedline - the line number
     *        in the query string where the error occured. Line numbering starts
     *        at 1. -1 if unknowncolumn - the column number in the query string
     *        where the error occured. Column numbering starts at 1. -1 if
     *        unknownposition - the position in the query string where the error
     *        occured. This is a 0 based position. -1 if unknownmoduleURI - the
     *        module URI of the module in which the error occurred. null when it
     *        is the main module or when the module is unknownerrorObject - an
     *        XQSequence representing the error object passed to fn:error().
     *        null if this error was not triggered by fn:error() or when the
     *        error object is not available.stackTrace - the XQuery stack trace
     *        where the error occurred. null if not available
     */
    public XQQueryException(String message, String vendorCode, QName errorCode, int line, int column, int position, String moduleURI, XQSequence errorObject, XQStackTraceElement[] stackTrace) {
        super(message, vendorCode);
        this.errorCode = errorCode;
        this.line = line;
        this.column = column;
        this.position = position;
        this.moduleURI = moduleURI;
        this.errorObject = errorObject;
        this.stackTrace = stackTrace;

    }

    /**
     * Gets the column number in the query string where the error occurred.
     * Column numbering starts at 1. -1 is returned if the column number is
     * unknown. If the implementation does not support this method, it must
     * return -1
     */
    public int getColumnNumber() {
        return column;
    }

    /**
     * Gets the code identifying the error according to the standard as
     * described in , , and its associated specifications; imlementation-defined
     * errors may also be raised; finally the error code may also be specified
     * in the query using fn:error().
     */
    public QName getErrorCode() {
        return errorCode;
    }

    /**
     * Gets an XQSequence representing the error object passed to fn:error().
     * Returns null if this error was not triggered by fn:error() or when the
     * error object is not available.
     */
    public XQSequence getErrorObject() {
        return errorObject;
    }

    /**
     * Gets the line number in the query string where the error occurred. Line
     * numbering starts at 1. -1 is returned if the line number is unknown. If
     * the implementation does not support this method, it must return -1
     */
    public int getLineNumber() {
        return line;
    }

    /**
     * Gets the module URI of the module in which the error occurred. null when
     * it is the main module or when the module is unknown.
     */
    public String getModuleURI() {
        return moduleURI;
    }

    /**
     * Gets the character position in the query string where this exception
     * occurred. This is a 0 based position. -1 if unknown.
     */
    public int getPosition() {
        return position;
    }

    /**
     * Returns the query stack stackTrace when the exception occurred, or null
     * if none. On some implementations only the top frame may be visible.
     */
    public XQStackTraceElement[] getQueryStackTrace() {
        return stackTrace;
    }

}
