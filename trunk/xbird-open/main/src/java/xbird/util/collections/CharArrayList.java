/*
 * @(#)$Id: IntArray.java 1833 2007-02-27 06:26:31Z yui $
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
package xbird.util.collections;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Dynamic Integer array.
 * <DIV lang="en">
 * This class does not provide strict equality features.
 * </DIV>
 * <DIV lang="ja">
 * </DIV>
 * 
 * @author Makoto YUI <yuin@bb.din.or.jp>
 */
public final class CharArrayList implements Serializable {
    private static final long serialVersionUID = 12356521778076725L;

    public static final int DEFAULT_CAPACITY = 12;

    /** array entity */
    private char[] data;
    private int used;

    public CharArrayList() {
        this(DEFAULT_CAPACITY);
    }

    public CharArrayList(int size) {
        this.data = new char[size];
        this.used = 0;
    }

    public CharArrayList(char[] initValues) {
        this(initValues.length);
        add(initValues);
    }

    public void add(char value) {
        if(used >= data.length) {
            expand(used + 1);
        }
        data[used++] = value;
    }

    public void add(char[] values) {
        final int needs = used + values.length;
        if(needs >= data.length) {
            expand(needs);
        }
        System.arraycopy(values, 0, data, used, values.length);
        this.used = needs;
    }

    /**
     * dynamic expansion.
     */
    private void expand(int max) {
        while(data.length < max) {
            final int len = data.length;
            char[] newArray = new char[len * 2];
            System.arraycopy(data, 0, newArray, 0, len);
            this.data = newArray;
        }
    }

    public char remove() {
        return data[--used];
    }

    public char remove(int index) {
        final char ret;
        if(index > used) {
            throw new IndexOutOfBoundsException();
        } else if(index == used) {
            ret = data[--used];
        } else { // index < used
            // removed value
            ret = data[index];
            final char[] newarray = new char[--used];
            // prefix
            System.arraycopy(data, 0, newarray, 0, index - 1);
            // appendix
            System.arraycopy(data, index + 1, newarray, index, used - index);
            // set fields.
            this.data = newarray;
        }
        return ret;
    }

    public void set(int index, char value) {
        if(index > used) {
            throw new IllegalArgumentException("Index MUST be less than \"size()\".");
        } else if(index == used) {
            ++used;
        }
        data[index] = value;
    }

    public char get(int index) {
        if(index >= used) {
            throw new IndexOutOfBoundsException();
        }
        return data[index];
    }

    public int indexOf(char key) {
        return Arrays.binarySearch(data, key);
    }

    public int size() {
        return used;
    }

    public void clear() {
        used = 0;
    }

    public char[] toArray() {
        final char[] newArray = new char[used];
        System.arraycopy(data, 0, newArray, 0, used);
        return newArray;
    }

    public char[] array() {
        return data;
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append('[');
        for(int i = 0; i < used; i++) {
            if(i != 0) {
                buf.append(", ");
            }
            buf.append(data[i]);
        }
        buf.append(']');
        return buf.toString();
    }
}