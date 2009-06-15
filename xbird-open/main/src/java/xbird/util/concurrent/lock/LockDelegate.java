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

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class LockDelegate implements ILock {

    private final Lock delegate;
    
    public LockDelegate() {
        this.delegate = new ReentrantLock(true);
    }
    
    public LockDelegate(Lock lock) {
        if(lock == null) {
            throw new IllegalArgumentException();
        }
        this.delegate = lock;
    }

    public boolean isLocked() {
        throw new UnsupportedOperationException();
    }

    public void lock() {
        delegate.lock();
    }

    public boolean tryLock() {
        return delegate.tryLock();
    }

    public void unlock() {
        delegate.unlock();
    }

}
