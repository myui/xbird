/*
 * @(#)$Id: AbstractNode.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.dm;

import java.io.Serializable;

import xbird.xquery.dm.instance.DataModel;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author yui (yuin405+xbird@gmail.com)
 */
public abstract class AbstractNode implements Serializable {

    protected long _id;

    protected AbstractNode(final long id) {
        super();
        this._id = id;
    }

    //--------------------------------------------
    // field accessor

    public abstract DataModel getDataModel();

    /**
     * Returns a string identifying the kind of node.
     */
    public abstract byte nodeKind();

    public abstract String getContent();

    public long getPosition() {
        return _id;
    }

    public void resetId(long id) {
        this._id = id;
    }

    //--------------------------------------------
    // Node traversing members

    public boolean isRoot() {
        return parent() == null;
    }

    public abstract AbstractNode getDocumentNode();

    public abstract AbstractNode firstChild();

    public abstract AbstractNode nextSibling();

    public abstract AbstractNode previousSibling();

    public abstract AbstractNode parent();

    public AbstractNode nextNode() {
        final AbstractNode fc = firstChild();
        return (fc == null) ? following(this, false) : fc;
    }

    /**
     * should be overrides in sub classes for the performance.
     */
    public AbstractNode lastChild() {
        AbstractNode lastchild = firstChild();
        for(;;) {
            final AbstractNode next = lastchild.nextSibling();
            if(next == null) {
                return lastchild;
            } else {
                lastchild = next;
            }
        }
    }

    /**
     * should be overrides in sub classes for the performance.
     */
    public AbstractNode following(final boolean firstcall) {
        return following(this, !firstcall);
    }

    /**
     * should be overrides in sub classes for the performance.
     */
    public AbstractNode preceding(final long parentId) {
        return preceding(this, parentId);
    }

    private static final AbstractNode preceding(final AbstractNode base, final long parentId) {
        final AbstractNode sib = base.previousSibling();
        if(sib == null) {
            AbstractNode pa = base.parent();
            if(pa != null) {
                if(pa.nodeKind() == NodeKind.DOCUMENT) {
                    return null;
                } else {
                    long pid = pa.getPosition();
                    if(pid == parentId) {
                        pa = pa.parent();
                        pid = pa.getPosition();
                        return preceding(pa, pid);
                    } else {
                        return pa;
                    }
                }
            }
            return null;
        } else {
            AbstractNode sibLastChild = sib.lastChild();
            if(sibLastChild != null) {
                for(;;) {
                    final AbstractNode lc = sibLastChild.lastChild();
                    if(lc == null) {
                        return sibLastChild;
                    }
                    sibLastChild = lc;
                }
            }
            return sib;
        }
    }

    private static final AbstractNode following(final AbstractNode base, final boolean includeDesc) {
        if(includeDesc) {
            final AbstractNode firstchild = base.firstChild();
            if(firstchild != null) {
                return firstchild;
            }
        }
        final AbstractNode sib = base.nextSibling();
        if(sib == null) {
            final AbstractNode pa = base.parent();
            if(pa != null) {
                return (pa.nodeKind() == NodeKind.DOCUMENT) ? null : following(pa, false);
            }
            return null;
        } else {
            return sib;
        }
    }

}
