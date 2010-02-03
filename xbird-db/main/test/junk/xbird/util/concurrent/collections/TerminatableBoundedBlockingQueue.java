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
package xbird.util.concurrent.collections;

import java.util.Collection;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class TerminatableBoundedBlockingQueue<E> extends ConcurrentLinkedBoundedBlockingQueue<E>
        implements TerminatableBlockingQueue<E> {
    private static final long serialVersionUID = -1241141586469010729L;

    private volatile boolean closed = false;

    public TerminatableBoundedBlockingQueue(int capacity) {
        super(capacity);
    }

    public TerminatableBoundedBlockingQueue(Collection<? extends E> c, int addtionalCapacity) {
        super(c, addtionalCapacity);
    }

    public void close() {
        close(false);
    }

    public void close(boolean clear) {
        if(!closed) {
            this.closed = true;
            if(clear) {
                poll(); // because put() may be blocked
            }
        }
    }

    public boolean isClosed() {
        return closed;
    }

}
