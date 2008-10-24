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
package xbird.xquery.dm.value.sequence;

import java.io.IOException;
import java.util.Collections;
import java.util.NoSuchElementException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.util.concurrent.collections.BoundedTransferQueue;
import xbird.util.concurrent.collections.DisposableBlockingQueue;
import xbird.util.concurrent.collections.IDisposableBlockingQueue;
import xbird.util.lang.PrintUtils;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.AbstractSequence;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.meta.AbstractFocus;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.IFocus;
import xbird.xquery.type.Type;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class IncrEvalSequence extends AbstractSequence<Item> implements Runnable {
    private static final long serialVersionUID = 1542706549802213098L;
    private static final Log LOG = LogFactory.getLog(IncrEvalSequence.class);
    public static final Item SENTINEL = new ValueSequence(Collections.<Item> emptyList(), DynamicContext.DUMMY);

    private final Sequence<Item> _delegate;
    private final IDisposableBlockingQueue<Item> _exqueue;

    @SuppressWarnings("unchecked")
    public IncrEvalSequence(Sequence delegate, int stockSize, DynamicContext dynEnv) {
        super(dynEnv);
        this._delegate = delegate;
        this._exqueue = DisposableBlockingQueue.of(new BoundedTransferQueue<Item>(stockSize), SENTINEL);
    }

    @Override
    public IncrEvalFocus iterator() {
        return new IncrEvalFocus(_exqueue);
    }

    public boolean next(IFocus<Item> focus) throws XQueryException {
        return _delegate.next(focus);
    }

    public Type getType() {
        return _delegate.getType();
    }

    public void run() {
        compute();
    }

    protected final void compute() {
        final IDisposableBlockingQueue<Item> exqueue = _exqueue;
        final IFocus<Item> itor = _delegate.iterator();
        for(Item e : itor) {
            try {
                if(!exqueue.putIfAvailable(e)) {
                    itor.closeQuietly();
                    return;
                }
            } catch (InterruptedException ie) {
                itor.closeQuietly();
                LOG.warn(PrintUtils.prettyPrintStackTrace(ie));
            }
        }
        itor.closeQuietly();
        try {
            exqueue.put(SENTINEL);
        } catch (InterruptedException ie) {
            LOG.warn(PrintUtils.prettyPrintStackTrace(ie));
        }
    }

    private static final class IncrEvalFocus extends AbstractFocus<Item> {

        final IDisposableBlockingQueue<Item> exqueue_;
        Item seeked = null;

        public IncrEvalFocus(IDisposableBlockingQueue<Item> exqueue) {
            super();
            this.exqueue_ = exqueue;
        }

        public boolean hasNext() {
            if(seeked != null) {
                return true;
            }
            final Item nextItem = emurateNext();
            if(nextItem != null) {
                this.seeked = nextItem;
                return true;
            } else {
                return false;
            }
        }

        private Item emurateNext() {
            final Item item;
            try {
                item = exqueue_.take();
            } catch (InterruptedException e) {
                LOG.error(PrintUtils.prettyPrintStackTrace(e));
                throw new IllegalStateException(e);
            }
            return item == null ? SENTINEL : item;
        }

        public Item next() {
            if(seeked != null) {
                Item curr = seeked;
                this.seeked = null;
                this._citem = curr;
                _cpos++;
                return curr;
            }
            final Item next = emurateNext();
            if(next == null) {
                throw new NoSuchElementException();
            }
            this._citem = next;
            _cpos++;
            return next;
        }

        public void close() throws IOException {
            exqueue_.dispose();
        }
    }

}
