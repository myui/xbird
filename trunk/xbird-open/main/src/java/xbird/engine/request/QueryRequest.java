/*
 * @(#)$Id: QueryRequest.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.engine.request;

import java.io.*;
import java.net.URI;

import xbird.config.Settings;
import xbird.engine.Request;
import xbird.engine.remote.RemoteFocus;
import xbird.engine.remote.RemoteFocusProxy;
import xbird.util.lang.ObjectUtils;
import xbird.xquery.expr.opt.ShippedVariable;
import xbird.xquery.meta.DynamicContext;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405 AT gmail.com)
 */
public class QueryRequest extends Request {
    private static final long serialVersionUID = 7843684612588018648L;
    private static final FetchMethod DEFAULT_FETCH_METHOD = FetchMethod.appropriateMethod();

    private/* final */String _query;
    private URI _baseUri = null;

    protected int _fetchSize = RemoteFocus.DEFAULT_FETCH_SIZE;
    protected float _fetchGrow = RemoteFocus.DEFAULT_FETCH_GROWFACTOR;
    protected FetchMethod _fetchMethod = DEFAULT_FETCH_METHOD;
    protected ShippedVariable[] _varToShip = null;

    @Deprecated
    private transient DynamicContext _dynEnv = null;

    public QueryRequest() {//Externalizable        
        super();
    }

    /** Constructs <code>QueryRequest</code> with the REMOTE_SEQUENCE <code>ReturnType</code>. */
    public QueryRequest(String query) {
        this(query, ReturnType.REMOTE_SEQUENCE);
    }

    public QueryRequest(String query, ReturnType retType) {
        super(retType);
        if(query == null) {
            throw new IllegalArgumentException();
        }
        this._query = query;
    }

    public QueryRequest(String query, String id) {
        this(query, id, ReturnType.REMOTE_SEQUENCE);
    }

    public QueryRequest(String query, String id, ReturnType retType) {
        super(id, retType);
        if(query == null) {
            throw new IllegalArgumentException();
        }
        this._query = query;
    }

    @Override
    public Signature getSignature() {
        return Signature.QUERY;
    }

    public String getQuery() {
        return _query;
    }

    public URI getBaseUri() {
        return _baseUri;
    }

    public void setBaseUri(URI uri) {
        this._baseUri = uri;
    }

    public int getFetchSize() {
        return _fetchSize;
    }

    public void setFetchSize(int size) {
        this._fetchSize = size;
    }

    public float getFetchSizeGrowFactor() {
        return _fetchGrow;
    }

    public void setFetchSizeGrowFactor(float grow) {
        _fetchGrow = grow;
    }

    /**
     * The default fetch method is {@code FetchMethod#bytes}.
     * {@code FetchMethod#asyncStream} is not recommended.
     * 
     * @see RemoteFocusProxy
     * @see RemoteFocus
     */
    public enum FetchMethod {
        bytes, compressed_bytes, syncStream, @Deprecated
        asyncStream;

        FetchMethod() {}

        public static FetchMethod appropriateMethod() {
            final String m = Settings.get("xbird.remote.fetch_method", "bytes");
            if("bytes".equals(m)) {
                return bytes;
            } else if("compressed_bytes".equals(m)) {
                return compressed_bytes;
            } else if("syncStream".equals(m)) {
                return syncStream;
            } else if("asyncStream".equals(m)) {
                return asyncStream;
            } else {
                throw new IllegalStateException("Unsupported fetch method: " + m);
            }
        }

        public static FetchMethod readFrom(final ObjectInput in) throws IOException {
            final int type = in.readByte();
            switch(type) {
                case 1:
                    return bytes;
                case 2:
                    return compressed_bytes;
                case 3:
                    return syncStream;
                case 4:
                    return asyncStream;
                default:
                    throw new IllegalStateException("Illegal pattern: " + type);
            }
        }

        public void writeTo(final ObjectOutput out) throws IOException {
            switch(this) {
                case bytes:
                    out.writeByte(1);
                    break;
                case compressed_bytes:
                    out.writeByte(2);
                    break;
                case syncStream:
                    out.writeByte(3);
                    break;
                case asyncStream:
                    out.writeByte(4);
                    break;
                default:
                    throw new IllegalStateException("Unexpected: " + this);
            }
        }
    }

    public FetchMethod getFetchMethod() {
        return _fetchMethod;
    }

    public void setFetchMethod(FetchMethod method) {
        this._fetchMethod = method;
    }

    public ShippedVariable[] getShippedVariables() {
        return _varToShip;
    }

    public void setVariablesToShip(ShippedVariable... toShip) {
        this._varToShip = toShip;
    }

    @Deprecated
    public DynamicContext getDynamicContext() {
        return _dynEnv;
    }

    @Deprecated
    public void setDynamicContext(DynamicContext env) {
        this._dynEnv = env;
    }

    @Override
    public String toString() {
        return _query;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        readQuery(in);
        this._fetchMethod = FetchMethod.readFrom(in);
        final int frag = in.readByte();
        if(frag != 0) {
            if((frag & 1) == 1) {
                this._baseUri = (URI) in.readObject();
            }
            if((frag & 2) == 2) {
                this._fetchSize = in.readInt();
            }
            if((frag & 4) == 4) {
                this._fetchGrow = in.readFloat();
            }
            if((frag & 8) == 8) {
                final int varsSize = in.readInt();
                final ShippedVariable[] vars = new ShippedVariable[varsSize];
                for(int i = 0; i < varsSize; i++) {
                    vars[i] = ShippedVariable.readFrom(in);
                }
                this._varToShip = vars;
            }
        }
    }

    protected void readQuery(ObjectInput in) throws IOException, ClassNotFoundException {
        this._query = ObjectUtils.readString(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        writeQuery(out);
        _fetchMethod.writeTo(out);

        final URI baseUri = _baseUri;
        final int fetchSize = _fetchSize;
        final float fetchGrow = _fetchGrow;
        final ShippedVariable[] varToShip = _varToShip;
        int frag = 0;
        if(baseUri != null) {
            frag |= 1;
        }
        if(fetchSize != RemoteFocus.DEFAULT_FETCH_SIZE) {
            frag |= 2;
        }
        if(fetchGrow != RemoteFocus.DEFAULT_FETCH_GROWFACTOR) {
            frag |= 4;
        }
        if(varToShip != null) {
            frag |= 8;
        }
        out.writeByte(frag);
        if(frag != 0) {
            if(baseUri != null) {
                out.writeObject(baseUri);
            }
            if(fetchSize != RemoteFocus.DEFAULT_FETCH_SIZE) {
                out.writeInt(fetchSize);
            }
            if(fetchGrow != RemoteFocus.DEFAULT_FETCH_GROWFACTOR) {
                out.writeFloat(fetchGrow);
            }
            if(varToShip != null) {
                final int varsSize = varToShip.length;
                out.writeInt(varsSize);
                for(int i = 0; i < varsSize; i++) {
                    varToShip[i].writeExternal(out);
                }
            }
        }
    }

    protected void writeQuery(ObjectOutput out) throws IOException {
        assert (_query != null);
        ObjectUtils.writeString(out, _query);
    }

}
