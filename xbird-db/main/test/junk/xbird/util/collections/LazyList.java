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
package xbird.util.collections;

import java.util.List;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class LazyList<T> extends ListDelegate<T> {

    private final Factory<T> _factory;

    public LazyList(List<T> delegate, Factory<T> factory) {
        super(delegate);
        if(factory == null) {
            throw new IllegalArgumentException();
        }
        this._factory = factory;
    }

    @Override
    public T get(int index) {
        final List<T> list = getList();
        final int size = list.size();
        if(index < size) {
            T object = list.get(index);
            if(object == null) {
                object = _factory.create();
                list.set(index, object);
                return object;
            } else {
                return object;
            }
        } else {
            for(int i = size; i < index; i++) {
                list.add(null);
            }
            T object = _factory.create();
            list.add(object);
            return object;
        }
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        List<T> sub = getList().subList(fromIndex, toIndex);
        return new LazyList<T>(sub, _factory);
    }

    public static <T> List<T> decorate(List<T> list, Factory<T> factory) {
        return new LazyList<T>(list, factory);
    }

}
