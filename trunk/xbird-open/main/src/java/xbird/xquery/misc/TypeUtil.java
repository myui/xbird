/*
 * @(#)$Id:TypeUtil.java 2335 2007-07-17 04:14:15Z yui $
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
package xbird.xquery.misc;

import xbird.xquery.TypeError;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.opt.TypePromotedExpr;
import xbird.xquery.meta.IFocus;
import xbird.xquery.type.AtomicType;
import xbird.xquery.type.ChoiceType;
import xbird.xquery.type.SequenceType;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeTable;
import xbird.xquery.type.Type.Occurrence;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class TypeUtil {

    private TypeUtil() {}

    public static boolean isOptional(final Type t) {
        if(t instanceof SequenceType) {
            if(((SequenceType) t).quantifier() == Occurrence.OCC_ZERO_OR_ONE) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static SequenceType toOptimal(final AtomicType t) {
        return new SequenceType(t, Occurrence.OCC_ZERO_OR_ONE);
    }

    /**
     * @link http://www.w3.org/TR/xquery-semantics/#jd_quantifier
     */
    public static Type quantify(final Type expected, final Type actual) {
        if(expected instanceof AtomicType) {
            if(actual instanceof SequenceType) {
                SequenceType seq = (SequenceType) actual;
                if(seq.quantifier() == Occurrence.OCC_EXACTLY_ONE) {
                    return expected;
                } else {
                    return new SequenceType((AtomicType) expected, seq.quantifier());
                }
            } else {
                return expected;
            }
        } else {
            return expected;
        }
    }

    /**
     *  Whether the first type is a subtype of the second?
     */
    public static boolean subtypeOf(final Type actual, final Type expected) {
        return (actual == expected) ? true : expected.accepts(actual);
    }

    public static boolean isPromotable(final Type src, final Type trg) {
        if(trg instanceof AtomicType) {
            return isPromotable(src, (AtomicType) trg);
        } else {
            return subtypeOf(src, trg);
        }
    }

    public static boolean isPromotable(final Type src, final AtomicType trg) {
        final Type primeType = src.prime();
        if(primeType instanceof AtomicType) {
            return TypeTable.isCastable((AtomicType) primeType, trg, true);
        } else if(primeType instanceof ChoiceType) {
            final ChoiceType choise = (ChoiceType) primeType;
            for(Type c : choise.getTypes()) {
                if(!isPromotable(c, trg)) {
                    return false;
                }
            }
            return true;
        } else {
            return subtypeOf(src, trg);
        }
    }

    public static XQExpression promote(final XQExpression expr, final Type destType)
            throws TypeError {
        final Type srcType = expr.getType();
        if(TypeUtil.subtypeOf(srcType, destType)) {
            return expr;
        } else {
            if(TypeUtil.isPromotable(srcType, destType)) {
                return new TypePromotedExpr(expr, destType, true);
            } else {
                throw new TypeError("err:XPTY0004", "Declared type '" + destType
                        + "' does not accept inferred type '" + srcType + "': \n" + expr);
            }
        }
    }

    public static boolean instanceOf(final Sequence<? extends Item> value, final Type type) {
        final Occurrence occ = type.quantifier();
        if(value.isEmpty()) {
            return occ.accepts(Occurrence.OCC_ZERO.getAlignment());
        }
        final Type expected = (type instanceof SequenceType) ? ((SequenceType) type).prime() : type;
        int count = 0;
        final IFocus<? extends Item> valueItor = value.iterator();
        for(Item it : valueItor) {
            final Type actual = it.getType();
            if(!subtypeOf(actual, expected)) {
                valueItor.closeQuietly();
                return false;
            }
            count++;
        }
        valueItor.closeQuietly();
        if(count == 0) {
            throw new IllegalStateException();
        }
        if(count == 1) {
            return occ.accepts(Occurrence.OCC_EXACTLY_ONE.getAlignment());
        } else {
            return occ.accepts(Occurrence.OCC_MORE.getAlignment());
        }
    }

    public static Type union(final Type baseType, final Type type) {
        if(baseType instanceof ChoiceType) {
            ((ChoiceType) baseType).combine(type);
            return baseType;
        } else {
            return new ChoiceType(baseType, type);
        }
    }
}