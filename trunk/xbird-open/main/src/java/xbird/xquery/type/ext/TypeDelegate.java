/*
 * @(#)$Id: TypeDelegate.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.type.ext;

import javax.xml.xquery.XQException;

import xbird.xquery.expr.XQExpression;
import xbird.xquery.type.Type;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class TypeDelegate extends Type {
    private static final long serialVersionUID = -6316846270403220038L;

    private final XQExpression _delegate;

    public TypeDelegate(XQExpression delegate) {
        this._delegate = delegate;
    }

    public Type prime() {
        return _delegate.getType().prime();
    }

    public Occurrence quantifier() {
        return _delegate.getType().quantifier();
    }

    public boolean accepts(Type expected) {
        return _delegate.getType().accepts(expected);
    }

    public Class getJavaObjectType() {
        return _delegate.getType().getJavaObjectType();
    }

    public String toString() {
        return _delegate.getType().toString();
    }

    @Override
    public int getXQJBaseType() throws XQException {
        return _delegate.getType().getXQJBaseType();
    }

}
