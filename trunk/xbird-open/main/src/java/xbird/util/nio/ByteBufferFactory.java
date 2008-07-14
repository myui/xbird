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

import java.nio.ByteBuffer;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class ByteBufferFactory {

    private ByteBufferFactory() {}

    public synchronized static ByteBuffer allocateDirectView(ByteBuffer byteBuffer, final int size) {
        if(byteBuffer == null || ((byteBuffer.capacity() - byteBuffer.limit()) < size)) {
            byteBuffer = ByteBuffer.allocateDirect(size);
        }
        byteBuffer.limit(byteBuffer.position() + size);
        ByteBuffer view = byteBuffer.slice();
        byteBuffer.position(byteBuffer.limit());
        return view;
    }

    public synchronized static ByteBuffer allocateView(ByteBuffer byteBuffer, final boolean direct, final int size) {
        if(direct) {
            return allocateDirectView(byteBuffer, size);
        }
        if(byteBuffer == null || ((byteBuffer.capacity() - byteBuffer.limit()) < size)) {
            byteBuffer = ByteBuffer.allocate(size);
        }
        byteBuffer.limit(byteBuffer.position() + size);
        ByteBuffer view = byteBuffer.slice();
        byteBuffer.position(byteBuffer.limit());
        return view;
    }
}
