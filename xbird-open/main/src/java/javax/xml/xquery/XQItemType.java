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
 * The XQItemType interface represents an item type as defined in XQuery 1.0: An
 * XML Query language. The XQItemType extends the XQSequenceType but restricts
 * the occurrance indicator to be exactly one. This derivation allows passing an
 * item type wherever a sequence type is expected, but not the other way. The
 * XQItemType interface contains methods to represent information about the
 * following aspects of an item type: The kind of the item - one of XQITEMKIND_*
 * constants Base type from which the item is derived - one of
 * XQBASETYPE_*constants Name of the node, if any Type name, if any. If present,
 * then also whether the typename is an anonymous type XML Schema URI associated
 * with the type, if any The nillability characteristics, if any An instance of
 * the XQItemType is a standalone object that is independant of the XQConnection
 * and any XQuery static or dynamic context.
 */
public interface XQItemType extends XQSequenceType {
    /**
     * Represents the schema type xs:anyAtomicType
     */
    static final int XQBASETYPE_ANYATOMICTYPE = 4;

    /**
     * Represents the schema type xs:anySimpleType
     */
    static final int XQBASETYPE_ANYSIMPLETYPE = 3;

    /**
     * Represents the schema type xs:anyType
     */
    static final int XQBASETYPE_ANYTYPE = 2;

    /**
     * Represents the schema type xs:anyURI
     */
    static final int XQBASETYPE_ANYURI = 8;

    /**
     * Represents the schema type xs:base64Binary
     */
    static final int XQBASETYPE_BASE64BINARY = 9;

    /**
     * Represents the schema type xs:boolean
     */
    static final int XQBASETYPE_BOOLEAN = 10;

    /**
     * Represents the schema type xs:byte
     */
    static final int XQBASETYPE_BYTE = 31;

    /**
     * Represents the schema type xs:date
     */
    static final int XQBASETYPE_DATE = 11;

    /**
     * Represents the schema type xs:dateTime
     */
    static final int XQBASETYPE_DATETIME = 16;

    /**
     * Represents the schema type xs:dayTimeDuration
     */
    static final int XQBASETYPE_DAYTIMEDURATION = 6;

    /**
     * Represents the schema type xs:decimal
     */
    static final int XQBASETYPE_DECIMAL = 17;

    /**
     * Represents the schema type xs:double
     */
    static final int XQBASETYPE_DOUBLE = 18;

    /**
     * Represents the schema type xs:duration
     */
    static final int XQBASETYPE_DURATION = 19;

    /**
     * Represents the schema type xs:ENTITIES
     */
    static final int XQBASETYPE_ENTITIES = 50;

    /**
     * Represents the schema type xs:ENTITY
     */
    static final int XQBASETYPE_ENTITY = 48;

    /**
     * Represents the schema type xs:float
     */
    static final int XQBASETYPE_FLOAT = 20;

    /**
     * Represents the schema type xs:gDay
     */
    static final int XQBASETYPE_GDAY = 21;

    /**
     * Represents the schema type xs:gMonth
     */
    static final int XQBASETYPE_GMONTH = 22;

    /**
     * Represents the schema type xs:gMonthDay
     */
    static final int XQBASETYPE_GMONTHDAY = 23;

    /**
     * Represents the schema type xs:gYear
     */
    static final int XQBASETYPE_GYEAR = 24;

    /**
     * Represents the schema type xs:gYearMonth
     */
    static final int XQBASETYPE_GYEARMONTH = 25;

    /**
     * Represents the schema type xs:hexBinary
     */
    static final int XQBASETYPE_HEXBINARY = 26;

    /**
     * Represents the schema type xs:ID
     */
    static final int XQBASETYPE_ID = 46;

    /**
     * Represents the schema type xs:IDREF
     */
    static final int XQBASETYPE_IDREF = 47;

    /**
     * Represents the schema type xs:IDREFS. Valid only if the item kind is
     * XQITEMKIND_ELEMENT, XQITEMKIND_DOCUMENT_ELEMENT, or XQITEMKIND_ATTRIBUTE
     */
    static final int XQBASETYPE_IDREFS = 49;

    /**
     * Represents the schema type xs:int
     */
    static final int XQBASETYPE_INT = 12;

    /**
     * Represents the schema type xs:integer
     */
    static final int XQBASETYPE_INTEGER = 13;

    /**
     * Represents the schema type xs:language
     */
    static final int XQBASETYPE_LANGUAGE = 42;

    /**
     * Represents the schema type xs:long
     */
    static final int XQBASETYPE_LONG = 15;

    /**
     * Represents the schema type xs:Name
     */
    static final int XQBASETYPE_NAME = 43;

    /**
     * Represents the schema type xs:NCName
     */
    static final int XQBASETYPE_NCNAME = 44;

    /**
     * Represents the schema type xs:negativeInteger
     */
    static final int XQBASETYPE_NEGATIVE_INTEGER = 34;

    /**
     * Represents the schema type xs:NMToken
     */
    static final int XQBASETYPE_NMTOKEN = 45;

    /**
     * Represents the schema type xs:NMTOKENS
     */
    static final int XQBASETYPE_NMTOKENS = 51;

    /**
     * Represents the schema type xs:nonNegativeInteger
     */
    static final int XQBASETYPE_NONNEGATIVE_INTEGER = 33;

    /**
     * Represents the schema type xs:nonPositiveInteger
     */
    static final int XQBASETYPE_NONPOSITIVE_INTEGER = 32;

    /**
     * Represents the schema type xs:normalizedString
     */
    static final int XQBASETYPE_NORMALIZED_STRING = 40;

    /**
     * Represents the schema type xs:NOTATION
     */
    static final int XQBASETYPE_NOTATION = 27;

    /**
     * Represents the schema type xs:positiveInteger
     */
    static final int XQBASETYPE_POSITIVE_INTEGER = 35;

    /**
     * Represents the schema type xs:QName
     */
    static final int XQBASETYPE_QNAME = 28;

    /**
     * Represents the schema type xs:short
     */
    static final int XQBASETYPE_SHORT = 14;

    /**
     * Represents the schema type xs:string
     */
    static final int XQBASETYPE_STRING = 29;

    /**
     * Represents the schema type xs:time
     */
    static final int XQBASETYPE_TIME = 30;

    /**
     * Represents the schema type xs:token
     */
    static final int XQBASETYPE_TOKEN = 41;

    /**
     * Represents the schema type xs:unsignedByte
     */
    static final int XQBASETYPE_UNSIGNED_BYTE = 39;

    /**
     * Represents the schema type xs:unsignedInt
     */
    static final int XQBASETYPE_UNSIGNED_INT = 37;

    /**
     * Represents the schema type xs:unsignedLong
     */
    static final int XQBASETYPE_UNSIGNED_LONG = 36;

    /**
     * Represents the schema type xs:unsignedShort
     */
    static final int XQBASETYPE_UNSIGNED_SHORT = 38;

    /**
     * Represents the schema type xs:untyped
     */
    static final int XQBASETYPE_UNTYPED = 1;

    /**
     * Represents the schema type xs:untypedAtomic
     */
    static final int XQBASETYPE_UNTYPEDATOMIC = 5;

    /**
     * Represents the schema type xs:yearMonthDuration
     */
    static final int XQBASETYPE_YEARMONTHDURATION = 7;

    /**
     * Some atomic type.
     */
    static final int XQITEMKIND_ATOMIC = 1;

    /**
     * Attribute node
     */
    static final int XQITEMKIND_ATTRIBUTE = 2;

    /**
     * Comment node
     */
    static final int XQITEMKIND_COMMENT = 3;

    /**
     * Document type (the type information represents the type of the document
     * element)
     */
    static final int XQITEMKIND_DOCUMENT = 4;

    /**
     * Document node containing a single element node as its child (type
     * information represents type of the element child)
     */
    static final int XQITEMKIND_DOCUMENT_ELEMENT = 5;

    /**
     * Document node containing a single schema element node as its child (type
     * information represents type of the schema element child)
     */
    static final int XQITEMKIND_DOCUMENT_SCHEMA_ELEMENT = 6;

    /**
     * Element node
     */
    static final int XQITEMKIND_ELEMENT = 7;

    /**
     * Any kind of item
     */
    static final int XQITEMKIND_ITEM = 8;

    /**
     * Some node type
     */
    static final int XQITEMKIND_NODE = 9;

    /**
     * Processing instruction node
     */
    static final int XQITEMKIND_PI = 10;

    /**
     * Schema attribute node
     */
    static final int XQITEMKIND_SCHEMA_ATTRIBUTE = 13;

    /**
     * Schema element node
     */
    static final int XQITEMKIND_SCHEMA_ELEMENT = 12;

    /**
     * Text node
     */
    static final int XQITEMKIND_TEXT = 11;

    /**
     * Compares the specified object with this item type for equality. The
     * result is true only if the argument is an item type object which
     * represents the same XQuery item type. In order to comply with the general
     * contract of equals and hashCode across different implementations the
     * following algorithm must be used. Return true if and only if both objects
     * are XQItemType and: getItemKind() is equal if getBaseType() is supported
     * for the item kind, it must be equal if getNodeName() is supported for the
     * item kind, it must be equal getSchemaURI() is equal if getTypeName() is
     * supported for the item kind, it must be equal isAnonymousType() is equal
     * isElementNillable() is equal if getPIName() is supported for the item
     * kind, it must be equal
     */
    boolean equals(Object o);

    /**
     * Returns the basic pre-defined type of the item. One of the XQBASETYPE_*
     * constants.
     */
    int getBaseType() throws XQException;

    /**
     * Returns the kind of the item. One of the XQITEMKIND_* constants.
     */
    int getItemKind();

    /**
     * Returns the occurrence indicator for the item type. This method will
     * always return the value XQSequenceType.OCC_EXACTLY_ONE.
     */
    int getItemOccurrence();

    /**
     * Returns the name of the node in case the item kind is an
     * XQITEMKIND_DOCUMENT_ELEMENT, XQITEMKIND_DOCUMENT_SCHEMA_ELEMENT,
     * XQITEMKIND_ELEMENT, XQITEMKIND_SCHEMA_ELEMENT, XQITEMKIND_ATTRIBUTE, or
     * XQITEMKIND_SCHEMA_ATTRIBUTE. For example, in the case of a type for
     * element "foo" this will return the QName foo. For wildcard entries a null
     * value will be returned.
     */
    javax.xml.namespace.QName getNodeName() throws XQException;

    /**
     * Returns the name of the processing instruction type. As such the item
     * kind of this XQItemType must be XQITEMKIND_PI.
     */
    String getPIName() throws XQException;

    /**
     * Returns the schema location URI of the schema that contains the item's
     * element or type definition. This method is implementation-definied and an
     * implementation will return a null value if it does not support retrieving
     * the schema location URI. If the item corresponds to a validated global
     * element in a schema, the result will be the schema location URI to the
     * XMLSchema containing the element definition. Otherwise if the item is a
     * schema validated node, the result will be the schema location URI of the
     * XMLSchema containing the type definition of that node. If the item is not
     * schema validated, the result is null
     */
    java.net.URI getSchemaURI();

    /**
     * Represents a type name (global or local). This can be used to represent
     * specific type name such as, element foo of type hatsize. The schema type
     * name is represented as a single QName. If the return type is an anonymous
     * type, the actual QName value returned is implementation defined.
     */
    javax.xml.namespace.QName getTypeName() throws XQException;

    /**
     * Returns a hash code consistent with the definition of the equals method.
     * In order to comply with the general contract of equals and hashCode
     * across different implementations the following algorithm must be used:
     * hashCode = this.getItemKind(); if this.getSchemaURI != null hashCode =
     * 31*hashCode + this.getSchemaURI().hashCode(); if this.getBaseType() is
     * supported for the item kind hashCode = 31*hashCode + this.getbaseType();
     * if this.getNodeName () is supported for the item kind and
     * this.getNodeName() != null hashCode = 31*hashCode +
     * this.getNodeName().hashCode() if this.getTypeName () is supported for the
     * item kind hashCode = 31*hashCode + this.getTypeName().hashCode(); if
     * this.getPIName () is supported for the item kind and this.getPIName () !=
     * null hashCode = 31*hashCode + this.getPIName().hashCode();
     */
    int hashCode();

    /**
     * Represents whether the item type is an anonymous type in the schema.
     */
    boolean isAnonymousType();

    /**
     * Returns whether the element type is nillable or not.
     */
    boolean isElementNillable();

    /**
     * Returns a human-readable implementation-defined string representation of
     * the item type.
     */
    String toString();

}
