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

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.oreilly.com/catalog/javapt2/
 */
public class TernarySearchTree {
    private static final int LOW_OFFSET = 1;
    private static final int HIGH_OFFSET = 2;
    private static final int EQUAL_OFFSET = 3;
    private static final int VALUE_OFFSET = 4;
    private static final int NODE_SIZE = 5;

    static final int INITIAL_TRIE_NODE = 1 + NODE_SIZE;
    static final int INITIAL_NODE = 1;
    static final int TRIE_CHAR_SIZE = 256;

    char[] buff_ = new char[5000];
    int[] nodes_;
    Object[] objects_;
    int nextNode_ = INITIAL_TRIE_NODE;
    int nextObject_ = 0;
    int initial_ = -1;
    Object[] trie1Objects_;
    int[][] trie2_;
    Object[][] trie2Objects_;

    public TernarySearchTree() {
        this(200000);
    }

    public TernarySearchTree(int size) {
        trie1Objects_ = new Object[TRIE_CHAR_SIZE];
        trie2_ = new int[TRIE_CHAR_SIZE][TRIE_CHAR_SIZE];
        trie2Objects_ = new Object[TRIE_CHAR_SIZE][TRIE_CHAR_SIZE];
        nodes_ = new int[NODE_SIZE * size + 1];
        objects_ = new Object[size];
    }

    public Object get(String key) {
        int len = key.length();
        key.getChars(0, len, buff_, 0);
        int first = buff_[0];
        int second = buff_[1];
        if(len == 1 && (first < TRIE_CHAR_SIZE)) {
            return trie1Objects_[first];
        } else if(len == 2 && (first < TRIE_CHAR_SIZE) && (second < TRIE_CHAR_SIZE)) {
            return trie2Objects_[first][second];
        } else if((first < TRIE_CHAR_SIZE) && (second < TRIE_CHAR_SIZE)) {
            int nodep = trie2_[first][second];
            if(nodep == 0) {
                return null;
            }
            return search(buff_, 2, len - 1, nodep);
        } else {
            //Use node[0] as a flag to determine if enetered here
            if(nodes_[0] == 0) {
                return null;
            }
            return search(buff_, 0, len - 1, INITIAL_NODE);
        }
    }
    
    public Object put(String key, Object value) {
        int len = key.length();
        key.getChars(0, len, buff_, 0);
        int first = buff_[0];
        int second = buff_[1];
        if(len == 1 && (first < TRIE_CHAR_SIZE)) {
            Object old = trie1Objects_[first];
            trie1Objects_[first] = value;
            return old;
        } else if(len == 2 && (first < TRIE_CHAR_SIZE) && (second < TRIE_CHAR_SIZE)) {
            Object old = trie2Objects_[first][second];
            trie2Objects_[first][second] = value;
            return old;
        } else if((first < TRIE_CHAR_SIZE) && (second < TRIE_CHAR_SIZE)) {
            int nodep = trie2_[first][second];
            if(nodep == 0) {
                nodep = trie2_[first][second] = nextNode_;
                nodes_[nextNode_] = buff_[2];
                nextNode_ += NODE_SIZE;
            }
            return insert(buff_, 2, len - 1, value, nodep);
        } else {
            //Use node[0] as a flag to determine if enetered here
            if(nodes_[0] == 0) {
                nodes_[0] = 1;
                nodes_[INITIAL_NODE] = first;
            }
            return insert(buff_, 0, len - 1, value, INITIAL_NODE);
        }
    }

    public Object search(char[] str, int strIdx, int strMaxIdx, int p) {
        int c;
        while(p != 0) {
            if((c = str[strIdx]) < nodes_[p])
                p = nodes_[p + LOW_OFFSET];
            else if(c == nodes_[p]) {
                if(strIdx == strMaxIdx)
                    return objects_[nodes_[p + VALUE_OFFSET]];
                else {
                    strIdx++;
                    p = nodes_[p + EQUAL_OFFSET];
                }
            } else
                p = nodes_[p + HIGH_OFFSET];
        }
        return null;
    }

    public Object insert(char[] str, int strIdx, int strMaxIdx, Object value, int p) {
        int c = str[strIdx];
        int cdiff;
        Object old;
        for(;;) {
            if((cdiff = c - nodes_[p]) < 0) {
                if(nodes_[p + LOW_OFFSET] == 0) {
                    nodes_[p + LOW_OFFSET] = nextNode_;
                    nodes_[nextNode_] = c;
                    nextNode_ += NODE_SIZE;
                }
                p = nodes_[p + LOW_OFFSET];
            } else if(cdiff == 0) {
                if(strIdx == strMaxIdx) {
                    if(nodes_[p + VALUE_OFFSET] == 0) {
                        nodes_[p + VALUE_OFFSET] = nextObject_;
                        nextObject_++;
                    }
                    old = objects_[nodes_[p + VALUE_OFFSET]];
                    objects_[nodes_[p + VALUE_OFFSET]] = value;
                    return old;
                } else {
                    strIdx++;
                    c = str[strIdx];
                    if(nodes_[p + EQUAL_OFFSET] == 0) {
                        nodes_[p + EQUAL_OFFSET] = nextNode_;
                        nodes_[nextNode_] = c;
                        nextNode_ += NODE_SIZE;
                    }
                    p = nodes_[p + EQUAL_OFFSET];
                }
            } else {
                if(nodes_[p + HIGH_OFFSET] == 0) {
                    nodes_[p + HIGH_OFFSET] = nextNode_;
                    nodes_[nextNode_] = c;
                    nextNode_ += NODE_SIZE;
                }
                p = nodes_[p + HIGH_OFFSET];
            }
        }
    }

    public int nodeCount() {
        return (nextNode_ - INITIAL_NODE + 1) / NODE_SIZE;
    }

    public void release() {
        nodes_ = null;
        objects_ = null;
    }

}
