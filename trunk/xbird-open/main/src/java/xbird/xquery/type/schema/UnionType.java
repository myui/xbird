/*
 * @(#)$Id: UnionType.java 3619 2008-03-26 07:23:03Z yui $
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

import java.util.ArrayList;
import java.util.List;

import xbird.xquery.type.ChoiceType;
import xbird.xquery.type.Type;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class UnionType extends Type implements SchemaType {
    private static final long serialVersionUID = -8163291613872248589L;
    
    private final List<Type> _list;
    
    public UnionType(int size) {
        this._list = new ArrayList<Type>(size);
    }

    public UnionType(Type... types) {
        this._list = new ArrayList<Type>(types.length);
        for (Type t : types) {
            _list.add(t);
        }
    }

    public void add(Type t) {
        assert (t != null);
        _list.add(t);
    }
    
    public boolean accepts(Type expected) {
        return false;
    }

    public Class getJavaObjectType() {
        return null;
    }

    public Occurrence quantifier() {
        return Occurrence.OCC_EXACTLY_ONE;  // FIXME
    }

    public Type prime() {
        return new ChoiceType(_list);
    }
    
    public String toString() {
        final StringBuilder buf = new StringBuilder(64);
        buf.append('(');
        for (int i=0; i < _list.size(); i++) {
            if (i != 0) {
                buf.append(" & ");
            }
            Type t = _list.get(i);
            buf.append(t.toString());
        }
        buf.append(')');
        return buf.toString();
    }

}
