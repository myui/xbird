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
package xbird.xquery.dm.value.sequence;

import java.util.*;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.*;
import xbird.xquery.meta.*;
import xbird.xquery.type.Type;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-semantics/#sec_distinct_docorder_or_atomic_sequence
 */
public final class DistinctDocOrderSequence extends AbstractSequence<XQNode>
        implements INodeSequence<XQNode> {
    private static final long serialVersionUID = -2688821378912850069L;

    private Sequence<XQNode> delegated;
    private final Type type;
    private final boolean reverse;

    private boolean done = false;
    private XQNode[] sorted = null;
    private int totalNodes = -1;

    private int idx = -1;
    private XQNode prevNode = null;

    public DistinctDocOrderSequence(Sequence<XQNode> src, DynamicContext dynEnv, Type type) {
        this(src, dynEnv, type, false);
    }

    public DistinctDocOrderSequence(Sequence<XQNode> src, DynamicContext dynEnv, Type type, boolean reverse) {
        super(dynEnv);
        this.delegated = src;
        this.type = type;
        this.reverse = reverse;
    }

    @Override
    public IFocus<XQNode> iterator() {
        if(!done) {
            sort();
        }
        reset();
        return new Focus<XQNode>(this, _dynEnv);
    }

    private void reset() {
        this.idx = 0;
        this.prevNode = null;
    }

    public boolean next(IFocus<XQNode> focus) throws XQueryException {
        final int last = totalNodes - 1;
        while(true) {
            if(idx > last) {
                return false;
            }
            XQNode curnode = sorted[idx++];
            if(curnode == null) {
                continue;
            } else {
                if(prevNode != null) {
                    int cmp = curnode.compareTo(prevNode);
                    if(cmp == 0) {
                        sorted[idx - 1] = null;
                        continue;
                    } else {
                        focus.setContextItem(curnode);
                        this.prevNode = curnode;
                        return true;
                    }
                } else {
                    focus.setContextItem(curnode);
                    this.prevNode = curnode;
                    return true;
                }
            }
        }
    }

    public Type getType() {
        return type;
    }

    private void sort() {
        XQNode[] nodes = new XQNode[1024];
        ArrayList<XQNode[]> list = null;
        int i = 0;
        final IFocus<XQNode> itor = delegated.iterator();
        for(XQNode n : itor) {
            if(i == 1024) {
                if(list == null) {
                    list = new ArrayList<XQNode[]>(4);
                }
                list.add(nodes);
                nodes = new XQNode[1024];
                i = 0;
            }
            nodes[i++] = n;
        }
        itor.closeQuietly();
        final int total;
        if(list != null) {
            final int len = list.size();
            total = i + (len * 1024);
            final XQNode[] newNodes = new XQNode[total];
            int destPos = 0;
            for(int j = 0; j < len; j++) {
                XQNode[] src = list.get(j);
                System.arraycopy(src, 0, newNodes, destPos, 1024);
                destPos += 1024;
            }
            System.arraycopy(nodes, 0, newNodes, destPos, i);
            nodes = newNodes;
        } else {
            total = i;
        }
        if(reverse) {
            Arrays.sort(nodes, 0, total, ReverseComparator.INSTANCE);
        } else {
            Arrays.sort(nodes, 0, total);
        }
        this.sorted = nodes;
        this.totalNodes = total;
        this.delegated = null;
        this.done = true;
    }

    private static final class ReverseComparator implements Comparator<XQNode> {
        static final ReverseComparator INSTANCE = new ReverseComparator();

        private ReverseComparator() {}

        public int compare(final XQNode n1, final XQNode n2) {
            return n2.compareTo(n1);
        }
    }

    @Override
    public boolean isEmpty() {
        if(done) {
            return totalNodes > 0;
        }
        return delegated.isEmpty();
    }

    public Sequence<XQNode> getSource() {
        return delegated;
    }
}
