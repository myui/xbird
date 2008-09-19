/*
 * @(#)$Id: XQEngineClient.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.engine;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.engine.Request.ReplyPattern;
import xbird.util.net.TimeoutSocketProdiver;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class XQEngineClient implements XQEngine {
    private static final Log LOG = LogFactory.getLog(XQEngineClient.class);

    private final String remoteEndpoint;
    private XQEngine engineRef = null;

    public XQEngineClient(String remoteEndpoint) {
        if(remoteEndpoint == null) {
            this.remoteEndpoint = "//localhost:" + RemoteBase.localRegistryPort + '/'
                    + XQEngineServer.bindName;
        } else {
            this.remoteEndpoint = remoteEndpoint;
        }
    }

    public XQEngineClient() {
        this(null);
    }

    public Object execute(Request request) throws RemoteException {
        final ReplyPattern replyPtn = request.getReplyPattern();
        if(replyPtn == ReplyPattern.CALLBACK) {
            throw new IllegalStateException("ResultHandler is required for the Callback reply pattern");
        }
        prepare();
        request.setInvoked(System.currentTimeMillis());
        final StampedResult stamped = (StampedResult) engineRef.execute(request);
        if(LOG.isDebugEnabled()) {
            final long encodingTime = stamped.getEncodingTime();
            final long decodingTime = stamped.getDecodingTime();
            final long codingTime = encodingTime + decodingTime;
            final long latency = System.currentTimeMillis() - stamped.getCreated();
            LOG.debug("Latency of the response `"
                    + request
                    + "': "
                    + ((latency < 0) ? "N/A" : (latency + "(msec) [network: "
                            + stamped.getLatency() + ']')) + ", total coding time (THREADED): "
                    + codingTime + "(msec) [encoding:" + encodingTime + ", decoding:"
                    + decodingTime + ']');
        }
        final Object result = stamped.getResult();
        return result;
    }

    public Object execute(Request request, ResultHandler handler) throws RemoteException {
        if(handler == null) {
            throw new IllegalArgumentException();
        }
        final ReplyPattern replyPtn = request.getReplyPattern();
        if(replyPtn == ReplyPattern.POLL) {
            LOG.warn("execute(Request) method should be used for Poll reply pattern. ResultHandler does not make sence in this method.");
        }
        if(replyPtn == ReplyPattern.CALLBACK) {
            exportMe(handler, request);
        }
        prepare();
        request.setInvoked(System.currentTimeMillis());
        final StampedResult stamped = (StampedResult) engineRef.execute(request, handler);
        if(LOG.isDebugEnabled()) {
            final long encodingTime = stamped.getEncodingTime();
            final long decodingTime = stamped.getDecodingTime();
            final long codingTime = encodingTime + decodingTime;
            final long latency = System.currentTimeMillis() - stamped.getCreated();
            LOG.debug("Latency of the response `"
                    + request
                    + "': "
                    + ((latency < 0) ? "N/A" : (latency + "(msec) [network: "
                            + stamped.getLatency() + ']')) + ", total coding time (THREADED): "
                    + codingTime + "(msec) [encoding:" + encodingTime + ", decoding:"
                    + decodingTime + ']');
        }
        final Object result = stamped.getResult();
        if(replyPtn == ReplyPattern.RESPONSE) {
            handler.handleResult(result);
            return null;
        }
        return result;
    }

    public Object poll(Request request) throws RemoteException {
        prepare();
        return engineRef.poll(request);
    }

    @Deprecated
    public void poll(Request request, ResultHandler handler) throws RemoteException {
        Object result = poll(request);
        handler.handleResult(result);
    }

    private synchronized void prepare() {
        if(engineRef == null) {
            final XQEngine ref;
            try {
                ref = (XQEngine) Naming.lookup(remoteEndpoint);
            } catch (MalformedURLException mue) {
                throw new IllegalStateException("lookup failed: " + remoteEndpoint, mue);
            } catch (RemoteException re) {
                throw new IllegalStateException("lookup failed: " + remoteEndpoint, re);
            } catch (NotBoundException nbe) {
                throw new IllegalStateException("lookup failed: " + remoteEndpoint, nbe);
            }
            this.engineRef = ref;
        }
    }

    private static void exportMe(final ResultHandler handler, final Request request)
            throws RemoteException {
        // Must use zero for an anonymous export port
        // http://archives.java.sun.com/cgi-bin/wa?A2=ind0501&L=rmi-users&P=556
        UnicastRemoteObject.exportObject(handler, 0, null, TimeoutSocketProdiver.createServerSocketFactory());
    }

    /** should be called at the end of operations */
    public void close() throws RemoteException {
        if(engineRef != null) {
            this.engineRef = null;
        }
    }

    public void shutdown() throws RemoteException {
        if(engineRef != null) {
            engineRef.shutdown();
            this.engineRef = null;
        }
    }

}
