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
 */
public final class SimpleSpinLock implements ILock {

    private boolean busy = false;

    public SimpleSpinLock() {}

    public void lock() {
        while(true) {
            if(!busy) { // test-and-test-and-set
                synchronized(this) {
                    if(!busy) {
                        busy = true;
                        return;
                    }
                }
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

    public synchronized void unlock() {
        this.busy = false;
    }

    public synchronized boolean isLocked() {
        return busy;
    }

}
