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
package xbird.util.xfer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.util.collections.SoftHashMap;
import xbird.util.datetime.StopWatch;
import xbird.util.io.FileUtils;
import xbird.util.io.IOUtils;
import xbird.util.nio.NIOUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class RecievedFileWriter implements TransferRequestListener {
    private static final Log LOG = LogFactory.getLog(RecievedFileWriter.class);

    @Nonnull
    private final File baseDir;
    @GuardedBy("locks")
    private final Map<String, ReadWriteLock> locks;

    public RecievedFileWriter(@Nonnull String baseDirPath) {
        this(new File(baseDirPath));
    }

    public RecievedFileWriter(@Nonnull File baseDir) {
        if(!baseDir.exists()) {
            throw new IllegalArgumentException("Directory not found: " + baseDir.getAbsolutePath());
        }
        if(!baseDir.isDirectory()) {
            throw new IllegalArgumentException(baseDir.getAbsolutePath() + " is not directory");
        }
        if(!baseDir.canWrite()) {
            throw new IllegalArgumentException(baseDir.getAbsolutePath() + " is not writable");
        }
        this.baseDir = baseDir;
        this.locks = new SoftHashMap<String, ReadWriteLock>(32);
    }

    public final void handleRequest(@Nonnull final SocketChannel inChannel, @Nonnull final Socket socket)
            throws IOException {
        final StopWatch sw = new StopWatch();
        if(!inChannel.isBlocking()) {
            inChannel.configureBlocking(true);
        }

        InputStream in = socket.getInputStream();
        DataInputStream dis = new DataInputStream(in);

        String fname = IOUtils.readString(dis);
        String dirPath = IOUtils.readString(dis);
        long len = dis.readLong();
        boolean append = dis.readBoolean();
        boolean ackRequired = dis.readBoolean();
        boolean hasAdditionalHeader = dis.readBoolean();
        if(hasAdditionalHeader) {
            readAdditionalHeader(dis, fname, dirPath, len, append, ackRequired);
        }

        final File file;
        if(dirPath == null) {
            file = new File(baseDir, fname);
        } else {
            File dir = FileUtils.resolvePath(baseDir, dirPath);
            file = new File(dir, fname);
        }

        preFileAppend(file, append);
        final FileOutputStream dst = new FileOutputStream(file, append);
        final String fp = file.getAbsolutePath();
        final ReadWriteLock filelock = accquireLock(fp, locks);
        final FileChannel fileCh = dst.getChannel();
        final long startPos = file.length();
        try {
            NIOUtils.transferFullyFrom(inChannel, 0, len, fileCh); // REVIEWME really an atomic operation?
        } finally {
            IOUtils.closeQuietly(fileCh, dst);
            releaseLock(fp, filelock, locks);
            postFileAppend(file, startPos, len);
        }
        if(ackRequired) {
            OutputStream out = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(out);
            dos.writeLong(len);
            postAck(file, startPos, len);
        }

        if(LOG.isDebugEnabled()) {
            SocketAddress remoteAddr = socket.getRemoteSocketAddress();
            LOG.debug("Received a " + (append ? "part of file '" : "file '")
                    + file.getAbsolutePath() + "' of " + len + " bytes from " + remoteAddr + " in "
                    + sw.toString());
        }
    }

    protected void readAdditionalHeader(@Nonnull DataInputStream in, @Nonnull String fname, @Nullable String dirPath, long len, boolean append, boolean sync)
            throws IOException {}

    protected void preFileAppend(@Nonnull File file, boolean append) throws IOException {}

    protected void postFileAppend(@Nonnull File file, long startPos, long len) throws IOException {}

    protected void postAck(@Nonnull File file, long startPos, long len) throws IOException {}

    private static ReadWriteLock accquireLock(final String key, final Map<String, ReadWriteLock> locks) {
        ReadWriteLock lock;
        synchronized(locks) {
            lock = locks.get(key);
            if(lock == null) {
                lock = new ReentrantReadWriteLock();
                locks.put(key, lock);
            }
        }
        lock.writeLock().lock();
        return lock;
    }

    private static void releaseLock(final String key, final ReadWriteLock lock, final Map<String, ReadWriteLock> locks) {
        lock.writeLock().unlock();
    }
}
