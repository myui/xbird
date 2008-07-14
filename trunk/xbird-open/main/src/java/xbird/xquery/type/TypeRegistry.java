/*
 * @(#)$Id: TypeRegistry.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.type;

import java.util.HashMap;
import java.util.Map;

import xbird.xquery.StaticError;
import xbird.xquery.XQueryConstants;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.type.Type.Occurrence;
import xbird.xquery.type.node.NodeType;
import xbird.xquery.type.xs.*;

public final class TypeRegistry {

    private static final Map<String, Type> types = new HashMap<String, Type>(128);

    static {
        // non atomic types
        put(Type.ANY);
        put(Type.ANY_SIMPLE);
        put(Untyped.UNTYPED);
        put(SequenceType.EMPTY);
        put(ItemType.ANY_ITEM);
        put(ENTITIESType.ENTITIES);
        put(IDREFSType.IDREFS);
        put(NMTOKENSType.NMTOKENS);
        // atomic types
        put(AtomicType.ANY_ATOMIC_TYPE);
        put(AnyURIType.ANYURI);
        put(Base64BinaryType.BASE64BINARY);
        put(HexBinaryType.HEXBINARY);
        put(BooleanType.BOOLEAN);
        put(DateTimeType.DATETIME);
        put(DateType.DATE);
        put(GDayType.GDAY);
        put(GMonthDayType.GMONTHDAY);
        put(GMonthType.GMONTH);
        put(GYearMonthType.GYEARMONTH);
        put(GYearType.GYEAR);
        put(TimeType.TIME);
        put(DurationType.DURATION);
        put(DayTimeDurationType.DAYTIME_DURATION);
        put(YearMonthDurationType.YEARMONTH_DURATION);
        put(NOTATIONType.NOTATION);
        put(DecimalType.DECIMAL);
        put(IntegerType.INTEGER);
        put(LongType.LONG);
        put(IntType.INT);
        put(ShortType.SHORT);
        put(ByteType.BYTE);
        put(NonNegativeIntegerType.NON_NEGATION_INTEGER);
        put(PositiveIntegerType.POSITIVE_INTEGER);
        put(UnsignedLongType.UNSIGNED_LONG);
        put(UnsignedIntType.UNSIGNED_INT);
        put(UnsignedShortType.UNSIGNED_SHORT);
        put(UnsignedByteType.UNSIGNED_BYTE);
        put(NonPositiveIntegerType.NON_POSITIVE_INTEGER);
        put(NegativeIntegerType.NEGATIVE_INTEGER);
        put(DoubleType.DOUBLE);
        put(FloatType.FLOAT);
        put(QNameType.QNAME);
        put(StringType.STRING);
        put(NormalizedStringType.NORMALIZED_STRING);
        put(TokenType.TOKEN);
        put(LanguageType.LANGUAGE);
        put(NMTokenType.NMTOKEN);
        put(NameType.NAME);
        put(NCNameType.NCNAME);
        put(ENTITYType.ENTITY);
        put(IDREFType.IDREF);
        put(IDType.ID);
        put(UntypedAtomicType.UNTYPED_ATOMIC);
        // extension types
        put(NumericType.getInstance());
        // sequence types
        put(new SequenceType(NodeType.ANYNODE, Occurrence.OCC_ZERO_OR_ONE)); // node()?
        put(new SequenceType(ItemType.ANY_ITEM, Occurrence.OCC_ZERO_OR_ONE)); // item()?
        put(new SequenceType(AnyURIType.ANYURI, Occurrence.OCC_ZERO_OR_ONE)); // xs:anyURI?
        put(new SequenceType(QNameType.QNAME, Occurrence.OCC_ZERO_OR_ONE)); // xs:QName?
        put(new SequenceType(DoubleType.DOUBLE, Occurrence.OCC_ZERO_OR_ONE)); // xs:double?
        put(new SequenceType(IntegerType.INTEGER, Occurrence.OCC_ZERO_OR_MORE)); // xs:integer*
        put(new SequenceType(StringType.STRING, Occurrence.OCC_ZERO_OR_ONE)); // xs:string?
        put(new SequenceType(BooleanType.BOOLEAN, Occurrence.OCC_ZERO_OR_ONE)); // xs:boolean?
        put(new SequenceType(AtomicType.ANY_ATOMIC_TYPE, Occurrence.OCC_ZERO_OR_ONE)); // xdt:anyAtomicType?
        put(new SequenceType(StringType.STRING, Occurrence.OCC_ZERO_OR_MORE)); // xs:string*
        put(new SequenceType(IntegerType.INTEGER, Occurrence.OCC_ZERO_OR_ONE)); // xs:integer?
        put(new SequenceType(YearMonthDurationType.YEARMONTH_DURATION, Occurrence.OCC_ZERO_OR_ONE)); // xdt:yearMonthDuration?
        put(new SequenceType(DecimalType.DECIMAL, Occurrence.OCC_ZERO_OR_ONE)); // xs:decimal?
        put(new SequenceType(DayTimeDurationType.DAYTIME_DURATION, Occurrence.OCC_ZERO_OR_ONE)); //  xdt:dayTimeDuration?
        put(new SequenceType(DateTimeType.DATETIME, Occurrence.OCC_ZERO_OR_ONE)); // xs:dateTime?
        put(new SequenceType(DateType.DATE, Occurrence.OCC_ZERO_OR_ONE)); // xs:date?
        put(new SequenceType(TimeType.TIME, Occurrence.OCC_ZERO_OR_ONE)); // xs:time?
        put(new SequenceType(NCNameType.NCNAME, Occurrence.OCC_ZERO_OR_ONE)); // xs:NCName?
        put(new SequenceType(AtomicType.ANY_ATOMIC_TYPE, Occurrence.OCC_ZERO_OR_MORE)); // xdt:anyAtomicType*
        put(new SequenceType(NodeType.ANYNODE, Occurrence.OCC_ZERO_OR_MORE)); // node()*
        put(new SequenceType(NodeType.TEXT, Occurrence.OCC_ZERO_OR_MORE)); // text()*
    }

    /** Restricts an instantiation. */
    private TypeRegistry() {}

    private static void put(Type t) {
        Type prev = types.put(t.toString(), t);
        if(prev != null) { // sanity check
            throw new IllegalStateException("Type is illegally overloaded: '" + t + "'");
        }
    }

    public static Type get(QualifiedName type) throws StaticError {
        final Type t = types.get(type.toString());
        if(t == null) {
            throw new StaticError("err:XPST0051", "Type not found: " + type.toString());
        }
        return t;
    }

    public static Type safeGet(QualifiedName type) {
        return types.get(type.toString());
    }

    public static Type get(final String type) throws StaticError {
        final Type resolved = safeGet(type);
        if(resolved == null) {
            throw new StaticError("err:XPST0051", "Type not found: " + type.toString());
        }
        return resolved;
    }

    public static Type safeGet(final String type) {
        final String resolved = resolve(type);
        Type t = types.get(resolved);
        if(t == null) {
            final int last = type.length() - 1;
            final char lastchar = type.charAt(last);
            if(lastchar == '?' || lastchar == '*') {
                assert (last > 1);
                final String baseTypeName = type.substring(0, last);
                final String resolvedTypeName = resolve(baseTypeName);
                if(resolvedTypeName == null) {
                    throw new IllegalArgumentException("Invalid type: " + baseTypeName);
                }
                final Type baseType = types.get(resolvedTypeName);
                if(baseType instanceof ItemType) {
                    t = new SequenceType((ItemType) baseType, Type.Occurrence.OCC_ZERO_OR_ONE);
                    put(t);
                }
            }
        }
        return t;
    }

    public static AtomicType safeGetAtomicType(final String type) {
        final String resolved = resolve(type);
        final Type t = types.get(resolved);
        return (AtomicType) t;
    }

    private static String resolve(final String rawType) {
        final int pos = rawType.indexOf(':');
        if(pos == -1) {
            return rawType;
        }
        final String prefix = rawType.substring(0, pos);
        final String lname = rawType.substring(pos + 1);
        assert (lname != null);
        final String uri;
        if(XQueryConstants.XDT.equals(prefix)) {
            uri = XQueryConstants.XDT_URI;
        } else if(XQueryConstants.XS.equals(prefix)) {
            uri = XQueryConstants.XS_URI;
        } else {
            return null;
        }
        return '{' + uri + '}' + lname;
    }

}
