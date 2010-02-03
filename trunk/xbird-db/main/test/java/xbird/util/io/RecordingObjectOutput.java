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
package xbird.util.io;

import java.io.*;

import xbird.util.lang.ObjectUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class RecordingObjectOutput implements ObjectOutput {

    private final ObjectOutput delegate;

    private final FastMultiByteArrayOutputStream buffered;
    private final DataOutputStream dataOutputPipe;

    public RecordingObjectOutput(ObjectOutput out) {
        this(out, 16384);
    }

    public RecordingObjectOutput(ObjectOutput out, int bufSize) {
        this.delegate = out;
        final FastMultiByteArrayOutputStream bufferedOut = new FastMultiByteArrayOutputStream(bufSize);
        this.buffered = bufferedOut;
        this.dataOutputPipe = new DataOutputStream(bufferedOut);
    }

    public byte[] toByteArray() {
        return buffered.toByteArray();
    }
    
    public byte[] toByteArray_clear() {
        return buffered.toByteArray_clear();
    }

    public void write(byte[] b, int off, int len) throws IOException {
        buffered.write(b, off, len);
        delegate.write(b, off, len);
    }

    public void write(byte[] b) throws IOException {
        buffered.write(b);
        delegate.write(b);
    }

    public void write(int b) throws IOException {
        buffered.write(b);
        delegate.write(b);
    }

    public void writeBoolean(boolean v) throws IOException {
        dataOutputPipe.writeBoolean(v);
        delegate.writeBoolean(v);
    }

    public void writeByte(int v) throws IOException {
        dataOutputPipe.writeByte(v);
        delegate.writeByte(v);
    }

    public void writeBytes(String s) throws IOException {
        dataOutputPipe.writeBytes(s);
        delegate.writeBytes(s);
    }

    public void writeChar(int v) throws IOException {
        dataOutputPipe.writeChar(v);
        delegate.writeChar(v);
    }

    public void writeChars(String s) throws IOException {
        dataOutputPipe.writeChars(s);
        delegate.writeChars(s);
    }

    public void writeDouble(double v) throws IOException {
        dataOutputPipe.writeDouble(v);
        delegate.writeDouble(v);
    }

    public void writeFloat(float v) throws IOException {
        dataOutputPipe.writeFloat(v);
        delegate.writeFloat(v);
    }

    public void writeInt(int v) throws IOException {
        dataOutputPipe.writeInt(v);
        delegate.writeInt(v);
    }

    public void writeLong(long v) throws IOException {
        dataOutputPipe.writeLong(v);
        delegate.writeLong(v);
    }

    public void writeShort(int v) throws IOException {
        dataOutputPipe.writeShort(v);
        delegate.writeShort(v);
    }

    public void writeUTF(String s) throws IOException {
        dataOutputPipe.writeUTF(s);
        delegate.writeUTF(s);
    }

    public void writeObject(Object obj) throws IOException {
        final byte[] b = ObjectUtils.toBytes(obj);
        final int len = b.length;
        buffered.writeInt(len);
        buffered.write(b);
        delegate.writeInt(len);
        delegate.write(b);
    }

    public void close() throws IOException {
        try {
            dataOutputPipe.flush();
        } finally {
            dataOutputPipe.close();
            // buffered.close();
        }
        try {
            delegate.flush();
        } finally {
            delegate.close();
        }
        buffered.reset();
    }

    public void flush() throws IOException {
        dataOutputPipe.flush();
        delegate.flush();
    }

    public static final class CustomObjectInput implements ObjectInput {
        private final ObjectInput delegate;

        public CustomObjectInput(final ObjectInput delegate) {
            this.delegate = delegate;
        }

        public Object readObject() throws ClassNotFoundException, IOException {
            final int len = delegate.readInt();
            final byte[] b = new byte[len];
            final Object obj = ObjectUtils.readObjectQuietly(b);
            return obj;
        }

        public int available() throws IOException {
            return delegate.available();
        }

        public void close() throws IOException {
            delegate.close();
        }

        public int read() throws IOException {
            return delegate.read();
        }

        public int read(byte[] b, int off, int len) throws IOException {
            return delegate.read(b, off, len);
        }

        public int read(byte[] b) throws IOException {
            return delegate.read(b);
        }

        public boolean readBoolean() throws IOException {
            return delegate.readBoolean();
        }

        public byte readByte() throws IOException {
            return delegate.readByte();
        }

        public char readChar() throws IOException {
            return delegate.readChar();
        }

        public double readDouble() throws IOException {
            return delegate.readDouble();
        }

        public float readFloat() throws IOException {
            return delegate.readFloat();
        }

        public void readFully(byte[] b, int off, int len) throws IOException {
            delegate.readFully(b, off, len);
        }

        public void readFully(byte[] b) throws IOException {
            delegate.readFully(b);
        }

        public int readInt() throws IOException {
            return delegate.readInt();
        }

        public String readLine() throws IOException {
            return delegate.readLine();
        }

        public long readLong() throws IOException {
            return delegate.readLong();
        }

        public short readShort() throws IOException {
            return delegate.readShort();
        }

        public int readUnsignedByte() throws IOException {
            return delegate.readUnsignedByte();
        }

        public int readUnsignedShort() throws IOException {
            return delegate.readUnsignedShort();
        }

        public String readUTF() throws IOException {
            return delegate.readUTF();
        }

        public long skip(long n) throws IOException {
            return delegate.skip(n);
        }

        public int skipBytes(int n) throws IOException {
            return delegate.skipBytes(n);
        }
    }
}
