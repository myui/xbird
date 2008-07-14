/*
 * @(#)$Id: XQExpression.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.expr;

import java.io.Serializable;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.XQEventReceiver;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.meta.*;
import xbird.xquery.optim.RewriteInfo;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.Type;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public interface XQExpression extends Serializable {

    //--------------------------------------------
    // Getter/Setter

    public int getBeginColumn();

    public int getBeginLine();

    public void setLocation(int beginLine, int beginColumn);

    public Type getType();

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException;

    //--------------------------------------------
    // Static Type check / Dynamic Evaluation

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException;

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException;

    public Item evalAsItem(Sequence<? extends Item> contextSeq, DynamicContext dynEnv, boolean opt)
            throws XQueryException;

    public void evalAsEvents(XQEventReceiver handler, Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException;

    // --------------------------------------------
    // auxiliary

    public void setLoopInvariant(boolean invariant);

    public boolean isLoopInvariant();

    public boolean isPathIndexAccessable(StaticContext statEnv, RewriteInfo info);
}
