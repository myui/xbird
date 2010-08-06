/*
 * @(#)$Id: Pair.java 2978 2007-12-29 09:16:30Z yui $
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

import java.io.Serializable;

import xbird.util.hashes.HashUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class MutableLongPair implements Serializable {
    private static final long serialVersionUID = 962105527378127441L;

    public final long first;
    public final long second;

    private final int hashcode;

    public MutableLongPair(final long x, final long y) {
        this.first = x;
        this.second = y;
        this.hashcode = HashUtils.hashCode(x) ^ HashUtils.hashCode(y);
    }

    public long getFirst() {
        return first;
    }

    public long getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        if(!(obj instanceof MutableLongPair)) {
            return false;
        }
        MutableLongPair other = (MutableLongPair) obj;
        return /* other.hashcode == hashcode && */other.second == second && other.first == first;
    }

    @Override
    public int hashCode() {
        return hashcode;
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append(first);
        buf.append(',');
        buf.append(second);
        return buf.toString();
    }

}
