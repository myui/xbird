/*
 * @(#)$Id: PIConstructor.java 3619 2008-03-26 07:23:03Z yui $
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
import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.XQEventReceiver;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.literal.XString;
import xbird.xquery.dm.value.node.DMProcessingInstruction;
import xbird.xquery.dm.value.sequence.AtomizedSequence;
import xbird.xquery.expr.*;
import xbird.xquery.expr.seq.SequenceExpression;
import xbird.xquery.meta.*;
import xbird.xquery.misc.TypeUtil;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.Type;
import xbird.xquery.type.node.PITest;
import xbird.xquery.type.xs.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery/#id-computed-pis
 * @link http://www.w3.org/TR/xquery-semantics/#sec_computed-pis
 */
public final class PIConstructor extends AbstractXQExpression implements NodeConstructor {
    private static final long serialVersionUID = -9069495256382769707L;

    private final String _target;
    protected XQExpression _targetExpr = null;

    private final String _content;
    private XQExpression _contentExpr = SequenceExpression.EMPTY_SEQUENCE;

    public PIConstructor(String target, XQExpression content) {
        this(target, getValued(content));
        this._contentExpr = content;
    }

    public PIConstructor(String target, String content) {
        assert (target != null);
        this._target = target;
        this._content = content;
        this._type = PITest.ANY_PI;
    }

    public PIConstructor(XQExpression target, XQExpression content) {
        this._targetExpr = target;
        this._contentExpr = content;
        if(_targetExpr instanceof LiteralExpr) {
            AtomicValue atomval = ((LiteralExpr) _targetExpr).getValue();
            if(atomval instanceof XString) {
                this._target = atomval.stringValue();
            } else {
                this._target = null;
            }
        } else {
            this._target = null;
        }
        if(content instanceof LiteralExpr) {
            AtomicValue atomval = ((LiteralExpr) content).getValue();
            this._content = atomval.stringValue();
        } else {
            this._content = null;
        }
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    //--------------------------------------------
    // getter/setter

    public String getContent() {
        return this._content;
    }

    public XQExpression getContentExpr() {
        return this._contentExpr;
    }

    public String getTarget() {
        return this._target;
    }

    public XQExpression getTargetExpr() {
        return this._targetExpr;
    }

    //--------------------------------------------
    // static analysis/dynamic eval

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        if(!_analyzed) {
            this._analyzed = true;
            if(_target != null) {
                if(!XMLUtils.isNCName(_target)) {
                    throw new DynamicError("XQDY0041", "Illegal target name as xs:NCName: "
                            + _target);
                }
            } else {
                assert (_targetExpr != null);
                this._targetExpr = _targetExpr.staticAnalysis(statEnv);
            }
            if(_content == null && _contentExpr != null) {
                this._contentExpr = _contentExpr.staticAnalysis(statEnv);
            }
        }
        return this;
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        String instTarget = _target;
        if(instTarget == null) {
            instTarget = processTarget(contextSeq, dynEnv);
        }
        String instContent = _content;
        if(instContent == null && _contentExpr != null) {
            instContent = processContent(contextSeq, dynEnv);
        }
        assert (instContent != null);
        String normedContent = XMLUtils.normalizeString(instContent);
        checkTokens(instTarget, normedContent);
        return new DMProcessingInstruction(instTarget, normedContent);
    }

    @Override
    public void evalAsEvents(XQEventReceiver handler, Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        String instTarget = _target;
        if(instTarget == null) {
            instTarget = processTarget(contextSeq, dynEnv);
        }
        String instContent = _content;
        if(instContent == null && _contentExpr != null) {
            instContent = processContent(contextSeq, dynEnv);
        }
        assert (instContent != null);
        String normedContent = XMLUtils.normalizeString(instContent);
        checkTokens(instTarget, instTarget);
        handler.evProcessingInstruction(instTarget, instTarget);
    }

    //--------------------------------------------
    // helpers

    private String processTarget(Sequence contextSeq, DynamicContext dynEnv) throws XQueryException {
        final Sequence<AtomicValue> ncnameSeq = AtomizedSequence.wrap(_targetExpr.eval(contextSeq, dynEnv), dynEnv);
        final IFocus<AtomicValue> ncnameItor = ncnameSeq.iterator();
        if(!ncnameItor.hasNext()) {
            ncnameItor.closeQuietly();
            reportError("err:XPTY0004", "The result of atomization was not a single atomic value.");
        }
        final AtomicValue it = ncnameItor.next();
        if(ncnameItor.hasNext()) {
            ncnameItor.closeQuietly();
            reportError("err:XPTY0004", "The result of atomization was not a single atomic value.");
        }
        ncnameItor.closeQuietly();
        final String ncname;
        final Type t = it.getType();
        if(t == UntypedAtomicType.UNTYPED_ATOMIC || TypeUtil.subtypeOf(t, StringType.STRING)
                || TypeUtil.subtypeOf(t, NCNameType.NCNAME)) {
            ncname = it.stringValue();
            if(!XMLUtils.isNCName(ncname)) {
                throw new DynamicError("XQDY0041", "Illegal target name as xs:NCName: " + _target);
            }
        } else {
            reportError("err:XPTY0004", "Result of atomization is expected to be a single atomic value of type xs:NCName, xs:string, or xs:untypedAtomic, but was.. "
                    + t);
            throw new IllegalStateException();
        }
        return ncname;
    }

    private String processContent(Sequence contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        assert (_contentExpr != null) : "processContent is called while contentExpr is null.";
        final Sequence<? extends Item> src = _contentExpr.eval(contextSeq, dynEnv);
        if(src.isEmpty()) {
            return "";
        } else {
            final Sequence<? extends Item> atomseq = src.atomize(dynEnv);
            final StringBuilder buf = new StringBuilder(128);
            final IFocus<? extends Item> itor = atomseq.iterator();
            for(Item it : itor) {
                buf.append(it.stringValue());
                if(itor.hasNext()) {
                    buf.append(' ');
                }
            }
            itor.closeQuietly();
            return buf.toString();
        }
    }

    private void checkTokens(final String target, final String content) throws XQueryException {
        assert (target != null);
        final String starget = target.toLowerCase();
        if(starget.indexOf("xml") != -1) {
            reportError("err:XQDY0064", "Invalid PITarget: " + target);
        }
        if(content != null && content.indexOf("?>") != -1) {
            reportError("err:XQDY0026", "Invalid PIContent: " + content);
        }
    }

}
