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

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://commons.apache.org/lang/
 */
public final class MutableLong extends Number implements Comparable<Number>, Externalizable {
    private static final long serialVersionUID = 5875689871048864932L;
    public static final long INT_MIN_VALUE = 0x80000000L;

    /** The mutable value. */
    private long _value;
    private transient int _hashcode;

    public MutableLong() {
        super();
        this._value = 0L;
    }

    public MutableLong(long value) {
        super();
        this._value = value;
        this._hashcode = (int) (value ^ (value >>> 32));
    }

    public MutableLong(Number value) {
        super();
        this._value = value.longValue();
    }

    public Long getValue() {
        return new Long(_value);
    }

    public void setValue(long v) {
        this._value = v;
    }

    public void setValue(Number v) {
        this._value = v.longValue();
    }

    @Override
    public double doubleValue() {
        return _value;
    }

    @Override
    public float floatValue() {
        return _value;
    }

    @Override
    public int intValue() {
        return (int) _value;
    }

    @Override
    public long longValue() {
        return _value;
    }

    public int compareTo(Number other) {
        final long thisVal = _value;
        final long anotherVal = other.longValue();
        return thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof MutableLong) {
            return _value == ((MutableLong) obj).longValue();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return _hashcode;
    }

    @Override
    public String toString() {
        return String.valueOf(_value);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long v = in.readLong();
        this._value = v;
        this._hashcode = (int) (v ^ (v >>> 32));
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(_value);
    }

    //-----------------------------------------------------------------------

    public long incrementAndGet() {
        return ++_value;
    }

    public long decrementAndGet() {
        return --_value;
    }

    public long getAndIncrement() {
        return _value++;
    }

    public long getAndDecrement() {
        return _value--;
    }

    public void increment() {
        _value++;
    }

    public void decrement() {
        _value--;
    }

    public void add(long operand) {
        this._value += operand;
    }

    public void add(Number operand) {
        this._value += operand.longValue();
    }

    public long addAndGet(long x) {
        _value += x;
        return _value;
    }

    public long getAndAdd(long x) {
        long prev = _value;
        _value += x;
        return prev;
    }

    public void subtract(long operand) {
        this._value -= operand;
    }

    public void subtract(Number operand) {
        this._value -= operand.longValue();
    }

}
