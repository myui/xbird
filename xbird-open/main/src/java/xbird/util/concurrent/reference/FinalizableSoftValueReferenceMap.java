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
package xbird.util.concurrent.reference;

import java.util.concurrent.ConcurrentMap;

import xbird.util.concurrent.collections.ConcurrentCollectionProvider;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class FinalizableSoftValueReferenceMap<K, V> extends ReferenceMap<K, V> {
    private static final long serialVersionUID = 7200623989171305154L;

    private final ReferentFinalizer<K, V> _finalizer;

    public FinalizableSoftValueReferenceMap(ConcurrentMap<K, V> delegate, ReferentFinalizer<K, V> finalizer, ReferenceType keyReferenceType) {
        super(delegate, keyReferenceType, ReferenceType.SOFT);
        this._finalizer = finalizer;
    }

    /**
     * Instantiates with using <code>ReferenceType.STRONG</code> for keys.
     */
    public FinalizableSoftValueReferenceMap(ReferentFinalizer<K, V> finalizer) {
        this(ConcurrentCollectionProvider.<K, V> createConcurrentMap(16), finalizer, ReferenceType.STRONG);
    }

    @Override
    Object referenceValue(Object keyReference, V value) {
        return new FinalizableSoftValueReference(keyReference, value);
    }

    final class FinalizableSoftValueReference extends FinalizableSoftReference<V>
            implements InternalReference {
        final Object keyReference;
        final V value;

        public FinalizableSoftValueReference(Object keyReference, V value) {
            super(value);
            this.keyReference = keyReference;
            this.value = value;
        }

        public void finalizeReferent() {
            delegate.remove(keyReference, this);
            _finalizer.finalize(dereferenceKey(keyReference), value);
        }

        @Override
        public boolean equals(Object obj) {
            return referenceEquals(this, obj);
        }
    }
}
