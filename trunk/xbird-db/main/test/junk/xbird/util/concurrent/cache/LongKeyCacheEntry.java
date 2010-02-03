/*
 * @(#)$Id: codetemplate_xbird.xml 943 2006-09-13 07:03:37Z yui $
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
package xbird.util.concurrent.cache;

import java.io.*;

import xbird.util.lang.Primitives;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class LongKeyCacheEntry<V> implements ICacheEntry<Long, V>, Externalizable {
    private static final long serialVersionUID = -5019377628831346450L;

    protected/* final */long _key;
    protected/* final */V _value;
    private int _hash;

    public LongKeyCacheEntry() {}

    public LongKeyCacheEntry(long key, V value) {
        this._key = key;
        this._value = value;
        this._hash = Primitives.hashCode(key);
    }

    public Long getKey() {
        return _key;
    }

    public long getKeyLong() {
        return _key;
    }

    public V getValue() {
        return _value;
    }

    public void setValue(V newValue) {
        this._value = newValue;
    }

    @Override
    public int hashCode() {
        return _hash;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        if(obj instanceof LongKeyCacheEntry) {
            final LongKeyCacheEntry e = (LongKeyCacheEntry) obj;
            return e.getValue() == _value && e.getKeyLong() == _key;
        }
        return false;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this._key = in.readInt();
        this._value = (V) in.readObject();
        this._hash = in.readInt();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(_key);
        out.writeObject(_value);
        out.writeInt(_hash);
    }

}
