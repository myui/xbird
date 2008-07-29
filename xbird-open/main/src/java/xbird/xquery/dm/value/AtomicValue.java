/*
 * @(#)$Id: AtomicValue.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.dm.value;

import java.io.*;

import xbird.xquery.*;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.AtomicType;
import xbird.xquery.type.TypeTable;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author yui (yuin405+xbird@gmail.com)
 */
public abstract class AtomicValue extends SingleItem implements IAtomized<Item>, Externalizable {
    private static final long serialVersionUID = 1L;

    protected AtomicType _type;
    protected String _lexicalValue;

    public AtomicValue() {// for Externalizable
        super();
    }

    public AtomicValue(String lexval, AtomicType type) {
        assert (lexval != null && type != null);
        this._lexicalValue = type.processWhitespace(lexval);
        this._type = type;
    }

    protected void onUpdate() {
        throw new UnsupportedOperationException("OnUpdate should not be called in `"
                + this.getClass().getName() + '`');
    }

    protected void update(String lexval) {
        this._lexicalValue = _type.processWhitespace(lexval);
    }

    protected final void setStringValue(String v) {
        this._lexicalValue = v;
    }

    public String stringValue() {
        return this._lexicalValue;
    }

    public final AtomicType getType() {
        return this._type;
    }

    @Override
    public Sequence<? extends Item> atomize(DynamicContext dynEnv) {
        return this;
    }

    public <T extends AtomicValue> T castAs(AtomicType trgType, DynamicContext dynEnv)
            throws XQueryException {
        final int ttid = trgType.getTypeId();
        if(ttid == TypeTable.NOTATION_TID || ttid == TypeTable.ANY_ATOM_TID) {
            throw new StaticError("err:XPST0080", "Illegal target type: " + trgType);
        }
        final AtomicType type = _type;
        if(type == trgType) {
            return (T) this;
        }
        final boolean mayCastable = TypeTable.isMayCastable(type, trgType);
        if(mayCastable) {
            final String sv = stringValue();
            final String literal = trgType.processWhitespace(sv);
            try {
                return (T) trgType.createInstance(literal, type, dynEnv);
            } catch (XQRTException xqe) {
                throw new DynamicError(xqe);
            } catch (RuntimeException re) {
                throw new DynamicError("err:FORG0001", "cast to '" + trgType + "' failed: " + sv, re);
            }
        }
        // TODO static error
        throw new DynamicError("err:XPTY0004", "failed to cast '" + _type + "' as '" + trgType
                + "': " + _lexicalValue);
    }

    public abstract <T extends Object> T toJavaObject() throws XQueryException;

    @Override
    public String toString() {
        return this._lexicalValue;
    }

    @Override
    public int hashCode() {
        final int hash;
        if(_lexicalValue == null) {
            hash = -1;
        } else {
            hash = _lexicalValue.hashCode();
        }
        return hash;
    }

    public int compareTo(Item trg) {
        String trgv = trg.stringValue();
        return _lexicalValue.compareTo(trgv);
    }

    public abstract int getIdentifier();

    public static final void writeAtomicValue(final AtomicValue atom, final ObjectOutput out)
            throws IOException {
        out.writeInt(atom.getIdentifier());
        atom.writeExternal(out);
    }

    public static final AtomicValue readAtomicValue(final ObjectInput in) throws IOException,
            ClassNotFoundException {
        final int id = in.readInt();
        final AtomicValue atom = AtomicValueFactory.createAtomicValue(id);
        atom.readExternal(in);
        return atom;
    }
    
    /**
     * To cope with the case when V1 and V2 are both NaN in Grouping.
     * 
     * @see http://www.w3.org/TR/2008/WD-xquery-11-20080711/#id-group-by
     */
    public AtomicValue asGroupingValue() {
        return this;
    }

}
