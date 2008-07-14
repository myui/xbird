/*
 * @(#)$Id: TypeTable.java 3619 2008-03-26 07:23:03Z yui $
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

/**
 * 
 * <DIV lang="en">
 * <pre>
 * uA = xdt:untypedAtomic
 * aURI = xs:anyURI
 * b64 = xs:base64Binary
 * bool = xs:boolean
 * dat = xs:date
 * gDay = xs:gDay
 * dbl = xs:double
 * dec = xs:decimal
 * dT = xs:dateTime
 * dTD = xdt:dayTimeDuration
 * dur = xs:duration
 * flt = xs:float
 * hxB = xs:hexBinary
 * gMD = xs:gMonthDay
 * gMon = xs:gMonth
 * int = xs:integer
 * NOT = xs:NOTATION
 * QN = xs:QName
 * str = xs:string
 * tim = xs:time
 * gYM = xs:gYearMonth
 * yMD = xdt:yearMonthDuration
 * gYr = xs:gYear
 * </pre>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#casting
 */
public final class TypeTable {

    public static final int UNTYPED_ATOMIC_TID = 0;
    public static final int STRING_TID = 1;
    public static final int FLOAT_TID = 2;
    public static final int DOUBLE_TID = 3;
    public static final int DECIMAL_TID = 4;
    public static final int INTEGER_TID = 5;
    public static final int DURATION_TID = 6;
    public static final int YEAR_MONTH_DURATION_TID = 7;
    public static final int DAYTIME_DURATION_TID = 8;
    public static final int DATETIME_TID = 9;
    public static final int TIME_TID = 10;
    public static final int DATE_TID = 11;
    public static final int GYEAR_MONTH_TID = 12;
    public static final int GYEAR_TID = 13;
    public static final int GMONTH_DAY_TID = 14;
    public static final int GDAY_TID = 15;
    public static final int GMONTH_TID = 16;
    public static final int BOOLEAN_TID = 17;
    public static final int BASE64_TID = 18;
    public static final int HEX_BINARY_TID = 19;
    public static final int ANY_URI_TID = 20;
    public static final int QNAME_TID = 21;
    public static final int NOTATION_TID = 22;

    /* 
     * Similarly, casting is not supported to or from xdt:anyAtomicType.
     * There are no atomic values with the type annotation xdt:anyAtomicType at runtime
     */
    public static final int ANY_ATOM_TID = -1;
    public static final int DATETIME_BASE_TID = -2;
    public static final int NUMERIC_TID = -3;
    
    public static final int UNRESOLVED = Integer.MIN_VALUE;

    private static final byte Y = 0;
    private static final byte N = 1;
    private static final byte M = -1;

    public enum Castable {
        Y, N, M;
    }

    static final byte[][] castingTable = new byte[24][];
    static {
        castingTable[UNTYPED_ATOMIC_TID] = new byte[] { Y, Y, M, M, M, M, M, M, M, M, M, M, M, M,
                M, M, M, M, M, M, M, N, N };
        castingTable[ANY_URI_TID] = new byte[] { Y, Y, N, N, N, N, N, N, N, N, N, N, N, N, N, N, N,
                N, N, N, Y, N, N };
        castingTable[BASE64_TID] = new byte[] { Y, Y, N, N, N, N, N, N, N, N, N, N, N, N, N, N, N,
                N, Y, Y, N, N, N };
        castingTable[BOOLEAN_TID] = new byte[] { Y, Y, Y, Y, Y, Y, N, N, N, N, N, N, N, N, N, N, N,
                Y, N, N, N, N, N };
        castingTable[DATE_TID] = new byte[] { Y, Y, N, N, N, N, N, N, N, Y, N, Y, Y, Y, Y, Y, Y, N,
                N, N, N, N, N };
        castingTable[GDAY_TID] = new byte[] { Y, Y, N, N, N, N, N, N, N, N, N, N, N, N, N, Y, N, N,
                N, N, N, N, N };
        castingTable[DOUBLE_TID] = new byte[] { Y, Y, Y, Y, M, M, N, N, N, N, N, N, N, N, N, N, N,
                Y, N, N, N, N, N };
        castingTable[DECIMAL_TID] = new byte[] { Y, Y, Y, Y, Y, Y, N, N, N, N, N, N, N, N, N, N, N,
                Y, N, N, N, N, N };
        castingTable[DATETIME_TID] = new byte[] { Y, Y, N, N, N, N, N, N, N, Y, Y, Y, Y, Y, Y, Y,
                Y, N, N, N, N, N, N };
        castingTable[DAYTIME_DURATION_TID] = new byte[] { Y, Y, N, N, N, N, Y, Y, Y, N, N, N, N, N,
                N, N, N, N, N, N, N, N, N };
        castingTable[DURATION_TID] = new byte[] { Y, Y, N, N, N, N, Y, Y, Y, N, N, N, N, N, N, N,
                N, N, N, N, N, N, N };
        castingTable[FLOAT_TID] = new byte[] { Y, Y, Y, Y, M, M, N, N, N, N, N, N, N, N, N, N, N,
                Y, N, N, N, N, N };
        castingTable[HEX_BINARY_TID] = new byte[] { Y, Y, N, N, N, N, N, N, N, N, N, N, N, N, N, N,
                N, N, Y, Y, N, N, N };
        castingTable[GMONTH_DAY_TID] = new byte[] { Y, Y, N, N, N, N, N, N, N, N, N, N, N, N, Y, N,
                N, N, N, N, N, N, N };
        castingTable[GMONTH_TID] = new byte[] { Y, Y, N, N, N, N, N, N, N, N, N, N, N, N, N, N, Y,
                N, N, N, N, N, N };
        castingTable[INTEGER_TID] = new byte[] { Y, Y, Y, Y, Y, Y, N, N, N, N, N, N, N, N, N, N, N,
                Y, N, N, N, N, N };
        castingTable[NOTATION_TID] = new byte[] { Y, Y, N, N, N, N, N, N, N, N, N, N, N, N, N, N,
                N, N, N, N, N, N, M };
        castingTable[QNAME_TID] = new byte[] { Y, Y, N, N, N, N, N, N, N, N, N, N, N, N, N, N, N,
                N, N, N, N, Y, N };
        castingTable[STRING_TID] = new byte[] { Y, Y, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M,
                M, M, M, M, M, M };
        castingTable[TIME_TID] = new byte[] { Y, Y, N, N, N, N, N, N, N, N, Y, N, N, N, N, N, N, N,
                N, N, N, N, N };
        castingTable[GYEAR_MONTH_TID] = new byte[] { Y, Y, N, N, N, N, N, N, N, N, N, N, Y, N, N,
                N, N, N, N, N, N, N, N };
        castingTable[YEAR_MONTH_DURATION_TID] = new byte[] { Y, Y, N, N, N, N, Y, Y, Y, N, N, N, N,
                N, N, N, N, N, N, N, N, N, N };
        castingTable[GYEAR_TID] = new byte[] { Y, Y, N, N, N, N, N, N, N, N, N, N, N, Y, N, N, N,
                N, N, N, N, N, N };
    }

    public static Castable castable(AtomicType source, AtomicType target) {
        final int sourceTid = source.getTypeId();
        final int targetTid = target.getTypeId();
        if(sourceTid < 0 || targetTid < 0) {
            throw new IllegalStateException("Illegal type... Source: " + source + ", target: "
                    + target);
        }
        if(sourceTid == targetTid) {
            return Castable.Y;
        }
        final int code = castingTable[sourceTid][targetTid];
        switch(code) {
            case Y:
                return Castable.Y;
            case N:
                return Castable.N;
            case M:
                return Castable.M;
            default:
                throw new IllegalStateException("Illegal code:" + code);
        }
    }

    public static boolean isCastable(AtomicType source, AtomicType target, boolean exact) {
        int sourceTid = source.getTypeId();
        int targetTid = target.getTypeId();
        if(sourceTid == targetTid) {
            return true;
        }
        if(sourceTid < 0 || targetTid < 0) {
            return !exact; // don't know
        }
        assert (sourceTid >= 0 && sourceTid < castingTable.length) : "Illegal source tid:"
                + sourceTid;
        assert (targetTid >= 0 && targetTid < castingTable.length) : "Illegal target tid:"
                + targetTid;
        int code = castingTable[sourceTid][targetTid];
        if(exact) {
            return (code == Y);
        } else {
            return (code == Y) || (code == M);
        }
    }

    public static boolean isMayCastable(AtomicType source, AtomicType target) {
        return isCastable(source, target, false);
    }
}