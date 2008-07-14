/*
 * @(#)$Id: RemoteInputStreamProxy.java 3619 2008-03-26 07:23:03Z yui $
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

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.rmi.server.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class RemoteInputStreamProxy extends UnicastRemoteObject implements IRemoteInputStreamProxy {
    private static final long serialVersionUID = 927766821399213458L;
    
    private final InputStream _delegate;

    public RemoteInputStreamProxy(InputStream delegate) throws RemoteException {
        super();
        this._delegate = delegate;
    }

    public RemoteInputStreamProxy(InputStream delegate, int port) throws RemoteException {
        super(port);
        this._delegate = delegate;
    }

    public RemoteInputStreamProxy(InputStream delegate, int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf)
            throws RemoteException {
        super(port, csf, ssf);
        this._delegate = delegate;
    }

    public int available() throws IOException, RemoteException {
        return _delegate.available();
    }

    public void close() throws IOException, RemoteException {
        _delegate.close();
        UnicastRemoteObject.unexportObject(this, true);
    }

    public int read() throws IOException, RemoteException {
        return _delegate.read();
    }

    public byte[] read(int len) throws IOException, RemoteException {
        byte[] b = new byte[len];
        int actlen = _delegate.read(b, 0, len);
        if(actlen != len) {
            byte[] nb = new byte[actlen];
            System.arraycopy(b, 0, nb, 0, actlen);
            return nb;
        } else {
            return b;
        }
    }

    public long skip(long n) throws IOException, RemoteException {
        return _delegate.skip(n);
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            this.close();
        } catch (IOException e) {
        }
    }

}
