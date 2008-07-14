/*
 * @(#)$Id: XQEventReceiver.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.dm;

import javax.xml.stream.XMLStreamWriter;

import org.xml.sax.helpers.DefaultHandler;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.AtomicValue;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.XQNode;
import xbird.xquery.misc.QNameTable.QualifiedName;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @see XMLStreamWriter
 * @see DefaultHandler
 */
public interface XQEventReceiver {

    boolean isRemotePagingEnabled();

    boolean interveBlanks();

    void setInterveBlanks(boolean enable);

    boolean setBlankRequired(boolean required);

    boolean isBlankRequired();

    void evStartDocument() throws XQueryException;

    void evEndDocument() throws XQueryException;

    void evStartElement(String prefix, String localName, String namespaceURI)
            throws XQueryException;

    void evStartElement(QualifiedName qname) throws XQueryException;

    void evEndElement(QualifiedName qname) throws XQueryException;

    void evEndElement(String prefix, String localName, String namespaceURI) throws XQueryException;

    void evAttribute(QualifiedName qname, String value) throws XQueryException;

    /**
     * @param prefix localPart of namespace decl name.
     */
    void evNamespace(String prefix, String uri) throws XQueryException;

    void evNamespace(QualifiedName prefix, String uri) throws XQueryException;

    void evProcessingInstruction(String target, String data) throws XQueryException;

    void evText(String content) throws XQueryException;

    void evText(char[] ch, int start, int length) throws XQueryException;

    void evCData(String str) throws XQueryException;

    void evCData(char[] ch, int start, int length) throws XQueryException;

    void evComment(String str) throws XQueryException;

    void evComment(char[] ch, int start, int length) throws XQueryException;

    // with node id report

    void evStartElement(long eid, QualifiedName qname) throws XQueryException;

    void evNamespace(long nsid, QualifiedName nsName, String uri) throws XQueryException;

    void evAttribute(long attid, QualifiedName attName, String attValue) throws XQueryException;

    void evEndElement(long eid, QualifiedName qname) throws XQueryException;

    void evNamespace(long nsid, String prefix, String content) throws XQueryException;

    void evText(long tid, String content) throws XQueryException;

    void evText(long tid, char[] ch) throws XQueryException;

    void evComment(long cid, String content) throws XQueryException;

    void evProcessingInstruction(long piid, String target, String content) throws XQueryException;

    void evAtomicValue(AtomicValue atom) throws XQueryException;

    void evNode(XQNode node) throws XQueryException;

    void endItem(boolean last) throws XQueryException;

    void emit(Sequence<? extends Item> seq) throws XQueryException;

}
