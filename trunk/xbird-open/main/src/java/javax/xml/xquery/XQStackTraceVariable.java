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
 * This class represents the list of variables and their values in an error
 * stack.
 * 
 * @see XQStackTraceElement, XQQueryException
 */
public class XQStackTraceVariable implements java.io.Serializable {
    private static final long serialVersionUID = 4486677104694680197L;

    private final QName qname;
    private final String value;

    /**
     * Construct a stack trace variable object.
     * 
     * @param qname - the QName of the variable in the error stackvalue - a
     *        vendor specific short string representation of the value of the
     *        variable in the error stack
     */
    public XQStackTraceVariable(QName qname, String value) {
        super();
        this.qname = qname;
        this.value = value;
    }

    /**
     * Gets the QName of the variable.
     */
    public QName getQName() {
        return qname;
    }

    /**
     * Gets a short string representation of the value of the stack variable.
     * Representations of values are vendor specific and for XML node types may
     * be XPath descriptions such as
     * "doc("0596003870/book1.xml")/book/chapter[5]". Sequences may print just
     * some set of values from the sequence such as '("5", "6", "7", ...)'.
     */
    public String getValue() {
        return value;
    }

}
