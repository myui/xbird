/*
 * @(#)$Id: QueryProcessor.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.server.backend;

import java.io.Serializable;
import java.io.StringReader;
import java.rmi.RemoteException;

import xbird.config.Settings;
import xbird.server.*;
import xbird.server.ServerConstants.ReturnType;
import xbird.util.PrintUtils;
import xbird.xquery.XQueryException;
import xbird.xquery.XQueryModule;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.expr.opt.Join.PromoteJoinExpression;
import xbird.xquery.meta.*;
import xbird.xquery.parser.XQueryParser;
import xbird.xquery.parser.visitor.AbstractXQueryParserVisitor;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class QueryProcessor extends RemoteServerBase implements BackendProcessor {

    private static final String _url = ServerConstants.GLOBAL_REGISTORY + '/'
            + Settings.get("xbird.rmi.qp.name");

    private static final int _port = Integer.parseInt(Settings.get("xbird.rmi.qp.port", "0"));

    public QueryProcessor() {
        super(_url, _port);
    }

    public <T extends Serializable> T execute(String query, RequestManager rm, ReturnType returnType)
            throws RemoteException {
        assert (query != null);
        assert (rm != null);
        // #1 local-compile
        final XQueryParser parser = new XQueryParser(new StringReader(query));
        XQExpression body = localCompile(parser);
        final int numQP = rm.getNumberOfQueryProcessors();
        if(numQP > 1) {
            // #2 distributed-compile
            body = distributedCompile(body, rm);
        }
        // #3 evaluation
        StaticContext statEnv = parser.getStaticContext();
        final Sequence result;
        try {
            result = body.eval(null, new DynamicContext(statEnv));
        } catch (XQueryException e) {
            throw new RemoteException("Evaluation failed.", e);
        }
        return (T) wrapResult(result, returnType);
    }

    private static XQExpression localCompile(final XQueryParser parser) throws RemoteException {
        final XQueryModule module;
        try {
            module = parser.parse();
        } catch (XQueryException e) {
            throw new RemoteException("parse failed.", e);
        }
        StaticContext statEnv = parser.getStaticContext();
        try {
            module.staticAnalysis(statEnv);
        } catch (XQueryException e) {
            throw new RemoteException("Error caused in the static analysis phase.", e);
        }
        return module.getExpression();
    }

    private XQExpression distributedCompile(final XQExpression expr, final RequestManager rm) {
        return expr;
    }

    private static final class DistributedJoinEnhancer extends AbstractXQueryParserVisitor {
        private final RequestManager _rm;

        public DistributedJoinEnhancer(final RequestManager _rm) {
            super();
            this._rm = _rm;
        }

        @Override
        public void visit(PromoteJoinExpression expr, XQueryContext ctxt) throws XQueryException {

        }
    }

    private static Serializable wrapResult(Sequence result, ServerConstants.ReturnType returnType) {
        if(returnType == ReturnType.Sequence) {
            return result;
        } else {
            // TODO
            throw new IllegalStateException("Illegal return type: " + returnType);
        }
    }

    public static void main(String[] args) {
        try {
            new QueryProcessor().run(args);
        } catch (Throwable e) {
            PrintUtils.prettyPrintStackTrace(e, System.err);
            System.exit(1);
        }
    }
}
