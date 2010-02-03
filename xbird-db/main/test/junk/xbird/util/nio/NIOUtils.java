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

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.WritableByteChannel;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class NIOUtils {

    private static final int defaultReadTimeout = 30000;

    private NIOUtils() {}

    public static void readFully(final ReadableByteChannel channel, final ByteBuffer buf)
            throws IOException {
        readFully(channel, buf, buf.remaining());
    }

    public static void readFully(final ReadableByteChannel channel, final ByteBuffer buf, final int length)
            throws IOException {
        int n, count = 0;
        while(-1 != (n = channel.read(buf))) {
            count += n;
            if(count == length) {
                break;
            }
        }
    }

    public static void writeFully(final WritableByteChannel channel, final ByteBuffer buf)
            throws IOException {
        do {
            int written = channel.write(buf);
            if(written < 0) {
                throw new EOFException();
            }
        } while(buf.hasRemaining());
    }

    public static void readFully(final SelectableChannel channel, final ByteBuffer buf, final int length, final ISelectorFactory selectorFactory)
            throws IOException {
        final boolean blocking = channel.isBlocking();
        if(blocking) {
            channel.configureBlocking(false);
        }
        int n, count = 0;
        while(-1 != (n = readWithTemporarySelector(channel, buf, defaultReadTimeout, selectorFactory))) {
            count += n;
            if(count == length) {
                break;
            }
        }
        if(blocking) {
            channel.configureBlocking(true);
        }
    }

    /**
     * Method reads data from <code>SelectableChannel</code> to 
     * <code>ByteBuffer</code>. If data is not immediately available - channel
     *  will be reregistered on temporary <code>Selector</code> and wait maximum
     * readTimeout milliseconds for data.
     * 
     * @param channel <code>SelectableChannel</code> to read data from
     * @param byteBuffer <code>ByteBuffer</code> to store read data to
     * @param readTimeout maximum time in millis operation will wait for 
     * incoming data
     * 
     * @return number of bytes were read
     * @throws <code>IOException</code> if any error was occured during read
     * @link http://weblogs.java.net/blog/jfarcand/archive/2006/07/tricks_and_tips_4.html
     */
    private static int readWithTemporarySelector(final SelectableChannel channel, final ByteBuffer byteBuffer, final long readTimeout, final ISelectorFactory selectorFactory)
            throws IOException {
        int count = 1;
        int byteRead = 0;
        int preReadInputBBPos = byteBuffer.position();
        Selector readSelector = null;
        SelectionKey tmpKey = null;

        try {
            ReadableByteChannel readableChannel = (ReadableByteChannel) channel;
            while(count > 0) {
                count = readableChannel.read(byteBuffer);
                if(count > -1) {
                    byteRead += count;
                } else {
                    byteRead = count;
                }
            }

            if(byteRead == 0 && byteBuffer.position() == preReadInputBBPos) {
                readSelector = selectorFactory.getSelector();

                if(readSelector == null) {
                    return 0;
                }
                count = 1;

                tmpKey = channel.register(readSelector, SelectionKey.OP_READ);
                tmpKey.interestOps(tmpKey.interestOps() | SelectionKey.OP_READ);
                int code = readSelector.select(readTimeout);
                tmpKey.interestOps(tmpKey.interestOps() & (~SelectionKey.OP_READ));

                if(code == 0) {
                    return 0; // Return on the main Selector and try again.
                }

                while(count > 0) {
                    count = readableChannel.read(byteBuffer);
                    if(count > -1) {
                        byteRead += count;
                    } else {
                        byteRead = count;
                    }
                }
            } else if(byteRead == 0 && byteBuffer.position() != preReadInputBBPos) {
                byteRead += (byteBuffer.position() - preReadInputBBPos);
            }
        } finally {
            if(tmpKey != null) {
                tmpKey.cancel();
            }
            if(readSelector != null) {// Bug 6403933
                try {
                    readSelector.selectNow();
                } catch (IOException e) {
                    ;
                }
                selectorFactory.returnSelector(readSelector);
            }
        }
        return byteRead;
    }
}
