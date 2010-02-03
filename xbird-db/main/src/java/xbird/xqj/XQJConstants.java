/*
 * @(#)$Id$
 *
 * Copyright 2006-2008 Makoto YUI
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
 *     Makoto YUI - initial implementation
 */
package xbird.xqj;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class XQJConstants {

    private XQJConstants() {}

    /** Some atomic type */
    public static final int XQITEMKIND_ATOMIC = 1;

    /** Attribute node */
    public static final int XQITEMKIND_ATTRIBUTE = 2;

    /** Comment node */
    public static final int XQITEMKIND_COMMENT = 3;

    /** Document type */
    public static final int XQITEMKIND_DOCUMENT = 4;

    /** Document node containing a single element node as its child */
    public static final int XQITEMKIND_DOCUMENT_ELEMENT = 5;

    /** Document node containing a single schema element node as its child */
    public static final int XQITEMKIND_DOCUMENT_SCHEMA_ELEMENT = 6;

    /** Element node */
    public static final int XQITEMKIND_ELEMENT = 7;

    /** Any kind of item */
    public static final int XQITEMKIND_ITEM = 8;

    /** Some node type */
    public static final int XQITEMKIND_NODE = 9;

    /** Processing instruction node */
    public static final int XQITEMKIND_PI = 10;

    /** Text node */
    public static final int XQITEMKIND_TEXT = 11;

    /** Schema element node */
    public static final int XQITEMKIND_SCHEMA_ELEMENT = 12;

    /** Schema attribute node */
    public static final int XQITEMKIND_SCHEMA_ATTRIBUTE = 13;

    /** Represents the schema type <code>xs:untyped</code> */
    public static final int XQBASETYPE_UNTYPED = 1;

    /** Represents the schema type <code>xs:anyType</code> */
    public static final int XQBASETYPE_ANYTYPE = 2;

    /** Represents the schema type <code>xs:anySimpleType</code> */
    public static final int XQBASETYPE_ANYSIMPLETYPE = 3;

    /** Represents the schema type <code>xs:anyAtomicType</code> */
    public static final int XQBASETYPE_ANYATOMICTYPE = 4;

    /** Represents the schema type <code>xs:untypedAtomic</code> */
    public static final int XQBASETYPE_UNTYPEDATOMIC = 5;

    /** Represents the schema type <code>xs:dayTimeDuration</code> */
    public static final int XQBASETYPE_DAYTIMEDURATION = 6;

    /** Represents the schema type <code>xs:yearMonthDuration</code> */
    public static final int XQBASETYPE_YEARMONTHDURATION = 7;

    /** Represents the schema type <code>xs:anyURI</code> */
    public static final int XQBASETYPE_ANYURI = 8;

    /** Represents the schema type <code>xs:base64Binary</code> */
    public static final int XQBASETYPE_BASE64BINARY = 9;

    /** Represents the schema type <code>xs:boolean</code> */
    public static final int XQBASETYPE_BOOLEAN = 10;

    /** Represents the schema type <code>xs:date</code> */
    public static final int XQBASETYPE_DATE = 11;

    /** Represents the schema type <code>xs:int</code>  */
    public static final int XQBASETYPE_INT = 12;

    /** Represents the schema type <code>xs:integer</code>  */
    public static final int XQBASETYPE_INTEGER = 13;

    /** Represents the schema type <code>xs:short</code> */
    public static final int XQBASETYPE_SHORT = 14;

    /** Represents the schema type <code>xs:long</code> */
    public static final int XQBASETYPE_LONG = 15;

    /** Represents the schema type <code>xs:dateTime</code> */
    public static final int XQBASETYPE_DATETIME = 16;

    /** Represents the schema type <code>xs:decimal</code> */
    public static final int XQBASETYPE_DECIMAL = 17;

    /** Represents the schema type <code>xs:double</code> */
    public static final int XQBASETYPE_DOUBLE = 18;

    /** Represents the schema type <code>xs:duration</code> */
    public static final int XQBASETYPE_DURATION = 19;

    /** Represents the schema type <code>xs:float</code> */
    public static final int XQBASETYPE_FLOAT = 20;

    /** Represents the schema type <code>xs:gDay</code> */
    public static final int XQBASETYPE_GDAY = 21;

    /** Represents the schema type <code>xs:gMonth</code> */
    public static final int XQBASETYPE_GMONTH = 22;

    /** Represents the schema type <code>xs:gMonthDay</code> */
    public static final int XQBASETYPE_GMONTHDAY = 23;

    /** Represents the schema type <code>xs:gYear</code> */
    public static final int XQBASETYPE_GYEAR = 24;

    /** Represents the schema type <code>xs:gYearMonth</code> */
    public static final int XQBASETYPE_GYEARMONTH = 25;

    /** Represents the schema type <code>xs:hexBinary</code> */
    public static final int XQBASETYPE_HEXBINARY = 26;

    /** Represents the schema type <code>xs:NOTATION</code> */
    public static final int XQBASETYPE_NOTATION = 27;

    /** Represents the schema type <code>xs:QName</code> */
    public static final int XQBASETYPE_QNAME = 28;

    /** Represents the schema type <code>xs:string</code> */
    public static final int XQBASETYPE_STRING = 29;

    /** Represents the schema type <code>xs:time</code> */
    public static final int XQBASETYPE_TIME = 30;

    /** Represents the schema type <code>xs:byte</code> */
    public static final int XQBASETYPE_BYTE = 31;

    /** Represents the schema type <code>xs:nonPositiveInteger</code> */
    public static final int XQBASETYPE_NONPOSITIVE_INTEGER = 32;

    /** Represents the schema type <code>xs:nonNegativeInteger</code> */
    public static final int XQBASETYPE_NONNEGATIVE_INTEGER = 33;

    /** Represents the schema type <code>xs:negativeInteger</code> */
    public static final int XQBASETYPE_NEGATIVE_INTEGER = 34;

    /** Represents the schema type <code>xs:positiveInteger</code> */
    public static final int XQBASETYPE_POSITIVE_INTEGER = 35;

    /** Represents the schema type <code>xs:unsignedLong</code> */
    public static final int XQBASETYPE_UNSIGNED_LONG = 36;

    /** Represents the schema type <code>xs:unsignedInt</code> */
    public static final int XQBASETYPE_UNSIGNED_INT = 37;

    /** Represents the schema type <code>xs:unsignedShort</code> */
    public static final int XQBASETYPE_UNSIGNED_SHORT = 38;

    /** Represents the schema type <code>xs:unsignedByte</code> */
    public static final int XQBASETYPE_UNSIGNED_BYTE = 39;

    /** Represents the schema type <code>xs:normalizedString</code> */
    public static final int XQBASETYPE_NORMALIZED_STRING = 40;

    /** Represents the schema type <code>xs:token</code> */
    public static final int XQBASETYPE_TOKEN = 41;

    /** Represents the schema type <code>xs:language</code> */
    public static final int XQBASETYPE_LANGUAGE = 42;

    /** Represents the schema type <code>xs:Name</code> */
    public static final int XQBASETYPE_NAME = 43;

    /** Represents the schema type <code>xs:NCName</code> */
    public static final int XQBASETYPE_NCNAME = 44;

    /** Represents the schema type <code>xs:NMToken</code> */
    public static final int XQBASETYPE_NMTOKEN = 45;

    /** Represents the schema type <code>xs:ID</code> */
    public static final int XQBASETYPE_ID = 46;

    /** Represents the schema type <code>xs:IDREF</code> */
    public static final int XQBASETYPE_IDREF = 47;

    /** Represents the schema type <code>xs:ENTITY</code> */
    public static final int XQBASETYPE_ENTITY = 48;

    /** Represents the schema type <code>xs:IDREFS</code>. */
    public static final int XQBASETYPE_IDREFS = 49;

    /** Represents the schema type <code>xs:ENTITIES</code> */
    public static final int XQBASETYPE_ENTITIES = 50;

    /** Represents the schema type <code>xs:NMTOKENS</code> */
    public static final int XQBASETYPE_NMTOKENS = 51;

}
