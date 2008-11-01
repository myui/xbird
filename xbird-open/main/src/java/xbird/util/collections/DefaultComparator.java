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

import java.io.Serializable;
import java.util.Comparator;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class DefaultComparator<E> implements Comparator<E>, Serializable {
    private static final long serialVersionUID = -8112728882225896150L;

    public DefaultComparator() {}

    @SuppressWarnings("unchecked")
    public int compare(E o1, E o2) {
        if(o1 instanceof Comparable) {
            final Comparable c1 = (Comparable) o1;
            return c1.compareTo(o2);
        }
        final int h1 = o1.hashCode();
        final int h2 = o2.hashCode();
        return (h1 == h2) ? 0 : ((h1 < h2) ? -1 : 1);
    }

}