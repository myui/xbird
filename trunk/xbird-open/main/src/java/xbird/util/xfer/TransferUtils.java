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

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

import javax.annotation.Nonnull;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.util.datetime.StopWatch;
import xbird.util.io.IOUtils;
import xbird.util.lang.PrintUtils;
import xbird.util.net.NetUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class TransferUtils {
    private static final Log LOG = LogFactory.getLog(TransferUtils.class);

    private TransferUtils() {}

    public static void sendfile(@Nonnull File file, @Nonnull InetAddress dstAddr, int dstPort)
            throws IOException {
        if(!file.exists()) {
            throw new IllegalArgumentException(file.getAbsolutePath() + " does not exist");
        }
        if(!file.isFile()) {
            throw new IllegalArgumentException(file.getAbsolutePath() + " is not file");
        }
        if(!file.canRead()) {
            throw new IllegalArgumentException(file.getAbsolutePath() + " cannot read");
        }

        final SocketAddress dstSockAddr = new InetSocketAddress(dstAddr, dstPort);

        SocketChannel channel = null;
        Socket socket = null;
        final OutputStream out;
        try {
            channel = SocketChannel.open();
            socket = channel.socket();
            socket.connect(dstSockAddr);
            out = socket.getOutputStream();
        } catch (IOException e) {
            IOUtils.closeQuietly(channel);
            NetUtils.closeQuietly(socket);
            LOG.error(PrintUtils.prettyPrintStackTrace(e, -1));
            throw e;
        }

        final DataOutputStream dos = new DataOutputStream(out);
        final StopWatch sw = new StopWatch();
        FileInputStream src = null;
        final long nbytes;
        try {
            src = new FileInputStream(file);
            FileChannel in = src.getChannel();

            String fileName = file.getName();
            dos.writeUTF(fileName);
            long filelen = in.size();
            dos.writeLong(filelen);
            dos.writeBoolean(true);

            // send file using zero-copy send            
            nbytes = in.transferTo(0, filelen, channel);

            src.close();
        } catch (FileNotFoundException e) {
            LOG.error(PrintUtils.prettyPrintStackTrace(e, -1));
            throw e;
        } catch (IOException e) {
            LOG.error(PrintUtils.prettyPrintStackTrace(e, -1));
            throw e;
        } finally {
            IOUtils.closeQuietly(src);
            IOUtils.closeQuietly(channel);
            NetUtils.closeQuietly(socket);
        }
        if(LOG.isInfoEnabled()) {
            LOG.info("Sent a file '" + file.getAbsolutePath() + "' of " + nbytes + " bytes to "
                    + dstSockAddr.toString() + " in " + sw.toString());
        }
    }

}
