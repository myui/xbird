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
package xbird.util.concurrent.cache.helpers;

import xbird.config.Settings;
import xbird.util.cache.ICacheEntry;
import xbird.util.concurrent.cache.ReplacementPolicy;
import xbird.util.concurrent.cache.algorithm.GClockCachePolicy;
import xbird.util.concurrent.cache.algorithm.LRUCachePolicy;
import xbird.util.concurrent.cache.algorithm.NbGClockCachePolicy;
import xbird.util.concurrent.cache.algorithm.NbGClockKCachePolicy;
import xbird.util.concurrent.cache.algorithm.TwoQueueCachePolicy;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class ReplacementPolicySelector {

    private static final int GCLOCK_MAX_COUNT = Integer.getInteger("xbird.gclock.max", 3);
    private static final ReplacementAlgorithm algorithm;
    static {
        final String algo = Settings.get("xbird.database.bufman.replacement_algorithm", "NBGCLOCK").toUpperCase();
        if("NBGCLOCKK".equals(algo)) {
            algorithm = ReplacementAlgorithm.NbGClockK;
        } else if("NBGCLOCK".equals(algo)) {
            algorithm = ReplacementAlgorithm.NbGClock;
        } else if("GCLOCK".equals(algo)) {
            algorithm = ReplacementAlgorithm.GClock;
        } else if("LRU".equals(algo)) {
            algorithm = ReplacementAlgorithm.LRU;
        } else if("2Q".equals(algo)) {
            algorithm = ReplacementAlgorithm.FullTwoQueue;
        } else if("BpLRu".equals(algo)) {
            algorithm = ReplacementAlgorithm.BpLRU;
        } else if("Bp2Q".equals(algo)) {
            algorithm = ReplacementAlgorithm.BpFullTwoQueue;
        } else {
            algorithm = ReplacementAlgorithm.NbGClock;
        }
    }

    private ReplacementPolicySelector() {}

    @SuppressWarnings("unchecked")
    public static <K, V> ReplacementPolicy<K, V, ICacheEntry<K, V>> provide(int capacity) {
        switch(algorithm) {
            case NbGClockK:
                return (ReplacementPolicy) new NbGClockKCachePolicy<K, V>(capacity, GCLOCK_MAX_COUNT);
            case NbGClock:
                return (ReplacementPolicy) new NbGClockCachePolicy<K, V>(capacity);
            case GClock:
                return (ReplacementPolicy) new GClockCachePolicy<K, V>(capacity);
            case LRU:
            case BpLRU:
                return (ReplacementPolicy) new LRUCachePolicy<K, V>(capacity);
            case FullTwoQueue:
            case BpFullTwoQueue:
                return (ReplacementPolicy) new TwoQueueCachePolicy<K, V>(capacity);
            default:
                throw new IllegalStateException("Illegal cache replacement policy: " + algorithm);
        }
    }

    @SuppressWarnings("unchecked")
    public static <K, V> ReplacementPolicy<K, V, ICacheEntry<K, V>> provide(int capacity, ReplacementAlgorithm algorithm) {
        switch(algorithm) {
            case NbGClockK:
                return (ReplacementPolicy) new NbGClockKCachePolicy<K, V>(capacity, GCLOCK_MAX_COUNT);
            case NbGClock:
                return (ReplacementPolicy) new NbGClockCachePolicy<K, V>(capacity);
            case GClock:
                return (ReplacementPolicy) new GClockCachePolicy<K, V>(capacity);
            case LRU:
            case BpLRU:
                return (ReplacementPolicy) new LRUCachePolicy<K, V>(capacity);
            case FullTwoQueue:
            case BpFullTwoQueue:
                return (ReplacementPolicy) new TwoQueueCachePolicy<K, V>(capacity);
            default:
                throw new IllegalStateException("Illegal cache replacement policy: " + algorithm);
        }
    }
}
