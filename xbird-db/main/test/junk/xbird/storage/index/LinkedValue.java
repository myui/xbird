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
package xbird.storage.index;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import xbird.util.lang.Primitives;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class LinkedValue extends Value {

    private long nextPtr = -1L;

    public LinkedValue(Value data) {
        super();
        this._data = data._data;
        this._len = data._len;
        this._pos = data._pos;
    }

    public LinkedValue(Value data, long nextPtr) {
        this(data);
        this.nextPtr = nextPtr;
    }

    private LinkedValue(byte[] data) {
        if(data.length < 8) {
            throw new IllegalArgumentException("Illegal data len: " + data.length);
        }
        this.nextPtr = Primitives.getLong(data);
        this._data = data;
        this._pos = 8;
        this._len = data.length - 8;
    }

    public void setNextPointer(long np) {
        this.nextPtr = np;
    }

    public long getNextPointer() {
        return nextPtr;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        super.readExternal(in);
        this.nextPtr = in.readLong();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeLong(nextPtr);
    }

    public byte[] toBytes() {
        int size = 8 + _len;
        final byte[] b = new byte[size];
        Primitives.putLong(b, 0, nextPtr);
        System.arraycopy(_data, _pos, b, 8, _len);
        return b;
    }

    public static LinkedValue readFrom(byte[] b) {
        return new LinkedValue(b);
    }

}
