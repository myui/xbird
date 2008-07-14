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
package xbird.xquery.dm.labeling;

import xbird.util.codec.UTF8Codec;
import xbird.util.collections.IntArrayList;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class TreeWalker {

    private static final int LABEL_INTERVAL = 1;

    /** Concatination of Reverse Path Identifiers, like '#4#3#1' */
    private final RevPathCoder coder = new RevPathCoder();

    private final IntArrayList label = new IntArrayList(32);
    
    private int localOrder = 1;

    public TreeWalker() {}

    public byte[] getEncodedPath() {
        return coder.encode();
    }

    public RevPathCoder getCoder() {
        return coder;
    }

    public byte[] getLabelAsBytea() {
        return UTF8Codec.encode(label.array(), label.size());
    }

    /**
     * The operation method to move down the position in the tree.
     */
    public void moveDownElement(final int eid) {
        // set current label
        label.add(localOrder);
        // set current path
        if(coder.isEmpty()) {
            coder.separatorSlash();
            coder.identifer(eid);
        } else {            
            coder.separatorSlash();
            coder.identifer(eid);
        }
        // set for the sibling node.
        this.localOrder = 1;        
    }

    /**
     * The operation method to move up the position in the tree.
     */
    public void moveUpElement() {
        // set for the sibling node.
        this.localOrder = label.get(label.size() - 1) + LABEL_INTERVAL;
        // set current label
        label.remove();
        // set current path
        coder.popUntilNextSeparator();               
    }

    public byte[] emurateRoundNode() {
        label.add(localOrder);
        final byte[] b = UTF8Codec.encode(label.toArray(), label.size());
        label.remove();
        this.localOrder += LABEL_INTERVAL;
        return b;
    }

    public byte[] emurateRoundDependant() {
        label.add(0);
        final byte[] b = UTF8Codec.encode(label.toArray(), label.size());
        label.remove();
        return b;
    }
}
