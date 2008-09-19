/*
 * @(#)$Id: RemoteSequenceProxy.java 3619 2008-03-26 07:23:03Z yui $
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

import java.io.Serializable;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.engine.request.QueryRequest;
import xbird.engine.request.QueryRequest.FetchMethod;
import xbird.util.net.TimeoutSocketProdiver;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
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
public class RemoteSequenceProxy implements IRemoteSequenceProxy, Serializable {
    private static final Log LOG = LogFactory.getLog("xbird.rmi");
    private static final long serialVersionUID = 4914765810212489042L;

    protected final Sequence<Item> _delegate;
    protected final int _fetchSize;
    protected final float _fetchGrow;
    protected final FetchMethod _fetchMethod;

    private final AtomicBoolean _closed = new AtomicBoolean(false);

    public RemoteSequenceProxy(Sequence<Item> delegate, QueryRequest request) {
        this._delegate = delegate;
        this._fetchSize = request.getFetchSize();
        this._fetchGrow = request.getFetchSizeGrowFactor();
        this._fetchMethod = request.getFetchMethod();
    }

    protected RemoteSequenceProxy(Sequence<Item> delegate) {
        this._delegate = delegate;
        this._fetchSize = RemoteFocus.DEFAULT_FETCH_SIZE;
        this._fetchGrow = RemoteFocus.DEFAULT_FETCH_GROWFACTOR;
        this._fetchMethod = FetchMethod.appropriateMethod();
    }

    public Sequence<? extends Item> atomize(DynamicContext dynEnv) throws RemoteException {
        return _delegate.atomize(dynEnv);
    }

    public Type getType() throws RemoteException {
        return _delegate.getType();
    }

    public boolean isEmpty() throws RemoteException {
        return _delegate.isEmpty();
    }

    public RemoteFocus iterator() throws RemoteException {
        final IFocus<Item> focus = _delegate.iterator();
        final IRemoteFocusProxy proxy = new RemoteFocusProxy(focus, this);
        UnicastRemoteObject.exportObject(proxy, 0, TimeoutSocketProdiver.createClientSocketFactory(), TimeoutSocketProdiver.createServerSocketFactory());
        return new RemoteFocus(proxy, _fetchSize, _fetchGrow, _fetchMethod);
    }

    public List<Item> materialize() throws RemoteException {
        return _delegate.materialize();
    }

    public boolean next(IFocus focus) throws XQueryException, RemoteException {
        return _delegate.next(focus);
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
        }
    }

}
