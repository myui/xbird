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
package xbird.util.primitive;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import xbird.util.lang.HashUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class MutableString
        implements CharSequence, Externalizable, Comparable<MutableString>, Cloneable {
    private static final long serialVersionUID = -7057356394839186769L;

    private char[] value;
    private int offset, count;

    private transient int hash = -1;

    public MutableString(char[] value, int offset, int count) {
        if(offset < 0) {
            throw new StringIndexOutOfBoundsException(offset);
        }
        if(count < 0) {
            throw new StringIndexOutOfBoundsException(count);
        }
        if(offset > (value.length - count)) {
            throw new StringIndexOutOfBoundsException(offset + count);
        }
        this.value = value;
        this.offset = offset;
        this.count = count;
    }

    public MutableString(String str) {
        if(str == null) {
            throw new IllegalArgumentException("NULL string is not allowed");
        }
        this.offset = 0;
        this.count = str.length();
        this.value = new char[count];
        str.getChars(0, count, value, 0);
    }

    public MutableString(String... strs) {
        if(strs == null) {
            throw new IllegalArgumentException();
        }
        this.offset = 0;
        int counting = 0;
        int len = strs.length;
        for(int i = 0; i < len; i++) {
            counting += strs[i].length();
        }
        this.count = counting;
        this.value = new char[counting];
        for(int i = 0; i < len; i++) {
            String s = strs[i];
            if(s != null) {
                s.getChars(0, counting, value, s.length());
            }
        }
    }

    public MutableString() {// externalizable
        this.offset = 0;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.count = in.readInt();
        this.value = new char[count];
        for(int i = 0; i < count; i++) {
            value[i] = in.readChar();
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(count);
        final int last = offset + count;
        for(int i = offset; i < last; i++) {
            out.writeChar(value[i]);
        }
    }

    public char charAt(int index) {
        if((index < 0) || (index >= count)) {
            throw new StringIndexOutOfBoundsException(index);
        }
        return value[offset + index];
    }

    public int length() {
        return count;
    }

    public CharSequence subSequence(int start, int end) {
        return new MutableString(value, offset + start, offset + end);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new MutableString(value, offset, count);
    }

    @Override
    public boolean equals(Object anObject) {
        if(this == anObject) {
            return true;
        }
        if(anObject instanceof MutableString) {
            MutableString anotherString = (MutableString) anObject;
            int n = count;
            if(n == anotherString.count) {
                char v1[] = value;
                char v2[] = anotherString.value;
                int i = offset;
                int j = anotherString.offset;
                while(n-- != 0) {
                    if(v1[i++] != v2[j++])
                        return false;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        if(hash == -1) {
            hash = HashUtils.hashCode(value, offset, count);
        }
        return hash;
    }

    @Override
    public String toString() {
        return new String(value, offset, count);
    }

    public int compareTo(MutableString anotherString) {
        int len1 = count;
        int len2 = anotherString.count;
        int n = Math.min(len1, len2);
        char v1[] = value;
        char v2[] = anotherString.value;
        int i = offset;
        int j = anotherString.offset;

        if(i == j) {
            int k = i;
            int lim = n + i;
            while(k < lim) {
                char c1 = v1[k];
                char c2 = v2[k];
                if(c1 != c2) {
                    return c1 - c2;
                }
                k++;
            }
        } else {
            while(n-- != 0) {
                char c1 = v1[i++];
                char c2 = v2[j++];
                if(c1 != c2) {
                    return c1 - c2;
                }
            }
        }
        return len1 - len2;
    }
}
