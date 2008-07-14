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
 * This class represents a frame in a stack trace, akin to the StackTraceElement
 * but for XQuery callstacks instead of Java.
 * 
 * @see XQQueryException.getQueryStackTrace
 */
public class XQStackTraceElement implements java.io.Serializable {
    private static final long serialVersionUID = -6841787324555686406L;

    private final String moduleURI;
    private final int line, column, position;
    private final QName function;
    private final XQStackTraceVariable[] variables;

    /**
     * Construct an XQStackTraceElement object representing a frame in a stack
     * trace.
     * 
     * @param moduleURI the module URI containing the execution point
     *        representing the stack trace element. null when it is the main
     *        module or when the module is unknownline - the line number in the
     *        query string where the error occured. Line numbering starts at 1.
     *        -1 if unknowncolumn - the column number in the query string where
     *        the error occured. Column numbering starts at 1. -1 if
     *        unknownposition - the position in the query string where the error
     *        occured. This is a 0 based position. -1 if unknownfunction - the
     *        QName of the function in which the exception occurred, or null if
     *        it occurred outside an enclosing functionvariables - the variables
     *        in scope at this execution point, or null if no variable value
     *        retrieval is possible
     */
    public XQStackTraceElement(String moduleURI, int line, int column, int position, QName function, XQStackTraceVariable[] variables) {
        this.moduleURI = moduleURI;
        this.line = line;
        this.column = column;
        this.position = position;
        this.function = function;
        this.variables = variables;
    }

    /**
     * Gets the column number in the query string containing the execution point
     * represented by this stack trace element. Column numbering starts at 1. -1
     * is returned if the column number is unknown. If the implementation does
     * not support this method, it must return -1
     */
    public int getColumnNumber() {
        return column;
    }

    /**
     * Gets the QName of the function in which the error occurred, or null if it
     * occurred outside an enclosing function (in a main module).
     */
    public QName getFunctionQName() {
        return function;
    }

    /**
     * Gets the line number in the query string containing the execution point
     * represented by this stack trace element. Line numbering starts at 1. -1
     * is returned if the line number is unknown. If the implementation does not
     * support this method, it must return -1
     */
    public int getLineNumber() {
        return line;
    }

    /**
     * Gets the module URI containing the execution point represented by this
     * stack trace element. null when it is the main module or when the module
     * is unknown.
     */
    public String getModuleURI() {
        return moduleURI;
    }

    /**
     * Gets the character position in the query string containing the execution
     * point represented by this stack trace element. This is a 0 based
     * position. -1 if unknown.
     */
    public int getPosition() {
        return position;
    }

    /**
     * Gets the variables in scope at this execution point, or null if no
     * variable value retrieval is possible.
     */
    public XQStackTraceVariable[] getVariables() {
        return variables;
    }

}
