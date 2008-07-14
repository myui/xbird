/*
 * @(#)$Id: CommandProcessor.java 3619 2008-03-26 07:23:03Z yui $
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
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.client.command.*;
import xbird.engine.*;
import xbird.engine.Request.Signature;
import xbird.engine.request.CommandRequest;
import xbird.storage.DbCollection;
import xbird.util.concurrent.collections.ConcurrentIdentityHashMap;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class CommandProcessor extends BackendProcessor {
    private static final Log LOG = LogFactory.getLog(CommandProcessor.class);

    private final Map<RequestContext, Thread> _runningThreads = new ConcurrentIdentityHashMap<RequestContext, Thread>(12);

    public CommandProcessor(ResponseListener handler) {
        super(handler);
    }

    public Signature associatedWith() {
        return Signature.COMMAND;
    }

    public void fire(RequestContext rc) {
        rc.setFired(System.currentTimeMillis());

        Request request = rc.getRequest();
        Signature rsig = request.getSignature();
        if(rsig != Signature.COMMAND) {
            throw new IllegalStateException("Illegal command is passed to CommandProcessor: "
                    + rsig);
        }
        CommandRequest command = (CommandRequest) request;
        String[] cmdArg = command.getArgs();
        String baseCol = command.getBaseCollection();

        DbCollection contextCol = DbCollection.getCollection(baseCol);
        Session session = new Session(contextCol);
        CommandInvoker invoker = new CommandInvoker(session);

        _runningThreads.put(rc, Thread.currentThread());
        try {
            boolean status = invoker.executeCommand(cmdArg);
            rc.setResult(status);
        } catch (CommandException ce) {
            LOG.error("command failed: " + Arrays.toString(cmdArg), ce);
            rc.setFault(ce);
        } finally {
            _runningThreads.remove(rc);
        }

        try {
            _resHandler.onResponse(rc);
        } catch (RemoteException re) {
            LOG.error("The respond for the request '" + rc.getRequest().getIdentifier()
                    + "' is failed", re);
        }
    }

    public void cancel(RequestContext rc) {
        Thread thread = _runningThreads.remove(rc);
        if(thread != null) {
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
