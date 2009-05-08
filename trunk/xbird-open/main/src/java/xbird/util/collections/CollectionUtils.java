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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class CollectionUtils {

    private CollectionUtils() {}

    public static <T> Collection<T> eliminateDuplication(List<T> list, boolean equalsByIdentity) {
        if(list.size() < 12) {
            return _eliminateDuplicationForSmall(list, equalsByIdentity);
        } else {
            return _eliminateDuplication(list, equalsByIdentity);
        }
    }

    public static <T> Set<T> asSet(T[] array) {
        final Set<T> set = new HashSet<T>(array.length);
        for(T e : array) {
            set.add(e);
        }
        return set;
    }

    private static <T> List<T> _eliminateDuplicationForSmall(final List<T> list, final boolean equalsByIdentity) {
        final int size = list.size();
        final List<T> newList;
        if(equalsByIdentity) {
            newList = new TinyIdentityList<T>(size);
            for(T e : list) {
                newList.add(e);
            }
        } else {
            newList = new ArrayList<T>(size);
            for(T e : list) {
                if(!newList.contains(e)) {
                    newList.add(e);
                }
            }
        }
        return newList;
    }

    private static <T> Set<T> _eliminateDuplication(final List<T> list, final boolean equalsByIdentity) {
        final int size = Math.max(list.size() / 10, 32);
        final Set<T> newSet = equalsByIdentity ? new IdentityHashSet<T>(size)
                : new HashSet<T>(size);
        for(T e : list) {
            newSet.add(e);
        }
        return newSet;
    }

    public static <T> T getFirst(final Collection<T> col) {
        for(T e : col) {
            return e;
        }
        return null;
    }

}
