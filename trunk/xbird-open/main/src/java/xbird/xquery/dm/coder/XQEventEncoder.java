/*
 * @(#)$Id: XQEventEncoder.java 3640 2008-04-03 18:12:47Z yui $
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

import java.io.IOException;
import java.io.ObjectOutput;

import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.instance.DataModel;
import xbird.xquery.dm.instance.DocumentTableModel.DTMNodeBase;
import xbird.xquery.dm.ser.Serializer;
import xbird.xquery.dm.value.AtomicValue;
import xbird.xquery.dm.value.XQNode;
import xbird.xquery.misc.QNameTable.QualifiedName;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/2005/WD-xslt-xquery-serialization-20050915/#id-errors
 */
public final class XQEventEncoder extends Serializer {

    public static final boolean FORCE_UTF = System.getProperty("xbird.rmi.force_utf") != null;

    // sax events
    public static final byte EV_START_DOC = (byte) 1;
    public static final byte EV_END_DOC = (byte) 2;
    public static final byte EV_START_ELEM = (byte) 3;
    public static final byte EV_END_ELEM = (byte) 4;
    public static final byte EV_ATTR = (byte) 5;
    public static final byte EV_NAMESPACE = (byte) 6;
    public static final byte EV_TEXT = (byte) 7;
    public static final byte EV_CDATA = (byte) 8;
    public static final byte EV_COMMENT = (byte) 9;
    public static final byte EV_PI = (byte) 10;
    // dm events
    public static final byte EV_ATOM = (byte) 100;
    public static final byte EV_NODE_START = (byte) 101;
    public static final byte EV_NODE_END = (byte) 102;
    public static final byte EV_DTM_NODE = (byte) 103;
    // control events
    public static final byte EV_END_ITEM = (byte) 110;
    public static final byte EV_EOF = Byte.MAX_VALUE; // 127

    private final ObjectOutput output;
    private boolean doRemotePaging = false;

    private final SerializationContext _serContext = new SerializationContext();

    public XQEventEncoder(ObjectOutput output) {
        super();
        this.output = output;
        setInterveBlanks(false);
    }

    public ObjectOutput getOutput() {
        return output;
    }

    public void setRemotePaging(boolean enable) {
        this.doRemotePaging = enable;
    }

    @Override
    public boolean isRemotePagingEnabled() {
        return doRemotePaging;
    }

    public void evStartDocument() throws XQueryException {
        try {
            output.writeByte(EV_START_DOC);
        } catch (IOException e) {
            throw new DynamicError("failed on evStartDocument", e);
        }
    }

    public void evEndDocument() throws XQueryException {
        try {
            output.writeByte(EV_END_DOC);
        } catch (IOException e) {
            throw new DynamicError("failed on evEndDocument", e);
        }
    }

    public void evStartElement(String prefix, String localName, String namespaceURI)
            throws XQueryException {
        try {
            output.writeByte(EV_START_ELEM);
            writeString(output, prefix);
            writeString(output, localName);
            writeString(output, namespaceURI);
        } catch (IOException e) {
            throw new DynamicError("failed on evStartElement", e);
        }
    }

    public void evEndElement(String prefix, String localName, String namespaceURI)
            throws XQueryException {
        try {
            output.writeByte(EV_END_ELEM);
            writeString(output, prefix);
            writeString(output, localName);
            writeString(output, namespaceURI);
        } catch (IOException e) {
            throw new DynamicError("failed on evEndElement", e);
        }
    }

    public void evAttribute(QualifiedName qname, String value) throws XQueryException {
        try {
            output.writeByte(EV_ATTR);
            qname.writeExternal(output);
            writeString(output, value);
        } catch (IOException e) {
            throw new DynamicError("failed on evAttribute", e);
        }
    }

    public void evNamespace(String prefix, String uri) throws XQueryException {
        try {
            output.writeByte(EV_NAMESPACE);
            writeString(output, prefix);
            writeString(output, uri);
        } catch (IOException e) {
            throw new DynamicError("failed on evNamespace", e);
        }
    }

    public void evText(char[] ch, int start, int length) throws XQueryException {
        try {
            output.writeByte(EV_TEXT);
            output.writeInt(length);
            int end = start + length;
            for(int i = start; i < end; i++) {
                output.writeChar(ch[i]);
            }
        } catch (IOException e) {
            throw new DynamicError("failed on evText", e);
        }
    }

    public void evCData(char[] ch, int start, int length) throws XQueryException {
        try {
            output.writeByte(EV_CDATA);
            output.writeInt(length);
            int end = start + length;
            for(int i = start; i < end; i++) {
                output.writeChar(ch[i]);
            }
        } catch (IOException e) {
            throw new DynamicError("failed on evCData", e);
        }
    }

    public void evComment(char[] ch, int start, int length) throws XQueryException {
        try {
            output.writeByte(EV_COMMENT);
            output.writeInt(length);
            int end = start + length;
            for(int i = start; i < end; i++) {
                output.writeChar(ch[i]);
            }
        } catch (IOException e) {
            throw new DynamicError("failed on evComment", e);
        }
    }

    public void evProcessingInstruction(String target, String data) throws XQueryException {
        try {
            output.writeByte(EV_PI);
            writeString(output, target);
            writeString(output, data);
        } catch (IOException e) {
            throw new DynamicError("failed on evProcessingInstruction", e);
        }
    }

    @Override
    public void evNode(XQNode node) throws XQueryException {
        try {
            final DataModel dm = node.getDataModel();
            if(doRemotePaging && dm.isMemoryMappedStore()) {
                output.writeByte(EV_DTM_NODE);
                DTMNodeBase dtmNode = (DTMNodeBase) node;
                dtmNode.writeTo(output, _serContext);
            } else {
                output.writeByte(EV_NODE_START);
                dm.export(node, this);
                output.writeByte(EV_NODE_END);
            }
        } catch (IOException e) {
            throw new DynamicError("failed on evNode", e);
        }
    }

    @Override
    public void evAtomicValue(AtomicValue atom) throws XQueryException {
        try {
            output.writeByte(EV_ATOM);
            AtomicValue.writeAtomicValue(atom, output); // tiny hack: output.writeObject(atom);
        } catch (IOException e) {
            throw new DynamicError("failed to write an atomic-value: " + atom, e);
        }
    }

    public void endItem(boolean last) throws XQueryException {
        try {
            output.writeByte(EV_END_ITEM);
            if(last) {
                output.writeByte(EV_EOF);
            }
        } catch (IOException e) {
            throw new DynamicError("failed on endItem", e);
        }
    }

    @Override
    protected void flushElement() throws XQueryException {}

    private static void writeString(final ObjectOutput out, final String s) throws IOException {
        if(FORCE_UTF) {
            out.writeUTF(s);
            return;
        }
        final int len = s.length();
        out.writeInt(len);
        for(int i = 0; i < len; i++) {
            final int v = s.charAt(i);
            out.write((v >>> 8) & 0xFF);
            out.write((v >>> 0) & 0xFF);
        }
    }

}
