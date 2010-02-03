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

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.oreilly.com/catalog/javapt2/
 */
public class TernarySearchTree {
    final static int INITIAL_NODE = 1;
    final static int LOW_OFFSET = 1;
    final static int HIGH_OFFSET = 2;
    final static int EQUAL_OFFSET = 3;
    final static int VALUE_OFFSET = 4;
    final static int NODE_SIZE = 5;

    private transient final char[] buff = new char[5000];
    private int[] nodes;
    private Object[] objects;
    private int nextNode = INITIAL_NODE;
    private int nextObject = 0;

    public TernarySearchTree() {
        this(500000);
    }

    public TernarySearchTree(int size) {
        nodes = new int[NODE_SIZE * size];
        objects = new Object[size];
    }

    public Object get(String key) {
        key.getChars(0, key.length(), buff, 0);
        return search(buff, 0, key.length() - 1);
    }

    public Object put(String key, Object value) {
        key.getChars(0, key.length(), buff, 0);
        if(nextNode == INITIAL_NODE) {
            nodes[INITIAL_NODE] = buff[0];
            nextNode += NODE_SIZE;
        }
        return insert(buff, 0, key.length() - 1, value);
    }

    public void release() {
        nodes = null;
        objects = null;
    }

    public Object search(char[] str, int strIdx, int strMaxIdx) {
        int p = INITIAL_NODE;
        int c;
        while(p != 0) {
            if((c = str[strIdx]) < nodes[p])
                p = nodes[p + LOW_OFFSET];
            else if(c == nodes[p]) {
                if(strIdx == strMaxIdx)
                    return objects[nodes[p + VALUE_OFFSET]];
                else {
                    strIdx++;
                    p = nodes[p + EQUAL_OFFSET];
                }
            } else
                p = nodes[p + HIGH_OFFSET];
        }
        return null;
    }

    public Object insert(char[] str, int strIdx, int strMaxIdx, Object o) {
        int p = INITIAL_NODE;
        int c = str[strIdx];
        Object old;
        for(;;) {
            if(c < nodes[p]) {
                if(nodes[p + LOW_OFFSET] == 0) {
                    nodes[p + LOW_OFFSET] = nextNode;
                    nodes[nextNode] = c;
                    nextNode += NODE_SIZE;
                }
                p = nodes[p + LOW_OFFSET];
            } else if(c == nodes[p]) {
                if(strIdx == strMaxIdx) {
                    if(nodes[p + VALUE_OFFSET] == 0) {
                        nodes[p + VALUE_OFFSET] = nextObject;
                        nextObject++;
                    }
                    old = objects[nodes[p + VALUE_OFFSET]];
                    objects[nodes[p + VALUE_OFFSET]] = o;
                    return old;
                } else {
                    strIdx++;
                    c = str[strIdx];
                    if(nodes[p + EQUAL_OFFSET] == 0) {
                        nodes[p + EQUAL_OFFSET] = nextNode;
                        nodes[nextNode] = c;
                        nextNode += NODE_SIZE;
                    }
                    p = nodes[p + EQUAL_OFFSET];
                }
            } else {
                if(nodes[p + HIGH_OFFSET] == 0) {
                    nodes[p + HIGH_OFFSET] = nextNode;
                    nodes[nextNode] = c;
                    nextNode += NODE_SIZE;
                }
                p = nodes[p + HIGH_OFFSET];
            }
        }
    }
}
