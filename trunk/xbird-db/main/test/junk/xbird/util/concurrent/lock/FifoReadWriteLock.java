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

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Maurice Herlihy
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class FifoReadWriteLock implements IReadWriteLock {

    private int readAcquires; // read acquires since start
    private int readReleases; // read releses since start
    private boolean writer; // writer present?
    private final Lock metaLock; // short-term synchronization
    private final Condition condition;
    private final ILock readLock; // readers apply here
    private final ILock writeLock; // writers apply here 

    public FifoReadWriteLock() {
        readAcquires = readReleases = 0;
        writer = false;
        metaLock = new ReentrantLock();
        condition = metaLock.newCondition();
        readLock = new ReadLock();
        writeLock = new WriteLock();
    }

    public ILock readLock() {
        return readLock;
    }

    public ILock writeLock() {
        return writeLock;
    }

    private final class ReadLock implements ILock {

        ReadLock() {}

        public void lock() {
            metaLock.lock();
            try {
                readAcquires++;
                while(writer) {
                    try {
                        condition.await();
                    } catch (InterruptedException ex) {
                        // do something application-specific
                    }
                }
            } finally {
                metaLock.unlock();
            }
        }

        public void unlock() {
            metaLock.lock();
            try {
                readReleases++;
                if(readAcquires == readReleases) {
                    condition.signalAll();
                }
            } finally {
                metaLock.unlock();
            }
        }

        public boolean isLocked() {
            throw new UnsupportedOperationException();
        }
    }

    private final class WriteLock implements ILock {

        WriteLock() {}

        public void lock() {
            metaLock.lock();
            try {
                while(readAcquires != readReleases) {
                    try {
                        condition.await();
                    } catch (InterruptedException e) {
                    }
                }
                writer = true;
            } finally {
                metaLock.unlock();
            }
        }

        public void unlock() {
            writer = false;
        }

        public boolean isLocked() {
            throw new UnsupportedOperationException();
        }
    }
}
