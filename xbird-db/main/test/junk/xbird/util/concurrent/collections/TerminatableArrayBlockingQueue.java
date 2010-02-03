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
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class TerminatableArrayBlockingQueue<E> extends ArrayBlockingQueue<E>
        implements TerminatableBlockingQueue<E> {
    private static final long serialVersionUID = 2131891462259619136L;

    private volatile boolean closed = false;

    public TerminatableArrayBlockingQueue(int capacity) {
        super(capacity);
    }

    public TerminatableArrayBlockingQueue(int capacity, boolean fair) {
        super(capacity, fair);
    }

    public TerminatableArrayBlockingQueue(int capacity, boolean fair, Collection<? extends E> c) {
        super(capacity, fair, c);
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
