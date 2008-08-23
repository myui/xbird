/*
 * @(#)$Id: Pair.java 3619 2008-03-26 07:23:03Z yui $
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

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class Pair<X, Y> implements Serializable {
    private static final long serialVersionUID = 1L;

    public X first;
    public Y second;

    public Pair(X x, Y y) {
        this.first = x;
        this.second = y;
    }

    public Pair() {}

    public void set(X x, Y y) {
        this.first = x;
        this.second = y;
    }

    public X getFirst() {
        return first;
    }

    public Y getSecond() {
        return second;
    }

    public void setFirst(X first) {
        this.first = first;
    }

    public void setSecond(Y second) {
        this.second = second;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        if(obj == null || !(obj instanceof Pair)) {
            return false;
        }
        Pair trg = (Pair) obj;
        return first.equals(trg.first) && second.equals(trg.second);
    }

    @Override
    public int hashCode() {
        return first.hashCode() ^ second.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(first);
        buf.append(',');
        buf.append(second);
        return buf.toString();
    }

}
