/*
 * @(#)$Id: DocumentTableModel.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.dm.instance;

import static xbird.xquery.dm.dtm.IDocumentTable.BLOCKS_PER_NODE;

import java.io.Closeable;
import java.io.Externalizable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.util.concurrent.ConcurrentMap;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import xbird.config.Settings;
import xbird.storage.io.DiskPagedLongCache;
import xbird.util.cache.ILongCache;
import xbird.util.codec.IntCodec;
import xbird.util.concurrent.jsr166.ConcurrentReferenceHashMap;
import xbird.util.concurrent.jsr166.ConcurrentReferenceHashMap.ReferenceType;
import xbird.util.datetime.StopWatch;
import xbird.util.io.FastBufferedInputStream;
import xbird.util.io.FileUtils;
import xbird.util.lang.ClassResolver;
import xbird.util.lang.ObjectUtils;
import xbird.xquery.DynamicError;
import xbird.xquery.XQRTException;
import xbird.xquery.XQueryConstants;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.NodeKind;
import xbird.xquery.dm.XQEventReceiver;
import xbird.xquery.dm.coder.SerializationContext;
import xbird.xquery.dm.coder.XQEventDecoder;
import xbird.xquery.dm.coder.XQEventEncoder;
import xbird.xquery.dm.dtm.DocumentTable;
import xbird.xquery.dm.dtm.DocumentTableBuilder;
import xbird.xquery.dm.dtm.IDocumentTable;
import xbird.xquery.dm.dtm.MemoryMappedDocumentTable;
import xbird.xquery.dm.dtm.PagingProfile;
import xbird.xquery.dm.dtm.PagingProfile.Strategy;
import xbird.xquery.dm.dtm.hooked.IProfiledDocumentTable;
import xbird.xquery.dm.dtm.hooked.ProfiledDocumentTable;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.XQNode;
import xbird.xquery.dm.value.xsi.BooleanValue;
import xbird.xquery.expr.path.NodeTest;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.misc.QNameTable;
import xbird.xquery.misc.QNameTable.QualifiedName;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class DocumentTableModel extends DataModel implements Externalizable {
    private static final long serialVersionUID = 6670281140109393291L;
    private static final Log LOG = LogFactory.getLog(DocumentTableModel.class);

    private static final String profileAccessPattern = System.getProperty("xbird.profile_dtm");
    private static final boolean resolveEntity = Boolean.parseBoolean(Settings.get("xbird.xml.resolve_entity"));
    private static final String HTML_PARSER_CLASS = Settings.get("xbird.xml.html_saxparser", "xbird.util.xml.HTMLSAXParser");
    private static final String TMP_DATA_DIR = Settings.get("xbird.database.tmpdir");

    private static final boolean hasSunXerces;
    static {
        hasSunXerces = ClassResolver.isPresent("com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
    }

    private transient final DocumentTableBuilder _handler;
    private transient final XMLReader _reader;
    private transient boolean _mmapedStore;

    private/* final */IDocumentTable _store;
    private int _docid = -1;

    private static ConcurrentMap<String, MemoryMappedDocumentTable> _remoteDoctblCache = null;

    /**
     * Only for {@link Externalizable}.
     */
    public DocumentTableModel() {
        this._handler = null;
        this._reader = null;
    }

    public DocumentTableModel(boolean parseAsHtml) {
        this(parseAsHtml, resolveEntity);
    }

    public DocumentTableModel(boolean parseAsHtml, boolean resolveEntity) {
        this(profileAccessPattern != null ? getProfiledDocumentTable() : new DocumentTable(), false, parseAsHtml, resolveEntity);
    }

    public DocumentTableModel(IDocumentTable store) {
        this(store, false, false, resolveEntity);
    }

    public DocumentTableModel(IDocumentTable store, boolean loaded) {
        this(store, loaded, false, resolveEntity);
    }

    private DocumentTableModel(IDocumentTable store, boolean loaded, boolean parseAsHtml, boolean resolveEntity) {
        super();
        this._mmapedStore = (store instanceof MemoryMappedDocumentTable);
        this._store = store;
        if(loaded) {
            this._handler = null;
            this._reader = null;
            this._docid = nextDocumentId();
        } else {
            this._handler = new DocumentTableBuilder(store);
            this._reader = getXMLReader(_handler, parseAsHtml, resolveEntity);
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this._docid = in.readInt();
        final IDocumentTable doctbl = (IDocumentTable) in.readObject();
        if(doctbl instanceof MemoryMappedDocumentTable) {
            this._mmapedStore = true;
            MemoryMappedDocumentTable mmDoctbl = (MemoryMappedDocumentTable) doctbl;
            String docId = mmDoctbl.getDocumentIdentifer();
            if(docId != null) {
                setPool(mmDoctbl, docId);
            }
        }
        this._store = doctbl;
    }

    private static synchronized void setPool(MemoryMappedDocumentTable mmDoctbl, String docId)
            throws IOException {
        ConcurrentMap<String, MemoryMappedDocumentTable> cache = _remoteDoctblCache;
        if(cache == null) {
            cache = new ConcurrentReferenceHashMap<String, MemoryMappedDocumentTable>(16, ReferenceType.STRONG, ReferenceType.SOFT);
            _remoteDoctblCache = cache;
        }
        MemoryMappedDocumentTable prevDoctbl = cache.putIfAbsent(docId, mmDoctbl);
        if(prevDoctbl != null) {
            ILongCache<int[]> prevCache = prevDoctbl.getBufferPool();
            if(prevCache != null) {
                mmDoctbl.setBufferPool(prevCache);
            }
        } else {
            File tmpDir = (TMP_DATA_DIR == null) ? null : new File(TMP_DATA_DIR);
            String docname = FileUtils.basename(docId);
            File tmpFile = File.createTempFile(docname, ".tmp", tmpDir);
            tmpFile.deleteOnExit();
            ILongCache<int[]> pool = new DiskPagedLongCache<int[]>(tmpFile, MemoryMappedDocumentTable.CACHED_PAGES, new IntCodec());
            mmDoctbl.setBufferPool(pool);
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(_docid);
        out.writeObject(_store);
    }

    @Deprecated
    public static DocumentTableModel read(ObjectInput in) throws IOException,
            ClassNotFoundException {
        DocumentTableModel model = new DocumentTableModel();
        model.readExternal(in);
        return model;
    }

    @Override
    public boolean isMemoryMappedStore() {
        return _mmapedStore;
    }

    private static DocumentTable getProfiledDocumentTable() {
        return new ProfiledDocumentTable(new File(profileAccessPattern));
    }

    public <T extends DTMNodeBase> T createNode(long id) {
        final byte kind = _store.getNodeKindAt(id);
        return this.<T> createNode(kind, id);
    }

    @SuppressWarnings("unchecked")
    public <T extends DTMNodeBase> T createNode(byte kind, long id) {
        switch(kind) {
            case NodeKind.DOCUMENT:
                return (T) new DTMDocument(this, id);
            case NodeKind.ELEMENT:
                return (T) new DTMElement(this, id);
            case NodeKind.ATTRIBUTE:
                return (T) new DTMAttribute(this, id);
            case NodeKind.NAMESPACE:
                return (T) new DTMNamespace(this, id);
            case NodeKind.COMMENT:
                return (T) new DTMComment(this, id);
            case NodeKind.TEXT:
                return (T) new DTMText(this, id);
            case NodeKind.PROCESSING_INSTRUCTION:
                return (T) new DTMProcessingInstruction(this, id);
            default:
                throw new IllegalStateException("Invalid node kind: " + kind + " for node#" + id);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends DTMNodeBase> T getPrototype(DocumentTableModel model, byte kind, long nid) {
        switch(kind) {
            case NodeKind.ELEMENT:
                return (T) new DTMElement(model, nid);
            case NodeKind.TEXT:
                return (T) new DTMText(model, nid);
            case NodeKind.ATTRIBUTE:
                return (T) new DTMAttribute(model, nid);
            case NodeKind.DOCUMENT:
                return (T) new DTMDocument(model, nid);
            case NodeKind.NAMESPACE:
                return (T) new DTMNamespace(model, nid);
            case NodeKind.COMMENT:
                return (T) new DTMComment(model, nid);
            case NodeKind.PROCESSING_INSTRUCTION:
                return (T) new DTMProcessingInstruction(model, nid);
            default:
                throw new IllegalStateException("Invalid node kind " + kind + " for nid #" + nid);
        }
    }

    public DTMDocument documentNode() {
        return new DTMDocument(this, 0);
    }

    /**
     * Traverses tree in depth first order and reports events.
     */
    public void export(final XQNode curNode, final XQEventReceiver receiver) throws XQueryException {
        this.export(curNode.getPosition(), receiver);
    }

    public void export(final long nodeid, final XQEventReceiver receiver) throws XQueryException {
        final PagingProfile profile = _store.getPagingProfile();
        Strategy origStrategy = null;
        if(profile != null) {
            origStrategy = profile.getStrategy();
            profile.setStrategy(Strategy.serialization);
        }
        final IDocumentTable doctbl = _store;
        doctbl.ensureOpen();
        switch(doctbl.getNodeKindAt(nodeid)) {
            case NodeKind.DOCUMENT:
                receiver.evStartDocument();
                final long firstChild = doctbl.firstChild(nodeid);
                if(firstChild != -1L) {
                    export(firstChild, receiver);
                    long nextSib = doctbl.nextSibling(firstChild);
                    while(nextSib != 0L) {
                        export(nextSib, receiver);
                        nextSib = doctbl.nextSibling(firstChild);
                    }
                }
                receiver.evEndDocument();
                break;
            case NodeKind.ELEMENT:
                final QualifiedName qname = doctbl.getName(nodeid);
                receiver.evStartElement(nodeid, qname);
                // namespace decl
                final int nsdeclCnt = doctbl.getNamespaceCountAt(nodeid);
                for(int i = 0; i < nsdeclCnt; i++) {
                    final long nsid = doctbl.getNamespaceDecl(nodeid, i);
                    final QualifiedName nsName = doctbl.getAttributeName(nsid);
                    final String uri = doctbl.getText(nsid);
                    receiver.evNamespace(nsid, nsName, uri);
                }
                // attribute
                final int attrCnt = doctbl.getAttributeCountAt(nodeid);
                for(int i = 0; i < attrCnt; i++) {
                    final long attid = doctbl.getAttribute(nodeid, i);
                    final QualifiedName attName = doctbl.getAttributeName(attid);
                    final String attValue = doctbl.getText(attid);
                    receiver.evAttribute(attid, attName, attValue);
                }
                final long elemFirstChild = doctbl.firstChild(nodeid);
                if(elemFirstChild != -1L) {
                    export(elemFirstChild, receiver);
                    long nextSib = doctbl.nextSibling(elemFirstChild);
                    while(nextSib != 0L) {
                        export(nextSib, receiver);
                        nextSib = doctbl.nextSibling(nextSib);
                    }
                }
                receiver.evEndElement(nodeid, qname);
                break;
            case NodeKind.ATTRIBUTE:
                receiver.evAttribute(nodeid, doctbl.getAttributeName(nodeid), doctbl.getText(nodeid));
                break;
            case NodeKind.NAMESPACE:
                receiver.evNamespace(nodeid, doctbl.getAttributeName(nodeid), doctbl.getText(nodeid));
                break;
            case NodeKind.TEXT:
                receiver.evText(nodeid, doctbl.getText(nodeid));
                break;
            case NodeKind.COMMENT:
                receiver.evComment(nodeid, doctbl.getText(nodeid));
                break;
            case NodeKind.PROCESSING_INSTRUCTION:
                final QualifiedName pi = doctbl.getName(nodeid);
                receiver.evProcessingInstruction(nodeid, pi.getLocalPart(), pi.getNamespaceURI());
                break;
            default:
                throw new IllegalStateException("Invalid node kind '"
                        + NodeKind.resolveName(doctbl.getNodeKindAt(nodeid)) + "' for node#"
                        + nodeid);
        }
        if(profile != null) {
            profile.setStrategy(origStrategy);
        }
    }

    public void loadDocument(InputStream is) throws XQueryException {
        if(!(is instanceof FastBufferedInputStream)) {
            is = new FastBufferedInputStream(is);
        }
        _handler.init();
        final StopWatch sw = new StopWatch();
        try {
            _reader.parse(new InputSource(is));
        } catch (IOException ie) {
            throw new DynamicError("Invalid source", ie);
        } catch (SAXException se) {
            throw new DynamicError("Parse failed", se);
        }
        if(LOG.isDebugEnabled()) {
            LOG.debug("Elasped time of building a DTM: " + sw.elapsed() + " msec");
        }
        this._docid = nextDocumentId();
        if(_store instanceof IProfiledDocumentTable) {
            ((IProfiledDocumentTable) _store).setProfiling(true);
        }
    }

    public void loadDocument(final InputStream is, final DynamicContext dynEnv)
            throws XQueryException {
        this.loadDocument(is);
    }

    private static final XMLReader getXMLReader(final DocumentTableBuilder handler, final boolean parseAsHtml, final boolean resolveEntity) {
        final XMLReader myReader;
        try {
            if(parseAsHtml) {
                Class clazz = ClassResolver.get(HTML_PARSER_CLASS);
                assert (clazz != null);
                myReader = ObjectUtils.<XMLReader> instantiate(clazz);
            } else {
                final SAXParserFactory factory;
                if(hasSunXerces) {
                    factory = SAXParserFactory.newInstance("com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl", null);
                } else {
                    factory = SAXParserFactory.newInstance();
                }
                factory.setNamespaceAware(true);
                SAXParser parser = factory.newSAXParser();
                myReader = parser.getXMLReader();
            }
        } catch (Exception e) {
            throw new XQRTException("Creating SAX XMLReader failed", e);
        }
        // setup handlers (requires saxHandler)
        myReader.setContentHandler(handler);
        try {
            myReader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
            myReader.setFeature("http://xml.org/sax/features/validation", true); // Validate the document and report validity errors.
            if(!parseAsHtml) {
                myReader.setFeature("http://apache.org/xml/features/validation/dynamic", true); // The parser will validate the document only if a grammar is specified.   
                myReader.setFeature("http://apache.org/xml/features/validation/schema", true); // Turn on XML Schema validation by inserting an XML Schema validator into the pipeline.   
            }
        } catch (Exception e) {
            throw new XQRTException("Configuaring SAX XMLReader failed.", e);
        }
        // setup entity resolver
        if(resolveEntity) {
            org.apache.xml.resolver.CatalogManager catalog = org.apache.xml.resolver.CatalogManager.getStaticManager();
            catalog.setIgnoreMissingProperties(true);
            catalog.setPreferPublic(true);
            catalog.setUseStaticCatalog(false);
            EntityResolver resolver = new org.apache.xml.resolver.tools.CatalogResolver(catalog);
            myReader.setEntityResolver(resolver);
        }
        return myReader;
    }

    public IDocumentTable getDocumentTable() {
        return _store;
    }

    public static abstract class DTMNodeBase extends XQNode implements Externalizable {
        private static final long serialVersionUID = 1L;
        protected DocumentTableModel _model;
        protected IDocumentTable _store;

        private transient Sequence<Item> _toReplace;

        public DTMNodeBase(DocumentTableModel model, long id) {
            super(id);
            this._model = model;
            this._store = model._store;
        }

        public DTMNodeBase() {//for serialization
            super(-1L);
            this._model = null;
            this._store = null;
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            final XQEventEncoder encoder = new XQEventEncoder(out);
            try {
                encoder.emit(this);
            } catch (XQueryException xqe) {
                throw new XQRTException(xqe);
            } catch (Throwable e) {
                LOG.fatal(e);
                throw new IllegalStateException("failed encoding", e);
            }
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            assert (_toReplace == null);
            final XQEventDecoder decoder = new XQEventDecoder(in);
            final Sequence<Item> decoded;
            try {
                decoded = decoder.decode();
            } catch (IOException ioe) {
                throw new XQRTException("decode failed", ioe);
            } catch (XQueryException xqe) {
                throw new XQRTException(xqe);
            } catch (Throwable e) {
                throw new IllegalStateException("decode failed", e);
            }
            this._toReplace = decoded;
        }

        public void writeTo(final ObjectOutput out, final SerializationContext context)
                throws IOException {
            out.writeObject(_model);
            out.writeByte(nodeKind());
            long curr = getPosition();
            out.writeLong(curr);
            long nextsib = _store.nextSibling(_id);
            long last = (nextsib == 0L) ? curr : nextsib;
            out.writeLong(last);
            long[] textBlocks = ((MemoryMappedDocumentTable) _store).referredTextBlocks(curr, last, context);
            out.writeInt(textBlocks.length);
            for(long tb : textBlocks) {
                out.writeLong(tb);
            }
        }

        public static DTMNodeBase readFrom(final ObjectInput in, SerializationContext serContext)
                throws IOException, ClassNotFoundException {
            DocumentTableModel model = (DocumentTableModel) in.readObject();
            byte nodeKind = in.readByte();
            long curr = in.readLong();
            DTMNodeBase node = getPrototype(model, nodeKind, curr);
            long last = in.readLong();
            final int tbSize = in.readInt();
            final long[] textBlocks = new long[tbSize];
            for(int i = 0; i < tbSize; i++) {
                textBlocks[i] = in.readLong();
            }
            MemoryMappedDocumentTable mmtable = (MemoryMappedDocumentTable) node._store;
            mmtable.markReferredBlocks(curr, last, textBlocks, serContext);
            return node;
        }

        public static void redirect(ObjectInput in, ObjectOutput out) throws IOException,
                ClassNotFoundException {
            DocumentTableModel model = (DocumentTableModel) in.readObject();
            out.writeObject(model);
            byte nodeKind = in.readByte();
            out.writeByte(nodeKind);
            long curr = in.readLong();
            out.writeLong(curr);
            long last = in.readLong();
            out.writeLong(last);
            final int tbsize = in.readInt();
            out.writeInt(tbsize);
            final byte[] b = new byte[80];
            int i = 0;
            final int limit = tbsize - 9;
            for(; i < limit; i += 10) {
                in.readFully(b, 0, 80);
                out.write(b, 0, 80);
            }
            for(; i < tbsize; i++) {
                in.readFully(b, 0, 8);
                out.write(b, 0, 8);
            }
        }

        protected Object readResolve() throws ObjectStreamException {
            Sequence replaced = _toReplace;
            this._toReplace = null;
            return replaced;
        }

        public void setInternalTable(DocumentTableModel model) {
            this._model = model;
            this._store = model._store;
        }

        public IDocumentTable documentTable() {
            return _store;
        }

        public DocumentTableModel getDataModel() {
            return _model;
        }

        public String baseUri() {
            return null;
        }

        public DTMNodeBase firstChild() {
            return null;
        }

        public String getContent() {
            return null;
        }

        public int getDocumentId() {
            final DocumentTableModel model = getDataModel();
            return model._docid;
        }

        public DTMDocument getDocumentNode() {
            final DocumentTableModel model = getDataModel();
            return model.createNode(NodeKind.DOCUMENT, 0);
        }

        public String getNamespaceURI() {
            return null;
        }

        public DTMNodeBase lastChild() {
            return null;
        }

        @Override
        public final XQNode following(final boolean firstcall) {
            final PagingProfile profile = _store.getPagingProfile();
            final XQNode node;
            if(profile != null) {
                Strategy origStrategy = profile.getStrategy();
                if(origStrategy != Strategy.nextsib) {
                    assert (origStrategy != null);
                    profile.setStrategy(Strategy.nextsib);
                    node = super.following(firstcall);
                    profile.setStrategy(origStrategy);
                } else {
                    node = super.following(firstcall);
                }
            } else {
                node = super.following(firstcall);
            }
            return node;
        }

        @Override
        public byte nodeKind() {
            return 0;
        }

        public DTMNodeBase nextSibling() {
            final IDocumentTable store = documentTable();
            final long nextsib = store.nextSibling(_id);
            if(nextsib == 0L) {
                return null;
            }
            final DocumentTableModel model = getDataModel();
            return model.createNode(store.getNodeKindAt(nextsib), nextsib);
        }

        public final DTMNodeBase nextSibling(NodeTest filter) {
            if(_id == 0L) {
                return null;
            }
            final IDocumentTable store = documentTable();
            final DocumentTableModel model = getDataModel();
            for(long nid = store.nextSibling(_id); nid != 0L; nid = store.nextSibling(nid)) {
                byte nodekind = store.getNodeKindAt(nid);
                DTMNodeBase probe = getPrototype(model, nodekind, nid);
                if(filter.accepts(probe)) {
                    return model.createNode(probe.nodeKind(), probe.getPosition());
                }
            }
            return null;
        }

        public QualifiedName nodeName() {
            return null;
        }

        public int getNameCode() {
            return -1;
        }

        public DTMElement parent() {
            final DocumentTableModel model = getDataModel();
            if(_id == BLOCKS_PER_NODE) {
                return model.createNode(NodeKind.DOCUMENT, 0);
            }
            final IDocumentTable store = documentTable();
            final long pid = store.parent(_id);
            if(pid == 0L) {
                return null;
            }
            return model.createNode(store.getNodeKindAt(pid), pid);
        }

        public DTMNodeBase previousSibling() {
            final IDocumentTable store = documentTable();
            final long prevSib = store.previousSibling(_id);
            if(prevSib == -1L) {
                return null;
            }
            final DocumentTableModel model = getDataModel();
            return model.createNode(store.getNodeKindAt(prevSib), prevSib);
        }

        public String stringValue() {
            final IDocumentTable store = documentTable();
            return store.stringValue(_id);
        }

        @Override
        public DTMNodeBase clone() {
            if(_cloned++ == 0) {
                return this;
            }
            return (DTMNodeBase) super.cloneWithoutIncr();
        }

    }

    public static class DTMElement extends DTMNodeBase {
        private static final long serialVersionUID = 7884779293497299039L;

        public DTMElement() {//for serialization
            super();
        }

        protected DTMElement(DocumentTableModel model, long id) {
            super(model, id);
        }

        public DTMAttribute attribute(int i) {
            final long attid = getAttribute(i);
            if(attid == -1) {
                return null;
            }
            final DocumentTableModel model = getDataModel();
            return new DTMAttribute(model, attid);
        }

        @Override
        public String baseUri() {
            DTMAttribute base = getAttribute(XMLConstants.XML_NS_URI, "base");
            if(base != null) {
                String uri = base.getContent();
                return uri;
            } else {
                XQNode p = parent();
                while(p != null) {
                    String uri = p.baseUri();
                    if(uri != null) {
                        return uri;
                    }
                    p = p.parent();
                }
                return null;
            }
        }

        @Override
        public DTMNodeBase firstChild() {
            final IDocumentTable store = documentTable();
            final long cid = store.firstChild(_id);
            if(cid == -1) {
                return null;
            }
            final DocumentTableModel model = getDataModel();
            return model.createNode(store.getNodeKindAt(cid), cid);
        }

        public long getAttribute(int i) {
            assert (i >= 0);
            final IDocumentTable store = documentTable();
            return store.getAttribute(_id, i);
        }

        public DTMAttribute getAttribute(String nsuri, String attname) {
            assert (attname != null);
            final IDocumentTable store = documentTable();
            int attcnt = store.getAttributeCountAt(_id);
            for(int i = 0; i < attcnt; i++) {
                long attid = store.getAttribute(_id, i);
                assert (attid != -1);
                assert (store.getNodeKindAt(attid) != NodeKind.ATTRIBUTE);
                QualifiedName qname = store.getAttributeName(attid);
                String attUri = qname.getNamespaceURI();
                String attName = qname.getLocalPart();
                if(nsuri == null || nsuri.equals(attUri)) {
                    if(attname.equals(attName)) {
                        final DocumentTableModel model = getDataModel();
                        return new DTMAttribute(model, attid);
                    }
                }
            }
            return null;
        }

        public int getAttributesCount() {
            final IDocumentTable store = documentTable();
            return store.getAttributeCountAt(_id);
        }

        public long getNamespaceDecl(int i) {
            assert (i >= 0);
            final IDocumentTable store = documentTable();
            return store.getNamespaceDecl(_id, i);
        }

        public DTMNamespace getNamespace(int i) {
            final long nsid = getNamespaceDecl(i);
            if(nsid == -1) {
                return null;
            }
            final DocumentTableModel model = getDataModel();
            return new DTMNamespace(model, nsid);
        }

        public int getNamespaceDeclCount() {
            final IDocumentTable store = documentTable();
            return store.getNamespaceCountAt(_id);
        }

        @Override
        public String getNamespaceURI() {
            return nodeName().getNamespaceURI();
        }

        @Override
        public DTMNodeBase lastChild() {
            final IDocumentTable store = documentTable();
            final long cid = store.lastChild(_id);
            if(cid == -1) {
                return null;
            }
            final DocumentTableModel model = getDataModel();
            return model.createNode(store.getNodeKindAt(cid), cid);
        }

        public boolean nilled() {
            final IDocumentTable store = documentTable();
            long attid;
            for(int i = 0; (attid = getAttribute(i)) != -1; i++) {
                assert (attid != -1);
                byte attkind = store.getNodeKindAt(attid);
                if(attkind == NodeKind.ATTRIBUTE) {
                    QualifiedName qname = store.getAttributeName(attid);
                    String nsuri = qname.getNamespaceURI();
                    String attname = qname.getLocalPart();
                    if(XQueryConstants.XSI_URI.equals(nsuri) && "nil".equals(attname)) {
                        String attval = store.getText(attid);
                        if(attval == null) {
                            return false;
                        }
                        return BooleanValue.toBoolean(attval);
                    }
                }
            }
            return false;
        }

        @Override
        public byte nodeKind() {
            return NodeKind.ELEMENT;
        }

        @Override
        public int getNameCode() {
            final IDocumentTable store = documentTable();
            return store.getNameCode(_id);
        }

        @Override
        public QualifiedName nodeName() {
            final IDocumentTable store = documentTable();
            return store.getName(_id);
        }
    }

    public static class DTMDocument extends DTMElement implements Closeable {
        private static final long serialVersionUID = -457402431467614608L;

        private String documentUri = null;

        public DTMDocument() {//for serialization
            super();
        }

        protected DTMDocument(DocumentTableModel model, long id) {
            super(model, id);
        }

        public DTMAttribute attribute(DTMElement elem, int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public DTMAttribute attribute(int i) {
            throw new UnsupportedOperationException();
        }

        public String documentUri() {
            return documentUri;
        }

        @Override
        public long getAttribute(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public DTMAttribute getAttribute(String nsuri, String attname) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getDocumentId() {
            return _docidCounter;
        }

        @Override
        public DTMDocument getDocumentNode() {
            return this;
        }

        @Override
        public String getNamespaceURI() {
            return null;
        }

        @Override
        public DTMElement getRoot() {
            return this;
        }

        @Override
        public DTMNodeBase nextSibling() {
            return null;
        }

        @Override
        public boolean nilled() {
            return false;
        }

        @Override
        public byte nodeKind() {
            return NodeKind.DOCUMENT;
        }

        @Override
        public QualifiedName nodeName() {
            return null;
        }

        @Override
        public DTMElement parent() {
            return null;
        }

        @Override
        public DTMNodeBase previousSibling() {
            return null;
        }

        public void setDocumentUri(String docUri) {
            this.documentUri = docUri;
        }

        public void close() throws IOException {
            _store.close();
        }
    }

    /**
     * Note: may be used within namespace node.
     */
    public static class DTMAttribute extends DTMNodeBase {
        private static final long serialVersionUID = 7263434327980382798L;

        public DTMAttribute() {//for serialization
            super();
        }

        protected DTMAttribute(DocumentTableModel model, long id) {
            super(model, id);
        }

        @Override
        public String getContent() {
            return _store.getText(_id);
        }

        @Override
        public String getNamespaceURI() {
            return nodeName().getNamespaceURI();
        }

        @Override
        public DTMNodeBase nextSibling() {
            return null;
        }

        @Override
        public byte nodeKind() {
            return NodeKind.ATTRIBUTE;
        }

        @Override
        public QualifiedName nodeName() {
            return _store.getAttributeName(_id);
        }

        @Override
        public int getNameCode() {
            return _store.getAttributeNameCode(_id);
        }

        @Override
        public DTMElement parent() {
            final long eid = _store.parent(_id);
            return _model.createNode(NodeKind.ELEMENT, eid);
        }

        @Override
        public DTMNodeBase previousSibling() {
            return null;
        }
    }

    public static final class DTMNamespace extends DTMAttribute {
        private static final long serialVersionUID = 5955223395627163938L;

        public DTMNamespace() {//for serialization
            super();
        }

        protected DTMNamespace(DocumentTableModel model, long id) {
            super(model, id);
        }

        @Override
        public byte nodeKind() {
            return NodeKind.NAMESPACE;
        }
    }

    public static final class DTMProcessingInstruction extends DTMNodeBase {
        private static final long serialVersionUID = -4129132047857524061L;

        public DTMProcessingInstruction() {//for serialization
            super();
        }

        protected DTMProcessingInstruction(DocumentTableModel model, long id) {
            super(model, id);
        }

        @Override
        public String getContent() {
            return _store.getName(_id).getNamespaceURI();
        }

        public String getTarget() {
            return _store.getName(_id).getLocalPart();
        }

        @Override
        public byte nodeKind() {
            return NodeKind.PROCESSING_INSTRUCTION;
        }

        @Override
        public QualifiedName nodeName() {
            final String target = getTarget();
            return QNameTable.instantiate(XMLConstants.NULL_NS_URI, target);
        }
    }

    public static final class DTMText extends DTMNodeBase {
        private static final long serialVersionUID = -1832518864998703501L;

        public DTMText() {//for serialization
            super();
        }

        protected DTMText(DocumentTableModel model, long id) {
            super(model, id);
        }

        @Override
        public String getContent() {
            return _store.getText(_id);
        }

        @Override
        public byte nodeKind() {
            return NodeKind.TEXT;
        }
    }

    public static final class DTMComment extends DTMNodeBase {
        private static final long serialVersionUID = -2555012444869523835L;

        public DTMComment() {//for serialization
            super();
        }

        protected DTMComment(DocumentTableModel model, long id) {
            super(model, id);
        }

        @Override
        public String getContent() {
            return _store.getText(_id);
        }

        @Override
        public byte nodeKind() {
            return NodeKind.COMMENT;
        }
    }

}
