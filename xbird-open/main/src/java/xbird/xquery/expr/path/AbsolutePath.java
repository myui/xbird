/*
 * @(#)$Id: AbsolutePath.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.expr.path;

import java.util.List;

import xbird.xquery.XQueryException;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.path.axis.DescendantOrSelfStep.RootDescStep;
import xbird.xquery.meta.XQueryContext;
import xbird.xquery.parser.XQueryParserVisitor;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class AbsolutePath extends PathExpr {
    private static final long serialVersionUID = 4202903813741923788L;

    public AbsolutePath(XQExpression... steps) {
        super(steps);
    }

    public AbsolutePath(List<XQExpression> steps) {
        this(steps, false); // steps may be null.
    }
    
    public AbsolutePath() { // for Externalizable
        super();
    }

    public AbsolutePath(List<XQExpression> steps, boolean rootDesc) {
        super(steps);
        if(rootDesc) {
            _steps.add(0, RootDescStep.INSTANCE);
        }
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }
}
