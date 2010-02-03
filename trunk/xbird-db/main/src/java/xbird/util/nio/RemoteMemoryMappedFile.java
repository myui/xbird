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
package xbird.util.nio;

import java.io.Externalizable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.file.DefaultFileRegion;
import org.apache.mina.core.file.FileRegion;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import xbird.server.services.RemotePagingService;
import xbird.server.services.RemotePagingService.RequestMessage;
import xbird.util.cache.ILongCache;
import xbird.util.io.IOUtils;
import xbird.util.net.NetUtils;
import xbird.util.net.PoolableSocketChannelFactory;
import xbird.util.pool.ConcurrentKeyedStackObjectPool;
import xbird.util.string.StringUtils;
import xbird.util.struct.LongRange;
import xbird.xquery.dm.coder.SerializationContext;
import xbird.xquery.dm.dtm.MemoryMappedDocumentTable;

/**
 * 
 * <DIV lang="en">
 * This class is not thread-safe as an intended behavior.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class RemoteMemoryMappedFile implements IMemoryMappedFile, Externalizable {
    private static final long serialVersionUID = 4842717419671591515L;
    private static final Log LOG = LogFactory.getLog("xbird.remotepaging");

    private/* final */InetSocketAddress _sockAddr;
    private/* final */String _filePath;
    private/* final */int _pageSize;
    private/* final */boolean _bigEndian;
    private/* final */long _maxBulkFetchSize;
    private transient String _fileIdentifier = null;

    private static final ConcurrentKeyedStackObjectPool<SocketAddress, ByteChannel> _sockPool;
    static {
        boolean datagram = "NIODATAGRAM".equals(RemotePagingService.CONN_TYPE);
        PoolableSocketChannelFactory factory = new PoolableSocketChannelFactory(datagram, true);
        factory.configure(RemotePagingService.SWEEP_INTERVAL, RemotePagingService.TIME_TO_LIVE, RemotePagingService.SO_RCVBUF_SIZE);
        _sockPool = new ConcurrentKeyedStackObjectPool<SocketAddress, ByteChannel>("RemoteMemoryMappedFile", factory);
    }
    private transient/* final */ByteBuffer _rcvbuf;

    private SerializationContext _serContext = null;

    public RemoteMemoryMappedFile() {}

    public RemoteMemoryMappedFile(int port, String filePath, int pageSize, boolean alloc, boolean bigEndian) {
        this._sockAddr = new InetSocketAddress(NetUtils.getLocalHost(), port);
        this._filePath = filePath;
        this._pageSize = pageSize;
        this._bigEndian = bigEndian;
        this._maxBulkFetchSize = pageSize * (MemoryMappedDocumentTable.CACHED_PAGES / 4);
        this._rcvbuf = alloc ? ByteBuffer.allocateDirect(pageSize) : null;
    }

    public String getFileIdentifier() {
        return _fileIdentifier;
    }

    public void setSerializationContext(SerializationContext serContext) {
        this._serContext = serContext;
    }

    public CloseableMappedByteBuffer allocateBuffer(long pageOffset) {
        throw new UnsupportedOperationException();
    }

    public int[] transferBuffer(final long pageOffset, final int aryLength) {
        final int[] dst;
        final ByteChannel channel;
        try {
            channel = openConnection(_sockAddr);
        } catch (IOException ioe) {
            throw new IllegalStateException("failed opening a socket", ioe);
        }
        try {
            sendRequest(channel, pageOffset, pageOffset, aryLength);
            dst = recvResponse(channel, _rcvbuf, aryLength);
        } catch (IOException ioe) {
            IOUtils.closeQuietly(channel);
            throw new IllegalStateException(ioe);
        } catch (Throwable e) {
            IOUtils.closeQuietly(channel);
            throw new IllegalStateException(e);
        } finally {
            _sockPool.returnObject(_sockAddr, channel);
        }
        return dst;
    }

    public int[] transferBuffers(long startPageOffset, int aryLength, ILongCache<int[]> _pool) {
        LongRange range = _serContext.ranges().getRangeOf(startPageOffset);
        final int pageSize = _pageSize;
        long endPageOffset = (range == null) ? startPageOffset + pageSize : range.getEnd();
        final long lastPageOffset = restrictEndOffset(startPageOffset, endPageOffset, aryLength, pageSize);
        assert (lastPageOffset > startPageOffset) : "Illegal condition.. start:" + startPageOffset
                + " < end:" + lastPageOffset;
        final ByteChannel channel;
        try {
            channel = openConnection(_sockAddr);
        } catch (IOException ioe) {
            throw new IllegalStateException("failed opening a socket", ioe);
        }
        if(LOG.isDebugEnabled()) {
            LOG.debug("Send a request to " + _sockAddr + " to transfer " + pageSize + " bytes of '"
                    + _filePath + "' from the offset " + startPageOffset);
        }
        final int[] dst;
        try {
            sendRequest(channel, startPageOffset, lastPageOffset, aryLength);
            dst = recvResponse(channel, _rcvbuf, aryLength);
            if(LOG.isDebugEnabled()) {
                LOG.debug("Received pages starting from the offset " + startPageOffset);
            }
            int[] tmp;
            long startPageId = (startPageOffset + pageSize) / pageSize;
            long endPageId = lastPageOffset / pageSize;
            for(long i = startPageId; i < endPageId; i++) {
                tmp = recvResponse(channel, _rcvbuf, aryLength);
                _pool.put(i, tmp);
            }
        } catch (IOException ioe) {
            IOUtils.closeQuietly(channel);
            throw new IllegalStateException(ioe);
        } catch (Throwable e) {
            IOUtils.closeQuietly(channel);
            throw new IllegalStateException(e);
        } finally {
            _sockPool.returnObject(_sockAddr, channel);
        }
        return dst;
    }

    private static ByteChannel openConnection(final SocketAddress sockAddr) throws IOException {
        return _sockPool.borrowObject(sockAddr);
    }

    private long restrictEndOffset(final long startOffset, final long endOffset, final int aryLength, final int pageSize) {
        final long diff = endOffset - startOffset;
        if(diff > _maxBulkFetchSize) {
            return startOffset + _maxBulkFetchSize;
        } else {
            return endOffset;
        }
    }

    private void sendRequest(final ByteChannel channel, final long startOffset, final long endOffset, final int aryLength)
            throws IOException {
        byte[] bFilePath = StringUtils.getBytes(_filePath);
        int buflen = bFilePath.length + 29;//+ 4 + 4 + 4 + 8 + 8 + 1;
        if(buflen > RemotePagingService.MAX_COMMAND_BUFLEN) {
            throw new IllegalStateException("command size exceeds limit in MAX_COMMAND_BUFLEN("
                    + RemotePagingService.MAX_COMMAND_BUFLEN + "): " + buflen + " bytes");
        }
        ByteBuffer oprBuf = ByteBuffer.allocate(buflen);
        oprBuf.putInt(buflen - 4);
        oprBuf.putInt(RemotePagingService.COMMAND_READ);
        oprBuf.putInt(bFilePath.length); // #1
        oprBuf.put(bFilePath); // #2
        oprBuf.putLong(startOffset); // #3
        oprBuf.putLong(endOffset); // #4
        oprBuf.put((byte) (_bigEndian ? 1 : 0)); // # 5 is big endian?
        oprBuf.flip();
        NIOUtils.writeFully(channel, oprBuf);
    }

    public static final class ReadRequestMessage extends RequestMessage {

        final String filePath;
        final long startOffset;
        final long endOffset;
        final boolean isBigEndian;

        private ReadRequestMessage(IoBuffer in) {
            super(RemotePagingService.COMMAND_READ);
            int filePathLen = in.getInt(); // #1
            byte[] bFilePath = new byte[filePathLen];
            in.get(bFilePath);
            this.filePath = StringUtils.toString(bFilePath, 0, filePathLen); // #2
            this.startOffset = in.getLong(); // #3
            this.endOffset = in.getLong(); // #4
            byte bigEndian = in.get(); // #5
            this.isBigEndian = (bigEndian == 1) ? true : false;
        }

        public static ReadRequestMessage decode(IoBuffer in) {
            return new ReadRequestMessage(in);
        }
    }

    public static void handleResponse(final ReadRequestMessage request, final ProtocolEncoderOutput out, final ConcurrentMap<String, FileChannel> fdCacheMap) {
        final String filePath = request.filePath;

        FileChannel fileChannel = fdCacheMap.get(filePath);
        if(fileChannel == null) {
            File file = new File(filePath);
            if(!file.exists()) {
                throw new IllegalStateException("file not exists: " + filePath);
            }
            final RandomAccessFile raf;
            try {
                raf = new RandomAccessFile(file, "r");
            } catch (FileNotFoundException e) {
                throw new IllegalStateException(e);
            }
            fileChannel = raf.getChannel();
            fdCacheMap.put(filePath, fileChannel);
        }
        long count = request.endOffset - request.startOffset;
        long position = request.startOffset;

        if(LOG.isDebugEnabled()) {
            LOG.debug("Transfer " + count + " bytes of file '" + filePath + "' from the offset "
                    + position);
        }

        FileRegion fileRegion = new DefaultFileRegion(fileChannel, position, count);
        out.write(fileRegion);
    }

    private int[] recvResponse(final ReadableByteChannel channel, final ByteBuffer buf, final int dstlen)
            throws IOException {
        buf.clear();
        // set endian optimized for this machine
        final boolean isBufBigEndian = (buf.order() == ByteOrder.BIG_ENDIAN);
        if(_bigEndian != isBufBigEndian) {
            buf.order(_bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
        }
        NIOUtils.readFully(channel, buf, _pageSize);
        buf.flip();
        IntBuffer ibuf = buf.asIntBuffer();
        int[] dst = new int[dstlen];
        ibuf.get(dst);
        return dst;
    }

    public void flush() {
        throw new IllegalStateException();
    }

    public void close() throws IOException {}

    public void ensureOpen() {}

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this._bigEndian = in.readBoolean();
        this._pageSize = in.readInt();
        this._maxBulkFetchSize = _pageSize * (MemoryMappedDocumentTable.CACHED_PAGES / 4);
        this._rcvbuf = ByteBuffer.allocateDirect(_pageSize);
        String host = IOUtils.readString(in);
        int port = in.readInt();
        this._sockAddr = new InetSocketAddress(host, port);
        this._filePath = IOUtils.readString(in);
        this._fileIdentifier = generateFileIdentifier(_sockAddr, _filePath);
    }

    private static String generateFileIdentifier(final InetSocketAddress sockAddr, final String filePath) {
        final StringBuilder buf = new StringBuilder();
        buf.append(sockAddr.getHostName());
        buf.append(':');
        buf.append(sockAddr.getPort());
        buf.append('/');
        buf.append(filePath);
        return buf.toString();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeBoolean(_bigEndian);
        out.writeInt(_pageSize);
        IOUtils.writeString(_sockAddr.getHostName(), out);
        out.writeInt(_sockAddr.getPort());
        IOUtils.writeString(_filePath, out);
        close();
    }

    public static RemoteMemoryMappedFile read(ObjectInput in) throws IOException,
            ClassNotFoundException {
        RemoteMemoryMappedFile mmfile = new RemoteMemoryMappedFile();
        mmfile.readExternal(in);
        return mmfile;
    }

    public RemoteMemoryMappedFile externalize() {
        return this;
    }

}
