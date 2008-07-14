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
package xbird.engine;

import java.io.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class StampedResult implements Externalizable {
    private static final long serialVersionUID = 4063831147715127502L;

    private/* final */Object _result;
    private/* final */long _created;

    private long _encodingTime = -1L;
    private transient long _decodingTime = -1L;
    private transient long _latency = -1L;

    public StampedResult(Object result) {
        this._result = result;
        this._created = System.currentTimeMillis();
    }

    public StampedResult() {} // Externalizable

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        final long before = System.currentTimeMillis();
        this._result = in.readObject();
        this._decodingTime = System.currentTimeMillis() - before;
        this._encodingTime = in.readLong();
        this._created = in.readLong();
        this._latency = before - in.readLong();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        final long before = System.currentTimeMillis();
        out.writeObject(_result);
        final long curr = System.currentTimeMillis();
        final long encodingTime = curr - before;
        out.writeLong(encodingTime);
        out.writeLong(_created);
        out.writeLong(curr);
    }

    public Object getResult() {
        return _result;
    }

    public long getCreated() {
        return _created;
    }

    public long getLatency() {
        return _latency;
    }

    public long getEncodingTime() {
        return _encodingTime;
    }

    public long getDecodingTime() {
        return _decodingTime;
    }

}
