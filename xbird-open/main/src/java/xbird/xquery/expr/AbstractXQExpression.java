/*
 * @(#)$Id: AbstractXQExpression.java 3619 2008-03-26 07:23:03Z yui $
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

import xbird.xquery.*;
import xbird.xquery.dm.XQEventReceiver;
import xbird.xquery.dm.value.*;
import xbird.xquery.meta.*;
import xbird.xquery.misc.ErrorCodes;
import xbird.xquery.optim.RewriteInfo;
import xbird.xquery.parser.visitor.PrintVisitor;
import xbird.xquery.type.Type;
import xbird.xquery.type.xs.Untyped;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class AbstractXQExpression implements XQExpression {
    private static final long serialVersionUID = 1L;

    protected transient int _beginLine = -1, _beginColumn = -1;
    protected Type _type = Untyped.UNTYPED;

    protected transient boolean _analyzed = false;
    protected transient boolean _isLoopInvariant = false;

    public AbstractXQExpression() {}

    public int getBeginColumn() {
        return _beginColumn;
    }

    public int getBeginLine() {
        return _beginLine;
    }

    public void setLocation(int beginLine, int beginColumn) {
        this._beginLine = beginLine;
        this._beginColumn = beginColumn;
    }

    public Type getType() {
        return _type;
    }

    public boolean isLoopInvariant() {
        return _isLoopInvariant;
    }

    public void setLoopInvariant(boolean invariant) {
        this._isLoopInvariant = invariant;
    }

    public boolean isAnalyzed() {
        return _analyzed;
    }

    public boolean isPathIndexAccessable(StaticContext statEnv, RewriteInfo info) {
        return false;
    }

    @Override
    public String toString() {
        final PrintVisitor pv = new PrintVisitor();
        try {
            visit(pv, new StaticContext());
        } catch (XQueryException e) {
            return "";
        }
        return pv.getResult();
    }

    public void evalAsEvents(XQEventReceiver handler, Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        final Sequence<? extends Item> result = eval(contextSeq, dynEnv);
        handler.emit(result);
    }

    public Item evalAsItem(Sequence<? extends Item> contextSeq, DynamicContext dynEnv, boolean opt)
            throws XQueryException {
        Sequence<? extends Item> result = eval(contextSeq, dynEnv);
        final IFocus<? extends Item> itor = result.iterator();
        final Item item;
        try {
            if(!itor.hasNext()) {
                if(!opt) {
                    throw new XQueryException("err:XPTY0004", "Empty sequence is not allowed");
                }
                return null;
            }
            item = itor.next();
            if(itor.hasNext()) {
                throw new XQueryException("err:XPTY0004", "More than one item is not allowed");
            }
        } finally {
            itor.closeQuietly();
        }
        return item;
    }

    //--------------------------------------------
    // helper

    public AbstractXQExpression copyLocations(XQExpression expr) {
        this._beginLine = expr.getBeginLine();
        this._beginColumn = expr.getBeginColumn();
        return this;
    }

    protected void reportError(String errorCode, String msg) throws XQueryException {
        if(_beginLine != -1 || _beginColumn != -1) {
            msg = " - Encountered at line " + this._beginLine + ", column " + this._beginColumn
                    + "\n" + msg;
        }
        if(errorCode.startsWith(ErrorCodes.XPTY)) {
            throw new TypeError(errorCode, msg);
        } else if(errorCode.startsWith(ErrorCodes.XPDY)) {
            throw new DynamicError(errorCode, msg);
        } else {
            throw new XQueryException(errorCode, msg);
        }
    }

    protected static String getValued(XQExpression expr) {
        if(expr instanceof LiteralExpr) {
            AtomicValue atomval = ((LiteralExpr) expr).getValue();
            String sv = atomval.stringValue();
            return sv;
        } else {
            return null;
        }
    }

}
