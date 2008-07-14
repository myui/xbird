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
package xbird.engine.backend;

import java.rmi.RemoteException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.engine.*;
import xbird.engine.Request.Signature;
import xbird.engine.request.CompileRequest;
import xbird.xquery.XQueryException;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.meta.StaticContext;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class DistributedCompiler extends BackendProcessor {
    private static final Log LOG = LogFactory.getLog(DistributedCompiler.class);

    public DistributedCompiler(ResponseListener handler) {
        super(handler);
    }

    public Signature associatedWith() {
        return Signature.COMPILE;
    }

    public void fire(RequestContext rc) throws RemoteException {
        rc.setFired(System.currentTimeMillis());
        
        final Request request = rc.getRequest();
        final Signature rsig = request.getSignature();
        if(rsig != Signature.COMPILE) {
            throw new IllegalStateException("Illegal command is passed to DistributedCompileProcessor: "
                    + rsig);
        }
        final CompileRequest compileRequest = (CompileRequest) request;
        final XQExpression rawExpr = compileRequest.getExpression();

        final StaticContext statEnv = new StaticContext();
        final XQExpression compiledExpr;
        try {
            compiledExpr = rawExpr.staticAnalysis(statEnv);
        } catch (XQueryException e) {
            rc.setFault(e);
            _resHandler.onResponse(rc);
            return;
        }

        rc.setResult(compiledExpr);

        try {
            _resHandler.onResponse(rc);
        } catch (RemoteException re) {
            LOG.error("The respond for the request '" + rc.getRequest().getIdentifier()
                    + "' is failed", re);
        }
    }

    /** nop */
    public void cancel(RequestContext rc) throws RemoteException {}
}
