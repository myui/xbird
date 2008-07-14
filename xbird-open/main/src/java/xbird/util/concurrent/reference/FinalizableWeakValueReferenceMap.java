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
public final class FinalizableWeakValueReferenceMap<K, V> extends ReferenceMap<K, V> {
    private static final long serialVersionUID = 7988328921403486657L;

    private final ReferentFinalizer<K, V> _finalizer;

    public FinalizableWeakValueReferenceMap(ConcurrentMap<K, V> delegate, ReferentFinalizer<K, V> finalizer, ReferenceType keyReferenceType) {
        super(delegate, keyReferenceType, ReferenceType.WEAK);
        this._finalizer = finalizer;
    }

    /**
     * Instantiates with using <code>ReferenceType.STRONG</code> for keys.
     */
    public FinalizableWeakValueReferenceMap(ReferentFinalizer<K, V> finalizer) {
        this(ConcurrentCollectionProvider.<K, V> createConcurrentMap(16), finalizer, ReferenceType.STRONG);
    }

    @Override
    Object referenceValue(Object keyReference, V value) {
        return new FinalizableWeakValueReference(keyReference, value);
    }

    final class FinalizableWeakValueReference extends FinalizableWeakReference<V>
            implements InternalReference {
        final Object keyReference;
        final V value;

        public FinalizableWeakValueReference(Object keyReference, V value) {
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
