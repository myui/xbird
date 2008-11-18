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
package xbird.util.concurrent.lock;

/**
 * 
 * <DIV lang="en">
 * do {
 *    seq = readBegin();
 *    ..
 * } while(readRetry(seq));
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class SeqLock64 {

    private long counter;
    private volatile int mfence;

    private final ILock spin;

    public SeqLock64() {
        this.counter = 0;
        this.spin = new AtomicBackoffLock();
        // must perform at least one volatile write to conform to JMM
        this.mfence = 0;
    }

    @SuppressWarnings("unused")
    public long readBegin() {
        long ret = counter;
        int lfence = mfence; // lfence
        return ret;
    }

    @SuppressWarnings("unused")
    public boolean readRetry(long v) {
        int lfence = mfence; // lfence
        return (v & 1) == 1 || counter != v; // v is odd or sequence number is changed
    }

    public void writeLock() {
        spin.lock();
        ++counter;
        mfence = 0; // sfence
    }

    public void writeUnlock() {
        mfence = 0; // sfence
        counter++;
        spin.unlock();
    }

    /**
     * assumes only one writer
     */
    public void writeBegin() {
        ++counter;
        mfence = 0; // sfence
    }

    /**
     * assumes only one writer
     */
    public void writeEnd() {
        mfence = 0; // sfence
        counter++;
    }

}
