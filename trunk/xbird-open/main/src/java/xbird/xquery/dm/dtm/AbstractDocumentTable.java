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
package xbird.xquery.dm.dtm;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import xbird.storage.DbCollection;
import xbird.util.io.FastMultiByteArrayOutputStream;
import xbird.util.lang.PrintUtils;
import xbird.util.resource.PropertyMap;
import xbird.util.xml.SAXWriter;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.NodeKind;
import xbird.xquery.dm.instance.DocumentTableModel;
import xbird.xquery.dm.ser.SAXSerializer;
import xbird.xquery.misc.IStringChunk;
import xbird.xquery.misc.QNameTable;
import xbird.xquery.misc.QNameTable.QualifiedName;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class AbstractDocumentTable implements IDocumentTable {
    private static final long serialVersionUID = -7355233782454201238L;

    /* for all nodes */
    /** @see NodeKind */
    private static final int KIND_BITS = 3; /* .....111 */
    private static final int KIND_MASK = (1 << KIND_BITS) - 1;
    private static final int FLAG_FIRST_ENTRY = 1 << 3; /* ....1000 */
    private static final int FLAG_LAST_ENTRY = 1 << 4; /* ...1|0000 */// FIXME currently not used

    /*  for element */

    private static final int FLAG_HAS_CHILD = 1 << 5; /* ..10|0000 */

    private static final int ATTR_COUNT_SHIFT = 6;
    private static final int ATTR_COUNT_BITS = 10;
    /* 0000|0000|0000|0000|1111|1111|1100|0000 */
    private static final int ATTR_COUNT_MASK = ((1 << ATTR_COUNT_BITS) - 1) << ATTR_COUNT_SHIFT;

    private static final int NAMESPACE_COUNT_BITS = 8;
    private static final int NAMESPACE_COUNT_SHIFT = ATTR_COUNT_SHIFT + ATTR_COUNT_BITS;
    /* 0000|0000|1111|1111|0000|0000|0000|0000 */
    private static final int NAMESPACE_COUNT_MASK = ((1 << NAMESPACE_COUNT_BITS) - 1) << NAMESPACE_COUNT_SHIFT;

    //--------------------------------------------
    // in-memory stuff

    protected long _curNode = 0L;
    private transient long _prevNode = -1L;
    private Map<String, String> _nsMap = null;

    protected final AtomicInteger _refcount = new AtomicInteger(1);

    //--------------------------------------------
    // persistent stuff

    protected long _blockPtr;
    protected/* final */QNameTable _nameTable;
    protected/* final */IStringChunk _strChunk;

    //--------------------------------------------

    public AbstractDocumentTable() {}

    public AbstractDocumentTable(final long blockPtr, final QNameTable nameTable, final IStringChunk strChunk) {
        this._blockPtr = blockPtr;
        this._nameTable = nameTable;
        this._strChunk = strChunk;
    }

    public AbstractDocumentTable(final DbCollection coll, final String docName, final PropertyMap docProps) {
        if(docProps == null) {
            this._blockPtr = 0L;
        } else {
            final String ptr = docProps.getProperty(KEY_BLOCK_PTR + docName);
            this._blockPtr = (ptr == null) ? 0L : Long.parseLong(ptr);
        }
        this._nameTable = coll.getSymbols().getQnameTable();
        try {
            this._strChunk = coll.getStringChunk();
        } catch (IOException e) {
            throw new IllegalStateException("failed loading string chunk of the collection: "
                    + coll.getCollectionName(), e);
        }
    }

    public final AtomicInteger getReferenceCount() {
        return _refcount;
    }

    public final QNameTable getNameTable() {
        return _nameTable;
    }

    public final void setDeclaredNamespaces(final Map<String, String> nsmap) {
        this._nsMap = nsmap; //Collections.unmodifiableMap(nsmap);
    }

    public final Map<String, String> getDeclaredNamespaces() {
        return _nsMap;
    }

    public final long openNode(final byte ev) {
        final long newnode = _blockPtr;
        if(_prevNode != -1L) {
            setData(_prevNode + NEXTSIB_OFFSET, newnode);
        }
        if(ev == NodeKind.DOCUMENT) {
            setFlag(newnode, ev | FLAG_FIRST_ENTRY | FLAG_LAST_ENTRY);
        } else {
            setFlag(newnode, ev);
        }
        fastSetData(newnode + PARENT_OFFSET, _curNode);
        this._curNode = newnode;
        this._prevNode = -1L;
        _blockPtr += BLOCKS_PER_NODE;
        return newnode;
    }

    public void closeNode() {
        this._prevNode = _curNode;
        final long parentCandicate = parent(_curNode);
        if(parentCandicate != 0L) {
            if(!hasChildAt(parentCandicate)) {
                fastSetFlag(parentCandicate, FLAG_HAS_CHILD);
                fastSetFlag(_curNode, FLAG_FIRST_ENTRY);
            }
        } else {// document node has child            
            fastSetFlag(0, FLAG_HAS_CHILD);
        }
        this._curNode = parentCandicate;
    }

    public final long putAttribute(final byte ev, final long parent, final int index, final int attsSize) {
        final long newnode = _blockPtr;
        setData(newnode, index == 0 /* first */? (ev | FLAG_FIRST_ENTRY) : ev);
        if(index == (attsSize - 1)) { // last
            fastSetFlag(newnode, FLAG_LAST_ENTRY);
            // increment counter
            final int cntFlag;
            if(ev == NodeKind.ATTRIBUTE) {
                cntFlag = (attsSize << ATTR_COUNT_SHIFT) & ATTR_COUNT_MASK;
            } else if(ev == NodeKind.NAMESPACE) {
                cntFlag = (attsSize << NAMESPACE_COUNT_SHIFT) & NAMESPACE_COUNT_MASK;
            } else {
                throw new IllegalStateException("Illegal node kind: " + NodeKind.resolveName(ev));
            }
            fastSetFlag(parent, cntFlag);
        }
        fastSetData(newnode + PARENT_OFFSET, parent);
        _blockPtr += BLOCKS_PER_NODE;
        return newnode;
    }

    public final byte getNodeKindAt(final long at) {
        return (byte) (dataAt(at) & KIND_MASK);
    }

    public final long firstChild(final long curnode) {
        final long code = dataAt(curnode);
        if(!hasChild(code)) {
            return -1L;
        }
        final int nscnt = getNamespaceCount(code);
        final int attcnt = getAttributeCount(code);
        final long addr = curnode + ((nscnt + attcnt) * BLOCKS_PER_NODE) + BLOCKS_PER_NODE;
        return addr;
    }

    public final long lastChild(final long curnode) {
        long lastchild = firstChild(curnode);
        if(lastchild == -1L) {
            return -1L;
        }
        while(true) {
            final long sb = nextSibling(lastchild);
            if(sb == 0L) {
                return lastchild;
            } else {
                lastchild = sb;
            }
        }
    }

    public final long nextSibling(final long curnode) {
        return dataAt(curnode + NEXTSIB_OFFSET);
    }

    public final long parent(final long curnode) {
        return dataAt(curnode + PARENT_OFFSET);
    }

    public final long previousSibling(final long curnode) {
        if(isFirstAt(curnode)) {
            return -1L;
        }
        final long parent = parent(curnode);
        if(parent == 0L) {
            return -1L;
        }
        for(long prevAddr = curnode - BLOCKS_PER_NODE; prevAddr > 0; prevAddr -= BLOCKS_PER_NODE) {
            final long prevParent = parent(prevAddr);
            if(prevParent == parent) {
                return prevAddr;
            }
        }
        return -1L;
    }

    public final long getAttribute(final long elemid, final int index) {
        final long code = dataAt(elemid);
        final int attcnt = getAttributeCount(code);
        if((attcnt > 0) && (index < attcnt)) {
            final int nscnt = getNamespaceCount(code);
            final long firstAttrAddr = elemid + (nscnt * BLOCKS_PER_NODE);
            final long addr = firstAttrAddr + (BLOCKS_PER_NODE * (index + 1));
            return addr;
        }
        return -1L;
    }

    public final int getAttributeCountAt(final long addr) {
        return getAttributeCount(dataAt(addr));
    }

    public final QualifiedName getAttributeName(final long at) {
        return getNameAt(at + ATTR_NAME_OFFSET);
    }

    public final int getAttributeNameCode(final long at) {
        return (int) dataAt(at + ATTR_NAME_OFFSET);
    }

    public final int getNamespaceCountAt(final long addr) {
        return getNamespaceCount(dataAt(addr));
    }

    public final long getNamespaceDecl(final long elemid, final int index) {
        final long code = dataAt(elemid);
        final byte nodeKind = nodeKind(code);
        if((nodeKind != NodeKind.ELEMENT) && (nodeKind != NodeKind.DOCUMENT)) {
            throw new IllegalStateException("Illegal node kind: " + NodeKind.resolveName(nodeKind));
        }
        final int nscnt = getNamespaceCount(code);
        if(index < nscnt) {
            final long addr = elemid + (BLOCKS_PER_NODE * (index + 1));
            return addr;
        }
        return -1L;
    }

    public final QualifiedName getName(final long at) {
        return getNameAt(at + CONTENT_OFFSET);
    }

    public final int getNameCode(final long at) {
        return (int) dataAt(at + CONTENT_OFFSET);
    }

    public final QualifiedName getNameAt(final long at) {
        return _nameTable.getName((int) dataAt(at));
    }

    public final String getText(final long at) {
        return _strChunk.getString(dataAt(at + CONTENT_OFFSET));
    }

    public final void getText(final long at, final StringBuilder sb) {
        _strChunk.get(dataAt(at + CONTENT_OFFSET), sb);
    }

    public final long getTextPageAddr(final long at) {
        long cid = dataAt(at + CONTENT_OFFSET);
        return _strChunk.getBufferAddress(cid);
    }

    public final String stringValue(final long curnode) {
        final byte nodeKind = getNodeKindAt(curnode);
        switch(nodeKind) {
            case NodeKind.ELEMENT:
            case NodeKind.DOCUMENT:
                // concat descendant text node values
                final long firstChild = firstChild(curnode);
                if(firstChild == -1L) {
                    return ""; // has no descendant text nodes.
                } else {
                    final StringBuilder buf = new StringBuilder(384);
                    final long nextsib = nextSibling(curnode);
                    if(nextsib == 0L) {
                        final long til = _blockPtr;
                        for(long n = firstChild; n < til; n += BLOCKS_PER_NODE) {
                            final long parent = parent(n);
                            if(parent < curnode) {
                                break;
                            }
                            final byte nk = getNodeKindAt(n);
                            if(nk == NodeKind.TEXT) {
                                getText(n, buf);
                            }
                        }
                    } else {
                        for(long n = firstChild; n < nextsib; n += BLOCKS_PER_NODE) {
                            final byte nk = getNodeKindAt(n);
                            if(nk == NodeKind.TEXT) {
                                getText(n, buf);
                            }
                        }
                    }
                    return buf.toString(); // TODO cache
                }
            case NodeKind.ATTRIBUTE:
            case NodeKind.NAMESPACE:
            case NodeKind.PROCESSING_INSTRUCTION:
            case NodeKind.COMMENT:
            case NodeKind.TEXT:
            case NodeKind.CDATA:
                return getText(curnode);
            default:
                throw new IllegalStateException("Illegal node kind: " + nodeKind);
        }
    }

    public final int setAttributeName(final long at, final String nsuri, final String localName) {
        return setNameAt(at + ATTR_NAME_OFFSET, nsuri, localName);
    }

    public final int setAttributeName(final long at, final String nsuri, final String localName, final String prefix) {
        return setNameAt(at + ATTR_NAME_OFFSET, nsuri, localName, prefix);
    }

    public final void setName(final long eid, final int code) {
        setData(eid + CONTENT_OFFSET, code);
    }

    public final int setName(final long at, final String nsuri, final String localName) {
        return setNameAt(at + CONTENT_OFFSET, nsuri, localName);
    }

    public final int setName(final long at, final String nsuri, final String localName, final String prefix) {
        return setNameAt(at + CONTENT_OFFSET, nsuri, localName, prefix);
    }

    private final int setNameAt(final long at, final String nsuri, final String localName) {
        final int nid = _nameTable.regist(nsuri, localName);
        setData(at, nid);
        return nid;
    }

    private final int setNameAt(final long at, final String nsuri, final String localName, final String prefix) {
        final int nameId = _nameTable.regist(nsuri, localName, prefix);
        setData(at, nameId);
        return nameId;
    }

    public final void setAttributeName(final long attid, final int code) {
        setData(attid + ATTR_NAME_OFFSET, code);
    }

    public final long setTextAt(final long at, final char[] ch, final int start, final int length) {
        final long cid = _strChunk.store(ch, start, length);
        setData(at + CONTENT_OFFSET, cid);
        return cid;
    }

    public final long setTextAt(final long at, final String str) {
        final long cid = _strChunk.store(str);
        setData(at + CONTENT_OFFSET, cid);
        return cid;
    }

    private static final byte nodeKind(final long code) {
        return (byte) (code & KIND_MASK);
    }

    private static final boolean hasChild(final long code) {
        return (code & FLAG_HAS_CHILD) != 0;
    }

    private final boolean hasChildAt(final long addr) {
        return (dataAt(addr) & FLAG_HAS_CHILD) != 0L;
    }

    private final boolean isFirstAt(final long addr) {
        return isFirst(dataAt(addr));
    }

    private static final boolean isFirst(final long code) {
        return (code & FLAG_FIRST_ENTRY) != 0;
    }

    private static final int getAttributeCount(final long code) {
        return (int) ((ATTR_COUNT_MASK & code) >> ATTR_COUNT_SHIFT);
    }

    private static final int getNamespaceCount(final long code) {
        return (int) ((NAMESPACE_COUNT_MASK & code) >> NAMESPACE_COUNT_SHIFT);
    }

    protected void fastSetData(final long at, final long value) {
        this.setData(at, value);
    }

    protected abstract void setData(final long at, final long value);

    protected void fastSetFlag(final long at, final int flag) {
        this.setFlag(at, flag);
    }

    protected abstract void setFlag(final long at, final int flag);

    @Override
    public String toString() {
        final DocumentTableModel dtm = new DocumentTableModel(this);
        final FastMultiByteArrayOutputStream baos = new FastMultiByteArrayOutputStream();
        final SAXWriter writer = new SAXWriter(baos);
        final SAXSerializer ser = new SAXSerializer(writer);
        try {
            dtm.export(0, ser);
        } catch (XQueryException e) {
            PrintUtils.prettyPrintStackTrace(e, new PrintStream(baos));
        }
        return baos.toString();
    }

    public void close() throws IOException {
        if(_refcount.getAndDecrement() == 1) {
            _close();
        }
    }

    public void tryClose() throws IOException {
        if(_refcount.get() < 1) {
            _close();
        }
    }

    protected final void _close() throws IOException {
        this._nameTable = null;
        if(_strChunk != null) {
            _strChunk.close();
            this._strChunk = null;
        }
    }
}
