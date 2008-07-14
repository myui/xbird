/*
 * @(#)$Id: RemoteFocusProxy.java 3619 2008-03-26 07:23:03Z yui $
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

import java.io.*;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.util.compress.LZFOutputStream;
import xbird.util.io.*;
import xbird.xquery.dm.value.Item;
import xbird.xquery.meta.IFocus;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class RemoteFocusProxy implements IRemoteFocusProxy {
    private static final Log LOG = LogFactory.getLog("xbird.rmi");

    private final IFocus<Item> _delegate;
    private final IRemoteSequenceProxy _seq;

    private final AtomicBoolean _closed = new AtomicBoolean(false);

    public RemoteFocusProxy(IFocus<Item> delegate, IRemoteSequenceProxy seq) {
        this._delegate = delegate;
        this._seq = seq;
    }

    public Iterator getBaseFocus() throws RemoteException {
        return _delegate.getBaseFocus();
    }

    public Item getContextItem() throws RemoteException {
        return _delegate.getContextItem();
    }

    public int getContextPosition() throws RemoteException {
        return _delegate.getContextPosition();
    }

    public int getLast() throws RemoteException {
        return _delegate.getLast();
    }

    public int getPosition() throws RemoteException {
        return _delegate.getPosition();
    }

    public boolean hasNext() throws RemoteException {
        return _delegate.hasNext();
    }

    public int incrPosition() throws RemoteException {
        return _delegate.incrPosition();
    }

    public Item next() throws RemoteException {
        return _delegate.next();
    }

    public void offerItem(Item item) throws RemoteException {
        _delegate.offerItem(item);
    }

    public Item pollItem() throws RemoteException {
        return _delegate.pollItem();
    }

    public boolean reachedEnd() throws RemoteException {
        return _delegate.reachedEnd();
    }

    public void remove() throws RemoteException {
        _delegate.remove();
    }

    public Item setContextItem(Item item) throws RemoteException {
        return _delegate.setContextItem(item);
    }

    public void setContextPosition(int pos) throws RemoteException {
        _delegate.setContextPosition(pos);
    }

    public void setLast(int last) throws RemoteException {
        _delegate.setLast(last);
    }

    public void setReachedEnd(boolean end) throws RemoteException {
        _delegate.setReachedEnd(end);
    }

    public byte[] fetchBytes(int size, boolean compress) throws RemoteException {
        if(size < 1) {
            throw new IllegalArgumentException("Illegal fetch size: " + size);
        }
        final Item[] result = new Item[size];
        final IFocus<Item> delegate = _delegate;
        int actsize = 0;
        while(actsize < size) {
            if(!delegate.hasNext()) {
                break;
            }
            result[actsize++] = delegate.next();
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

    /** fetch items in synchronous and return the stream. */
    public RemoteInputStream fetch(int size) throws RemoteException {
        byte[] ary = fetchBytes(size, false);
        FastByteArrayInputStream bis = new FastByteArrayInputStream(ary);
        IRemoteInputStreamProxy proxy = new RemoteInputStreamProxy(bis);
        RemoteInputStream remote = new RemoteInputStream(proxy);
        return remote;
    }

    public RemoteInputStream asyncFetch(final int size) throws RemoteException {
        final FastPipedOutputStream pos = new FastPipedOutputStream();
        final FastPipedInputStream pin;
        try {
            pin = new FastPipedInputStream(pos);
        } catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        }
        String curThreadName = Thread.currentThread().getName();
        Thread writeThread = new Thread("asyncFetch#" + curThreadName) {
            public void run() {
                try {
                    fetchTo(size, pos);
                } catch (IOException e) {
                    throw new IllegalStateException("failed to fetch items", e);
                }
            }
        };
        writeThread.start();
        IRemoteInputStreamProxy proxy = new RemoteInputStreamProxy(pin);
        RemoteInputStream remote = new RemoteInputStream(proxy);
        // wait for the first item is obtained
        synchronized(writeThread) {
            try {
                writeThread.wait();
            } catch (InterruptedException e) {
            }
        }
        return remote;
    }

    private void fetchTo(int atleast, OutputStream os) throws IOException {
        if(atleast < 1) {
            throw new IllegalArgumentException("Illegal fetch size: " + atleast);
        }
        final ObjectOutputStream oos = new ObjectOutputStream(os);
        final IFocus<Item> delegate = _delegate;
        for(int i = 0;; i++) {
            if(!delegate.hasNext()) {
                oos.writeBoolean(false /* hasNext */);
                if(i == 0) {
                    oos.flush();
                    synchronized(this) {
                        notify();
                    }
                }
                break;
            }
            Item result = delegate.next();
            oos.writeBoolean(true /* hasNext */);
            oos.writeObject(result);
            if(i == atleast) {
                synchronized(this) {
                    notify();
                }
                oos.flush(); // enforce send the first item as soon as possible
            }
        }
        oos.flush();
        oos.close();
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
            _delegate.closeQuietly();
        }
    }

}
