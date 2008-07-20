/*
 * @(#)$Id: IOUtils.java 3619 2008-03-26 07:23:03Z yui $
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
 *     Makoto YUI - ported from jakarta commons io
 */
/*
 * Copyright 2001-2004 The Apache Software Foundation
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
 */
package xbird.util.io;

import java.io.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import xbird.util.lang.CancelAwareTimer;

/**
 * IO related utilities.
 * <DIV lang="en">
 * This code is ported from Jakarta's <a href="http://jakarta.apache.org/commons/io/">Commons-IO</a>.
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://jakarta.apache.org/commons/io/
 */
public final class IOUtils {

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    private IOUtils() {}

    public static void writeInt(final int v, final OutputStream out) throws IOException {
        out.write((v >>> 24) & 0xFF);
        out.write((v >>> 16) & 0xFF);
        out.write((v >>> 8) & 0xFF);
        out.write((v >>> 0) & 0xFF);
    }

    public static int readInt(InputStream in) throws IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        int ch3 = in.read();
        int ch4 = in.read();
        return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
    }

    /**
     * InputStream -> OutputStream
     */
    public static int copy(InputStream input, OutputStream output) throws IOException {
        final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int count = 0;
        int n = 0;
        while(-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * Reader -> Writer.
     */
    public static int copy(Reader input, Writer output) throws IOException {
        final char[] buffer = new char[DEFAULT_BUFFER_SIZE];
        int count = 0;
        int n = 0;
        while(-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * String -> OutputStream.
     */
    public static void copy(String input, OutputStream output) throws IOException {
        final StringReader in = new StringReader(input);
        final OutputStreamWriter out = new OutputStreamWriter(output);
        copy(in, out);
        // Unless anyone is planning on rewriting OutputStreamWriter, we have to flush here.
        out.flush();
    }

    /**
     * Serialize given InputStream as String.
     */
    public static String toString(InputStream input) throws IOException {
        final FastMultiByteArrayOutputStream output = new FastMultiByteArrayOutputStream();
        copy(input, output);
        return output.toString();
    }

    public static String toString(InputStream input, String cs) throws IOException {
        final FastMultiByteArrayOutputStream output = new FastMultiByteArrayOutputStream();
        copy(input, output);
        return output.toString(cs);
    }

    @Deprecated
    public static void getBytes(final List<byte[]> srcLst, final byte[] dest) {
        int pos = 0;
        for(byte[] bytes : srcLst) {
            final int len = bytes.length;
            System.arraycopy(bytes, 0, dest, pos, len);
            pos += len;
        }
    }

    public static void writeString(final DataOutput out, final String s) throws IOException {
        final int len = s.length();
        out.writeInt(len);
        for(int i = 0; i < len; i++) {
            int v = s.charAt(i);
            out.write((v >>> 8) & 0xFF);
            out.write((v >>> 0) & 0xFF);
        }
    }

    public static String readString(final DataInput in) throws IOException {
        final int len = in.readInt();
        final char[] ch = new char[len];
        for(int i = 0; i < len; i++) {
            ch[i] = in.readChar();
        }
        return new String(ch);
    }

    public static void closeQuietly(final Object toClose) {
        if(toClose instanceof Closeable) {
            try {
                ((Closeable) toClose).close();
            } catch (IOException e) {
                ;
            }
        }
    }

    public static void closeQuietly(final Closeable channel) {
        try {
            channel.close();
        } catch (IOException e) {
            ;
        }
    }

    public static void closeQuietly(final Closeable... channels) {
        for(Closeable c : channels) {
            if(c != null) {
                try {
                    c.close();
                } catch (IOException e) {
                    ;
                }
            }
        }
    }

    public static void closeAndRethrow(final Exception e, final Closeable... channels)
            throws IllegalStateException {
        closeQuietly(channels);
        throw new IllegalStateException(e);
    }

    /**
     * @param delay delay in milliseconds before task is to be executed.
     */
    public static void schduleCloseQuietly(final Timer timer, final long delay, final Closeable... channels) {
        if(delay == 0) {
            closeQuietly(channels);
            return;
        }
        final TimerTask cancel = new TimerTask() {
            @Override
            public void run() {
                closeQuietly(channels);
            }
        };
        timer.schedule(cancel, delay);
    }

    /**
     * @param delay delay in milliseconds before task is to be executed.
     */
    public static boolean schduleCloseQuietly(final CancelAwareTimer timer, final long delay, final AtomicInteger activeCount, final int limitSched, final Closeable... channels) {
        if(delay == 0) {
            closeQuietly(channels);
            return true;
        }
        if(timer.getNumberOfScheduled() >= limitSched) {
            return false;
        }
        final TimerTask cancel = new TimerTask() {
            public void run() {
                if(activeCount.get() < 1) {
                    closeQuietly(channels);
                }
            }
        };
        timer.schedule(cancel, delay);
        return true;
    }

    public static void schduleCloseQuietly(final ScheduledExecutorService sched, final int delay, final AtomicInteger activeCount, final Closeable... channels) {
        if(delay == 0) {
            closeQuietly(channels);
            return;
        }
        final Runnable cancel = new Runnable() {
            public void run() {
                if(activeCount.get() < 1) {
                    closeQuietly(channels);
                }
            }
        };
        sched.schedule(cancel, delay, TimeUnit.MILLISECONDS);
    }

}