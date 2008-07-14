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
package xbird.util.net;

import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread.State;
import java.net.SocketTimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class InterruptableInputStream extends InputStream {
    private static final Log LOG = LogFactory.getLog("xbird.socket");

    private final InputStream is;

    public InterruptableInputStream(InputStream is) {
        this.is = is;
    }

    @Override
    public int read() throws IOException {
        final byte[] b = {};
        final int count = internalRead(b, 0, 1);
        return count > 0 ? b[0] : -1;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return internalRead(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return internalRead(b, off, len);
    }

    private int internalRead(final byte[] b, final int off, final int len) throws IOException {
        boolean ntraced = true;
        while(true) {
            try {
                final int n = is.read(b, off, len);
                return n;
            } catch (SocketTimeoutException e) {
                if(Thread.interrupted()) {
                    throw e;
                } else {
                    Thread thread = Thread.currentThread();
                    if(!thread.isDaemon()) {
                        State state = thread.getState();
                        switch(state) {
                            case BLOCKED:
                            case WAITING:
                                if(LOG.isWarnEnabled()) {
                                    LOG.warn("thread (" + thread.getName() + "timeout: "
                                            + thread.getState(), e);
                                }
                                throw e;
                            default:
                                if(LOG.isTraceEnabled() && ntraced) {
                                    ntraced = false;
                                    LOG.trace("thread (" + thread.getName() + ") timeout: "
                                            + thread.getState());
                                }
                                break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public int available() throws IOException {
        return is.available();
    }

    @Override
    public void close() throws IOException {
        is.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        is.mark(readlimit);
    }

    @Override
    public boolean markSupported() {
        return is.markSupported();
    }

    @Override
    public synchronized void reset() throws IOException {
        is.reset();
    }

    @Override
    public long skip(long n) throws IOException {
        return is.skip(n);
    }
}
