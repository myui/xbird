/*
 * @(#)$Id: RemoteFocus.java 3619 2008-03-26 07:23:03Z yui $
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
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.engine.request.QueryRequest.FetchMethod;
import xbird.util.compress.LZFInputStream;
import xbird.util.io.FastBufferedInputStream;
import xbird.util.io.FastByteArrayInputStream;
import xbird.util.io.RemoteInputStream;
import xbird.xquery.XQRTException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.sequence.SingleCollection;
import xbird.xquery.meta.IFocus;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class RemoteFocus implements IFocus<Item>, Externalizable {
    private static final long serialVersionUID = 6866110139682464788L;
    private static final Log LOG = LogFactory.getLog(RemoteFocus.class);

    public static final int DEFAULT_FETCH_SIZE = 256;
    public static final float DEFAULT_FETCH_GROWFACTOR = 1.3f;

    //--------------------------------------------
    // shipped stuffs

    private IRemoteFocusProxy _proxy;
    private int _fetchSize;
    private float _fetchGrow;
    /** fetch-size watermark to prevent fetch-size growing */
    private int _fetchWatermark;
    private FetchMethod _fetchMethod;

    //--------------------------------------------
    // prefetch items

    private final Queue<Item> _fetchedQueue;
    private boolean _nomoreFetch = false;

    //--------------------------------------------
    // Formal Semantics variables    

    private Item _citem = null;
    private int _cpos = 0;

    //-------------------------------------------
    // auxiliary items

    private boolean _reachedEnd = false;
    private int _position = 0;
    private int _last = -1;

    private final AtomicBoolean _closed = new AtomicBoolean(false);

    public RemoteFocus() {//Externalizable
        this._fetchedQueue = new LinkedList<Item>();
    }

    public RemoteFocus(IRemoteFocusProxy proxy, int fetchSize, float fetchGrow, FetchMethod fetchMethod) {
        if(fetchSize < 1) {
            throw new IllegalArgumentException("Illegal fetch size: " + fetchSize);
        }
        this._proxy = proxy;
        this._fetchSize = fetchSize;
        this._fetchGrow = fetchGrow;
        this._fetchWatermark = (int) (fetchSize * Math.pow(fetchGrow, 19)); // 256*1.3^20=48652.7073, 256*1.3^19=37425.1594
        this._fetchedQueue = new LinkedList<Item>();
        this._fetchMethod = fetchMethod;
    }

    public IRemoteFocusProxy getProxy() {
        return _proxy;
    }

    public <T extends Item> Iterator<T> getBaseFocus() {
        try {
            return _proxy.getBaseFocus();
        } catch (RemoteException e) {
            throw new XQRemoteException(e);
        }
    }

    public boolean hasNext() {
        if(_reachedEnd) {
            return false;
        }
        if(!_fetchedQueue.isEmpty()) {
            return true;
        }
        if(_nomoreFetch) {
            this._reachedEnd = true;
            close(true);
            return false;
        }
        int fetchedLength = fetch();
        if(fetchedLength == 0) {
            this._reachedEnd = true;
            close(true);
            return false;
        }
        return true;
    }

    public Item next() {
        if(_reachedEnd) {
            return null;
        }
        int rest = _fetchedQueue.size();
        if(rest > 0) {
            Item head = _fetchedQueue.poll();
            this._citem = head;
            ++_cpos;
            return head;
        }
        if(_nomoreFetch) {
            this._reachedEnd = true;
            close(true);
            return null;
        }
        int fetchedLength = fetch();
        if(fetchedLength == 0) {
            this._reachedEnd = true;
            close(true);
            return null;
        }
        Item head = _fetchedQueue.peek();
        this._citem = head;
        ++_cpos;
        return head;
    }

    private int fetch() {
        final int fetched;
        switch(_fetchMethod) {
            case syncStream:
                fetched = fetchSyncStream();
                break;
            case bytes:
                fetched = fetchBytes(false);
                break;
            case compressed_bytes:
                fetched = fetchBytes(true);
                break;
            case asyncStream: // TODO FetchMethod.asyncStream
                throw new UnsupportedOperationException("FetchMethod.asyncStream is not supported yet.");
            default:
                throw new IllegalStateException("Illegal fetch method: " + _fetchMethod);
        }
        if(fetched < _fetchSize) {
            this._nomoreFetch = true;
        } else if(_fetchSize < _fetchWatermark) {
            this._fetchSize = (int) (_fetchSize * _fetchGrow);
        }
        if(LOG.isDebugEnabled()) {
            LOG.debug("fetched " + fetched + " items");
        }
        return fetched;
    }

    private int fetchSyncStream() {
        final RemoteInputStream remoteIs;
        try {
            remoteIs = _proxy.fetch(_fetchSize);
        } catch (RemoteException e) {
            throw new XQRemoteException(e);
        }
        final FastBufferedInputStream bufferedIs = new FastBufferedInputStream(remoteIs);
        final int fetched = fetchInternal(bufferedIs);
        return fetched;
    }

    @Deprecated
    private int fetchAsyncStream() {
        final RemoteInputStream remoteIs;
        try {
            remoteIs = _proxy.asyncFetch(_fetchSize);
        } catch (RemoteException e) {
            throw new XQRemoteException(e);
        }
        final FastBufferedInputStream bufferedIs = new FastBufferedInputStream(remoteIs);
        final ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(bufferedIs);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        int count = 0;
        try {
            while(true) {
                boolean hasmore = ois.readBoolean();
                if(!hasmore) {
                    break;
                }
                ++count;
                Object ro = ois.readObject();
                Item fetched = (Item) ro;
                _fetchedQueue.offer(fetched);
                if(count == _fetchSize) {
                    break;
                }
            }
        } catch (IOException ioe) {
            throw new XQRTException("failed to deserialize the fetched items", ioe);
        } catch (ClassNotFoundException cnf) {
            throw new XQRTException("failed to deserialize the fetched items", cnf);
        }
        return count;
    }

    private int fetchBytes(boolean compressed) {
        assert (!_nomoreFetch);
        assert (_fetchedQueue.isEmpty()) : _fetchedQueue;
        final byte[] fetchedData;
        try {
            fetchedData = _proxy.fetchBytes(_fetchSize, compressed);
        } catch (RemoteException e) {
            throw new XQRemoteException(e);
        }
        final FastByteArrayInputStream fis = new FastByteArrayInputStream(fetchedData);
        final InputStream is;
        try {
            is = compressed ? new LZFInputStream(fis) : fis;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        int fetched = fetchInternal(is);
        return fetched;
    }

    private int fetchInternal(InputStream is) {
        final ObjectInputStream ois;
        final int fetchedLength;
        try {
            ois = new ObjectInputStream(is);
            fetchedLength = ois.readInt();
        } catch (IOException ioe) {
            throw new XQRTException("failed to deserialize the fetched items", ioe);
        }
        try {
            for(int i = 0; i < fetchedLength; i++) {
                Object ro = ois.readObject();
                Item fetched = (Item) ro;
                _fetchedQueue.offer(fetched);
            }
        } catch (IOException ioe) {
            throw new XQRTException("failed to deserialize the fetched items", ioe);
        } catch (ClassNotFoundException cnf) {
            throw new XQRTException("failed to deserialize the fetched items", cnf);
        }
        return fetchedLength;
    }

    public void offerItem(Item item) {
        _fetchedQueue.add(item);
    }

    public Item pollItem() {
        return _fetchedQueue.poll();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public Item getContextItem() {
        return _citem;
    }

    public Item setContextItem(Item item) {
        assert (item != null);
        if(item instanceof SingleCollection) {
            final IFocus<Item> focus = item.iterator();
            item = focus.next();
            assert (!focus.hasNext());
        }
        Item prev = _citem;
        this._citem = item;
        return prev;
    }

    public int getContextPosition() {
        return _cpos;
    }

    public void setContextPosition(int pos) {
        this._cpos = pos;
    }

    public boolean reachedEnd() {
        return _reachedEnd;
    }

    public void setReachedEnd(boolean end) {
        this._reachedEnd = end;
    }

    public int getPosition() {
        return _position;
    }

    public int incrPosition() {
        return ++_position;
    }

    public int getLast() {
        return _last;
    }

    public void setLast(int last) {
        this._last = last;
    }

    public final RemoteFocus iterator() {
        return this;
    }

    public void close() {
        close(true);
    }

    public void closeQuietly() {
        close(true);
    }

    private void close(final boolean force) {
        if(_closed.compareAndSet(false, true)) {
            final IRemoteFocusProxy proxy = _proxy;
            if(proxy != null) {
                try {
                    proxy.close(force);
                } catch (RemoteException e) {
                    LOG.warn("failed terminating remote focus: " + proxy, e);
                } finally {
                    this._proxy = null;
                }
            }
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(_proxy);
        out.writeInt(_fetchSize);
        out.writeFloat(_fetchGrow);
        out.writeInt(_fetchWatermark);
        out.writeObject(_fetchMethod);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this._proxy = (IRemoteFocusProxy) in.readObject();
        this._fetchSize = in.readInt();
        this._fetchGrow = in.readFloat();
        this._fetchWatermark = in.readInt();
        this._fetchMethod = (FetchMethod) in.readObject();
    }

}
