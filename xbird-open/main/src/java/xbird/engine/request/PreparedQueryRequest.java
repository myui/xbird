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
package xbird.engine.request;

import java.io.*;

import xbird.xquery.expr.XQExpression;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class PreparedQueryRequest extends QueryRequest {
    private static final long serialVersionUID = -8880681218571760492L;

    private/* final */XQExpression _compiledExpr;

    public PreparedQueryRequest() {
        super();
    }

    public PreparedQueryRequest(XQExpression compiledExpr, ReturnType retType) {
        super();
        if(compiledExpr == null) {
            throw new IllegalArgumentException();
        }
        this._compiledExpr = compiledExpr;
        this._retType = retType;
    }

    @Override
    public Signature getSignature() {
        return Signature.PREPARED_QUERY;
    }

    public XQExpression getCompiledExpression() {
        return _compiledExpr;
    }

    @Override
    protected void readQuery(ObjectInput in) throws IOException, ClassNotFoundException {
        this._compiledExpr = (XQExpression) in.readObject();
    }

    @Override
    protected void writeQuery(ObjectOutput out) throws IOException {
        assert (_compiledExpr != null);
        out.writeObject(_compiledExpr);
    }

    @Override
    public String toString() {
        return getIdentifier();
    }

}
