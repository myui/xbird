/*
 * @(#)$Id: FnBoolean.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.func.seq;

import java.util.Iterator;

import xbird.xquery.*;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.literal.XNumber;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.dm.value.xsi.BooleanValue;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.*;
import xbird.xquery.type.xs.BooleanType;

/**
 * fn:boolean($arg as item()*) as xs:boolean.
 * <DIV lang="en">
 * Computes the effective boolean value of the argument sequence.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-boolean
 */
public final class FnBoolean extends BuiltInFunction {
    private static final long serialVersionUID = 1302253258184708562L;

    public static final String SYMBOL = "fn:boolean";

    public FnBoolean() {
        super(SYMBOL, BooleanType.BOOLEAN);
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[1];
        s[0] = new FunctionSignature(getName(), new Type[] { SequenceType.ANY_ITEMS });
        return s;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        return effectiveBooleanValue(argv) ? BooleanValue.TRUE : BooleanValue.FALSE;
    }

    public static boolean effectiveBooleanValue(Sequence seq) throws TypeError {
        return effectiveBooleanValue(seq, -1);
    }

    public static boolean effectiveBooleanValue(final Sequence seq, final int pos) throws TypeError {
        if(seq instanceof BooleanValue) {
            return ((BooleanValue) seq).booleanValue();
        }
        final Iterator<? extends Item> seqit = seq.iterator();
        // If $arg is the empty sequence, fn:boolean returns false.
        if(!seqit.hasNext()) {
            return false;
        }
        final Item sec = seqit.next();
        // If $arg is a sequence whose first item is a node, fn:boolean returns true.
        if(sec instanceof XQNode) {
            return true;
        }
        if(seqit.hasNext()) {
            throw new XQRTException("err:FORG0006", "failed calculating effectiveBooleanValue for '"
                    + seq + '\'');
        }
        // If $arg is a singleton value of type xs:boolean or a derived from xs:boolean, 
        // fn:boolean returns $arg.        
        if(sec instanceof BooleanValue) {
            return ((BooleanValue) sec).booleanValue();
        }
        // If $arg is a singleton value of type xs:string or a type derived from xs:string 
        // or xdt:untypedAtomic, fn:boolean returns false if the operand value has zero length;
        // otherwise it returns true.
        final Type secType = sec.getType();
        if(secType instanceof AtomicType) {
            final int tid = ((AtomicType) secType).getTypeId();
            if(tid == TypeTable.STRING_TID || tid == TypeTable.UNTYPED_ATOMIC_TID
                    || tid == TypeTable.ANY_URI_TID) {
                final String sv = sec.stringValue();
                return sv.length() != 0;
            }
            // If $arg is a singleton value of any numeric type or a type derived from a numeric type, 
            // fn:boolean returns false if the operand value is NaN or is numerically equal to zero; 
            // otherwise it returns true.
            if(sec instanceof XNumber) {
                final double d = ((XNumber) sec).getNumber().doubleValue();
                if(Double.isNaN(d) || d == 0) {
                    return false;
                } else {
                    if(pos != -1 && d != pos) {
                        return false;
                    }
                    return true;
                }
            }
        }
        throw new TypeError("err:FORG0006", "effectiveBooleanValue could not calculate.. " + seq
                + '(' + seq.getType() + ')');
    }

}