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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

import javax.annotation.Nonnull;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.util.datetime.StopWatch;

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
    }

    public void handleRequest(@Nonnull final SocketChannel channel, @Nonnull final Socket socket)
            throws IOException {
        final StopWatch sw = new StopWatch();
        if(!channel.isBlocking()) {
            channel.configureBlocking(true);
        }

        InputStream in = socket.getInputStream();
        DataInputStream dis = new DataInputStream(in);

        String fname = dis.readUTF();
        long len = dis.readLong();
        boolean append = dis.readBoolean();

        File file = new File(baseDir, fname);
        FileOutputStream dst = new FileOutputStream(file, append);
        FileChannel out = dst.getChannel();

        out.transferFrom(channel, 0, len);
        if(LOG.isInfoEnabled()) {
            SocketAddress remoteAddr = socket.getRemoteSocketAddress();
            LOG.info("Received a " + (append ? "part of file '" : "file '")
                    + file.getAbsolutePath() + "' of " + len + " bytes from + " + remoteAddr
                    + " in " + sw.toString());
        }
    }
}
