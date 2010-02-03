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
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.util.concurrent.ConcurrentMap;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.integration.jmx.IoServiceMBean;
import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.apr.AprSocketAcceptor;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import xbird.config.Settings;
import xbird.server.ServiceException;
import xbird.storage.io.RemoteVarSegments;
import xbird.storage.io.RemoteVarSegments.TrackReadRequestMessage;
import xbird.storage.io.VarSegments.IDescriptor;
import xbird.util.concurrent.ExecutorFactory;
import xbird.util.concurrent.jsr166.ConcurrentReferenceHashMap;
import xbird.util.concurrent.jsr166.ConcurrentReferenceHashMap.ReferenceType;
import xbird.util.concurrent.reference.FinalizableSoftValueReferenceMap;
import xbird.util.concurrent.reference.ReferenceMap;
import xbird.util.concurrent.reference.ReferentFinalizer;
import xbird.util.io.IOUtils;
import xbird.util.nio.RemoteMemoryMappedFile;
import xbird.util.nio.RemoteMemoryMappedFile.ReadRequestMessage;
import xbird.util.primitive.Primitives;
import xbird.util.system.SystemUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class RemotePagingService extends ServiceBase {
    private static final Log LOG = LogFactory.getLog(RemotePagingService.class);

    public static final String SRV_NAME = "RemotePaging";
    public static final int MAX_COMMAND_BUFLEN = 1024;
    public static final int COMMAND_READ = 1;
    public static final int COMMAND_TRACK_READ = 2;
    public static final int PORT = Integer.parseInt(Settings.get("xbird.remote.paging_serv.port", "8900"));
    public static final String CONN_TYPE = Settings.get("xbird.remote.paging_serv.connection.type", "").toUpperCase();

    private static final int SND_BUFSIZE = Integer.parseInt(Settings.get("xbird.remote.paging_serv.connection.so_sndbufsize", "8192"));
    private static final int SO_IDLETIME = Integer.parseInt(Settings.get("xbird.remote.paging_serv.idletime", "30"));
    private static final int SO_WRITE_TIMEOUT = Integer.parseInt(Settings.get("xbird.remote.paging_serv.write_timeout", "120"));
    private static final boolean JMX_MONITOR_ENABLED = Boolean.parseBoolean(Settings.get("xbird.remote.paging_serv.jmx_monitoring"));
    private static final int NUM_IO_PROCESSORS;

    public static final int SWEEP_INTERVAL;
    public static final int TIME_TO_LIVE;
    public static final int SO_RCVBUF_SIZE;

    static {
        final String numIoProcs = Settings.get("xbird.remote.paging_serv.io_processors", "-1");
        if("-1".equals(numIoProcs)) {
            NUM_IO_PROCESSORS = SystemUtils.availableProcessors() + 1;
        } else {
            NUM_IO_PROCESSORS = Integer.parseInt(numIoProcs);
        }
        String sweep = Settings.get("xbird.remote.paging_serv.connection.sweep_interval");
        String ttl = Settings.get("xbird.remote.paging_serv.connection.ttl");
        String rcvbuf = Settings.get("xbird.remote.paging_serv.connection.so_rcvbufsize");
        SWEEP_INTERVAL = Primitives.parseInt(sweep, 60000);
        TIME_TO_LIVE = Primitives.parseInt(ttl, 30000);
        SO_RCVBUF_SIZE = Primitives.parseInt(rcvbuf, 4096);
    }

    private final ReferenceMap<String, FileChannel> _fdCacheMap;
    private final ConcurrentMap<String, IDescriptor> _directoryCache;

    public RemotePagingService() {
        super(SRV_NAME);
        this._fdCacheMap = new FinalizableSoftValueReferenceMap<String, FileChannel>(new ReferentFinalizer<String, FileChannel>() {
            public void finalize(String key, FileChannel reclaimed) {
                IOUtils.closeQuietly(reclaimed);
            }
        });
        this._directoryCache = new ConcurrentReferenceHashMap<String, IDescriptor>(32, ReferenceType.STRONG, ReferenceType.SOFT);
    }

    public void start() throws ServiceException {
        try {
            acceptConnections();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * We add the ExecutorFilter after the ProtocolCodecFilter implementation. 
     * It is because the performance characteristic of most protocol codec implementations 
     * is CPU-bound, which should not be in same thread with I/O processor.
     * 
     * @link http://mina.apache.org/configuring-thread-model.html
     */
    private void acceptConnections() throws IOException {
        IoAcceptor accepter = setupAcceptor();
        accepter.setHandler(new IOHandler());

        DefaultIoFilterChainBuilder filterChainBuilder = accepter.getFilterChain();
        // Add CPU-bound job first,
        ProtocolCodecFilter protocolFiler = new ProtocolCodecFilter(new ResponseEncoder(), new RequestDecoder());
        filterChainBuilder.addLast("protocol", protocolFiler);
        // and then a thread pool. REVIEWME
        filterChainBuilder.addLast("execFilter", new ExecutorFilter(ExecutorFactory.newCachedThreadPool("execFilter")));

        InetAddress host = InetAddress.getLocalHost();
        InetSocketAddress sockAddr = new InetSocketAddress(host, PORT);
        accepter.bind(sockAddr);

        if(JMX_MONITOR_ENABLED) {
            setupMonitor(accepter);
        }
    }

    private static IoAcceptor setupAcceptor() {
        if("NIODATAGRAM".equals(CONN_TYPE)) {
            LOG.info("NioDatagramAcceptor is used for RemotePaging");
            NioDatagramAcceptor datagramAcceptor = new NioDatagramAcceptor(); // connection less
            DatagramSessionConfig config = datagramAcceptor.getSessionConfig();
            config.setReuseAddress(true);
            config.setReadBufferSize(1024);
            config.setSendBufferSize(SND_BUFSIZE);
            config.setBothIdleTime(SO_IDLETIME);
            config.setWriteTimeout(SO_WRITE_TIMEOUT);
            config.setBroadcast(false);
            return datagramAcceptor;
        } else {
            final SocketAcceptor acceptor;
            if("APRSOCKET".equals(CONN_TYPE)) {
                LOG.info("AprSocketAcceptor is used for RemotePaging");
                acceptor = new AprSocketAcceptor(NUM_IO_PROCESSORS);
            } else {
                LOG.info("NioSocketAcceptor is used for RemotePaging");
                acceptor = new NioSocketAcceptor(NUM_IO_PROCESSORS);
            }
            SocketSessionConfig config = acceptor.getSessionConfig();
            config.setReuseAddress(true);
            config.setReadBufferSize(1024);
            config.setSendBufferSize(SND_BUFSIZE);
            config.setBothIdleTime(SO_IDLETIME);
            config.setWriteTimeout(SO_WRITE_TIMEOUT);
            //config.setTcpNoDelay(true); // Disable Nagle's algorithm
            return acceptor;
        }
    }

    private static void setupMonitor(IoAcceptor service) {
        final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        try {
            server.registerMBean(new IoServiceMBean(service), new ObjectName("org.apache.mina:type=service,name="
                    + SRV_NAME));
        } catch (InstanceAlreadyExistsException iaee) {
            throw new IllegalStateException(iaee);
        } catch (MBeanRegistrationException mre) {
            throw new IllegalStateException(mre);
        } catch (NotCompliantMBeanException ncme) {
            throw new IllegalStateException(ncme);
        } catch (MalformedObjectNameException mone) {
            throw new IllegalStateException(mone);
        } catch (NullPointerException npe) {
            throw new IllegalStateException(npe);
        }
    }

    private static final class IOHandler extends IoHandlerAdapter {

        IOHandler() {
            super();
        }

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            session.write(message);
        }

        @Override
        public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
            LOG.error(cause);
            //session.close();
        }

    }

    private static final class RequestDecoder extends CumulativeProtocolDecoder {

        public RequestDecoder() {
            super();
        }

        protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out)
                throws Exception {
            // Remember the initial position.
            final int start = in.position();
            if(in.remaining() < 4) {
                return false;
            }
            final int cmdLen = in.getInt();
            final int remaining = in.remaining();
            if(remaining < cmdLen) {
                in.position(start);
                return false;
            }
            final int cmd = in.getInt();
            final RequestMessage reqMsg;
            switch(cmd) {
                case COMMAND_READ:
                    reqMsg = ReadRequestMessage.decode(in);
                    break;
                case COMMAND_TRACK_READ:
                    reqMsg = TrackReadRequestMessage.decode(in);
                    break;
                default:
                    throw new IllegalStateException("Unknown command: " + cmd);
            }
            out.write(reqMsg);
            return true;
        }

    }

    private final class ResponseEncoder extends ProtocolEncoderAdapter {

        public ResponseEncoder() {
            super();
        }

        public void encode(IoSession session, Object message, ProtocolEncoderOutput out)
                throws Exception {
            final RequestMessage requestMsg = (RequestMessage) message;
            switch(requestMsg.cmdSign) {
                case COMMAND_READ:
                    RemoteMemoryMappedFile.handleResponse((ReadRequestMessage) requestMsg, out, _fdCacheMap);
                    break;
                case COMMAND_TRACK_READ:
                    RemoteVarSegments.handleResponse((TrackReadRequestMessage) requestMsg, out, _fdCacheMap, _directoryCache);
                    break;
                default:
                    throw new IllegalStateException("Unknown command: " + requestMsg.cmdSign);
            }
        }

    }

    public static class RequestMessage {

        protected final int cmdSign;

        public RequestMessage(int cmdSign) {
            this.cmdSign = cmdSign;
        }

    }

    public void stop() throws ServiceException {
        _fdCacheMap.clear();
    }

}
