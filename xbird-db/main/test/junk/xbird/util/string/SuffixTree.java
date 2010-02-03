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
package xbird.util.string;

import java.util.ArrayList;

import xbird.util.lang.Primitives;
import xbird.util.struct.Pointer;

/**
 * A Generalized Suffix Tree which employs Ukkonen's fast tree construction method.
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.csse.monash.edu.au/~lloyd/tildeAlgDS/Tree/Suffix/
 */
public class SuffixTree {
    
    private static final int EQUAL = 0;
    private static final int SKIPPABLE = -1;

    private final int _wordsize;
    private final int _threshold;

    private final Node _root;
    private final ArrayList<byte[]> _elements = new ArrayList<byte[]>();

    public SuffixTree() {
        this(Primitives.TINY_INT_SIZE, 0);
    }

    /**
     * @param threshold Suffixes are not added whenever adding txt is less than threshold. 
     *  Use 0 to disable the threshold.
     */
    public SuffixTree(int wordsize, int threshold) {
        this._wordsize = wordsize;
        this._threshold = threshold;
        this._root = new Node(wordsize);
    }

    public int add(byte[] str) {
        final ArrayList<byte[]> elements = _elements;
        final int fragid = elements.size();
        elements.add(str);

        final Node root = _root;
        final Pointer<Node> parent = new Pointer<Node>(null);
        final int strlen = str.length;
        Node curNode = insertSuffix(root, str, strlen, fragid, 0, parent);
        assert (parent.get() != null);

        final int threshold = _threshold;
        if(strlen >= threshold) {
            for(int i = 1; i + threshold < strlen; i++) {
                final Node c_suffixlink = curNode.suffixlink_;
                final Node p_suffixlink = parent.get().suffixlink_;
                final Node nextNode;
                if(c_suffixlink != null) {
                    nextNode = insertSuffix(c_suffixlink, str, strlen, fragid, i, parent);
                } else if(p_suffixlink != null) {
                    nextNode = insertSuffix(p_suffixlink, str, strlen, fragid, i, parent);
                } else {
                    nextNode = insertSuffix(root, str, strlen, fragid, i, parent);
                }
                curNode.suffixlink_ = nextNode;
                curNode = nextNode;
            }
        }
        return fragid;
    }

    /** 
     * Inserts a string (with all suffixes) into the given tree 
     * and return the corresponding leaf.
     */
    private final Node insertSuffix(final Node subroot, final byte[] str, final int strlen, final int fragid, final int suffix, final Pointer<Node> parent) {
        Node curnode = subroot;
        parent.set(subroot);
        for(int i = subroot.depth_ + suffix;;) {
            if(i == strlen) {
                final Leaf leaf = new Leaf(fragid, suffix, curnode.leaf_);
                curnode.leaf_ = leaf;
                return curnode;
            }
            final int kth = map(str[i]);
            final Node child = curnode.children_[kth];
            if(child == null) { /* add a new node and leaf node */
                final Node node = new Node(_wordsize, fragid, i, strlen - i, strlen - suffix); /* add a node */
                curnode.children_[kth] = node;
                final Leaf leaf = new Leaf(fragid, suffix); /* add a leaf */
                node.leaf_ = leaf;
                parent.set(curnode);
                return node;
            } else { /* split or continue travesing */
                final byte[] arcStr = _elements.get(child.fragId_);
                final int arcOff = child.first_;
                final int lcp = compare(str, i, strlen - 1, arcStr, arcOff, child.length_);
                if(lcp == EQUAL) { /* If the same path is found, add a new entry there. */
                    final Leaf leaf = new Leaf(fragid, suffix, child.leaf_);
                    child.leaf_ = leaf;
                    parent.set(curnode);
                    return child;
                } else if(lcp == SKIPPABLE) { /* suffix which has more long prefix already exists */
                    i += child.length_;
                    parent.set(curnode);
                    curnode = child;
                    continue;
                } else { /* has common prefix */
                    final Node explicitNode = new Node(_wordsize, fragid, child.first_, lcp, curnode.depth_
                            + lcp);
                    final int lth = map(arcStr[lcp]);
                    explicitNode.children_[lth] = child;
                    curnode.children_[kth] = explicitNode;
                    child.first_ += lcp;
                    child.length_ += lcp;
                    i += lcp;
                    parent.set(curnode);
                    curnode = explicitNode;
                    continue;
                }
            }
        }
    }

    /** permit override */
    protected int map(final byte b) {
        return Primitives.toTinyInt(b);
    }

    private static final int compare(final byte[] str, final int strOff, final int strLen, final byte[] arcStr, final int arcOff, final int arcLen) {
        int i = 0;
        for(; i < strLen && i < arcLen; i++) {
            if(str[strOff + i] != arcStr[arcOff + i]) {
                break;
            }
        }
        if(i == strLen && i == arcLen) {
            return EQUAL;
        } else if(i == arcLen) {
            return SKIPPABLE;
        } else {
            return i; /* common prefix */
        }
    }

    private static final class Node {
        final int fragId_;
        int first_;
        int length_;
        final int depth_;

        final Node[] children_;
        Node suffixlink_;
        Leaf leaf_ = null;

        Node(int wordsize) {
            this.fragId_ = 0;
            this.first_ = 0;
            this.length_ = 0;
            this.depth_ = 0;
            this.children_ = new Node[wordsize];
            this.suffixlink_ = Node.this;
        }

        Node(int wordsize, int fragid, int first, int length, int depth) {
            this.fragId_ = fragid;
            this.first_ = first;
            this.length_ = length;
            this.depth_ = depth;
            this.children_ = new Node[wordsize];
        }
    }

    private static final class Leaf {
        final int fragId_;
        final int suffix_;
        final Leaf next_;

        Leaf(int fragid, int suffix) {
            this(fragid, suffix, null);
        }

        Leaf(int fragid, int suffix, Leaf next) {
            this.fragId_ = fragid;
            this.suffix_ = suffix;
            this.next_ = next;
        }
    }
}
