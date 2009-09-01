/*
 * @(#)$Id:QNameTable.java 2335 2007-07-17 04:14:15Z yui $
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
package xbird.xquery.misc;

import java.io.Externalizable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import xbird.storage.DbCollection;
import xbird.storage.DbException;
import xbird.util.concurrent.reference.ReferenceMap;
import xbird.util.concurrent.reference.ReferenceType;
import xbird.util.string.StringUtils;
import xbird.util.xml.XMLUtils;
import xbird.xquery.XQRTException;
import xbird.xquery.expr.path.NodeTest;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class QNameTable implements Externalizable {
    private static final long serialVersionUID = 1L;

    private static final int FLAG_BITS = 1;
    private static final int HAS_PREFIX_FLAG = FLAG_BITS << 31;
    private static final int FLAP_PREFIX = HAS_PREFIX_FLAG - 1;
    private static final int PREFIX_BITS = 4; // prefix is resctricted to 16 per QName
    private static final int NAME_BITS = 32 - FLAG_BITS - PREFIX_BITS; // 27
    private static final int MAX_NAMES = 1 << NAME_BITS; // resctricted to 2^27
    private static final int NAME_MASK = MAX_NAMES - 1;
    private static final int PREFIX_SHIFT = NAME_BITS;

    private static final Map<QualifiedName, QualifiedName> __pendings = new ReferenceMap<QualifiedName, QualifiedName>(ReferenceType.STRONG, ReferenceType.SOFT, 64);
    private final QualifiedName probe = new QualifiedName("", "");
    private boolean _dirty = true;

    //--------------------------------------------
    // persistent stuff

    private Map<QualifiedName, QualifiedName> nameMap;
    private List<QualifiedName> orderedNames;
    private int totalOrderedNames = 0;
    private int estimateSize = 0;
    private List<String> prefixes = new ArrayList<String>(24);

    //--------------------------------------------

    public QNameTable() {}

    public QNameTable(int initialCapacity) {
        this.nameMap = new HashMap<QualifiedName, QualifiedName>(initialCapacity, 0.7f);
        this.orderedNames = new ArrayList<QualifiedName>(initialCapacity);
    }

    public void load(QNameTable symbols) {
        final Collection<QualifiedName> names = symbols.orderedNames;
        for(QualifiedName qn : names) {
            regist(qn);
        }
    }

    public void setDirty(final boolean flag) {
        this._dirty = flag;
    }

    public QualifiedName find(QualifiedName qname) {
        assert (qname != null);
        return find(qname.getNamespaceURI(), qname.getLocalPart());
    }

    public QualifiedName find(String nsuri, String name) {
        probe.nsuri = (nsuri == null) ? XMLUtils.NULL_NS_URI : nsuri;
        assert (name != null);
        probe.localName = name;
        return nameMap.get(probe);
    }

    @Deprecated
    public boolean remove(QualifiedName qname) {
        String nsuri = qname.getNamespaceURI();
        probe.nsuri = (nsuri == null) ? XMLUtils.NULL_NS_URI : nsuri;
        probe.localName = qname.getLocalPart();
        QualifiedName removed = nameMap.remove(probe);
        return qname == removed;
    }

    @Deprecated
    public boolean contains(QualifiedName qname) {
        String nsuri = qname.getNamespaceURI();
        probe.nsuri = (nsuri == null) ? XMLUtils.NULL_NS_URI : nsuri;
        probe.localName = qname.getLocalPart();
        return nameMap.containsKey(probe);
    }

    public int regist(QualifiedName qname) {
        final String nsuri = qname.getNamespaceURI();
        final String lpart = qname.getLocalPart();
        assert (lpart != null);
        return regist((nsuri == null) ? XMLUtils.NULL_NS_URI : nsuri, lpart);
    }

    public int regist(String nsuri, String name) {
        probe.nsuri = (nsuri == null) ? XMLUtils.NULL_NS_URI : nsuri;
        assert (name != null);
        probe.localName = name;
        final QualifiedName predefined = __pendings.remove(probe);
        if(predefined != null) {
            assert (predefined.id == -1);
            predefined.id = nameMap.size();
            final int eid = put(predefined);
            return eid;
        }
        QualifiedName entry = nameMap.get(probe);
        if(entry == null) {
            entry = new QualifiedName(nsuri, name);
            entry.id = nameMap.size();
            final int eid = put(entry);
            return eid;
        } else {
            return entry.id;
        }
    }

    public int regist(String nsuri, String localName, String prefix) {
        final int nameId = regist(nsuri, localName);
        return bindPrefix(nameId, prefix);
    }

    private int put(QualifiedName entry) {
        assert (entry != null);
        entry.id = nameMap.size();
        assert (entry.id <= MAX_NAMES);
        QualifiedName replaced = nameMap.put(entry, entry);
        assert (replaced == null);
        orderedNames.add(entry);
        ++totalOrderedNames;
        estimateSize += (2 * (entry.getNamespaceURI().length() + entry.getLocalPart().length())) + 4;
        return entry.id;
    }

    private int bindPrefix(int nameId, final String prefix) {
        assert (prefix != null);
        if(prefix.length() == 0) {
            return nameId;
        }
        int prefixCode = prefixes.indexOf(prefix);
        if(prefixCode != -1 && (nameId & HAS_PREFIX_FLAG) != 0) {
            if(prefixCodeEquals(nameId, prefixCode)) {
                return nameId;
            }
            QualifiedName allocated = getName(nameId);
            QualifiedName newName = new QualifiedName(allocated.nsuri, allocated.localName, prefix);
            nameId = nameMap.size();
            QualifiedName replaced = nameMap.put(newName, newName);
            assert (replaced == null);
            orderedNames.add(newName);
            ++totalOrderedNames;
        } else {
            QualifiedName qname = getName(nameId);
            qname.prefix = prefix;
        }
        assert ((nameId & HAS_PREFIX_FLAG) == 0) : "should not have prefix flag: "
                + StringUtils.toBitString(nameId);
        int code = nameId | HAS_PREFIX_FLAG;
        if(prefixCode == -1) {
            prefixCode = prefixes.size();
            prefixes.add(prefix);
        }
        estimateSize += (2 * prefix.length());
        return code | (prefixCode << PREFIX_SHIFT);
    }

    private static boolean prefixCodeEquals(int nameId, int prefixCode) {
        assert ((nameId & HAS_PREFIX_FLAG) != 0);
        final int pindex = (nameId & FLAP_PREFIX) >> PREFIX_SHIFT;
        return pindex == prefixCode;
    }

    public static boolean nameEquals(final int myNameCode, final int nameCode) {
        if((myNameCode & HAS_PREFIX_FLAG) != 0 || (nameCode & HAS_PREFIX_FLAG) != 0) {
            return (NAME_MASK & myNameCode) == (NAME_MASK & nameCode);
        } else {
            return myNameCode == nameCode;
        }
    }

    public QualifiedName getName(int at) {
        final int addr;
        if((at & HAS_PREFIX_FLAG) != 0) {
            addr = (at & NAME_MASK);
        } else {
            addr = at;
        }
        final int last = totalOrderedNames - 1;
        if(at > last) {
            throw new NoSuchElementException("element at #" + at + " not found, last=" + last + '.');
        }
        return orderedNames.get(addr);
    }

    public void clear() {
        nameMap.clear();
    }

    public int estimateMemorySize() {
        return estimateSize;
    }

    public String getPrefix(int id) {
        if((id & HAS_PREFIX_FLAG) == 0) {
            return "";
        }
        return prefixes.get(getPrefixCode(id));
    }

    public int getPrefixCode(int id) {
        assert ((id & HAS_PREFIX_FLAG) != 0);
        final int pindex = (id & FLAP_PREFIX) >> PREFIX_SHIFT;
        assert (pindex <= (1 << PREFIX_BITS));
        assert (pindex < prefixes.size());
        return pindex;
    }

    public static QualifiedName instantiate(String nsuri, String localName, String prefix) {
        final QualifiedName name = new QualifiedName(nsuri, localName, prefix);
        __pendings.put(name, name);
        return name;
    }

    public static QualifiedName instantiate(String nsuri, String localName) {
        return instantiate(nsuri, localName, XMLConstants.DEFAULT_NS_PREFIX);
    }

    public static final class QualifiedName implements Externalizable {
        private static final long serialVersionUID = 670169881289325639L;

        private int id = -1;
        private String prefix = "";
        private String nsuri, localName;

        public QualifiedName() {}

        private QualifiedName(String nsuri, String localName, String prefix) {
            assert (nsuri != null && localName != null);
            assert (prefix != null);
            if(localName.length() != 0 && !NodeTest.ANY.equals(localName)
                    && !XMLUtils.isNCName(localName)) {
                throw new XQRTException("err:XPST0081", "Illegally specified a non-ncname string as name: "
                        + localName);
            }
            if("*".equals(prefix)) {
                this.nsuri = "*";// hack
            } else {
                this.nsuri = nsuri.intern();
            }
            this.localName = localName.intern();
            this.prefix = prefix;
        }

        private QualifiedName(String nsuri, String localName) {
            this(nsuri, localName, XMLConstants.DEFAULT_NS_PREFIX);
        }

        @Override
        public int hashCode() {
            return nsuri.hashCode() ^ localName.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == this) {
                return true;
            }
            if(obj == null || !(obj instanceof QualifiedName)) {
                return false;
            }
            final QualifiedName trg = (QualifiedName) obj;
            if(id != -1 && trg.id != -1) {
                if((id & HAS_PREFIX_FLAG) != 0 || (trg.id & HAS_PREFIX_FLAG) != 0) {
                    return (NAME_MASK & id) == (NAME_MASK & trg.id);
                } else {
                    return id == trg.id;
                }
            } else {
                return nsuri.equals(trg.getNamespaceURI()) && localName.equals(trg.getLocalPart());
            }
        }

        public int identity() {
            return id;
        }

        public String getNamespaceURI() {
            return nsuri;
        }

        public String getLocalPart() {
            return localName;
        }

        public String getPrefix() {
            return prefix;
        }

        @Override
        public String toString() {
            if(nsuri.length() == 0) {
                return localName;
            } else {
                return '{' + nsuri + '}' + localName;
            }
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeInt(id);
            out.writeUTF(prefix);
            out.writeUTF(nsuri);
            out.writeUTF(localName);
        }

        public void readExternal(ObjectInput in) throws IOException {
            this.id = in.readInt();
            this.prefix = in.readUTF();
            this.nsuri = in.readUTF().intern();
            this.localName = in.readUTF().intern();
        }

        public static QualifiedName readFrom(final ObjectInput in) throws IOException {
            final QualifiedName qname = new QualifiedName();
            qname.readExternal(in);
            return qname;
        }

        public static QName toJavaxQName(final QualifiedName qname) {
            if(qname == null) {
                return null;
            }
            return new QName(qname.nsuri, qname.localName, qname.prefix);
        }

    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(nameMap);
        out.writeObject(orderedNames);
        out.writeInt(estimateSize);
        out.writeObject(prefixes);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.nameMap = (Map<QualifiedName, QualifiedName>) in.readObject();
        this.orderedNames = (List<QualifiedName>) in.readObject();
        this.totalOrderedNames = orderedNames.size();
        this.estimateSize = in.readInt();
        this.prefixes = (List<String>) in.readObject();
    }

    public static QNameTable read(ObjectInput in) throws IOException, ClassNotFoundException {
        QNameTable tbl = new QNameTable();
        tbl.readExternal(in);
        return tbl;
    }

    public synchronized void flush(DbCollection col) throws DbException {
        if(!_dirty) {
            return;
        }
        File file = new File(col.getDirectory(), col.getCollectionName()
                + DbCollection.QNAMES_FILE_SUFFIX);
        if(file.exists()) {
            if(!file.delete()) {
                throw new DbException("Could not delete symbol file: " + file.getAbsolutePath());
            }
        }
        if(file.canWrite()) {
            throw new IllegalStateException("Does not have write permission for "
                    + file.getAbsolutePath());
        }
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(this);
            oos.flush();
            oos.close();
        } catch (FileNotFoundException fe) {
            throw new DbException(fe);
        } catch (IOException ioe) {
            throw new DbException(ioe);
        }
    }

}
