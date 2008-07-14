/*
 * @(#)$Id: CastingFunction.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.func.constructor;

import java.util.Iterator;

import xbird.xquery.XQRTException;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.type.*;
import xbird.xquery.type.xs.QNameType;
import xbird.xquery.type.xs.StringType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#casting
 */
public class CastingFunction extends BuiltInFunction {
    private static final long serialVersionUID = -5912574732355599110L;
    public static final String CLAZZ = CastingFunction.class.getName();

    public CastingFunction(String funcName) {
        super(funcName, obtainType(funcName));
    }

    protected CastingFunction(String funcName, Type retType) {
        super(funcName, retType);
    }

    public CastingFunction() {
        super();
    }

    public void initialize(String funcName) {
        QualifiedName resolvedName = resolve(funcName);
        this._funcName = resolvedName;
        Type type = obtainType(funcName);
        this._returnType = type;
        final FunctionSignature[] sig = new FunctionSignature[1];
        sig[0] = new FunctionSignature(resolvedName, new Type[] { TypeRegistry.safeGet("xs:anyAtomicType?") });
        this._signs = sig;
    }

    private static Type obtainType(String baseName) {
        String typeName = baseName + '?';
        Type type = TypeRegistry.safeGet(typeName);
        if(type == null) {
            Type baseType = TypeRegistry.safeGet(baseName);
            if(baseType == null) {
                throw new XQRTException("err:XPST0051", "Type not found: " + type);
            }
            assert (baseType instanceof ItemType);
            type = new SequenceType((ItemType) baseType, Type.Occurrence.OCC_ZERO_OR_ONE);
        }
        return type;
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[1];
        s[0] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:anyAtomicType?") });
        return s;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        if(argv == null) {
            throw new IllegalStateException();
        }
        final Item arg = argv.getItem(0);
        final Iterator<? extends Item> argItor = arg.iterator();
        if(!argItor.hasNext()) {
            return ValueSequence.EMPTY_SEQUENCE;
        }
        final AtomicValue tocast = (AtomicValue) argItor.next();
        final Type retType = getReturnType().prime();
        final AtomicType casttoType = (AtomicType) retType;
        final AtomicValue converted = tocast.castAs(casttoType, dynEnv);
        return converted;
    }

    /**
     * xs:QName($arg as xs:string) as xs:QName.
     */
    public static final class XsQName extends CastingFunction {
        private static final long serialVersionUID = -6928355100709355013L;

        public static final String SYMBOL = "xs:QName";

        public XsQName() {
            super(SYMBOL, QNameType.QNAME);
        }

        @Override
        protected FunctionSignature[] signatures() {
            final FunctionSignature[] s = new FunctionSignature[1];
            s[0] = new FunctionSignature(getName(), new Type[] { StringType.STRING });
            return s;
        }
    }

}
