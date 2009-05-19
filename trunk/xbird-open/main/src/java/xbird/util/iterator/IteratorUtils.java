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
package xbird.util.iterator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import xbird.util.lang.ArrayUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class IteratorUtils {

    private IteratorUtils() {}

    @Nonnull
    public static <T> T[] toArray(@Nonnull final Iterator<T> itor, @Nonnull final Class<? extends T[]> newType) {
        List<T> list = toList(itor);
        return ArrayUtils.toArray(list, newType);
    }

    @Nonnull
    public static <T> List<T> toList(@Nonnull Iterator<T> itor) {
        return toList(itor, 12);
    }

    @Nonnull
    public static <T> List<T> toList(@Nonnull Iterator<T> itor, @Nonnegative int estimatedSize) {
        final List<T> list = new ArrayList<T>(estimatedSize);
        addToList(itor, list);
        return list;
    }

    public static <T> List<T> toListUnique(@Nonnull Iterator<T> itor) {
        final List<T> list = new ArrayList<T>(32);
        final Set<T> set = new HashSet<T>(32);
        while(itor.hasNext()) {
            final T e = itor.next();
            if(set.add(e)) {
                list.add(e);
            }
        }
        return list;
    }

    @Nonnull
    public static <T> Set<T> toSet(@Nonnull Iterator<T> itor) {
        final Set<T> set = new LinkedHashSet<T>();
        while(itor.hasNext()) {
            set.add(itor.next());
        }
        return set;
    }

    @SuppressWarnings("unchecked")
    public static void addToList(@Nonnull Iterator itor, @Nonnull List list) {
        while(itor.hasNext()) {
            list.add(itor.next());
        }
    }

    public static int count(@Nonnull Iterator<?> itor) {
        int sum = 0;
        while(itor.hasNext()) {
            sum++;
            itor.next();
        }
        return sum;
    }
}
