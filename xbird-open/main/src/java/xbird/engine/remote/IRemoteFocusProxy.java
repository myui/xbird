/*
 * @(#)$Id: IRemoteFocusProxy.java 3619 2008-03-26 07:23:03Z yui $
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
import java.util.Iterator;

import xbird.util.io.RemoteInputStream;
import xbird.xquery.dm.value.Item;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public interface IRemoteFocusProxy extends Remote {

    public Iterator getBaseFocus() throws RemoteException;

    public Item getContextItem() throws RemoteException;

    public int getContextPosition() throws RemoteException;

    public int getLast() throws RemoteException;

    public int getPosition() throws RemoteException;

    public boolean hasNext() throws RemoteException;

    public int incrPosition() throws RemoteException;

    public Item next() throws RemoteException;

    public void offerItem(Item item) throws RemoteException;

    public Item pollItem() throws RemoteException;

    public boolean reachedEnd() throws RemoteException;

    public void remove() throws RemoteException;

    public Item setContextItem(Item item) throws RemoteException;

    public void setContextPosition(int pos) throws RemoteException;

    public void setLast(int last) throws RemoteException;

    public void setReachedEnd(boolean end) throws RemoteException;

    public byte[] fetchBytes(int size, boolean compress) throws RemoteException;

    public RemoteInputStream fetch(int size) throws RemoteException;
    
    public RemoteInputStream asyncFetch(int size) throws RemoteException;
    
    public void close(boolean force) throws RemoteException;
}