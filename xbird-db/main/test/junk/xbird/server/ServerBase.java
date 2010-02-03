/*
 * @(#)$Id: ServerBase.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.server;

import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.RemoteException;

import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kohsuke.args4j.*;
import xbird.util.PrintUtils;
import xbird.xquery.XQueryException;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class ServerBase implements Serializable {

    private static final Log LOG = LogFactory.getLog(ServerBase.class);

    @Option(name = "-bindUrl", usage = "Specify bind URL for the server object", metaVar = "URL")
    protected String _bindUrl;

    @Option(name = "-exportPort", usage = "Specify a port to export server object at", metaVar = "Integer")
    protected int _exportPort;

    private CmdLineParser _parser = null;

    private Context _boundedContext = null;

    public ServerBase( String bindUrl,  int servPort) {
        assert (bindUrl != null);
        this._bindUrl = bindUrl;
        this._exportPort = servPort;
    }

    protected void start() throws ServerSideException {
        try {
            bind();
        } catch (RemoteException e) {
            throw new ServerSideException("Bind failed!", e);
        } catch (NamingException e) {
            throw new ServerSideException("Bind failed!", e);
        }
    }

    protected abstract void bind() throws RemoteException, NamingException;

    protected void unbind() {
        try {
            if (_boundedContext != null) {
                _boundedContext.unbind(_bindUrl);
            } else {
                Naming.unbind(_bindUrl);
            }
        } catch (Exception e) {
            LOG.debug("Unbind failed!", e);
        }
    }

    protected void run(String[] args) throws XQueryException {
        parseArgs(args);
        run();
    }

    public void run() throws XQueryException {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                unbind();
            }
        });
        try {
            start();
        } catch (ServerSideException e) {
            LOG.fatal("failed to start server!", e);
            PrintUtils.prettyPrintStackTrace(e, System.err);
        }
    }

    public abstract void shutdown(boolean forceExit) throws RemoteException;

    private void parseArgs(String[] args) throws XQueryException {
        final CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            showHelp();
            return;
        }
        this._parser = parser;
    }

    private void showHelp() {
        assert (_parser != null);
        System.err.println("[Usage] \n $ java " + getClass().getSimpleName()
                + _parser.printExample(ExampleMode.ALL));
        _parser.printUsage(System.err);
    }

}
