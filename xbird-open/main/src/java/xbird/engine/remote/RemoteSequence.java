/*
 * @(#)$Id: RemoteSequence.java 3619 2008-03-26 07:23:03Z yui $
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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.rmi.RemoteException;
import java.util.List;

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
public final class RemoteSequence implements Sequence<Item>, Externalizable {
    private static final long serialVersionUID = 5852737871608039403L;

    private Type _type;
    private IRemoteSequenceProxy _proxy;

    // ---------------------------------------
    // local cache entries

    private Boolean _isEmpty = null;
    //private CachedFocus _cachedFocus = null;

    public RemoteSequence() {}//for Externalizable

    public RemoteSequence(IRemoteSequenceProxy proxy, Type type) {
        if(proxy == null) {
            throw new IllegalArgumentException();
        }
        if(type == null) {
            throw new IllegalArgumentException();
        }
        this._proxy = proxy;
        this._type = type;
    }

    public Sequence<? extends Item> atomize(DynamicContext dynEnv) {
        checkOpen();
        try {
            return _proxy.atomize(dynEnv);
        } catch (RemoteException e) {
            throw new XQRemoteException(e);
        }
    }

    public Type getType() {
        return _type;
    }

    public boolean isEmpty() {
        if(_isEmpty != null) {
            return _isEmpty;
        }
        checkOpen();
        final boolean isEmpty;
        try {
            isEmpty = _proxy.isEmpty();
        } catch (RemoteException e) {
            throw new XQRemoteException(e);
        }
        this._isEmpty = isEmpty;
        return isEmpty;
    }

    public IFocus<Item> iterator() {
        //if(_cachedFocus != null) {
        //    return _cachedFocus.clone();
        //}
        checkOpen();
        final IFocus<Item> focus;
        try {
            focus = _proxy.iterator();
        } catch (RemoteException e) {
            throw new XQRemoteException(e);
        }
        //final CachedFocus cached = new CachedFocus(focus);
        //this._cachedFocus = cached;
        return focus;
    }

    public List<Item> materialize() {
        checkOpen();
        try {
            return _proxy.materialize();
        } catch (RemoteException e) {
            throw new XQRemoteException(e);
        }
    }

    public boolean next(IFocus<Item> focus) throws XQueryException {
        throw new IllegalStateException();
    }

    private void checkOpen() {
        if(_proxy == null) {
            throw new IllegalStateException("Remote connection is already closed");
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this._type = (Type) in.readObject();
        this._proxy = (IRemoteSequenceProxy) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(_type);
        out.writeObject(_proxy);
    }

    public static RemoteSequence readFrom(final ObjectInput in) throws IOException,
            ClassNotFoundException {
        final RemoteSequence remote = new RemoteSequence();
        remote.readExternal(in);
        return remote;
    }
}
