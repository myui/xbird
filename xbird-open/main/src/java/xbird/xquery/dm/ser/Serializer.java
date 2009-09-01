/*
 * @(#)$Id: Serializer.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.dm.ser;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.XQEventReceiver;
import xbird.xquery.dm.instance.DataModel;
import xbird.xquery.dm.value.AtomicValue;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.XQNode;
import xbird.xquery.meta.IFocus;
import xbird.xquery.misc.QNameTable.QualifiedName;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xslt-xquery-serialization/#serparam
 */
public abstract class Serializer implements XQEventReceiver {

    public static final char[] SPACE = " ".toCharArray();
    public static final char[] NEW_LINE = "\n".toCharArray();

    private boolean _interveningBlanks = false;
    private boolean _isBlankRequired = false;

    public Serializer() {
        super();
    }

    public boolean isRemotePagingEnabled() {
        return false;
    }

    public void setInterveBlanks(final boolean enable) {
        this._interveningBlanks = enable;
    }

    public boolean interveBlanks() {
        return _interveningBlanks;
    }

    public void evCData(final String str) throws XQueryException {
        this.evCData(str.toCharArray(), 0, str.length());
    }

    public void evComment(final String str) throws XQueryException {
        this.evComment(str.toCharArray(), 0, str.length());
    }

    public void evEndElement(final QualifiedName qname) throws XQueryException {
        this.evEndElement(qname.getPrefix(), qname.getLocalPart(), qname.getNamespaceURI());
    }

    public void evEndElement(final String uri, final QualifiedName qname) throws XQueryException {
        this.evEndElement(qname.getPrefix(), qname.getLocalPart(), uri);
    }

    public void evNamespace(final QualifiedName prefix, final String uri) throws XQueryException {
        this.evNamespace(prefix.getLocalPart(), uri);
    }

    public void evStartElement(final QualifiedName qname) throws XQueryException {
        this.evStartElement(qname.getPrefix(), qname.getLocalPart(), qname.getNamespaceURI());
    }

    protected abstract void flushElement() throws XQueryException;

    public void evText(String content) throws XQueryException {
        if(content != null) {
            this.evText(content.toCharArray(), 0, content.length());
        }
    }

    public void emit(final Sequence<? extends Item> seq) throws XQueryException {
        emit(seq, false);
    }

    private void emit(final Sequence<? extends Item> seq, boolean isSeq) throws XQueryException {
        if(seq == null) {
            throw new IllegalArgumentException();
        }
        final boolean interveningBlanks = _interveningBlanks;
        boolean init = true;
        final IFocus<? extends Item> itor = seq.iterator();
        while(itor.hasNext()) {
            final Item it = itor.next();
            if(!init) {
                if(isSeq) {
                    if(interveningBlanks) {
                        evText(SPACE, 0, 1);
                    }
                } else {
                    endItem(false);//TODO REVIEWME
                }
            }
            if(it instanceof AtomicValue) {
                evAtomicValue((AtomicValue) it);
                isSeq = true;
            } else if(it instanceof XQNode) {
                evNode((XQNode) it);
                isSeq = false;
            } else {
                emit(it, true);
            }
            init = false;
        }
        itor.closeQuietly();
        endItem(true);
        this._isBlankRequired = false;
    }

    public void evAtomicValue(final AtomicValue atom) throws XQueryException {
        if(_interveningBlanks && _isBlankRequired) {
            evText(SPACE, 0, 1);
            this._isBlankRequired = false;
        }
        evText(atom.stringValue());
    }

    public void evNode(final XQNode node) throws XQueryException {
        final DataModel dm = node.getDataModel();
        dm.export(node, this);
    }

    public void evAttribute(final long attid, final QualifiedName attName, final String attValue)
            throws XQueryException {
        this.evAttribute(attName, attValue);
    }

    public void evEndElement(final long eid, final QualifiedName qname) throws XQueryException {
        this.evEndElement(qname);
    }

    public void evNamespace(final long nsid, final QualifiedName nsName, final String uri)
            throws XQueryException {
        this.evNamespace(nsName, uri);
    }

    public void evStartElement(final long eid, final QualifiedName qname) throws XQueryException {
        this.evStartElement(qname);
    }

    public void evComment(final long cid, final String content) throws XQueryException {
        this.evComment(content);
    }

    public void evNamespace(final long nsid, final String prefix, final String content)
            throws XQueryException {
        this.evNamespace(prefix, content);
    }

    public void evProcessingInstruction(final long piid, final String target, final String content)
            throws XQueryException {
        this.evProcessingInstruction(target, content);
    }

    public void evText(final long tid, final String content) throws XQueryException {
        this.evText(content);
    }

    public final void evText(final long tid, final char[] ch) throws XQueryException {
        this.evText(ch, 0, ch.length);
    }

    public final boolean setBlankRequired(final boolean required) {
        final boolean old = _isBlankRequired;
        this._isBlankRequired = required;
        return old;
    }

    public final boolean isBlankRequired() {
        return _isBlankRequired;
    }

}
