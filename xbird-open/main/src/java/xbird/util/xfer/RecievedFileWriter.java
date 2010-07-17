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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.util.datetime.StopWatch;
import xbird.util.io.FileUtils;
import xbird.util.io.IOUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class RecievedFileWriter implements TransferRequestListener {
    private static final Log LOG = LogFactory.getLog(RecievedFileWriter.class);

    @Nonnull
    private final File baseDir;
    private final boolean ackRequired;

    private final ConcurrentMap<String, ReadWriteLock> locks;

    public RecievedFileWriter(@Nonnull String baseDirPath, boolean sendAck) {
        this(new File(baseDirPath), sendAck);
    }

    public RecievedFileWriter(@Nonnull File baseDir, boolean sendAck) {
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
        this.ackRequired = sendAck;
        this.locks = new ConcurrentHashMap<String, ReadWriteLock>(32);
    }

    public void handleRequest(@Nonnull final SocketChannel channel, @Nonnull final Socket socket)
            throws IOException {
        final StopWatch sw = new StopWatch();
        if(!channel.isBlocking()) {
            channel.configureBlocking(true);
        }

        InputStream in = socket.getInputStream();
        DataInputStream dis = new DataInputStream(in);

        String fname = IOUtils.readString(dis);
        String dirPath = IOUtils.readString(dis);
        long len = dis.readLong();
        boolean append = dis.readBoolean();

        final File file;
        if(dirPath == null) {
            file = new File(baseDir, fname);
        } else {
            File dir = FileUtils.resolvePath(baseDir, dirPath);
            file = new File(dir, fname);
        }

        final FileOutputStream dst = new FileOutputStream(file, append);
        final long wrote;
        final String fp = file.getAbsolutePath();
        final ReadWriteLock filelock = accquireLock(fp, locks);
        try {
            FileChannel fileCh = dst.getChannel();
            wrote = fileCh.transferFrom(channel, 0, len); // REVIEWME really an atomic operation?
        } finally {
            releaseLock(fp, filelock, locks);
            dst.close();
        }
        if(wrote != len) {
            throw new IllegalStateException("Received " + len + " bytes, but wrote only " + wrote
                    + " bytes");
        }
        if(ackRequired) {
            OutputStream out = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(out);
            dos.writeLong(wrote);
        }

        if(LOG.isDebugEnabled()) {
            SocketAddress remoteAddr = socket.getRemoteSocketAddress();
            LOG.debug("Received a " + (append ? "part of file '" : "file '")
                    + file.getAbsolutePath() + "' of " + len + " bytes from " + remoteAddr + " in "
                    + sw.toString());
        }
    }

    private static ReadWriteLock accquireLock(String key, ConcurrentMap<String, ReadWriteLock> locks) {
        ReadWriteLock lock = locks.get(key);
        if(lock == null) {
            lock = new ReentrantReadWriteLock();
            ReadWriteLock oldlock = locks.putIfAbsent(key, lock);
            if(oldlock != null) {
                lock = oldlock;
            }
        }
        lock.writeLock().lock();
        return lock;
    }

    private static void releaseLock(String key, ReadWriteLock lock, ConcurrentMap<String, ReadWriteLock> locks) {
        lock.writeLock().unlock();
        locks.remove(key, lock);
    }
}
