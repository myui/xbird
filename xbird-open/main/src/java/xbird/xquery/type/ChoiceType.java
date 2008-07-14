/*
 * @(#)$Id: ChoiceType.java 3619 2008-03-26 07:23:03Z yui $
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

import java.util.ArrayList;
import java.util.List;

/**
 * A choice of item types, called a prime type.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-semantics/#jd_prime
 */
public class ChoiceType extends Type {
    private static final long serialVersionUID = 7751599145163518572L;
    private final List<Type> _types;

    public ChoiceType(List<Type> t) {
        this._types = t;
    }

    public ChoiceType(Type... type) {
        this(new ArrayList<Type>(type.length));
        combine(type);
    }

    public List<Type> combine(Type... type) {
        for(Type t : type) {
            if(!_types.contains(type)) {
                _types.add(t);
            }
        }
        return this._types;
    }

    public boolean accepts(Type expected) {
        final Type prime = expected.prime();
        if(prime == null) { // for SequenceType$Empty
            return false;
        }
        final List<Type> choice = ((ChoiceType) prime).getTypes();
        for(Type t : _types) {
            if(prime instanceof ChoiceType) {
                for(Type c : choice) {
                    if(t.accepts(c)) {
                        return true;
                    }
                }
            } else {
                if(t.accepts(prime)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Class getJavaObjectType() {
        throw new UnsupportedOperationException("getJavaObjectType() for PrimeType is not supported.");
    }

    public Occurrence quantifier() {
        return _types.isEmpty() ? Occurrence.OCC_ZERO_OR_MORE : Occurrence.OCC_ONE_OR_MORE;
    }

    public Type prime() {
        return this;
    }

    public List<Type> getTypes() {
        return _types;
    }

    public String toString() {
        final StringBuilder buf = new StringBuilder(64);
        buf.append('(');
        for(int i = 0; i < _types.size(); i++) {
            if(i != 0) {
                buf.append(" | ");
            }
            Type t = _types.get(i);
            buf.append(t.toString());
        }
        buf.append(')');
        return buf.toString();
    }

}
