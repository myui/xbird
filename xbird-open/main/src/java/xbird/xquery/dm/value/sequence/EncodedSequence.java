/*
 * @(#)$Id$
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
package xbird.xquery.dm.value.sequence;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import javax.annotation.Nonnull;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.util.io.FastByteArrayInputStream;
import xbird.util.io.FastByteArrayOutputStream;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.coder.XQEventDecoder;
import xbird.xquery.dm.coder.XQEventEncoder;
import xbird.xquery.dm.value.AbstractSequence;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.IFocus;
import xbird.xquery.type.Type;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class EncodedSequence extends AbstractSequence<Item> implements Externalizable {
    private static final Log LOG = LogFactory.getLog(EncodedSequence.class);

    private byte[] encodedSequence = null;

    @Nonnull
    private transient Sequence actualSequence;
    @Nonnull
    private Type type;

    public EncodedSequence(Sequence delegate, DynamicContext dynEnv) {
        super(dynEnv);
        this.actualSequence = delegate;
        this.type = delegate.getType();
    }

    public Type getType() {
        return type;
    }

    public boolean next(IFocus<Item> focus) throws XQueryException {
        return actualSequence.next(focus);
    }

    public synchronized void doEncode() throws IOException {
        if(encodedSequence != null) {
            final FastByteArrayOutputStream bufOut = new FastByteArrayOutputStream(16384);
            final ObjectOutputStream objOut = new ObjectOutputStream(bufOut);
            final XQEventEncoder encoder = new XQEventEncoder(objOut);
            try {
                encoder.emit(actualSequence);
                objOut.flush();
            } catch (XQueryException xqe) {
                throw new IOException("failed encoding", xqe);
            } catch (Throwable e) {
                LOG.fatal(e);
                throw new IOException("failed encoding", e);
            }
            this.encodedSequence = bufOut.toByteArray();
        }
    }

    private static Sequence decode(final byte[] b) throws IOException {
        FastByteArrayInputStream inputBuf = new FastByteArrayInputStream(b);
        ObjectInputStream objInput = new ObjectInputStream(inputBuf);
        final XQEventDecoder decoder = new XQEventDecoder(objInput);
        try {
            return decoder.decode();
        } catch (XQueryException e) {
            throw new IOException("failed decoding a sequence", e);
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.type = (Type) in.readObject();
        final int len = in.readInt();
        final byte[] b = new byte[len];
        in.read(b, 0, len);
        //this.encodedSequence = b;
        this.actualSequence = decode(b);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        doEncode();
        out.writeObject(type);

        final byte[] b = encodedSequence;
        if(b == null) {
            throw new IllegalStateException();
        }
        final int len = b.length;
        out.writeInt(len);
        out.write(b, 0, len);
    }

}
