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
package xbird.engine.remote;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.util.compress.LZFOutputStream;
import xbird.util.concurrent.collections.IDisposableBlockingQueue;
import xbird.util.io.FastByteArrayInputStream;
import xbird.util.io.FastMultiByteArrayOutputStream;
import xbird.util.io.IRemoteInputStreamProxy;
import xbird.util.io.RemoteInputStream;
import xbird.util.io.RemoteInputStreamProxy;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.IFocus;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class ExchangingRemoteFocusProxy implements IRemoteFocusProxy {
    private static final Log LOG = LogFactory.getLog("xbird.rmi");
    public static final Item SENTINEL = new ValueSequence(Collections.<Item> emptyList(), DynamicContext.DUMMY);

    private final Sequence<? extends Item> _delegate;
    private final IDisposableBlockingQueue<Item> _queue;
    private final IRemoteSequenceProxy _seq;

    private final AtomicBoolean _closed = new AtomicBoolean(false);

    public ExchangingRemoteFocusProxy(Sequence<? extends Item> delegate, IDisposableBlockingQueue<Item> queue, IRemoteSequenceProxy seq) {
        this._delegate = delegate;
        this._queue = queue;
        this._seq = seq;
    }

    public RemoteInputStream fetch(int size) throws RemoteException {
        final byte[] ary = fetchBytes(size, false);
        final FastByteArrayInputStream bis = new FastByteArrayInputStream(ary);
        final IRemoteInputStreamProxy proxy = new RemoteInputStreamProxy(bis);
        final RemoteInputStream remote = new RemoteInputStream(proxy);
        return remote;
    }

    public byte[] fetchBytes(int size, boolean compress) throws RemoteException {
        if(size < 1) {
            throw new IllegalArgumentException("Illegal fetch size: " + size);
        }
        final Item[] result = new Item[size];
        int actsize = 0;
        while(actsize < size) {
            final Item e;
            try {
                e = _queue.take();
            } catch (InterruptedException ie) {
                throw new RemoteException(ie.getMessage(), ie);
            }
            if(e == SENTINEL) {
                break;
            }
            result[actsize++] = e;
        }
        final FastMultiByteArrayOutputStream bos = new FastMultiByteArrayOutputStream();
        final OutputStream os = compress ? new LZFOutputStream(bos) : bos;
        final ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(os);
            oos.writeInt(actsize);
            for(int i = 0; i < actsize; i++) {
                oos.writeObject(result[i]);
                result[i] = null;
            }
            oos.flush();
        } catch (IOException e) {
            throw new RemoteException("failed to serialize", e);
        }
        final byte[] ary = bos.toByteArray_clear();
        try {
            oos.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return ary;
    }

    public RemoteInputStream asyncFetch(int size) throws RemoteException {
        throw new UnsupportedOperationException();
    }

    public Iterator<Item> getBaseFocus() throws RemoteException {
        final IFocus<? extends Item> focus = _delegate.iterator();
        return focus.getBaseFocus();
    }

    public Item getContextItem() throws RemoteException {
        throw new IllegalStateException();
    }

    public int getContextPosition() throws RemoteException {
        throw new IllegalStateException();
    }

    public int getLast() throws RemoteException {
        throw new IllegalStateException();
    }

    public int getPosition() throws RemoteException {
        throw new IllegalStateException();
    }

    public boolean hasNext() throws RemoteException {
        throw new IllegalStateException();
    }

    public int incrPosition() throws RemoteException {
        throw new IllegalStateException();
    }

    public Item next() throws RemoteException {
        throw new IllegalStateException();
    }

    public void offerItem(Item item) throws RemoteException {
        throw new IllegalStateException();
    }

    public Item pollItem() throws RemoteException {
        throw new IllegalStateException();
    }

    public boolean reachedEnd() throws RemoteException {
        throw new IllegalStateException();
    }

    public void remove() throws RemoteException {
        throw new IllegalStateException();
    }

    public Item setContextItem(Item item) throws RemoteException {
        throw new IllegalStateException();
    }

    public void setContextPosition(int pos) throws RemoteException {
        throw new IllegalStateException();
    }

    public void setLast(int last) throws RemoteException {
        throw new IllegalStateException();
    }

    public void setReachedEnd(boolean end) throws RemoteException {
        throw new IllegalStateException();
    }

    public void close(final boolean force) throws RemoteException {
        if(_closed.compareAndSet(false, true)) {
            if(LOG.isDebugEnabled()) {
                LOG.debug("unexportObject `" + this.toString()
                        + (force ? "' explicitly." : "' by finalizer."));
            }
            try {
                UnicastRemoteObject.unexportObject(this, force);
            } catch (NoSuchObjectException e) {
                LOG.error("failed unexportObject `" + this.toString()
                        + (force ? "' explicitly." : "' by finilizer."), e);
                throw e;
            }
            _seq.close(force);
        }
    }

}
