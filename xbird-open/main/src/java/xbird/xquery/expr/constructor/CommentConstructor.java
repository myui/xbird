/*
 * @(#)$Id: CommentConstructor.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.expr.constructor;

import xbird.util.xml.XMLUtils;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.XQEventReceiver;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.node.DMComment;
import xbird.xquery.dm.value.sequence.AtomizedSequence;
import xbird.xquery.expr.AbstractXQExpression;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.meta.*;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.node.NodeType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class CommentConstructor extends AbstractXQExpression implements NodeConstructor {
    private static final long serialVersionUID = 8031506689888119544L;

    private final String _content; // avoid null for computed construction
    private XQExpression _contentExpr = null;

    public CommentConstructor(XQExpression content) {
        this(getValued(content));
        this._contentExpr = content;
    }

    public CommentConstructor(String content) {
        this._content = content;
        this._type = NodeType.COMMENT;
    }

    public String getContent() {
        return this._content;
    }

    public XQExpression getContentExpr() {
        return this._contentExpr;
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    //--------------------------------------------
    // static analysis/dynamic eval

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        if(!_analyzed) {
            this._analyzed = true;
            if(_content == null) {
                assert (_contentExpr != null);
                this._contentExpr = _contentExpr.staticAnalysis(statEnv);
            } else {
                checkToken(_content);
            }
        }
        return this;
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        String instContent = _content;
        if(instContent == null) {
            instContent = processContentExpr(contextSeq, dynEnv);
        }
        assert (instContent != null);
        checkToken(instContent);
        return new DMComment(instContent);
    }

    @Override
    public void evalAsEvents(XQEventReceiver handler, Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        String instContent = _content;
        if(instContent == null) {
            instContent = processContentExpr(contextSeq, dynEnv);
        }
        assert (instContent != null);
        handler.evComment(instContent);
    }

    //--------------------------------------------
    // helpers

    private String processContentExpr(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        final StringBuilder buf = new StringBuilder(128);
        final Sequence<? extends Item> seq = _contentExpr.eval(contextSeq, dynEnv);
        final IAtomized<AtomicValue> atomized = AtomizedSequence.wrap(seq, dynEnv);
        final IFocus<AtomicValue> itor = atomized.iterator();
        for(AtomicValue it : itor) {
            buf.append(it.stringValue());
            if(itor.hasNext()) {
                buf.append(' ');
            }
        }
        itor.closeQuietly();
        return buf.toString();
    }

    private void checkToken(String content) throws XQueryException {
        assert (content != null);
        if(!XMLUtils.isValidComment(content)) {
            reportError("err:XQDY0072", "The result format of the content expression is invalid: "
                    + content);
        }
    }

}
