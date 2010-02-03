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
package xbird.xquery.dm.dtm.hooked;

import java.io.IOException;
import java.util.Map;

import xbird.storage.DbCollection;
import xbird.storage.DbException;
import xbird.storage.io.Segments;
import xbird.xquery.dm.dtm.IDocumentTable;
import xbird.xquery.dm.dtm.PagingProfile;
import xbird.xquery.misc.QNameTable;
import xbird.xquery.misc.QNameTable.QualifiedName;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @deprecated
 */
public abstract class DocumentTableDelegate implements IDocumentTable {

    protected final IDocumentTable _doc;
    
    public DocumentTableDelegate(IDocumentTable doc) {
        this._doc = doc;
    }

    public IDocumentTable getDelegated() {
        return _doc;
    }
    
    public void closeNode() {
        _doc.closeNode();
    }

    public long dataAt(long at) {
        return _doc.dataAt(at);
    }

    public long firstChild(long curnode) {
        return _doc.firstChild(curnode);
    }

    public void flush(DbCollection coll, String docName) throws IOException, DbException {
        _doc.flush(coll, docName);
    }

    public long getAttribute(long elemid, int index) {
        return _doc.getAttribute(elemid, index);
    }

    public int getAttributeCountAt(long addr) {
        return _doc.getAttributeCountAt(addr);
    }

    public QualifiedName getAttributeName(long at) {
        return _doc.getAttributeName(at);
    }

    public int getAttributeNameCode(long at) {
        return _doc.getAttributeNameCode(at);
    }

    public char[] getChars(long at) {
        return _doc.getChars(at);
    }

    public Map<String, String> getDeclaredNamespaces() {
        return _doc.getDeclaredNamespaces();
    }

    public QualifiedName getName(long id) {
        return _doc.getName(id);
    }

    public int getNameCode(long at) {
        return _doc.getNameCode(at);
    }

    public int getNamespaceCountAt(long addr) {
        return _doc.getNamespaceCountAt(addr);
    }

    public long getNamespaceDecl(long elemid, int index) {
        return _doc.getNamespaceDecl(elemid, index);
    }

    public QNameTable getNameTable() {
        return _doc.getNameTable();
    }

    public byte getNodeKindAt(long at) {
        return _doc.getNodeKindAt(at);
    }

    public Segments getPaged(DbCollection coll, String docName) {
        return _doc.getPaged(coll, docName);
    }

    public PagingProfile getPagingProfile() {
        return _doc.getPagingProfile();
    }

    public void getText(long at, StringBuilder sb) {
        _doc.getText(at, sb);
    }

    public String getText(long at) {
        return _doc.getText(at);
    }

    public long lastChild(long curnode) {
        return _doc.lastChild(curnode);
    }

    public long nextSibling(long curnode) {
        return _doc.nextSibling(curnode);
    }

    public long openNode(byte ev) {
        return _doc.openNode(ev);
    }

    public long parent(long curnode) {
        return _doc.parent(curnode);
    }

    public long previousSibling(long curnode) {
        return _doc.previousSibling(curnode);
    }

    public long putAttribute(byte ev, long parent, int index, int attsSize) {
        return _doc.putAttribute(ev, parent, index, attsSize);
    }

    public void setAttributeName(long attid, int i) {
        _doc.setAttributeName(attid, i);
    }

    public int setAttributeName(long attidx, String nsuri, String localName, String prefix) {
        return _doc.setAttributeName(attidx, nsuri, localName, prefix);
    }

    public int setAttributeName(long nsidx, String nsuri, String localName) {
        return _doc.setAttributeName(nsidx, nsuri, localName);
    }

    public void setDeclaredNamespaces(Map<String, String> nsmap) {
        _doc.setDeclaredNamespaces(nsmap);
    }

    public void setName(long at, int code) {
        _doc.setName(at, code);
    }

    public int setName(long elemidx, String nsuri, String localName, String prefix) {
        return _doc.setName(elemidx, nsuri, localName, prefix);
    }

    public int setName(long idx, String nsuri, String localName) {
        return _doc.setName(idx, nsuri, localName);
    }

    public long setTextAt(long index, char[] ch, int start, int length) {
        return _doc.setTextAt(index, ch, start, length);
    }

    public long setTextAt(long nsidx, String str) {
        return _doc.setTextAt(nsidx, str);
    }

    public String stringValue(long nid) {
        return _doc.stringValue(nid);
    }

    public QualifiedName getNameAt(long at) {
        return _doc.getNameAt(at);
    }

}
