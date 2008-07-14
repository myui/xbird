/*
 * @(#)$Id: CommandRequest.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.engine.request;

import java.io.*;
import java.util.Arrays;

import xbird.engine.Request;
import xbird.util.lang.ObjectUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class CommandRequest extends Request implements Externalizable {
    private static final long serialVersionUID = -7524388115564842179L;

    private/* final */String[] _args;
    private String _baseCol = null;

    public CommandRequest() {// for Externalizable
        super(ReturnType.BOOLEAN);
    }

    public CommandRequest(String[] args) {
        super(ReturnType.BOOLEAN);
        if(args == null) {
            throw new IllegalArgumentException();
        }
        this._args = args;
    }

    public CommandRequest(String[] args, String id) {
        super(id, ReturnType.BOOLEAN);
        if(args == null) {
            throw new IllegalArgumentException();
        }
        this._args = args;
    }

    public String[] getArgs() {
        return _args;
    }

    public void setBaseCollection(String colpath) {
        this._baseCol = colpath;
    }

    public String getBaseCollection() {
        return _baseCol;
    }

    @Override
    public Signature getSignature() {
        return Signature.COMMAND;
    }

    @Override
    public String toString() {
        return Arrays.toString(_args);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        final int arglen = in.readInt();
        final String[] args = new String[arglen];
        for(int i = 0; i < arglen; i++) {
            args[i] = ObjectUtils.readString(in);
        }
        final boolean hasBaseCol = in.readBoolean();
        if(hasBaseCol) {
            this._baseCol = ObjectUtils.readString(in);
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        final String[] args = _args;
        final int arglen = args.length;
        out.writeInt(arglen);
        if(arglen > 0) {
            for(String arg : args) {
                ObjectUtils.writeString(out, arg);
            }
        }
        final String basecol = _baseCol;
        final boolean hasBaseCol = (basecol != null);
        out.writeBoolean(hasBaseCol);
        if(hasBaseCol) {
            ObjectUtils.writeString(out, basecol);
        }
    }

}
