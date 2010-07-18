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
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.WritableByteChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;

import xbird.util.io.IOUtils;
import xbird.util.system.SystemUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class NIOUtils {

    private NIOUtils() {}

    public static void readFully(final ReadableByteChannel channel, final ByteBuffer buf)
            throws IOException {
        readFully(channel, buf, buf.remaining());
    }

    public static int readFully(final ReadableByteChannel channel, final ByteBuffer buf, final int length)
            throws IOException {
        int n, count = 0;
        while(-1 != (n = channel.read(buf))) {
            count += n;
            if(count == length) {
                break;
            }
        }
        return count;
    }

    public static void readFully(final FileChannel channel, final ByteBuffer dst, final long position)
            throws IOException {
        while(dst.remaining() > 0) {
            if(-1 == channel.read(dst, position + dst.position())) {
                throw new EOFException();
            }
        }
    }

    public static void readFully(final FileChannel channel, final ByteBuffer dst, final long position, final int length)
            throws IOException {
        int n, count = 0;
        while(-1 != (n = channel.read(dst, position))) {
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

    public static int countingWriteFully(final WritableByteChannel channel, final ByteBuffer buf)
            throws IOException {
        int written = 0;
        do {
            final int n = channel.write(buf);
            if(n < 0) {
                throw new EOFException();
            }
            written += n;
        } while(buf.hasRemaining());
        return written;
    }

    public static void writeFully(final FileChannel channel, final ByteBuffer dst, final long position)
            throws IOException {
        while(dst.remaining() > 0) {
            channel.write(dst, position + dst.position());
        }
    }

    /**
     * @link http://weblogs.java.net/blog/jfarcand/archive/2006/07/tricks_and_tips_4.html
     */
    public static int readWithTemporarySelector(final SelectableChannel channel, final ByteBuffer byteBuffer, final long readTimeout, final NioSelectorPool selectorPool)
            throws IOException {
        final int preBufPos = byteBuffer.position();
        int byteRead = 0;
        Selector readSelector = null;
        SelectionKey tmpKey = null;

        try {
            ReadableByteChannel readableChannel = (ReadableByteChannel) channel;
            int count = 1;
            while(count > 0) {
                count = readableChannel.read(byteBuffer);
                if(count > -1) {
                    byteRead += count;
                } else {
                    byteRead = count;
                }
            }

            if(byteRead == 0 && byteBuffer.position() == preBufPos) {
                readSelector = selectorPool.getSelector();
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
            } else if(byteRead == 0 && byteBuffer.position() != preBufPos) {
                byteRead += (byteBuffer.position() - preBufPos);
            }
        } finally {
            if(tmpKey != null) {
                tmpKey.cancel();
            }
            if(readSelector != null) {
                selectorPool.selectNowAndReturnSelector(readSelector);
            }
        }
        return byteRead;
    }

    public static void close(SelectionKey key) {
        IOUtils.closeQuietly(key.channel()); // key is automatically removed with this call.
    }

    public static void close(Selector selector) {
        if(selector != null && selector.isOpen()) {
            for(SelectionKey key : selector.keys()) {
                close(key);
            }
            try {
                selector.close();
            } catch (IOException e) {
                ;
            }
        }
    }

    public static void transferFullyFrom(final ReadableByteChannel src, final long position, final long count, final FileChannel dst)
            throws IOException {
        long pos = position;
        long remaining = count;
        while(remaining > 0) {
            long wrote = dst.transferFrom(src, pos, remaining);
            pos += wrote;
            remaining -= wrote;
        }
    }

    /**
     * Overlapping mapped files cannot be unmapped on windows.
     * The process cannot access the file because another process has locked a portion of the file.
     * See Sun bug id #4938372. Fixed in JDK 7.
     * 
     * @link http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4724038
     * @link http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4938372
     */
    public static boolean clean(final MappedByteBuffer buffer) {
        if(!SystemUtils.isMunmapAvailable()) {
            return false;
        }
        return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            public Boolean run() {
                try {
                    Method getCleanerMethod = buffer.getClass().getMethod("cleaner", new Class[0]);
                    getCleanerMethod.setAccessible(true);
                    sun.misc.Cleaner cleaner = (sun.misc.Cleaner) getCleanerMethod.invoke(buffer, new Object[0]);
                    cleaner.clean();
                } catch (Exception e) {
                    return Boolean.FALSE;
                }
                return Boolean.TRUE;
            }
        }).booleanValue();
    }

}
