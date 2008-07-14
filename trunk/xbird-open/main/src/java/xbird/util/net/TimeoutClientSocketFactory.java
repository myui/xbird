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
import java.net.Socket;
import java.rmi.server.RMIClientSocketFactory;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class TimeoutClientSocketFactory implements RMIClientSocketFactory, Serializable {
    private static final long serialVersionUID = -2167861948687915767L;

    private final int timeout;

    /**
     * @param timeout timeout in mills
     */
    public TimeoutClientSocketFactory(int timeout) {
        this.timeout = timeout;
    }

    public Socket createSocket(String host, int port) throws IOException {
        Socket s = new Socket(host, port);
        if(timeout == 0) {
            return s;
        }
        return new TimeoutSocket(s, timeout);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TimeoutClientSocketFactory;
    }

    @Override
    public int hashCode() {
        return getClass().getName().hashCode();
    }

}
