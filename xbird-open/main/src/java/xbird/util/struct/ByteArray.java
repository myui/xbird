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
import java.util.Arrays;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import xbird.util.io.IOUtils;
import xbird.util.lang.ArrayUtils;
import xbird.util.primitives.Primitives;
import xbird.util.string.StringUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class ByteArray implements Externalizable, Comparable<Object> {
    private static final long serialVersionUID = 6647627881820721867L;

    @Nonnull
    private byte[] bytea;

    public ByteArray() {}//Externalizable

    public ByteArray(@CheckForNull final byte[] data) {
        if(data == null) {
            throw new IllegalArgumentException();
        }
        this.bytea = data;
    }

    public ByteArray(@CheckForNull String data) {
        if(data == null) {
            throw new IllegalArgumentException();
        }
        this.bytea = StringUtils.getBytes(data);
    }

    public ByteArray(int data) {
        this.bytea = Primitives.toBytes(data);
    }

    public ByteArray(long data) {
        this.bytea = Primitives.toBytes(data);
    }

    public ByteArray(float data) {
        this.bytea = Primitives.toBytes(data);
    }

    public ByteArray(double data) {
        this.bytea = Primitives.toBytes(data);
    }

    public ByteArray(@CheckForNull char[] data) {
        if(data == null) {
            throw new IllegalArgumentException();
        }
        this.bytea = Primitives.toBytes(data);
    }

    public byte[] getInternalArray() {
        return bytea;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytea);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        if(obj instanceof ByteArray) {
            ByteArray rhs = (ByteArray) obj;
            return Arrays.equals(bytea, rhs.bytea);
        }
        return false;
    }

    @Override
    public String toString() {
        return Arrays.toString(bytea);
    }

    public int compareTo(Object o) {
        if(o == this) {
            return 0;
        }
        if(o instanceof ByteArray) {
            ByteArray rhs = (ByteArray) o;
            return ArrayUtils.compareTo(bytea, rhs.bytea);
        }
        throw new IllegalArgumentException("Uncomparable: " + o.getClass().getName());
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.bytea = IOUtils.readBytes(in);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        IOUtils.writeBytes(bytea, out);
    }

}
