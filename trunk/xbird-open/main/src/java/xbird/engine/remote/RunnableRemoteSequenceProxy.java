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

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import xbird.engine.request.QueryRequest;
import xbird.util.concurrent.collections.BoundedTransferQueue;
import xbird.util.concurrent.collections.DisposableBlockingQueue;
import xbird.util.concurrent.collections.IDisposableBlockingQueue;
import xbird.util.net.TimeoutSocketProdiver;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.meta.IFocus;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class RunnableRemoteSequenceProxy extends RemoteSequenceProxy implements Runnable, Remote {
    private static final long serialVersionUID = 4385607345013884943L;
    public static final int EXCHANGING_QUEUE_SIZE = 2048;

    private final QueryRequest _request;
    private final IDisposableBlockingQueue<Item> _exqueue;

    public RunnableRemoteSequenceProxy(Sequence<Item> delegate, QueryRequest request) {
        super(delegate, request);
        this._request = request;
        this._exqueue = DisposableBlockingQueue.of(new BoundedTransferQueue<Item>(EXCHANGING_QUEUE_SIZE), ExchangingRemoteFocusProxy.SENTINEL);
    }

    public RunnableRemoteSequenceProxy(Sequence<Item> delegate) {
        super(delegate);
        this._request = null;
        this._exqueue = DisposableBlockingQueue.of(new BoundedTransferQueue<Item>(EXCHANGING_QUEUE_SIZE), ExchangingRemoteFocusProxy.SENTINEL);
    }

    @Override
    public RemoteFocus iterator() throws RemoteException {
        final IRemoteFocusProxy proxy = new ExchangingRemoteFocusProxy(_delegate, _exqueue, this);
        UnicastRemoteObject.exportObject(proxy, 0, TimeoutSocketProdiver.createClientSocketFactory(), TimeoutSocketProdiver.createServerSocketFactory());
        return new RemoteFocus(proxy, _fetchSize, _fetchGrow, _fetchMethod);
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
                throw new IllegalStateException("failed processing request: " + _request, ie);
            }
        }
        itor.closeQuietly();
        try {
            exqueue.put(ExchangingRemoteFocusProxy.SENTINEL);
        } catch (InterruptedException ie) {
            throw new IllegalStateException(ie);
        }
    }

    @Override
    public void close(boolean force) throws RemoteException {
        _exqueue.dispose();
        super.close(force);
    }

}
