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

import java.util.ArrayList;
import java.util.Collection;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class TinyIdentityList<E> extends ArrayList<E> {
    private static final long serialVersionUID = 8005057045285560880L;

    public TinyIdentityList() {
        super();
    }

    public TinyIdentityList(Collection<? extends E> c) {
        super(c);
    }

    public TinyIdentityList(int initialCapacity) {
        super(initialCapacity);
    }

    @Override
    public int indexOf(Object o) {
        final int size = size();
        for(int i = 0; i < size; i++) {
            if(o == get(i)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        final int size = size();
        for(int i = size - 1; i >= 0; i--) {
            if(o == get(i)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean remove(Object o) {
        final int index = indexOf(o);
        if(index != -1) {
            remove(index);
            return true;
        }
        return false;
    }

}
