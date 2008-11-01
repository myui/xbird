/*
 * @(#)$Id$
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
package xbird.util.struct;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class KeyValue<K extends Comparable<K> & Serializable, V extends Serializable>
        implements Comparable<KeyValue<K, V>>, Externalizable {
    private static final long serialVersionUID = -2404017844476627950L;

    @Nonnull
    private transient K key;
    private transient V value;

    public KeyValue() {}

    public KeyValue(@CheckForNull K key) {
        if(key == null) {
            throw new IllegalArgumentException();
        }
        this.key = key;
    }

    public KeyValue(@CheckForNull K key, V value) {
        if(key == null) {
            throw new IllegalArgumentException();
        }
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(@CheckForNull K key) {
        if(key == null) {
            throw new IllegalArgumentException();
        }
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public int compareTo(KeyValue<K, V> o) {
        K otherKey = o.getKey();
        return key.compareTo(otherKey);
    }

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.key = (K) in.readObject();
        this.value = (V) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(key);
        out.writeObject(value);
    }

}
