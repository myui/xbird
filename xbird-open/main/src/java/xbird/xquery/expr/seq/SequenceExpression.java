/*
 * @(#)$Id: SequenceExpression.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.expr.seq;

import java.util.LinkedList;
import java.util.List;

import xbird.util.io.IOUtils;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.XQEventReceiver;
import xbird.xquery.dm.value.AbstractSequence;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.expr.AbstractXQExpression;
import xbird.xquery.expr.Evaluable;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.Focus;
import xbird.xquery.meta.IFocus;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.meta.XQueryContext;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.SequenceType;
import xbird.xquery.type.Type;
import xbird.xquery.type.schema.ListType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class SequenceExpression extends AbstractXQExpression {
    private static final long serialVersionUID = 461264801836624888L;

    public static final SequenceExpression EMPTY_SEQUENCE = new EmptySequenceExpression();

    protected final List<XQExpression> _exprs;

    public SequenceExpression() {
        this._exprs = new LinkedList<XQExpression>();
    }

    public SequenceExpression(List<XQExpression> exprs) {
        this._exprs = exprs;
    }

    public List<XQExpression> getExpressions() {
        return _exprs;
    }

    public void addExpression(XQExpression expr) {
        _exprs.add(expr);
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        if(!_analyzed) {
            this._analyzed = true;
            int size = _exprs.size();
            for(int i = 0; i < size; i++) {
                XQExpression e = _exprs.get(i);
                _exprs.set(i, e.staticAnalysis(statEnv));
            }
            normalize(statEnv);
            if(size == 0) {
                return EMPTY_SEQUENCE;
            } else if(size == 1) {
                return _exprs.get(0);
            } else {
                size = _exprs.size();
                ListType listtype = new ListType(size);
                for(int i = 0; i < size; i++) {
                    XQExpression analyzed = _exprs.get(i).staticAnalysis(statEnv);
                    _exprs.set(i, analyzed);
                    Type inferredType = analyzed.getType();
                    listtype.add(inferredType);
                }
                this._type = listtype;
            }
        }
        return this;
    }

    private AbstractXQExpression normalize(StaticContext statEnv) throws XQueryException {
        for(int i = 0; i < _exprs.size(); i++) {
            XQExpression e = _exprs.get(i);
            if(e instanceof SequenceExpression) {
                _exprs.remove(i);
                List<XQExpression> sublist = ((SequenceExpression) e).getExpressions();
                if(!sublist.isEmpty()) { // may be SequenceExpression.EMPTY_LIST
                    _exprs.addAll(i, sublist);
                }
            }
        }
        return this;
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        return new SequenceEmuration(_exprs, contextSeq, dynEnv, _type);
    }

    @Override
    public void evalAsEvents(XQEventReceiver handler, Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        final boolean orig = handler.isBlankRequired();
        for(XQExpression e : _exprs) {
            e.evalAsEvents(handler, contextSeq, dynEnv);
            handler.setBlankRequired(true);
        }
        handler.setBlankRequired(orig);
    }

    private static final class SequenceEmuration extends AbstractSequence<Item> {
        private static final long serialVersionUID = -5490747230201729749L;

        private final Sequence<? extends Item> _contextSeq;
        private final List<XQExpression> _exprs;
        private final Type _type;

        private SequenceEmuration(List<XQExpression> exprs, Sequence<? extends Item> contextSeq, DynamicContext dynEnv, Type type) {
            super(dynEnv);
            this._contextSeq = contextSeq;
            this._exprs = exprs;
            this._type = type;
        }

        @Override
        public FocusSequence<Item> iterator() {
            final FocusSequence<Item> focus = new FocusSequence<Item>(this, _dynEnv);
            return focus;
        }

        public boolean next(IFocus focus) throws XQueryException {
            final FocusSequence<Item> focuss = (FocusSequence<Item>) focus;
            if(focuss.reachedEnd()) {
                return false;
            }
            final IFocus<? extends Item> remaining = focuss.getRemaining();
            if(remaining != null && remaining.hasNext()) {
                final Item it = remaining.next();
                focuss.setContextItem(it);
                return true;
            }
            final int exprlen = _exprs.size();
            while(focuss.incrExprPosition() < exprlen) {
                final int at = focuss.getExprPosition();
                final XQExpression e = _exprs.get(at);
                final Sequence<? extends Item> res = e.eval(_contextSeq, _dynEnv);
                final IFocus<? extends Item> resItor = res.iterator();
                if(resItor.hasNext()) {
                    final Item it = resItor.next();
                    focuss.setContextItem(it);
                    focuss.setRemaining(resItor);
                    return true;
                }
            }
            if(remaining != null) {
                IOUtils.closeQuietly(remaining);
            }
            focuss.setReachedEnd(true);
            return false;
        }

        public Type getType() {
            return _type;
        }

    }

    private static final class FocusSequence<T extends Item> extends Focus<T> {
        private static final long serialVersionUID = -8957042590608517069L;

        private int _exprPos = -1;
        private IFocus<? extends Item> _remaining = null;

        public FocusSequence(Sequence<T> src, DynamicContext dynEnv) {
            super(src, dynEnv);
        }

        public int getExprPosition() {
            return _exprPos;
        }

        public int incrExprPosition() {
            return ++_exprPos;
        }

        public IFocus<? extends Item> getRemaining() {
            return _remaining;
        }

        public void setRemaining(IFocus<? extends Item> remaining) {
            this._remaining = remaining;
        }

    }

    private static final class EmptySequenceExpression extends SequenceExpression
            implements Evaluable {
        private static final long serialVersionUID = 7666496505733630282L;

        public EmptySequenceExpression() {
            super();
            this._type = SequenceType.EMPTY;
            this._analyzed = true;
        }

        @Override
        public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
            return this;
        }

        @Override
        public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
                throws XQueryException {
            return ValueSequence.emptySequence();
        }

    }

}