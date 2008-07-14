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

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class ObjectOutputStreamAdapter extends ObjectOutputStream {

    protected final ObjectOutputStream out;

    public ObjectOutputStreamAdapter(ObjectOutputStream out) throws IOException {
        super(); // allow calls to writeObjectOverride()
        this.out = out;
    }

    @Override
    protected void writeObjectOverride(Object obj) throws IOException {
        out.writeObject(obj);
    }

    @Override
    public void close() throws IOException {
        out.close();
    }

    @Override
    public void defaultWriteObject() throws IOException {
        out.defaultWriteObject();
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public PutField putFields() throws IOException {
        return out.putFields();
    }

    @Override
    public void reset() throws IOException {
        out.reset();
    }

    @Override
    public void useProtocolVersion(int version) throws IOException {
        out.useProtocolVersion(version);
    }

    @Override
    public void write(byte[] buf, int off, int len) throws IOException {
        out.write(buf, off, len);
    }

    @Override
    public void write(byte[] buf) throws IOException {
        out.write(buf);
    }

    @Override
    public void write(int val) throws IOException {
        out.write(val);
    }

    @Override
    public void writeBoolean(boolean val) throws IOException {
        out.writeBoolean(val);
    }

    @Override
    public void writeByte(int val) throws IOException {
        out.writeByte(val);
    }

    @Override
    public void writeBytes(String str) throws IOException {
        out.writeBytes(str);
    }

    @Override
    public void writeChar(int val) throws IOException {
        out.writeChar(val);
    }

    @Override
    public void writeChars(String str) throws IOException {
        out.writeChars(str);
    }

    @Override
    public void writeDouble(double val) throws IOException {
        out.writeDouble(val);
    }

    @Override
    public void writeFields() throws IOException {
        out.writeFields();
    }

    @Override
    public void writeFloat(float val) throws IOException {
        out.writeFloat(val);
    }

    @Override
    public void writeInt(int val) throws IOException {
        out.writeInt(val);
    }

    @Override
    public void writeLong(long val) throws IOException {
        out.writeLong(val);
    }

    @Override
    public void writeShort(int val) throws IOException {
        out.writeShort(val);
    }

    @Override
    public void writeUnshared(Object obj) throws IOException {
        out.writeUnshared(obj);
    }

    @Override
    public void writeUTF(String str) throws IOException {
        out.writeUTF(str);
    }

}
