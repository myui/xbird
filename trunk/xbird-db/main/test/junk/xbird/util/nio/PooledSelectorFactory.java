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

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.EmptyStackException;
import java.util.Stack;

import xbird.config.Settings;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class PooledSelectorFactory implements ISelectorFactory {

    public static final int DEFAULT_MAX_SELECTORS = Integer.parseInt(Settings.get("xbird.remote.paging_serv.client.selectors", "12"));

    /**
     * The timeout before we exit.
     */
    private final long timeout;

    private int maxSelectors;
    private final Stack<Selector> selectors;

    public PooledSelectorFactory(int selectors) {
        this(selectors, 5000);
    }

    public PooledSelectorFactory(int selectors, int timeout) {
        this.timeout = timeout;
        this.selectors = new Stack<Selector>();
        try {
            setMaxSelectors(selectors);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Set max selector pool size.
     * @param size max pool size
     */
    public void setMaxSelectors(int size) throws IOException {
        synchronized(selectors) {
            if(size < maxSelectors) {
                reduce(size);
            } else if(size > maxSelectors) {
                grow(size);
            }
            maxSelectors = size;
        }
    }

    /**
     * Returns max selector pool size
     * @return max pool size
     */
    public int getMaxSelectors() {
        return maxSelectors;
    }

    /**
     * Get a exclusive <code>Selector</code>
     * @return <code>Selector</code>
     */
    public Selector getSelector() {
        synchronized(selectors) {
            Selector s = null;
            try {
                if(selectors.size() != 0) {
                    s = selectors.pop();
                }
            } catch (EmptyStackException ex) {
                ;
            }

            int attempts = 0;
            try {
                while(s == null && attempts < 2) {
                    selectors.wait(timeout);
                    try {
                        if(selectors.size() != 0) {
                            s = selectors.pop();
                        }
                    } catch (EmptyStackException ex) {
                        break;
                    }
                    attempts++;
                }
            } catch (InterruptedException ex) {
                ;
            }
            return s;
        }
    }

    /**
     * Return the <code>Selector</code> to the cache
     * @param s <code>Selector</code>
     */
    public void returnSelector(final Selector s) {
        synchronized(selectors) {
            selectors.push(s);
            if(selectors.size() == 1) {
                selectors.notify();
            }
        }
    }

    /**
     * Increase <code>Selector</code> pool size
     */
    private void grow(int size) throws IOException {
        for(int i = 0; i < size - maxSelectors; i++) {
            selectors.add(Selector.open());
        }
    }

    /**
     * Decrease <code>Selector</code> pool size
     */
    private void reduce(int size) {
        for(int i = 0; i < maxSelectors - size; i++) {
            try {
                Selector selector = selectors.pop();
                selector.close();
            } catch (IOException e) {
                ;
            }
        }
    }

    public void close() throws IOException {
        Selector selector;
        while(null != (selector = selectors.pop())) {
            selector.close();
        }
        selectors.clear();
    }

}
