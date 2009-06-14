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
package xbird.util.concurrent.lock;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://gee.cs.oswego.edu/dl/cpj/
 * @link http://usbing.com/books/concurrentprogramminginjava-designprinciplesandpatterns/
 */
public final class VolatileBackoffLock implements ILock {

    private final int _spinsBeforeYield;
    private final int _spinsBeforeSleep;
    private final long _initSleepTime;

    private volatile boolean busy = false;

    public VolatileBackoffLock() {
        this(100, 200, 1);
    }

    public VolatileBackoffLock(int spinsBeforeYield, int spinsBeforeSleep, long initSleepTime) {
        this._spinsBeforeSleep = spinsBeforeSleep;
        this._spinsBeforeYield = spinsBeforeYield;
        this._initSleepTime = initSleepTime;
    }

    public void lock() {
        final int spinsBeforeYield = _spinsBeforeYield;
        final int spinsBeforeSleep = _spinsBeforeSleep;
        long sleepTime = _initSleepTime;
        int spins = 0;
        while(true) {
            if(!busy) { // test-and-test-and-set
                synchronized(this) {
                    if(!busy) {
                        busy = true;
                        return;
                    }
                }
            }
            if(spins < spinsBeforeYield) { // spin phase
                ++spins;
            } else if(spins < spinsBeforeSleep) { // yield phase
                ++spins;
                Thread.yield();
            } else { // back-off phase
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }
                sleepTime = (3 * sleepTime) >> 1 + 1; // 50% is arbitrary
            }
        }
    }

    public boolean tryLock() {
        if(!busy) { // test-and-test-and-set
            synchronized(this) {
                if(!busy) {
                    busy = true;
                    return true;
                }
            }
        }
        return false;
    }

    public/* synchronized */void unlock() {
        this.busy = false;
    }

    public boolean isLocked() {
        return busy;
    }

}
