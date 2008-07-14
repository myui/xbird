/*
 * @(#)$Id: TextConstructor.java 3619 2008-03-26 07:23:03Z yui $
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

import xbird.xquery.XQueryException;
import xbird.xquery.dm.XQEventReceiver;
import xbird.xquery.dm.instance.XQueryDataModel;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.node.DMText;
import xbird.xquery.expr.*;
import xbird.xquery.meta.*;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.node.NodeType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery/#id-textConstructors
 * @link http://www.w3.org/TR/xquery-semantics/#sec_textConstructors
 */
public final class TextConstructor extends AbstractXQExpression implements NodeConstructor {
    private static final long serialVersionUID = -4141307426495140505L;

    private final String _content;
    private XQExpression _contentExpr;

    public TextConstructor(XQExpression content) {
        this._content = getValued(content);
        this._contentExpr = content;
        this._type = NodeType.TEXT;
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    public XQExpression getContent() {
        return this._contentExpr;
    }

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        if(!_analyzed) {
            this._analyzed = true;
            if(_content == null) {
                XQExpression analyzed = _contentExpr.staticAnalysis(statEnv);
                if(analyzed instanceof Evaluable) {
                    return analyzed;
                }
                this._contentExpr = analyzed;
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
        if(instContent.length() == 0) {
            return DMText.EMPTY_SEQ.clone();
        } else {
            return XQueryDataModel.createText(instContent);
        }
    }

    @Override
    public void evalAsEvents(XQEventReceiver handler, Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        String instContent = _content;
        if(instContent == null) {
            instContent = processContentExpr(contextSeq, dynEnv);
        }
        if(instContent.length() > 0) {
            handler.evText(instContent);
        }
    }

    private String processContentExpr(Sequence contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        final Sequence src = _contentExpr.eval(contextSeq, dynEnv);
        final Sequence<? extends Item> textSeq = src.atomize(dynEnv);
        final IFocus<? extends Item> itor = textSeq.iterator();
        if(!itor.hasNext()) {
            itor.closeQuietly();
            return "";
        }
        final StringBuilder buf = new StringBuilder(255);
        while(true) {
            final Item it = itor.next();
            buf.append(it.stringValue());
            if(itor.hasNext()) {
                buf.append(' ');
            } else {
                break;
            }
        }
        itor.closeQuietly();
        return buf.toString();
    }

}
