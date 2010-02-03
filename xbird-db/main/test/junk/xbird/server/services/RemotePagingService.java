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
package xbird.server.services;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.config.Settings;
import xbird.server.ServiceException;
import xbird.storage.io.RemoteVarSegments;
import xbird.storage.io.VarSegments.IDescriptor;
import xbird.util.concurrent.reference.FinalizableSoftValueReferenceMap;
import xbird.util.concurrent.reference.ReferenceMap;
import xbird.util.concurrent.reference.ReferenceType;
import xbird.util.concurrent.reference.ReferentFinalizer;
import xbird.util.io.IOUtils;
import xbird.util.lang.SystemUtils;
import xbird.util.nio.NIOUtils;
import xbird.util.nio.RemoteMemoryMappedFile;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class RemotePagingService extends ServiceBase {
    private static final Log LOG = LogFactory.getLog(RemotePagingService.class);

    public static final int MAX_COMMAND_BUFLEN = 1024;
    public static final int COMMAND_READ = 1;
    public static final int COMMAND_TRACK_READ = 2;
    public static final String SRV_NAME = "RemotePaging";
    public static final int PORT = Integer.parseInt(Settings.get("xbird.remote.paging_serv.port", "8900"));
    private static final int SND_BUFSIZE = 16 * 1024; // 16k

    private final FinalizableSoftValueReferenceMap<String, FileChannel> _fdCacheMap;
    private final ReferenceMap<String, IDescriptor> _directoryCache;
    private final ByteBuffer _sndBufSegm;
    private final ByteBuffer _sndBufDTM;

    public RemotePagingService() {
        super(SRV_NAME);
        this._fdCacheMap = new FinalizableSoftValueReferenceMap<String, FileChannel>(new ReferentFinalizer<FileChannel>() {
            public void finalize(FileChannel reclaimed) {
                IOUtils.closeQuietly(reclaimed);
            }
        });
        this._directoryCache = new ReferenceMap<String, IDescriptor>(ReferenceType.STRONG, ReferenceType.SOFT);
        if(SystemUtils.isSendfileSupported()) {
            this._sndBufSegm = null;
            this._sndBufDTM = null;
        } else {
            this._sndBufSegm = ByteBuffer.allocateDirect(SND_BUFSIZE);
            _sndBufSegm.order(ByteOrder.BIG_ENDIAN);
            this._sndBufDTM = ByteBuffer.allocateDirect(SND_BUFSIZE);
            _sndBufDTM.order(ByteOrder.LITTLE_ENDIAN);
        }
    }

    public void start() throws ServiceException {
        final ServerSocketChannel channel = createServerSocketChannel();
        final Thread pagerThread = new Thread(SRV_NAME) {
            public void run() {
                try {
                    acceptConnections(channel);
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                } finally {
                    IOUtils.closeQuietly(channel);
                }
            }
        };
        pagerThread.setDaemon(true);
        pagerThread.start();
    }

    private static ServerSocketChannel createServerSocketChannel() {
        try {
            return ServerSocketChannel.open();
        } catch (IOException e) {
            throw new IllegalStateException("failed to create a server socket", e);
        }
    }

    private void acceptConnections(final ServerSocketChannel channel) throws IOException {
        // set to non blocking mode
        channel.configureBlocking(false);

        // Bind the server socket to the local host and port
        InetAddress host = InetAddress.getLocalHost();
        InetSocketAddress sockAddr = new InetSocketAddress(host, PORT);
        ServerSocket socket = channel.socket();
        //socket.setReuseAddress(true);
        socket.bind(sockAddr);

        // Register accepts on the server socket with the selector. This
        // step tells the selector that the socket wants to be put on the
        // ready list when accept operations occur.
        Selector selector = Selector.open();

        ByteBuffer cmdBuffer = ByteBuffer.allocateDirect(MAX_COMMAND_BUFLEN);
        IOHandler ioHandler = new IOHandler(cmdBuffer);
        AcceptHandler acceptHandler = new AcceptHandler(ioHandler);

        channel.register(selector, SelectionKey.OP_ACCEPT, acceptHandler);

        int n;
        while((n = selector.select()) > 0) {
            // Someone is ready for I/O, get the ready keys
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            final Iterator<SelectionKey> keyItor = selectedKeys.iterator();
            while(keyItor.hasNext()) {
                SelectionKey key = keyItor.next();
                keyItor.remove();
                // The key indexes into the selector so you
                // can retrieve the socket that's ready for I/O
                Handler handler = (Handler) key.attachment();
                try {
                    handler.handle(key);
                } catch (CancelledKeyException cke) {
                    ;
                } catch (IOException ioe) {
                    LOG.fatal(ioe);
                    NIOUtils.cancelKey(key);
                } catch (Throwable e) {
                    LOG.fatal(e);
                    NIOUtils.cancelKey(key);
                }
            }
        }
    }

    private interface Handler {
        public void handle(SelectionKey key) throws ClosedChannelException, IOException;
    }

    private static final class AcceptHandler implements Handler {

        final Handler nextHandler;

        AcceptHandler(Handler nextHandler) {
            this.nextHandler = nextHandler;
        }

        public void handle(SelectionKey key) throws ClosedChannelException, IOException {
            if(!key.isValid()) {
                return;
            }
            ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
            SocketChannel socketChannel = serverChannel.accept();
            if(LOG.isDebugEnabled()) {
                LOG.debug("accepted a connection from " + socketChannel.socket().getInetAddress());
            }
            socketChannel.configureBlocking(false);
            socketChannel.register(key.selector(), SelectionKey.OP_READ, nextHandler);
        }

    }

    private final class IOHandler implements Handler {

        final ByteBuffer cmdBuffer;

        IOHandler(ByteBuffer cmdBuffer) {
            this.cmdBuffer = cmdBuffer;
        }

        public void handle(SelectionKey key) throws ClosedChannelException, IOException {
            if(!key.isValid()) {
                return;
            }
            SocketChannel channel = (SocketChannel) key.channel();
            if(key.isReadable()) {
                if(doRead(channel, cmdBuffer)) {
                    key.interestOps(SelectionKey.OP_WRITE);
                } else {
                    key.selector().wakeup();
                }
            } else if(key.isWritable()) {
                doWrite(channel, cmdBuffer);
                key.interestOps(SelectionKey.OP_READ);
            } else {
                if(LOG.isWarnEnabled()) {
                    LOG.warn("Illegal state was detected for key: " + key);
                }
            }
        }

    }

    private static boolean doRead(final SocketChannel channel, final ByteBuffer cmdBuffer)
            throws IOException {
        cmdBuffer.clear();
        int n, count = 0;
        while(channel.isOpen() && (n = channel.read(cmdBuffer)) > 0) {
            count += n;
        }
        return count > 0;
    }

    /**
     * @return is closeable?
     */
    private void doWrite(final SocketChannel socketChannel, final ByteBuffer cmdBuffer) {
        // receive a command
        cmdBuffer.flip();
        final int cmd = cmdBuffer.getInt();
        switch(cmd) {
            case COMMAND_READ:
                RemoteMemoryMappedFile.sendback(socketChannel, cmdBuffer, _fdCacheMap, _sndBufDTM);
                break;
            case COMMAND_TRACK_READ:
                RemoteVarSegments.sendback(socketChannel, cmdBuffer, _fdCacheMap, _directoryCache, _sndBufSegm);
                break;
            default:
                throw new IllegalStateException("Unknown command: " + cmd);
        }
    }

    public void stop() throws ServiceException {
        _fdCacheMap.clear();
    }

}
