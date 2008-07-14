/*
 * @(#)$Id: EagerEvaluated.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.func.opt;

import java.io.*;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.func.*;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.XQueryContext;
import xbird.xquery.parser.XQueryParserVisitor;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class EagerEvaluated extends AbstractFunction {
    private static final long serialVersionUID = -8442336887322853531L;

    private/* final */Function delegate;
    private/* final */Sequence result;

    public EagerEvaluated(Function delegate, Sequence result) {
        super(delegate.getName(), delegate.getReturnType());
        if(result == null) {
            throw new IllegalArgumentException();
        }
        this.delegate = delegate;
        this.result = result;
        this._evalPocily = EvaluationPolicy.eager;
    }

    public EagerEvaluated() {
        super();
    }// for Externalizable

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        this.delegate = (Function) in.readObject();
        this.result = (Sequence) in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(delegate);
        out.writeObject(result);
    }

    public FunctionSignature[] getFunctionSignatures() {
        return delegate.getFunctionSignatures();
    }

    public void visit(XQueryParserVisitor visitor, XQueryContext ctxt) throws XQueryException {}

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        return result;
    }
}
