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
package xbird.util.struct;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class Candidates<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = -2455874999467067461L;

    private final T _primary;
    private final Set<T> _substitutes;

    public Candidates(@CheckForNull T primary, T... substitutes) {
        if(primary == null) {
            throw new IllegalArgumentException();
        }
        this._primary = primary;
        final Set<T> m = new HashSet<T>(substitutes.length);
        for(T e : substitutes) {
            m.add(e);
        }
        this._substitutes = m;
    }

    public Candidates(@CheckForNull T primary, @Nonnull List<T> substitutes) {
        if(primary == null) {
            throw new IllegalArgumentException();
        }
        if(substitutes == null) {
            throw new IllegalArgumentException();
        }
        this._primary = primary;
        final Set<T> m = new HashSet<T>(substitutes.size());
        for(T e : substitutes) {
            m.add(e);
        }
        this._substitutes = m;
    }

    public T getPrimary() {
        return _primary;
    }

    public Set<T> getSubstitutes() {
        return _substitutes;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Candidates) {
            final Object otherPrimary = ((Candidates) obj)._primary;
            if(_primary == otherPrimary) {
                return true;
            }
            return _primary.equals(otherPrimary);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return _primary.hashCode();
    }

}
