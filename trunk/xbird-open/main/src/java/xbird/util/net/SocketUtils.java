/*
 * @(#)$Id$
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
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class SocketUtils {
    private static final Log LOG = LogFactory.getLog(SocketUtils.class);
    private static final long POLL_DELAY = 2000L;

    private SocketUtils() {}

    public static Socket openSocket(InetAddress addr, int port, int connectTimeout, long pollDelay, int maxRetry)
            throws IOException {
        SocketAddress sockAddr = new InetSocketAddress(addr, port);
        return openSocket(sockAddr, connectTimeout, pollDelay, maxRetry);
    }

    public static Socket openSocket(String host, int port, int connectTimeout, long pollDelay, int maxRetry)
            throws IOException {
        SocketAddress sockAddr = new InetSocketAddress(host, port);
        return openSocket(sockAddr, connectTimeout, pollDelay, maxRetry);
    }

    public static Socket openSocket(SocketAddress sockAddr, int connectTimeout) throws IOException {
        return openSocket(sockAddr, connectTimeout, POLL_DELAY, 1);
    }

    public static Socket openSocket(SocketAddress sockAddr, int connectTimeout, int maxRetry)
            throws IOException {
        return openSocket(sockAddr, connectTimeout, POLL_DELAY, maxRetry);
    }

    /**
     * @param connectTimeout A timeout of zero is interpreted as an infinite timeout.
     * @param pollDelay sleep in mills before retry.
     * @param maxRetry No-retry if the value is 1.
     */
    public static Socket openSocket(SocketAddress sockAddr, int connectTimeout, long pollDelay, int maxRetry)
            throws IOException {
        assert (sockAddr != null);
        assert (pollDelay >= 0) : pollDelay;
        assert (maxRetry > 0) : maxRetry;
        final Socket socket = new Socket();
        for(int i = 0; i < maxRetry; i++) {
            try {
                socket.connect(sockAddr, connectTimeout);
            } catch (SocketTimeoutException ste) {
                if(LOG.isWarnEnabled()) {
                    LOG.warn("Socket.connect to " + sockAddr + " timeout #" + (i + 1));
                }
            } catch (IOException e) {
                if(LOG.isWarnEnabled()) {
                    LOG.warn("Socket.connect to " + sockAddr + " failed #" + (i + 1), e);
                }
            } catch (Throwable e) {
                LOG.fatal("failed to connect: " + sockAddr, e);
                throw new IOException(e);
            }
            if(socket.isConnected()) {
                return socket;
            }
            if(pollDelay > 0) {
                try {
                    Thread.sleep(pollDelay);
                } catch (InterruptedException ie) {
                    ;
                }
            }
        }
        throw new InterruptedIOException("Could not connect to " + sockAddr);
    }

}
