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
package xbird.xquery.dm.coder;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.SortedSet;
import java.util.TreeSet;

import xbird.util.struct.LongRangeSet;

/**
 * 
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class SerializationContext implements Externalizable {
    private static final long serialVersionUID = -4242982470885381123L;

    private final LongRangeSet _ranges;
    private final SortedSet<Long> _textBufAddrs;

    public SerializationContext() {
        this._ranges = new LongRangeSet();
        this._textBufAddrs = new TreeSet<Long>();
    }

    public LongRangeSet ranges() {
        return _ranges;
    }

    public SortedSet<Long> textBufferAddresses() {
        return _textBufAddrs;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _ranges.readExternal(in);
        final int numTextBufs = in.readInt();
        for(int i = 0; i < numTextBufs; i++) {
            _textBufAddrs.add(in.readLong());
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        _ranges.writeExternal(out);
        final int numTextBufs = _textBufAddrs.size();
        out.writeInt(numTextBufs);
        for(Long addr : _textBufAddrs) {
            out.writeLong(addr.longValue());
        }
    }

}