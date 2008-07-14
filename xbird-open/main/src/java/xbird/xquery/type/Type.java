/*
 * @(#)$Id: Type.java 3619 2008-03-26 07:23:03Z yui $
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

import java.io.Serializable;

import javax.xml.xquery.XQException;

import xbird.xqj.XQJConstants;
import xbird.xquery.XQueryConstants;
import xbird.xquery.type.xs.AnySimpleType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#casting
 */
public abstract class Type implements Serializable {

    /** xs:anyType */
    public static final Type ANY = new Type() {
        private static final long serialVersionUID = 3726428590819113084L;

        public boolean accepts(Type expected) {
            return true;
        }

        public Class getJavaObjectType() {
            return Object.class;
        }

        public Occurrence quantifier() {
            return Occurrence.OCC_ZERO_OR_MORE;
        }

        public Type prime() {
            return this;
        }

        public String toString() {
            return "{" + XQueryConstants.XS_URI + "}anyType";
        }

        @Override
        public int getXQJBaseType() {
            return XQJConstants.XQBASETYPE_ANYTYPE;
        }
    };

    public static final Type NONE = new Type() {
        private static final long serialVersionUID = 5767436713969793136L;

        @Override
        public boolean accepts(Type expected) {
            return false;
        }

        @Override
        public Class getJavaObjectType() {
            return null;
        }

        @Override
        public Type prime() {
            return this;
        }

        @Override
        public Occurrence quantifier() {
            return Occurrence.OCC_ZERO;
        }

        @Override
        public String toString() {
            return "None type";
        }

        @Override
        public int getXQJBaseType() {
            return XQJConstants.XQBASETYPE_UNTYPED; // REVIEWME
        }

    };

    /** xs:anySimpleType */
    public static final Type ANY_SIMPLE = new SimpleType();

    public Type() {}

    //--------------------------------------------
    // abstract

    /**
     * @link http://www.w3.org/TR/xquery-semantics/#jd_prime
     */
    public abstract Type prime();

    /**
     * @link http://www.w3.org/TR/xquery-semantics/#jd_quantifier
     */
    public abstract Occurrence quantifier();

    /**
     * Is super type of expected?
     * 
     * @link http://www.w3.org/TR/xpath20/#promotion
     */
    public abstract boolean accepts(Type expected);

    public abstract Class getJavaObjectType();

    public int getXQJBaseType() throws XQException { // should be overrided
        throw new XQException("Illegal type as an item kind: " + toString(), "err:XQJxxxx");
    }

    //--------------------------------------------
    // overrides/implements

    /**
     * should be overrided in sub-classes.
     */
    public abstract String toString();

    //--------------------------------------------
    // Internal class

    private static final class SimpleType extends Type implements AnySimpleType {
        private static final long serialVersionUID = 8564084707238438495L;

        private SimpleType() {
            super();
        }

        public boolean accepts(Type expected) {
            return false;
        }

        public String toString() {
            return "{" + XQueryConstants.XS_URI + "}anySimpleType";
        }

        public Class getJavaObjectType() {
            return Object.class;
        }

        public Occurrence quantifier() {
            return Occurrence.OCC_EXACTLY_ONE;
        }

        public Type prime() {
            return this;
        }

        @Override
        public int getXQJBaseType() {
            return XQJConstants.XQBASETYPE_ANYSIMPLETYPE;
        }
    }

    /**
     * @link http://www.w3.org/TR/xquery-semantics/#jd_quantifier
     */
    public enum Occurrence {
        //  *  1  0
        //  x  x  x                
        OCC_ZERO('0', (byte) 1), OCC_EXACTLY_ONE('1', (byte) 2), OCC_ZERO_OR_ONE('?', (byte) 3), OCC_MORE(
                '*', (byte) 4), OCC_ONE_OR_MORE('+', (byte) 6), OCC_ZERO_OR_MORE('*', (byte) 7);

        private final char sig;
        private final byte align;

        Occurrence(char c, byte a) {
            this.sig = c;
            this.align = a;
        }

        public boolean accepts(byte target) {
            final int and = align & target;
            return and == target;
        }

        public byte getAlignment() {
            return align;
        }

        public String toString() {
            return String.valueOf(sig);
        }
    }

}
