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

import xbird.engine.Request;
import xbird.xquery.expr.XQExpression;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class CompileRequest extends Request {
    private static final long serialVersionUID = 8639307775055284689L;

    private/* final */XQExpression _expr;

    public CompileRequest() {//Externalizable
        super();
    }

    public CompileRequest(XQExpression expr) {
        super();
        if(expr == null) {
            throw new IllegalArgumentException();
        }
        this._expr = expr;
    }

    @Override
    public Signature getSignature() {
        return Signature.COMPILE;
    }

    public XQExpression getExpression() {
        return _expr;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        this._expr = (XQExpression) in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(_expr);
    }

}
