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

import java.io.Closeable;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class CloseableMappedByteBuffer implements Closeable {

    private final AtomicInteger refcount;
    private final MappedByteBuffer delegate;

    public CloseableMappedByteBuffer(MappedByteBuffer delegate) {
        this.refcount = new AtomicInteger(1);
        this.delegate = delegate;
    }

    public MappedByteBuffer getBuffer() {
        return delegate;
    }

    public AtomicInteger referenceCount() {
        return refcount;
    }

    public void close() throws IOException {
        if(refcount.getAndDecrement() == 0) {
            NIOUtils.clean(delegate);
        }
    }

}
