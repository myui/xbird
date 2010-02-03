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
package xbird.storage.index;

import java.io.*;
import java.util.Arrays;

import xbird.util.lang.ArrayUtils;
import xbird.util.lang.BitUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.csse.monash.edu.au/~lloyd/tildeAlgDS/Tree/PATRICIA/
 */
public final class PatriciaTrie implements Externalizable {
    private static final long serialVersionUID = -787826284886255210L;

    private/* final */int _pageSize;
    private/* final */byte[] _prefix;
    private/* final */int _prefixBits;

    private/* final */transient int _splitThreshold;
    private/* final */transient int _free;

    private PatriciaNode _root;

    // for Externalizable
    public PatriciaTrie() {}

    public PatriciaTrie(int pageSize) {
        this(pageSize, new byte[0], 0);
    }

    private PatriciaTrie(int pageSize, PatriciaNode parentNode, PatriciaNode root) {
        this(pageSize, parentNode._key, parentNode._bitIndex);
        this._root = root;
    }

    private PatriciaTrie(int pageSize, byte[] prefix, int prefixBits) {
        if(prefix == null) {
            throw new IllegalArgumentException();
        }
        this._pageSize = pageSize;
        this._prefix = prefix;
        this._prefixBits = prefixBits;
        this._splitThreshold = pageSize / 2;
        this._free = (int) ((pageSize - 13 - prefix.length) * 0.9);
    }

    public Value find(final byte[] key) {
        if(!BitUtils.startsWith(key, _prefix, _prefixBits)) {
            return null;
        }
        final byte[] actkey;
        if(_prefix.length == 0) {
            actkey = key;
        } else {
            int from = _prefixBits >>> 3;
            actkey = ArrayUtils.copyOfRange(key, from, key.length);
        }
        PatriciaNode node = search(actkey);
        if(node == null) {
            return null;
        }
        return node._value;
    }

    public Value insert(final byte[] key, final Value value) {
        final byte[] actkey;
        if(_prefix.length == 0) {
            actkey = key;
        } else {
            if(!BitUtils.startsWith(key, _prefix, _prefixBits)) {
                throw new IllegalStateException("key '" + Arrays.toString(key)
                        + "' does not have the prefix '" + Arrays.toString(_prefix) + '\'');
            }
            int from = _prefixBits >>> 3;
            actkey = ArrayUtils.copyOfRange(key, from, key.length);
        }
        if(_root == null) {
            PatriciaNode root = new PatriciaNode(actkey);
            PatriciaNode leaf = new PatriciaNode(root, actkey, value);
            if(root.isBitSet(actkey)) {
                root._right = leaf;
            } else {
                root._left = leaf;
            }
            this._root = root;
            return null;
        }
        return insertToTree(actkey, value);
    }

    private Value insertToTree(final byte[] key, final Value value) {
        PatriciaNode parent = blindSearch(key);
        if(parent == null) {
            throw new IllegalStateException("Root node is not found");
        }
        final byte[] nodeKey = parent._key;
        final int diffBit = getFirstDifferingBitPosition(nodeKey, key);
        final int keyBits = key.length << 3;
        if(diffBit == keyBits && nodeKey.length == key.length) { // Is the key already in the tree? 
            return parent.putValue(value);
        }
        // find appropriate place to insert the key.
        PatriciaNode subtree = _root;
        while(subtree != null && !subtree.isLeaf()) {
            final int bi = subtree._bitIndex;
            if(bi <= diffBit) {
                parent = subtree;
                subtree = BitUtils.isBitSet(key, bi) ? parent._right : parent._left;
            } else {
                break; // found to replace
            }
        }
        // create new node
        final PatriciaNode newNode = new PatriciaNode(parent, key, diffBit);
        final PatriciaNode valueNode = new PatriciaNode(newNode, key, value);
        if(newNode.isBitSet(key)) {
            assert (newNode.isBitSet(subtree._key));
            subtree._parent = newNode;
            newNode._left = subtree;
            newNode._right = valueNode;
        } else {
            assert (!newNode.isBitSet(subtree._key));
            subtree._parent = newNode;
            newNode._left = valueNode;
            newNode._right = subtree;
        }
        // reconnect to the tree
        if(parent.isBitSet(key)) {
            parent._right = newNode;
        } else {
            parent._left = newNode;
        }
        return null;
    }

    public boolean delete(byte[] key, Value value) {
        /* search a leaf node for the given key */
        if(!BitUtils.startsWith(key, _prefix, _prefixBits)) {
            return false;
        }
        final PatriciaNode node = blindSearch(key);
        if(node == null || ArrayUtils.compareTo(node._key, key) != 0) {
            return false;
        }
        assert (node.isLeaf());

        /* remove operation */
        final boolean deleted = node.removeValue(value);
        if(!deleted) {
            return false;
        }
        // rewire nodes
        PatriciaNode parent = node._parent;
        while(parent != null) {
            final PatriciaNode replaceNode;
            if(parent.isBitSet(key)) {
                parent._right = null;
                replaceNode = parent._left;
            } else {
                parent._left = null;
                replaceNode = parent._right;
            }
            // rewire ancestors
            final PatriciaNode curNode = parent;
            parent = curNode._parent;
            if(parent == null) {
                if(curNode == _root && replaceNode == null) {
                    this._root = null;
                }
                break;
            }
            if(replaceNode == null) {
                continue;
            } else {
                if(parent.isBitSet(key)) {
                    parent._right = replaceNode;
                } else {
                    parent._left = replaceNode;
                }
                break;
            }
        }
        return true;
    }

    private PatriciaNode search(final byte[] key) {
        final PatriciaNode node = blindSearch(key);
        if(node == null || ArrayUtils.compareTo(node._key, key) != 0) {
            return null;
        }
        return node;
    }

    private PatriciaNode blindSearch(final byte[] key) {
        if(_root == null) {
            return null;
        }
        PatriciaNode node = _root;
        while(!node.isLeaf()) {
            final boolean flagOn = node.isBitSet(key);
            if(flagOn) {
                PatriciaNode right = node._right;
                if(right == null) {
                    return node;
                } else {
                    node = right;
                }
            } else {
                PatriciaNode left = node._left;
                if(left == null) {
                    return node;
                } else {
                    node = left;
                }
            }
        }
        return node;
    }

    public PatriciaTrie split(final StringBTree sbtree) {
        assert (!needSplit());
        final int threshold = _splitThreshold;
        PatriciaNode ret = null;
        PatriciaNode next = _root;
        while(!next.isLeaf()) {
            ret = next;
            final int size = next.size();
            if(size > threshold) {
                final PatriciaNode left = next._left;
                final PatriciaNode right = next._right;
                final int lsize = (left == null) ? -1 : (left.isLeaf() ? 0 : left.size());
                final int rsize = (right == null) ? -1 : (right.isLeaf() ? 0 : right.size());
                if(lsize > rsize) {
                    next = left;
                } else {
                    next = right;
                }
            } else {
                break;
            }
        }
        // special cases as if leaf value is too large, and it cannot split the tree.
        if(ret == null) {
            return null;
        } else if(ret == _root) {
            return null;
        }
        final PatriciaTrie newTrie = new PatriciaTrie(_pageSize, ret._parent, ret);
        // TODO
        return newTrie;
    }

    public boolean needSplit() {
        final PatriciaNode root = _root;
        if(root == null) {
            return false;
        }
        final int treeSize = root.size();
        return treeSize > _free;
    }

    private static final class PatriciaNode implements Externalizable {
        private static final long serialVersionUID = -4638980859116632240L;

        /* final */byte[] _key;
        /* final */int _bitIndex;
        /* final */boolean _leaf;

        PatriciaNode _parent;
        PatriciaNode _left, _right;

        Value _value;

        transient int _size = -1;

        // for Externalizable  
        public PatriciaNode() {}

        /** Constructs a root node */
        PatriciaNode(byte[] key) {
            if(key == null) {
                throw new IllegalArgumentException();
            }
            this._key = key;
            this._bitIndex = 0;
            this._leaf = false;
            this._parent = null;
        }

        /** Constructs a leaf node */
        PatriciaNode(PatriciaNode parent, byte[] key, Value value) {
            if(parent == null) {
                throw new IllegalArgumentException();
            }
            if(key == null) {
                throw new IllegalArgumentException();
            }
            if(value == null) {
                throw new IllegalArgumentException();
            }
            this._key = key;
            this._bitIndex = -1;
            this._leaf = true;
            this._value = value;
            this._parent = parent;
        }

        /** Constructs a branch node */
        PatriciaNode(PatriciaNode parent, byte[] key, int bitIndex) {
            if(parent == null) {
                throw new IllegalArgumentException();
            }
            if(key == null) {
                throw new IllegalArgumentException();
            }
            this._key = key;
            this._bitIndex = bitIndex;
            this._leaf = false;
            this._parent = parent;
        }

        boolean isBitSet(byte[] key) {
            return BitUtils.isBitSet(key, _bitIndex);
        }

        Value putValue(Value v) {
            if(_value == null) {
                this._value = v;
                return null;
            } else {
                Value prev = _value;
                this._value = new MultiValue(prev, v);
                return prev;
            }
        }

        boolean removeValue(Value v) {
            if(_value == null) {
                return false;
            } else {
                Value value = _value;
                if(value.equals(v)) {
                    this._value = null;
                    return true;
                } else if(value instanceof MultiValue) {
                    MultiValue mv = (MultiValue) value;
                    return mv.remove(v);
                }
                return false;
            }
        }

        int size() {
            int size = _size;
            if(size == -1) {
                size = estimateSize();
                this._size = size;
            }
            return size;
        }

        private boolean isLeaf() {
            return _leaf;
        }

        @Override
        public String toString() {
            final StringBuilder buf = new StringBuilder();
            this.toString(buf, 0, false, 0);
            return buf.toString();
        }

        private void toString(final StringBuilder buf, final int depth, final boolean left, final int parentOffset) {
            if(_leaf) {
                for(int i = 0; i < depth; i++) {
                    buf.append(' ');
                }
                buf.append("<Leaf align='").append(left ? "left" : "right");
                //buf.append("' keyId='").append(_key);
                buf.append("' keyValue='").append(BitUtils.toString(_key, 0, _key.length << 3, parentOffset, '*')).append("'>\n");
                for(int i = 0; i <= depth; i++) {
                    buf.append(' ');
                }
                buf.append(_value).append('\n');
                for(int i = 0; i < depth; i++) {
                    buf.append(' ');
                }
                buf.append("</Leaf>\n");
            } else {
                for(int i = 0; i < depth; i++) {
                    buf.append(' ');
                }
                if(depth == 0) {
                    buf.append("<Branch");
                } else {
                    buf.append("<Branch align='").append(left ? "left" : "right").append("'");
                }
                //buf.append(" keyId='").append(_key).append('\'');
                buf.append(" keyValue='").append(BitUtils.toString(_key, 0, _bitIndex + 1, parentOffset, '*'));
                buf.append("' bitOffset='").append(_bitIndex).append("'>\n");
                if(_left != null) {
                    _left.toString(buf, depth + 1, true, _bitIndex);
                }
                if(_right != null) {
                    _right.toString(buf, depth + 1, false, _bitIndex);
                }
                for(int i = 0; i < depth; i++) {
                    buf.append(' ');
                }
                buf.append("</Branch>\n");
            }
        }

        private int estimateSize() {
            int size = 1;
            if(_leaf) {
                size += 4;
                size += _key.length;
                size += _value.size();
            } else {
                size += 4;
                size += 2;
                if(_left != null) {
                    size += _left.size();
                }
                if(_right != null) {
                    size += _right.size();
                }
            }
            return size;
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeBoolean(_leaf);
            if(_leaf) {
                out.writeInt(_key.length);
                out.write(_key);
                _value.writeExternal(out);
            } else {
                final int bitIndex = _bitIndex;
                out.writeInt(bitIndex);
                if(_left != null) {
                    out.writeBoolean(true);
                    _left.writeExternal(out);
                } else {
                    out.writeBoolean(false);
                }
                if(_right != null) {
                    out.writeBoolean(true);
                    _right.writeExternal(out);
                } else {
                    out.writeBoolean(false);
                }
            }
        }

        public void readExternal(ObjectInput in) throws IOException {
            final boolean leaf = in.readBoolean();
            this._leaf = leaf;
            if(leaf) {
                final int keylen = in.readInt();
                final byte[] key = new byte[keylen];
                in.read(key);
                this._key = key;
                this._value = Value.readObject(in);
            } else {
                this._bitIndex = in.readInt();
                final boolean hasLeft = in.readBoolean();
                if(hasLeft) {
                    final PatriciaNode left = readObject(in);
                    left._parent = this;
                    this._left = left;
                    this._key = left._key;
                }
                final boolean hasRight = in.readBoolean();
                if(hasRight) {
                    final PatriciaNode right = readObject(in);
                    right._parent = this;
                    this._right = right;
                    if(!hasLeft) {
                        this._key = right._key;
                    }
                }
                assert (_key != null);
            }
        }

        private static PatriciaNode readObject(final ObjectInput in) throws IOException {
            final PatriciaNode n = new PatriciaNode();
            n.readExternal(in);
            return n;
        }

    }

    private static int getFirstDifferingBitPosition(final byte[] trg, final byte[] root) {
        assert (trg != null);
        assert (root != null);
        final int trglen = trg.length;
        final int rootlen = root.length;
        final int limit = Math.min(trglen, rootlen);
        int bytePtr = 0;
        while(bytePtr < limit && trg[bytePtr] == root[bytePtr]) {
            bytePtr++;
        }
        if(bytePtr == limit) {
            int bitPtr = 0;
            if(trglen > rootlen) {
                for(int i = bytePtr; i < trglen; i++) {
                    if(trg[i] != 0) {
                        while(!BitUtils.isBitSet(trg[i], bitPtr)) {
                            ++bitPtr;
                        }
                        break;
                    }
                    bytePtr++;
                }
            } else if(rootlen > trglen) {
                for(int i = bytePtr; i < rootlen; i++) {
                    if(root[i] != 0) {
                        while(!BitUtils.isBitSet(root[i], bitPtr)) {
                            ++bitPtr;
                        }
                        break;
                    }
                    bytePtr++;
                }
            }
            return (bytePtr << 3) + bitPtr;
        } else {
            int bitPtr = 0;
            while(BitUtils.isBitSet(trg[bytePtr], bitPtr) == BitUtils.isBitSet(root[bytePtr], bitPtr)) {
                ++bitPtr;
            }
            return (bytePtr << 3) + bitPtr;
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(_pageSize);
        out.writeInt(_prefix.length);
        out.write(_prefix);
        out.writeInt(_prefixBits);
        if(_root == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            _root.writeExternal(out);
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        final int pageSize = in.readInt();
        this._pageSize = pageSize;
        final int prefixlen = in.readInt();
        final byte[] prefix = new byte[prefixlen];
        in.read(prefix);
        this._prefix = prefix;
        this._prefixBits = in.readInt();
        final boolean hasRoot = in.readBoolean();
        if(hasRoot) {
            this._root = PatriciaNode.readObject(in);
        }
    }

    @Override
    public String toString() {
        final PatriciaNode root = _root;
        return (root != null) ? root.toString() : super.toString();
    }
}
