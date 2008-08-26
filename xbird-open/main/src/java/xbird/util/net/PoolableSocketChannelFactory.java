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
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.ByteChannel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;

import xbird.util.pool.PoolableObjectFactory;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class PoolableSocketChannelFactory
        implements PoolableObjectFactory<SocketAddress, ByteChannel> {

    private int sweepInterval = 60000;
    private int ttl = 30000;
    private int soRcvBufSize = 4096;

    private final boolean datagram;
    private final boolean blocking;

    public PoolableSocketChannelFactory(boolean datagram, boolean blocking) {
        this.datagram = datagram;
        this.blocking = blocking;
    }

    public void configure(int sweepInterval, int ttl, int receiveBufferSize) {
        this.sweepInterval = sweepInterval;
        this.ttl = ttl;
        this.soRcvBufSize = receiveBufferSize;
    }

    public ByteChannel makeObject(SocketAddress sockAddr) {
        if(datagram) {
            return createDatagramChannel(sockAddr, blocking);
        } else {
            return createSocketChannel(sockAddr, blocking, soRcvBufSize);
        }
    }

    private static SocketChannel createSocketChannel(final SocketAddress sockAddr, final boolean blocking, final int rcvbufSize) {
        final SocketChannel ch;
        try {
            ch = SocketChannel.open();
            ch.configureBlocking(blocking);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        final Socket sock = ch.socket();
        try {
            sock.setReceiveBufferSize(rcvbufSize);
            //sock.setTcpNoDelay(true);
        } catch (SocketException e) {
            throw new IllegalStateException(e);
        }
        try {
            ch.connect(sockAddr);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return ch;
    }

    private static DatagramChannel createDatagramChannel(final SocketAddress sockAddr, final boolean blocking) {
        final DatagramChannel ch;
        try {
            ch = DatagramChannel.open();
            ch.configureBlocking(blocking);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        try {
            ch.socket().setBroadcast(false);
        } catch (SocketException e) {
            throw new IllegalStateException(e);
        }
        try {
            ch.connect(sockAddr);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return ch;
    }

    public boolean validateObject(ByteChannel sock) {
        if(sock == null) {
            return false;
        }
        return sock.isOpen();
    }

    public int getSweepInterval() {
        return sweepInterval;
    }

    public int geTimeToLive() {
        return ttl;
    }

}
