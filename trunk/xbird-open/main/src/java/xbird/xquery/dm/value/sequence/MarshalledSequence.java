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
package xbird.xquery.dm.value.sequence;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.config.Settings;
import xbird.util.io.AppendingObjectOutputStream;
import xbird.util.io.FastByteArrayInputStream;
import xbird.util.io.FastByteArrayOutputStream;
import xbird.util.io.FastMultiByteArrayInputStream;
import xbird.util.io.FastMultiByteArrayOutputStream;
import xbird.util.io.NoHeaderObjectInputStream;
import xbird.util.io.TeeOutputStream;
import xbird.util.lang.PrintUtils;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.coder.XDMTreeBuilder;
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
public final class MarshalledSequence extends AbstractSequence<Item> implements Externalizable {
    private static final long serialVersionUID = 296919228793372877L;
    private static final int BUFFERING_BLOCK_SIZE = 16384;
    private static final boolean ENV_SER_PIPED = Boolean.parseBoolean(Settings.get("xbird.remote.ser.piped", "true"));
    private static final boolean DEBUG_DESER_SPEED = System.getProperty("xbird.debug.deser_speed") != null;
    private static final Log LOG = LogFactory.getLog("xbird.remote.ser");

    private/* final */Type _type;
    private/* final */Sequence<Item> _entity;

    private boolean _redirectable = false;
    private boolean _piped = ENV_SER_PIPED;
    private boolean _reaccessable = false; // TODO REVIEWME
    private boolean _remotePaging = false;

    public MarshalledSequence(Sequence<? extends Item> seq, DynamicContext dynEnv) {
        super(dynEnv);
        if(seq == null) {
            throw new IllegalArgumentException();
        }
        if(DEBUG_DESER_SPEED) {
            this._entity = new ValueSequence(seq.materialize(), dynEnv);
        } else {
            this._entity = (Sequence<Item>) seq;
        }
        this._type = seq.getType();
    }

    public MarshalledSequence() { // for Externalizable
        super(DynamicContext.DUMMY);
    }

    public void setRedirectable(boolean redirectable) {
        this._redirectable = redirectable;
    }

    public boolean isRedirectable() {
        return _redirectable;
    }

    public void setPiped(boolean piped) {
        this._piped = piped;
    }

    public void setReaccessable(boolean reaccessable) {
        this._reaccessable = reaccessable;
    }

    public void setRemotePaging(boolean enable) {
        this._remotePaging = enable;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(_type);
        final boolean reaccessable = _reaccessable;
        final boolean redirectable = _redirectable;
        out.writeBoolean(redirectable);
        if(redirectable) {
            out.writeObject(_entity);
        } else {
            if(_entity instanceof IncrDecodedSequnece) {
                out.writeBoolean(true);
                IncrDecodedSequnece incr = ((IncrDecodedSequnece) _entity);
                incr._reaccessable = reaccessable;
                incr.writeExternal(out);
            } else {
                out.writeBoolean(false);
                if(_piped) {
                    out.writeBoolean(true);
                    pipedOut(out, _entity, _remotePaging);
                } else {
                    out.writeBoolean(false);
                    bulkOut(out, _entity, _remotePaging);
                }
            }
        }
        if(!reaccessable) {
            this._entity = null;
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        final Type type = (Type) in.readObject();
        this._type = type;
        final boolean redirectable = in.readBoolean();
        if(redirectable) {
            this._redirectable = true;
            this._entity = (Sequence<Item>) in.readObject();
        } else {
            this._redirectable = false; // just for readability
            final boolean isDecodedSeq = in.readBoolean();
            if(isDecodedSeq) {
                IncrDecodedSequnece entity = IncrDecodedSequnece.readFrom(in);
                entity._type = type;
                this._entity = entity;
            } else {
                final boolean piped = in.readBoolean();
                final XQEventDecoder decoder;
                if(piped) {
                    this._piped = true;
                    decoder = pipedIn(in, _reaccessable);
                } else {
                    this._piped = false;
                    decoder = bulkIn(in); // This is required for nested Object serialization/deserialization                    
                }
                if(DEBUG_DESER_SPEED) {
                    try {
                        this._entity = decoder.decode();
                    } catch (XQueryException e) {
                        throw new IllegalStateException("failed decoding", e);
                    }
                } else {
                    final IncrDecodedSequnece entity = new IncrDecodedSequnece(decoder, type);
                    entity._piped = piped;
                    this._entity = entity;
                }
            }
        }
    }

    @Override
    public IFocus<Item> iterator() {
        IFocus<Item> focus = _entity.iterator();
        return focus;
    }

    public boolean next(IFocus<Item> focus) throws XQueryException {
        final boolean hasNext = _entity.next(focus);
        if(!hasNext) {
            //focus.close();    // may already be closed
            if(!_reaccessable) {
                this._entity = null;
            }
            return false;
        }
        return true;
    }

    public Type getType() {
        return _type;
    }

    private static final class IncrDecodedSequnece extends AbstractSequence<Item>
            implements Externalizable {
        private static final long serialVersionUID = 5692780446373650820L;
        private static final float DECODE_UNIT_GROWTH = 1.4f;

        private final ArrayList<Item> _decodedItems = new ArrayList<Item>(256);
        private final XDMTreeBuilder _treeBuilder;

        private/* final */XQEventDecoder _decoder; // may be null if decoding is already finished
        private/* final */Type _type; // not null

        private transient boolean _decodeFinished = false;
        private transient boolean _piped = false;
        private transient boolean _reaccessable = true;

        private transient int _decodeUnit = 32;

        public IncrDecodedSequnece(XQEventDecoder decoder, Type type) {
            super(DynamicContext.DUMMY);
            this._treeBuilder = new XDMTreeBuilder();
            this._decoder = decoder;
            this._type = type;
        }

        public IncrDecodedSequnece() {
            super(DynamicContext.DUMMY);
            this._treeBuilder = new XDMTreeBuilder();
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            //this._type = (Type) in.readObject();
            final ArrayList<Item> decodedItems = _decodedItems;
            final int numDecoded = in.readInt();
            for(int i = 0; i < numDecoded; i++) {
                Item item = (Item) in.readObject();
                decodedItems.add(item);
            }
            final boolean decodeFinished = in.readBoolean();
            if(decodeFinished) {
                this._decodeFinished = true;
            } else {
                this._decodeFinished = false;
                final boolean piped = in.readBoolean();
                if(piped) {
                    this._piped = true;
                    // avoid readStreamHeader()
                    ObjectInputStream ois = (ObjectInputStream) in; //TODO REVIEWME too hacky
                    this._decoder = pipedIn(new NoHeaderObjectInputStream(ois), _reaccessable);
                } else {
                    this._piped = false;
                    this._decoder = bulkIn(in);
                }
            }
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            //out.writeObject(_type);
            final ArrayList<Item> decodedItems = _decodedItems;
            final int numDecoded = decodedItems.size();
            out.writeInt(numDecoded);
            for(int i = 0; i < numDecoded; i++) {
                Item item = decodedItems.get(i);
                out.writeObject(item);
            }
            final boolean decodeFinished = _decodeFinished;
            if(decodeFinished) {
                out.writeBoolean(true);
            } else {
                out.writeBoolean(false);
                if(_piped && out instanceof ObjectOutputStream) {
                    out.writeBoolean(true);
                    if(_reaccessable) {
                        incrPipedOutReaccessable((ObjectOutputStream) out);
                    } else {
                        incrPipedOut((ObjectOutputStream) out);
                    }
                } else {// rarely comes here
                    if(LOG.isDebugEnabled()) {
                        LOG.debug("Unusually piped in/out streams in BULK mode.");
                    }
                    out.writeBoolean(false);
                    incrBulkOut(out);
                }
            }
        }

        private void incrPipedOut(final ObjectOutputStream out) throws IOException {
            assert (!_reaccessable);
            final XQEventDecoder decoder = _decoder;
            decoder.redirectTo(out);
            decoder.close();
        }

        /**
         * @link http://java.sun.com/javase/technologies/core/basic/serializationFAQ.jsp#appendSerialStream
         */
        private void incrPipedOutReaccessable(final ObjectOutputStream out) throws IOException {
            assert (_reaccessable);
            final FastMultiByteArrayOutputStream bufOut = new FastMultiByteArrayOutputStream(BUFFERING_BLOCK_SIZE);
            final TeeOutputStream tee = new TeeOutputStream(out, bufOut);
            final ObjectOutputStream objectOut = new AppendingObjectOutputStream(tee);
            final XQEventDecoder decoder = _decoder;
            decoder.redirectTo(objectOut);
            decoder.close();
            objectOut.flush();

            // Because input of decoder fully read, this section required for re-object serialization, etc.
            final byte[][] buf = bufOut.toMultiByteArray();
            final int bufTotalSize = bufOut.size();
            bufOut.clear();
            final FastMultiByteArrayInputStream inputBuf = new FastMultiByteArrayInputStream(buf, BUFFERING_BLOCK_SIZE, bufTotalSize);
            if(!_reaccessable) {
                inputBuf.setCleanable(true);
            }
            final ObjectInputStream objInput = new NoHeaderObjectInputStream(inputBuf);
            this._decoder = new XQEventDecoder(objInput); // replace old Decoder with fresh Decoder
        }

        private void incrBulkOut(final ObjectOutput out) throws IOException {
            final FastByteArrayOutputStream bufOut = new FastByteArrayOutputStream(16384);
            final ObjectOutputStream objectOut = new ObjectOutputStream(bufOut);
            final XQEventDecoder decoder = _decoder;
            decoder.redirectTo(objectOut);
            decoder.close();
            objectOut.flush();
            final byte[] buf = bufOut.toByteArray();
            bufOut.clear();

            // Because input of decoder fully read, this section required for re-object serialization, etc.
            if(_reaccessable) {
                final FastByteArrayInputStream inputBuf = new FastByteArrayInputStream(buf);
                final ObjectInputStream objectInput = new ObjectInputStream(inputBuf);
                this._decoder = new XQEventDecoder(objectInput); // replace old Decoder with fresh Decoder
            }

            final int buflen = buf.length;
            if(LOG.isDebugEnabled()) {
                LOG.debug("encoded sequence size: " + buflen);
            }
            out.writeInt(buflen);
            out.write(buf);
        }

        public boolean next(IFocus<Item> focus) throws XQueryException {
            if(focus.reachedEnd()) {
                return false;
            }

            final ArrayList<Item> decodedItems = _decodedItems;
            final int decodedCount = decodedItems.size();
            final int curPos = focus.getContextPosition();
            if(curPos < decodedCount) {
                Item it = decodedItems.get(curPos);
                if(it == null) {
                    throw new IllegalStateException();
                }
                focus.setContextItem(it);
                return true;
            }

            if(_decodeFinished) {
                focus.setReachedEnd(true);
                focus.closeQuietly();
                return false;
            }
            final XDMTreeBuilder treeBuilder = _treeBuilder;
            final int last = _decodeUnit - 1;
            for(int i = 0; i < _decodeUnit; i++) {
                final Item item;
                try {
                    item = _decoder.decodeItem(treeBuilder);
                } catch (IOException e) {
                    throw new XQueryException("failed decoding an Item", e);
                } finally {
                    treeBuilder.reset();
                }
                if(item != null) {
                    decodedItems.add(item);
                    if(i == 0) {
                        focus.setContextItem(item);
                    } else if(i == last) {
                        _decodeUnit *= DECODE_UNIT_GROWTH;
                        return true;
                    }
                } else {
                    this._decodeFinished = true;
                    _decoder.close();
                    if(i > 0) {
                        return true;
                    } else {
                        break;
                    }
                }
            }
            focus.setReachedEnd(true);
            focus.closeQuietly();
            return false;
        }

        public Type getType() {
            return _type;
        }

        public static IncrDecodedSequnece readFrom(ObjectInput in) throws IOException,
                ClassNotFoundException {
            final IncrDecodedSequnece seq = new IncrDecodedSequnece();
            seq.readExternal(in);
            return seq;
        }
    }

    private static XQEventDecoder pipedIn(ObjectInput in, boolean reaccessable) throws IOException {
        final FastMultiByteArrayOutputStream bufOut = new FastMultiByteArrayOutputStream(BUFFERING_BLOCK_SIZE);
        final ObjectOutputStream objectOut = new ObjectOutputStream(bufOut);
        final XQEventDecoder decoder = new XQEventDecoder(in);
        decoder.redirectTo(objectOut);
        objectOut.flush();

        final byte[][] buf = bufOut.toMultiByteArray();
        final int totalBufSize = bufOut.size();
        bufOut.clear();

        // Because input of decoder fully read, this section required for re-object serialization, etc.
        final FastMultiByteArrayInputStream inputBuf = new FastMultiByteArrayInputStream(buf, BUFFERING_BLOCK_SIZE, totalBufSize);
        if(!reaccessable) {
            inputBuf.setCleanable(true);
        }
        final ObjectInputStream objectInput = new ObjectInputStream(inputBuf);
        final XQEventDecoder newDecoder = new XQEventDecoder(objectInput); // replace old Decoder with fresh Decoder
        return newDecoder;
    }

    private static void pipedOut(ObjectOutput out, Sequence<Item> entity, boolean remotePaing)
            throws IOException {
        final XQEventEncoder encoder = new XQEventEncoder(out);
        if(remotePaing) {
            encoder.setRemotePaging(true);
        }
        try {
            encoder.emit(entity);
        } catch (XQueryException xqe) {
            throw new IllegalStateException("failed encoding", xqe);
        } catch (Throwable e) {
            LOG.fatal(PrintUtils.prettyPrintStackTrace(e));
            throw new IllegalStateException("failed encoding", e);
        }
    }

    private static XQEventDecoder bulkIn(final ObjectInput input) throws IOException {
        final int inputLen = input.readInt();
        final byte[] buf = new byte[inputLen];
        input.readFully(buf);
        if(LOG.isDebugEnabled()) {
            LOG.debug("decoding sequence size: " + inputLen);
        }
        FastByteArrayInputStream inputBuf = new FastByteArrayInputStream(buf);
        ObjectInputStream objInput = new ObjectInputStream(inputBuf);
        XQEventDecoder decoder = new XQEventDecoder(objInput);
        return decoder;
    }

    private static void bulkOut(ObjectOutput out, Sequence<Item> entity, boolean remotePaing)
            throws IOException {
        final FastByteArrayOutputStream bufOut = new FastByteArrayOutputStream(16384);
        final ObjectOutputStream objOut = new ObjectOutputStream(bufOut);
        final XQEventEncoder encoder = new XQEventEncoder(objOut);
        if(remotePaing) {
            encoder.setRemotePaging(true);
        }
        try {
            encoder.emit(entity);
        } catch (XQueryException xqe) {
            throw new IllegalStateException("failed encoding", xqe);
        } catch (Throwable e) {
            LOG.fatal(e);
            throw new IllegalStateException("failed encoding", e);
        } finally {
            objOut.flush();
        }
        final byte[] buf = bufOut.toByteArray();
        bufOut.clear();
        final int buflen = buf.length;
        if(LOG.isDebugEnabled()) {
            LOG.debug("encoded sequence size: " + buflen);
        }
        out.writeInt(buflen);
        out.write(buf);
    }

}
