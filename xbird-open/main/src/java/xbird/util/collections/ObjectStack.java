/*
 * @(#)$Id: ObjectStack.java 3619 2008-03-26 07:23:03Z yui $
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
 *     Makoto YUI - initial porting
 */
package xbird.util.collections;

import java.io.Serializable;

/**
 * Light-weight ObjectStack implementation.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class ObjectStack implements Serializable {
    private static final long serialVersionUID = -1652603821997551249L;

    public static final int DEFAULT_SIZE = 12;

    private Object[] stack;
    private int counter = 0;

    public ObjectStack() {
        this(DEFAULT_SIZE);
    }

    public ObjectStack(int size) {
        this.stack = new Object[size];
    }

    public void push(Object value) {
        ensureCapacity(counter + 1);
        stack[counter++] = value;
    }

    public Object pop() {
        Object poped = stack[--counter];
        stack[counter] = null;
        return poped;
    }
    
    public Object get(int at) {
        if(at >= counter || at < 0) {
            throw new IllegalArgumentException("Out of index: " + at);
        }
        return stack[at];
    }

    public boolean isEmpty() {
        return counter == 0;
    }

    public int size() {
        return counter;
    }

    public Object peek() {
        return counter == 0 ? null : stack[counter - 1];
    }

    public void clear() {
        for(int i = 0; i < counter; i++) {
            stack[i] = null;
        }
        counter = 0;
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append('[');
        for(int i = 0; i < counter; i++) {
            if (i != 0) {
                buf.append(", ");
            }
            buf.append(stack[i]);
        }
        buf.append(']');
        return buf.toString();
    }

    private void ensureCapacity(int minCapacity) {
        int oldCapacity = stack.length;
        if (minCapacity > oldCapacity) {
            int newCapacity = oldCapacity * 2;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            Object[] newStack = new Object[newCapacity];
            System.arraycopy(stack, 0, newStack, 0, counter);
            this.stack = newStack;
        }
    }

}
