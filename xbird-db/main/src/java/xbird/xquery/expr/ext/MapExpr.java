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
package xbird.xquery.expr.ext;

import java.rmi.RemoteException;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gridool.Grid;
import gridool.GridClient;
import gridool.GridNode;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.expr.AbstractXQExpression;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.flwr.FLWRExpr;
import xbird.xquery.expr.var.BindingVariable;
import xbird.xquery.ext.grid.MapQueryJob;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.meta.XQueryContext;
import xbird.xquery.parser.XQueryParserVisitor;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class MapExpr extends AbstractXQExpression {
    private static final long serialVersionUID = -5300593397513494854L;
    private static final Log LOG = LogFactory.getLog(MapExpr.class);

    @Nonnull
    private final String colPath;
    @Nonnull
    private final BindingVariable bindingVar;
    @Nonnull
    private XQExpression expr;

    public MapExpr(@CheckForNull String colPath, @CheckForNull BindingVariable bindingVar, @CheckForNull FLWRExpr expr) {
        if(colPath == null) {
            throw new IllegalArgumentException("Collection path is not set");
        }
        if(bindingVar == null) {
            throw new IllegalArgumentException("BindingVariable is not set");
        }
        if(expr == null) {
            throw new IllegalArgumentException("FLWRExpr is not set");
        }
        this.colPath = colPath;
        this.bindingVar = bindingVar;
        this.expr = expr;
    }

    @Nonnull
    public String getCollectionPath() {
        return colPath;
    }

    @Nonnull
    public BindingVariable getBindingVariable() {
        return bindingVar;
    }

    @Nonnull
    public XQExpression getBodyExpression() {
        return expr;
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return expr.visit(visitor, ctxt);
    }

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        if(!_analyzed) {
            this._analyzed = true;
            this.expr = expr.staticAnalysis(statEnv);
        }
        return this;
    }

    public Sequence<? extends Item> eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        final Grid grid = connectToGrid(true);
        final Sequence<? extends Item> result;
        try {
            result = grid.execute(MapQueryJob.class, this);
        } catch (RemoteException e) {
            throw new XQueryException(e.getMessage(), e);
        }
        return result;
    }

    private static Grid connectToGrid(boolean delegate) {
        boolean delegated = false;
        GridClient grid = new GridClient();
        if(delegate) {
            try {
                final GridNode delegatedNode = grid.delegate(true);
                if(delegatedNode != null) {
                    delegated = true;
                    grid = new GridClient(delegatedNode);
                }
            } catch (RemoteException e) {
                LOG.warn(e.getMessage(), e);
            }
            if(LOG.isInfoEnabled()) {
                LOG.info("Connected to the Grid (" + (delegated ? "remote" : "local")
                        + " master): " + grid.getEndpoint());
            }
        }
        return grid;
    }
}
