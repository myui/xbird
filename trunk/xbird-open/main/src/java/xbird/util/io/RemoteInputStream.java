/*
 * @(#)$Id: RemoteInputStream.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.util.io;

import java.io.*;

/**
 * 
 * <DIV lang="en">
 * Should be wrapped with {@link BufferedInputStream} at the client-side.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class RemoteInputStream extends InputStream implements Externalizable {
    private static final long serialVersionUID = 858574435617359426L;

    private IRemoteInputStreamProxy _proxy;

    public RemoteInputStream() {}
    
    public RemoteInputStream(IRemoteInputStreamProxy proxy) {
        this._proxy = proxy;
    }

    @Override
    public int read() throws IOException {
        return _proxy.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        byte[] rb = _proxy.read(len);
        if(rb == null) {
            return -1;
        }
        System.arraycopy(rb, 0, b, off, rb.length);
        return rb.length;
    }

    @Override
    public int available() throws IOException {
        return _proxy.available();
    }

    @Override
    public long skip(long n) throws IOException {
        return _proxy.skip(n);
    }

    @Override
    public void close() throws IOException {
        _proxy.close();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(_proxy);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this._proxy = (IRemoteInputStreamProxy) in.readObject();
    }

}
