/*
 * @(#)$Id: IRemoteSequenceProxy.java 3619 2008-03-26 07:23:03Z yui $
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
public interface IRemoteSequenceProxy extends Remote {

    public Sequence<? extends Item> atomize(DynamicContext dynEnv) throws RemoteException;

    public Type getType() throws RemoteException;

    public boolean isEmpty() throws RemoteException;

    public IFocus<Item> iterator() throws RemoteException;

    public List<Item> materialize() throws RemoteException;

    public boolean next(IFocus focus) throws XQueryException, RemoteException;
    
    public void close(boolean force) throws RemoteException;

}