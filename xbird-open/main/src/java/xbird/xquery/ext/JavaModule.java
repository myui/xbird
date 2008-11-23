/*
 * @(#)$Id: JavaModule.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.ext;

import xbird.xquery.AbstractModule;
import xbird.xquery.XQueryException;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.meta.XQueryContext;
import xbird.xquery.parser.XQueryParserVisitor;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 *
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @deprecated
 */
public class JavaModule extends AbstractModule {
    private static final long serialVersionUID = -2204259902044080355L;
    
    public static final String SCHEME = "java:";

    public JavaModule(String target) {
        setLocation(target);
        setNamespace(SCHEME + target);
    }

    public void staticAnalysis(StaticContext context) throws XQueryException {}

    public void visit(XQueryParserVisitor visitor, XQueryContext ctxt) throws XQueryException {}

    public XQExpression getExpression() {
        return null;
    }

}
