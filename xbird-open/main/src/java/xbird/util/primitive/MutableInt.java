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
public final class MutableInt extends Number implements Comparable<Number>, Externalizable {
    private static final long serialVersionUID = 5875689871048864932L;

    /** The mutable value. */
    private int _value;

    public MutableInt() {
        super();
    }

    public MutableInt(int value) {
        super();
        this._value = value;
    }

    public MutableInt(Number value) {
        super();
        this._value = value.intValue();
    }

    public Integer getValue() {
        return new Integer(_value);
    }

    public void setValue(int v) {
        this._value = v;
    }

    public void setValue(Number v) {
        this._value = v.intValue();
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
    public long longValue() {
        return _value;
    }

    @Override
    public int intValue() {
        return _value;
    }

    public int compareTo(Number other) {
        final int thisVal = _value;
        final int anotherVal = other.intValue();
        return thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof MutableInt) {
            return _value == ((MutableInt) obj).intValue();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return _value;
    }

    @Override
    public String toString() {
        return String.valueOf(_value);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this._value = in.readInt();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(_value);
    }

    //-----------------------------------------------------------------------

    public int incrementAndGet() {
        return ++_value;
    }

    public int decrementAndGet() {
        return --_value;
    }

    public int getAndIncrement() {
        return _value++;
    }

    public int getAndDecrement() {
        return _value--;
    }

    public void increment() {
        _value++;
    }

    public void decrement() {
        _value--;
    }

    public void add(int operand) {
        this._value += operand;
    }

    public void add(Number operand) {
        this._value += operand.intValue();
    }

    public int addAndGet(int x) {
        _value += x;
        return _value;
    }

    public int getAndAdd(int x) {
        int prev = _value;
        _value += x;
        return prev;
    }

    public void subtract(int operand) {
        this._value -= operand;
    }

    public void subtract(Number operand) {
        this._value -= operand.intValue();
    }

}
