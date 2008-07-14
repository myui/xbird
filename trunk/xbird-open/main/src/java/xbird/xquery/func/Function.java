/*
 * @(#)$Id: Function.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.func;

import java.io.Serializable;
import java.util.List;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.meta.*;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.Type;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public interface Function extends Serializable {

    public QualifiedName getName();

    public FunctionSignature[] getFunctionSignatures();

    public FunctionSignature getFunctionSignature(int arity) throws XQueryException;

    public Type getReturnType();

    public Type getReturnType(List<XQExpression> params);

    public void visit(XQueryParserVisitor visitor, XQueryContext ctxt) throws XQueryException;

    public Function staticAnalysis(StaticContext context, List<? extends XQExpression> params)
            throws XQueryException;

    /**
     * @param argv null or argv.size() > 1 (never be argv.size() == 0).
     */
    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException;

    public EvaluationPolicy getEvaluationPolicy();

    public XQExpression rewrite(List<? extends XQExpression> params, StaticContext context)
            throws XQueryException;

    public enum EvaluationPolicy {
        dynamic /*default*/, lazy, eager, rewritten, threaded;
    }
}