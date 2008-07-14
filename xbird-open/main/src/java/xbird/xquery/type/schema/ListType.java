/*
 * @(#)$Id: ListType.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.type.schema;

import java.util.*;

import xbird.xquery.type.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class ListType extends Type implements SchemaType {
    private static final long serialVersionUID = -2479802443238568170L;

    private final List<Type> _list;
    private final Set<Type> _set;

    private boolean hasNoneType = false;

    public ListType(List<Type> t) {
        this._list = t;
        this._set = new HashSet<Type>(8);
    }

    public ListType(int size) {
        this(new ArrayList<Type>(size));
    }

    @Deprecated
    public ListType(Type... types) {
        this(new ArrayList<Type>(types.length));
        for(Type t : types) {
            if(t == Type.NONE) {
                this.hasNoneType = true;
            }
            _list.add(t);
            _set.add(t);
        }
    }

    public void add(Type t) {
        if(t == null) {
            throw new IllegalArgumentException();
        }
        if(!hasNoneType) {
            if(t == Type.NONE) {
                this.hasNoneType = true;
            }
            _list.add(t);
            _set.add(t);
        }
    }

    public Type get(int i) {
        if(i >= size()) {
            throw new IndexOutOfBoundsException("Out of index: " + i);
        }
        return _list.get(i);
    }

    public int size() {
        return _list.size();
    }

    public List<Type> asList() {
        return _list;
    }

    public Occurrence quantifier() {
        final int size = _list.size();
        if(size == 0) {
            return Occurrence.OCC_ZERO;
        } else if(size == 1) {
            return Occurrence.OCC_EXACTLY_ONE;
        } else {
            return Occurrence.OCC_ONE_OR_MORE;
        }
    }

    public boolean accepts(Type expected) {
        if(expected instanceof ListType) {
            ListType elist = (ListType) expected;
            final int maxlen = Math.max(_list.size(), elist.size());
            for(int i = 0; i < maxlen; i++) {
                Type t = _list.get(i);
                Type e = elist.get(i);
                if(!t.accepts(e)) {
                    return false;
                }
            }
            return true;
        } else {
            if(_list.size() == 1) {
                Type t = _list.get(0);
                if(t.accepts(expected)) {
                    return true;
                }
            }
            return false;
        }
    }

    public Class getJavaObjectType() {
        return List.class;
    }

    private transient Type[] _cachedUniqTypes = null;

    public Type prime() {
        if(hasNoneType) {
            return Type.NONE;
        }
        final int uniqTypeLength = _set.size();
        if(uniqTypeLength == 0) {
            return SequenceType.EMPTY;
        } else if(uniqTypeLength == 1) {
            return _list.get(0);
        } else {
            final Type[] uniqTypes;
            if(_cachedUniqTypes != null && _cachedUniqTypes.length == uniqTypeLength) {
                uniqTypes = _cachedUniqTypes;
            } else {
                uniqTypes = new Type[uniqTypeLength];
                _set.toArray(uniqTypes);
                this._cachedUniqTypes = uniqTypes;
            }
            return new ChoiceType(uniqTypes);
        }
    }

    public String toString() {
        final StringBuilder buf = new StringBuilder(64);
        buf.append('(');
        for(int i = 0; i < _list.size(); i++) {
            if(i != 0) {
                buf.append(", ");
            }
            Type t = _list.get(i);
            buf.append(t.toString());
        }
        buf.append(')');
        return buf.toString();
    }

}
