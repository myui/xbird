/*
 * @(#)$Id: codetemplate_xbird.xml 943 2006-09-13 07:03:37Z yui $
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
package xbird.util.net;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.rmi.server.RMIServerSocketFactory;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class TimeoutServerSocketFactory implements RMIServerSocketFactory, Serializable {
    private static final long serialVersionUID = 5414912989457395336L;

    private final int timeout;
    private int backlog = 200;
    private transient InetAddress bindAddress = null;

    /**
     * @param timeout timeout in mills
     */
    public TimeoutServerSocketFactory(int timeout) {
        this.timeout = timeout;
    }

    public ServerSocket createServerSocket(int port) throws IOException {
        if(timeout == 0) {
            return new ServerSocket(port, backlog, bindAddress);
        }
        return new TimeoutServerSocket(timeout, port, backlog, bindAddress);
    }

    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    public void setBindAddress(InetAddress bindAddress) {
        this.bindAddress = bindAddress;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TimeoutServerSocketFactory;
    }

    @Override
    public int hashCode() {
        return getClass().getName().hashCode();
    }
}
