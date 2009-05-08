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
package xbird.util.collections;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractList;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class ImmutableArrayList<E> extends AbstractList<E> implements Externalizable {
    private static final long serialVersionUID = -7247049431905029154L;

    private/* final */E[] elements;
    private/* final */int size;

    public ImmutableArrayList(E[] elements) {
        this.elements = elements;
        this.size = elements.length;
    }

    public E[] getInternalArray() {
        return elements;
    }

    public int size() {
        return size;
    }

    public E get(int index) {
        return elements[index];
    }

    @Override
    public E set(int index, E element) {
        final E old = elements[index];
        elements[index] = element;
        return old;
    }

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.size = in.readInt();
        final Object[] ary = new Object[size];
        for(int i = 0; i < size; i++) {
            ary[i] = in.readObject();
        }
        this.elements = (E[]) ary;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(size);
        for(E e : elements) {
            out.writeObject(e);
        }
    }

}
