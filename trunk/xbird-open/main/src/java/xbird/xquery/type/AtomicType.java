/*
 * @(#)$Id: AtomicType.java 3619 2008-03-26 07:23:03Z yui $
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

import java.io.*;
import java.util.List;

import xbird.util.lang.ObjectUtils;
import xbird.xqj.XQJConstants;
import xbird.xquery.XQueryConstants;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.AtomicValue;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.misc.QNameTable;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.type.xs.AnySimpleType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class AtomicType extends ItemType implements AnySimpleType, Externalizable {

    public static final int PRESERVE = 0;
    public static final int REPLACE = 1;
    public static final int COLLAPSE = 2;

    public static final ItemType ANY_ATOMIC_TYPE = new AnyAtomicType();

    private final int _typeId;
    protected final QualifiedName _typeName;
    private int _whitespaceProcMode = COLLAPSE;

    private/* final */transient String _type;

    // for Externalizable
    public AtomicType() {
        this._typeId = -1;
        this._typeName = null;
    }

    protected AtomicType(final int tid, final String type) {
        this(tid, resolve(type), type);
    }

    protected AtomicType(final int tid, final QualifiedName typeName, final String type) {
        super();
        assert (typeName != null);
        assert (type != null);
        this._typeId = tid;
        this._typeName = typeName;
        this._type = type;
    }

    protected void setWhitespaceProcessing(final int mode) {
        this._whitespaceProcMode = mode;
    }

    public int getTypeId() {
        return _typeId;
    }

    public QualifiedName getTypeName() {
        return _typeName;
    }

    @Override
    public String toString() {
        return _typeName.toString();
    }

    public String processWhitespace(String literal) {
        return processWhitesapce(literal, _whitespaceProcMode);
    }

    private static final String processWhitesapce(final String literal, final int mode) {
        assert (literal != null);
        switch(mode) {
            case PRESERVE:
                return literal;
            case REPLACE: {
                final int len = literal.length();
                final StringBuilder result = new StringBuilder(len);
                for(int i = 0; i < len; i++) {
                    char ch = literal.charAt(i);
                    if(isWhiteSpace(ch)) {
                        result.append(' ');
                    } else {
                        result.append(ch);
                    }
                }
                return result.toString();
            }
            case COLLAPSE: {
                int len = literal.length();
                final StringBuilder result = new StringBuilder(len);
                boolean inStripMode = true;
                for(int i = 0; i < len; i++) {
                    char ch = literal.charAt(i);
                    boolean b = isWhiteSpace(ch);
                    if(inStripMode && b) {
                        continue; // skip leading whitespace
                    }
                    inStripMode = b;
                    if(inStripMode) {
                        result.append(' ');
                    } else {
                        result.append(ch);
                    }
                }
                len = result.length();
                if(len > 0 && result.charAt(len - 1) == ' ') {
                    result.setLength(len - 1); // remove trailing whitespaces
                }
                return result.toString();
            }
            default:
                throw new IllegalStateException("Illegal whitespace processing mode:" + mode);
        }
    }

    private static final boolean isWhiteSpace(final char ch) {
        return ch == 0x9 || ch == 0xA || ch == 0xD || ch == 0x20;
    }

    public abstract AtomicValue createInstance(String literal, AtomicType srcType, DynamicContext dynEnv)
            throws XQueryException;

    //--------------------------------------------
    // Helper

    /**
     * Resolve Type name from string.
     */
    public static QualifiedName resolve(String type) {
        final String s[] = type.split(":");
        if(s.length != 2) {
            throw new IllegalArgumentException("Invalid type: " + type);
        }
        final String uri;
        if(XQueryConstants.XDT.equals(s[0])) {
            uri = XQueryConstants.XDT_URI;
        } else if(XQueryConstants.XS.equals(s[0])) {
            uri = XQueryConstants.XS_URI;
        } else {
            throw new IllegalArgumentException("Invalid type: " + type);
        }
        return QNameTable.instantiate(uri, s[1], s[0]);
    }

    @Override
    public boolean accepts(final Type expected) {
        return accepts(expected, false);
    }

    private boolean accepts(final Type expected, final boolean derives) {
        if(expected == null) {
            return false;
        }
        if(this == expected) {
            return true;
        }
        final Type prime = expected.prime();
        if(prime instanceof ChoiceType) {
            final List<Type> choice = ((ChoiceType) prime).getTypes();
            for(Type t : choice) {
                if(!accepts(t, false)) {
                    return false;
                }
            }
            return true;
        } else if(prime == ItemType.ANY_ITEM || prime == ANY_ATOMIC_TYPE) {
            return true;
        } else if(prime instanceof AtomicType) {// for SequenceType TODO REVIEWME
            final AtomicType primeAtomic = (AtomicType) prime;
            final int tid = primeAtomic.getTypeId();
            if(tid == TypeTable.ANY_ATOM_TID) {
                return true;
            }
            if(!derives) {
                return isSuperTypeOf(primeAtomic);
            } else {
                return this.getClass().isAssignableFrom(expected.getClass());
            }
        } else {
            return false;
        }
    }

    protected boolean isSuperTypeOf(final AtomicType expected) {
        return this.getClass().isAssignableFrom(expected.getClass());
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this._type = ObjectUtils.readString(in);
    }

    protected AtomicType readResolve() throws ObjectStreamException {
        final String type = _type;
        if(type == null) {
            throw new IllegalStateException();
        }
        final AtomicType atomic = TypeRegistry.safeGetAtomicType(type);
        if(atomic == null) {
            throw new IllegalStateException("Atomic type not found: " + atomic);
        }
        return atomic;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        ObjectUtils.writeString(out, _type);
    }

    public static AtomicType readAtomicType(final ObjectInput in) throws IOException {
        final String type = ObjectUtils.readString(in);
        if(type == null) {
            throw new IllegalStateException();
        }
        final AtomicType atomic = TypeRegistry.safeGetAtomicType(type);
        if(atomic == null) {
            throw new IllegalStateException("Atomic type not found: " + atomic);
        }
        return atomic;
    }

    private static final class AnyAtomicType extends AtomicType {
        private static final long serialVersionUID = 5727024892510776718L;

        public AnyAtomicType() {
            super(TypeTable.ANY_ATOM_TID, "xs:anyAtomicType");
        }

        public Class getJavaObjectType() {
            return Object.class;
        }

        public AtomicValue createInstance(String literal, AtomicType srcType, DynamicContext dynEnv)
                throws XQueryException {
            throw new IllegalStateException("createInstance should not be called for `"
                    + getTypeName() + "`");
        }

        @Override
        public String processWhitespace(String literal) {
            return literal;
        }

        @Override
        protected boolean isSuperTypeOf(final AtomicType expected) {
            return true;
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return this;
        }

        @Override
        public int getXQJBaseType() {
            return XQJConstants.XQBASETYPE_ANYATOMICTYPE;
        }

    };
}
