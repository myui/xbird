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
 * The XQSequenceType interface represents a sequence type as XQuery 1.0: An XML
 * Query language. The XQSequenceType is the base interface for the XQItemType
 * interface and contains an occurence indicator.
 */
public interface XQSequenceType {
    static final int OCC_EMPTY = 5;
    static final int OCC_EXACTLY_ONE = 2;
    static final int OCC_ONE_OR_MORE = 4;
    static final int OCC_ZERO_OR_MORE = 3;
    static final int OCC_ZERO_OR_ONE = 1;

    /**
     * Compares the specified object with this sequence type for equality. The
     * result is true only if the argument is a sequence type object which
     * represents the same XQuery sequence type. In order to comply with the
     * general contract of equals and hashCode across different implementations
     * the following algorithm must be used. Return true if and only if both
     * objects are XQsequenceType and: getItemOccurrence() is equal if not
     * OCC_EMPTY, getItemType() is equal
     */
    boolean equals(Object o);

    /**
     * Returns the occurrence indicator for the sequence type. One of:
     * Description Value Zero or one OCC_ZERO_OR_ONE Exactly one OCC_EXACTLY_ONE
     * Zero or more OCC_ZERO_OR_MORE One or more OCC_ONE_OR_MORE Empty OCC_EMPTY
     */
    int getItemOccurrence();

    /**
     * Returns the type of the item in the sequence type.
     */
    XQItemType getItemType();

    /**
     * Returns a hash code consistent with the definition of the equals method.
     * In order to comply with the general contract of equals and hashCode
     * across different implementations the following algorithm must be used:
     * int hashCode; if (getItemOccurrence() == XQSequenceType.OCC_EMPTY) {
     * hashCode = 1; } else { hashCode = getItemOccurrence()*31 +
     * getItemType().hashCode(); }
     */
    int hashCode();

    /**
     * Returns a human-readable implementation-defined string representation of
     * the sequence type.
     */
    String toString();

}
