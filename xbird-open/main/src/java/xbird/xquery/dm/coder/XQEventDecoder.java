/*
 * @(#)$Id: XQEventDecoder.java 3619 2008-03-26 07:23:03Z yui $
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

import static xbird.xquery.dm.coder.XQEventEncoder.FORCE_UTF;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.instance.DocumentTableModel.DTMNodeBase;
import xbird.xquery.dm.value.AtomicValue;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.XQNode;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.misc.QNameTable.QualifiedName;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class XQEventDecoder {

    private final ObjectInput _input;
    private final SerializationContext _serContext = new SerializationContext();

    public XQEventDecoder(ObjectInput input) {
        this._input = input;
    }

    // TODO support pipeline processing
    public XQEventDecoder(ObjectInputStream input) {
        this._input = input;
    }

    public ObjectInput getInput() {
        return _input;
    }

    public Sequence<Item> decode() throws IOException, XQueryException {
        final XDMTreeBuilder handler = new XDMTreeBuilder();
        final List<Item> items = new ArrayList<Item>(256);
        boolean prevNodeStart = false;
        final ObjectInput input = _input;
        while(true) {
            final byte ev = input.readByte();
            if(ev == XQEventEncoder.EV_EOF) {
                handler.endItem(true);
                break;
            }
            switch(ev) {
                case XQEventEncoder.EV_ATOM: {
                    final AtomicValue atom;
                    try {
                        atom = AtomicValue.readAtomicValue(input);
                    } catch (ClassNotFoundException e) {
                        throw new IllegalStateException(e);
                    }
                    handler.evAtomicValue(atom);
                    items.add(atom);
                    break;
                }
                case XQEventEncoder.EV_DTM_NODE: {
                    final DTMNodeBase dtmNode;
                    try {
                        dtmNode = DTMNodeBase.readFrom(input, _serContext);
                    } catch (ClassNotFoundException e) {
                        throw new IllegalStateException(e);
                    }
                    items.add(dtmNode);
                    break;
                }
                case XQEventEncoder.EV_NODE_START:
                    prevNodeStart = true;
                    continue;
                case XQEventEncoder.EV_NODE_END: {
                    prevNodeStart = false;
                    handler.reset();
                    break;
                }
                case XQEventEncoder.EV_END_ITEM:
                    handler.endItem(false);
                    break;
                case XQEventEncoder.EV_START_DOC:
                    handler.evStartDocument();
                    break;
                case XQEventEncoder.EV_END_DOC:
                    handler.evEndDocument();
                    break;
                case XQEventEncoder.EV_START_ELEM: {
                    String prefix = readString(input);
                    String localName = readString(input);
                    String namespaceURI = readString(input);
                    handler.evStartElement(prefix, localName, namespaceURI);
                    break;
                }
                case XQEventEncoder.EV_END_ELEM: {
                    String prefix = readString(input);
                    String localName = readString(input);
                    String namespaceURI = readString(input);
                    handler.evEndElement(prefix, localName, namespaceURI);
                    break;
                }
                case XQEventEncoder.EV_ATTR: {
                    QualifiedName qname = QualifiedName.readFrom(input);
                    String value = readString(input);
                    handler.evAttribute(qname, value);
                    break;
                }
                case XQEventEncoder.EV_NAMESPACE: {
                    String prefix = readString(input);
                    String uri = readString(input);
                    handler.evNamespace(prefix, uri);
                    break;
                }
                case XQEventEncoder.EV_TEXT: {
                    final int length = input.readInt();
                    final char[] ch = new char[length];
                    for(int i = 0; i < length; i++) {
                        ch[i] = input.readChar();
                    }
                    handler.evText(ch, 0, length);
                    break;
                }
                case XQEventEncoder.EV_CDATA: {
                    final int length = input.readInt();
                    final char[] ch = new char[length];
                    for(int i = 0; i < length; i++) {
                        ch[i] = input.readChar();
                    }
                    handler.evCData(ch, 0, length);
                    break;
                }
                case XQEventEncoder.EV_COMMENT: {
                    final int length = input.readInt();
                    final char[] ch = new char[length];
                    for(int i = 0; i < length; i++) {
                        ch[i] = input.readChar();
                    }
                    handler.evComment(ch, 0, length);
                    break;
                }
                case XQEventEncoder.EV_PI: {
                    String target = readString(input);
                    String data = readString(input);
                    handler.evProcessingInstruction(target, data);
                    break;
                }
                default:
                    throw new IllegalStateException("Illegal event: " + ev);
            }
            if(prevNodeStart) {// harvest nodes
                XQNode node = handler.harvest();
                if(node != null) {
                    items.add(node);
                    prevNodeStart = false;
                }
            }
        }
        if(items.isEmpty()) {
            return ValueSequence.EMPTY_SEQUENCE;
        }
        if(items.size() == 1) {
            return items.get(0);
        }
        return new ValueSequence(items, DynamicContext.DUMMY);
    }

    /**
     * @param null if there are no items.
     */
    public Item decodeItem(final XDMTreeBuilder handler) throws IOException, XQueryException {
        final ObjectInput input = _input;
        while(true) {
            final byte ev = input.readByte();
            switch(ev) {
                case XQEventEncoder.EV_ATOM: {
                    final AtomicValue atom;
                    try {
                        atom = AtomicValue.readAtomicValue(input);
                    } catch (ClassNotFoundException e) {
                        throw new IllegalStateException(e);
                    }
                    return atom;
                }
                case XQEventEncoder.EV_DTM_NODE: {
                    final DTMNodeBase dtmNode;
                    try {
                        dtmNode = DTMNodeBase.readFrom(input, _serContext);
                    } catch (ClassNotFoundException e) {
                        throw new IllegalStateException(e);
                    }
                    return dtmNode;
                }
                case XQEventEncoder.EV_NODE_START:
                    final XQNode node = decodeNode(input, handler, _serContext);
                    return node;
                case XQEventEncoder.EV_TEXT: {
                    final int length = input.readInt();
                    final char[] ch = new char[length];
                    for(int i = 0; i < length; i++) {
                        ch[i] = input.readChar();
                    }
                    handler.evText(ch, 0, length);
                    break;
                }
                case XQEventEncoder.EV_END_ITEM:
                    break; // ignorable
                case XQEventEncoder.EV_EOF:
                    return null;
                default:
                    throw new IllegalStateException("Illegal event: " + ev);
            }
        }
    }

    private static XQNode decodeNode(final ObjectInput input, final XDMTreeBuilder handler, final SerializationContext serContext)
            throws IOException, XQueryException {
        while(true) {
            final byte ev = input.readByte();
            switch(ev) {
                case XQEventEncoder.EV_NODE_END: {
                    final XQNode node = handler.harvest();
                    handler.reset();
                    return node;
                }
                case XQEventEncoder.EV_DTM_NODE: {
                    final DTMNodeBase dtmNode;
                    try {
                        dtmNode = DTMNodeBase.readFrom(input, serContext);
                    } catch (ClassNotFoundException e) {
                        throw new IllegalStateException(e);
                    }
                    return dtmNode;
                }
                case XQEventEncoder.EV_START_DOC:
                    handler.evStartDocument();
                    break;
                case XQEventEncoder.EV_END_DOC:
                    handler.evEndDocument();
                    break;
                case XQEventEncoder.EV_START_ELEM: {
                    String prefix = readString(input);
                    String localName = readString(input);
                    String namespaceURI = readString(input);
                    handler.evStartElement(prefix, localName, namespaceURI);
                    break;
                }
                case XQEventEncoder.EV_END_ELEM: {
                    String prefix = readString(input);
                    String localName = readString(input);
                    String namespaceURI = readString(input);
                    handler.evEndElement(prefix, localName, namespaceURI);
                    break;
                }
                case XQEventEncoder.EV_ATTR: {
                    QualifiedName qname = QualifiedName.readFrom(input);
                    String value = readString(input);
                    handler.evAttribute(qname, value);
                    break;
                }
                case XQEventEncoder.EV_NAMESPACE: {
                    String prefix = readString(input);
                    String uri = readString(input);
                    handler.evNamespace(prefix, uri);
                    break;
                }
                case XQEventEncoder.EV_TEXT: {
                    final int length = input.readInt();
                    final char[] ch = new char[length];
                    for(int i = 0; i < length; i++) {
                        ch[i] = input.readChar();
                    }
                    handler.evText(ch, 0, length);
                    break;
                }
                case XQEventEncoder.EV_CDATA: {
                    final int length = input.readInt();
                    final char[] ch = new char[length];
                    for(int i = 0; i < length; i++) {
                        ch[i] = input.readChar();
                    }
                    handler.evCData(ch, 0, length);
                    break;
                }
                case XQEventEncoder.EV_COMMENT: {
                    final int length = input.readInt();
                    final char[] ch = new char[length];
                    for(int i = 0; i < length; i++) {
                        ch[i] = input.readChar();
                    }
                    handler.evComment(ch, 0, length);
                    break;
                }
                case XQEventEncoder.EV_PI: {
                    String target = readString(input);
                    String data = readString(input);
                    handler.evProcessingInstruction(target, data);
                    break;
                }
                default:
                    throw new IllegalStateException("Illegal event: " + ev);
            }
        }
    }

    public void redirectTo(ObjectOutput out) throws IOException {
        final ObjectInput input = _input;
        while(true) {
            final byte ev = input.readByte();
            out.writeByte(ev);
            if(ev == XQEventEncoder.EV_EOF) {
                return;
            }
            switch(ev) {
                case XQEventEncoder.EV_ATOM: {
                    final AtomicValue atom;
                    try {
                        atom = AtomicValue.readAtomicValue(input);
                    } catch (ClassNotFoundException e) {
                        throw new IllegalStateException(e);
                    }
                    AtomicValue.writeAtomicValue(atom, out);
                    break;
                }
                case XQEventEncoder.EV_DTM_NODE: {
                    try {
                        DTMNodeBase.redirect(input, out);
                    } catch (ClassNotFoundException e) {
                        throw new IllegalStateException(e);
                    }
                    break;
                }
                case XQEventEncoder.EV_NODE_START:
                    break;
                case XQEventEncoder.EV_NODE_END:
                    break;
                case XQEventEncoder.EV_START_DOC:
                    break;
                case XQEventEncoder.EV_END_DOC:
                    break;
                case XQEventEncoder.EV_START_ELEM: {
                    redirectString(input, out);
                    redirectString(input, out);
                    redirectString(input, out);
                    break;
                }
                case XQEventEncoder.EV_END_ELEM: {
                    redirectString(input, out);
                    redirectString(input, out);
                    redirectString(input, out);
                    break;
                }
                case XQEventEncoder.EV_ATTR: {
                    QualifiedName qname = QualifiedName.readFrom(input);
                    qname.writeExternal(out);
                    redirectString(input, out);
                    break;
                }
                case XQEventEncoder.EV_NAMESPACE:
                    redirectString(input, out);
                    redirectString(input, out);
                    break;
                case XQEventEncoder.EV_TEXT: {
                    final int length = input.readInt();
                    out.writeInt(length);
                    for(int i = 0; i < length; i++) {
                        char c = input.readChar();
                        out.writeChar(c);
                    }
                    break;
                }
                case XQEventEncoder.EV_CDATA: {
                    final int length = input.readInt();
                    out.writeInt(length);
                    for(int i = 0; i < length; i++) {
                        char c = input.readChar();
                        out.writeChar(c);
                    }
                    break;
                }
                case XQEventEncoder.EV_COMMENT: {
                    final int length = input.readInt();
                    out.writeInt(length);
                    for(int i = 0; i < length; i++) {
                        char c = input.readChar();
                        out.writeChar(c);
                    }
                    break;
                }
                case XQEventEncoder.EV_PI:
                    redirectString(input, out);
                    redirectString(input, out);
                    break;
                case XQEventEncoder.EV_END_ITEM:
                    break;
                default:
                    throw new IllegalStateException("Illegal event: " + ev);
            }
        }
    }

    public void close() {
        try {
            _input.close();
        } catch (IOException e) {
            ;
        }
    }

    private static String readString(final ObjectInput in) throws IOException {
        if(FORCE_UTF) {
            return in.readUTF();
        }
        final int len = in.readInt();
        final char[] chrs = new char[len];
        for(int i = 0; i < len; i++) {
            chrs[i] = in.readChar();
        }
        return new String(chrs);
    }

    private static void redirectString(final ObjectInput in, final ObjectOutput out)
            throws IOException {
        if(FORCE_UTF) {
            String s = in.readUTF();
            out.writeUTF(s);
        }
        final int len = in.readInt();
        out.writeInt(len);
        for(int i = 0; i < len; i++) {
            char c = in.readChar();
            out.writeChar(c);
        }
    }
}
