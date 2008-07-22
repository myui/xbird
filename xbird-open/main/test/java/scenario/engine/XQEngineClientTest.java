/*
 * @(#)$Id: XQEngineClientTest.java 3619 2008-03-26 07:23:03Z yui $
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
package scenario.engine;

import java.net.UnknownHostException;
import java.rmi.RemoteException;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

import xbird.engine.Request;
import xbird.engine.ResultHandler;
import xbird.engine.XQEngine;
import xbird.engine.XQEngineClient;
import xbird.engine.Request.ReplyPattern;
import xbird.engine.Request.ReturnType;
import xbird.engine.remote.RemoteSequence;
import xbird.engine.request.CommandRequest;
import xbird.engine.request.QueryRequest;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.literal.XInteger;
import xbird.xquery.meta.IFocus;

public class XQEngineClientTest extends TestCase {

    private static final String bindHost = "Atom";

    public XQEngineClientTest() {
        super(XQEngineClientTest.class.getName());
    }

    //@Test
    public void testExecuteImportDocument() throws RemoteException, UnknownHostException {
        XQEngine engine = new XQEngineClient("//" + bindHost + ":1099/xbird/srv-01");

        CommandRequest request1 = new CommandRequest(new String[] { "import", "document",
                "C:/Software/xmark/xmark001.xml" });
        request1.setBaseCollection("/xmark");
        Object result1 = engine.execute(request1);
        Assert.assertEquals(Boolean.TRUE, result1);
    }

    @Test
    public void testExecuteQuery() throws RemoteException, UnknownHostException {
        XQEngine engine = new XQEngineClient("//" + bindHost + ":1099/xbird/srv-01");

        QueryRequest request1 = new QueryRequest("1+2", ReturnType.STRING);
        Object result1 = engine.execute(request1);
        Assert.assertEquals("3", result1);

        QueryRequest request2 = new QueryRequest("1+4");
        Object result2 = engine.execute(request2);
        Assert.assertEquals(new XInteger(5), result2);
    }

    @Test
    public void testExecuteQueryRemoteSequence() throws RemoteException, UnknownHostException {
        XQEngine engine = new XQEngineClient("//" + bindHost + ":1099/xbird/srv-01");

        QueryRequest request1 = new QueryRequest("1+2", ReturnType.REMOTE_SEQUENCE);

        Object result1 = engine.execute(request1);
        RemoteSequence remoteSequence = (RemoteSequence) result1;

        Assert.assertFalse(remoteSequence.isEmpty());
        IFocus<Item> focus = remoteSequence.iterator();
        Assert.assertEquals(new XInteger(3), focus.next());
        Assert.assertFalse(focus.hasNext());
    }

    @Test
    public void testExecuteQueryPoll() throws RemoteException, InterruptedException {
        XQEngine engine = new XQEngineClient("//" + bindHost + ":1099/xbird/srv-01");

        QueryRequest request1 = new QueryRequest("1+2", ReturnType.STRING);
        request1.setReplyPattern(ReplyPattern.POLL);
        // not send
        Assert.assertNull(engine.poll(request1));
        // send#1
        Assert.assertNull(engine.execute(request1));
        // poll#1             
        Thread.sleep(3000);
        Assert.assertEquals("3", engine.poll(request1));
        // after polled
        Assert.assertNull(engine.poll(request1));
        // send#2
        QueryRequest request2 = new QueryRequest("1+4");
        request2.setReplyPattern(ReplyPattern.POLL);
        Assert.assertNull(engine.execute(request2));
        // poll#2
        Thread.sleep(3000);
        Assert.assertEquals(new XInteger(5), engine.poll(request2));
    }

    @Test
    public void testExecuteQueryPollOutOfOrder() throws RemoteException, InterruptedException {
        XQEngine engine = new XQEngineClient("//" + bindHost + ":1099/xbird/srv-01");

        QueryRequest request1 = new QueryRequest("1+2", ReturnType.STRING);
        request1.setReplyPattern(ReplyPattern.POLL);
        // not send
        Assert.assertNull(engine.poll(request1));
        // send#2
        QueryRequest request2 = new QueryRequest("1+4");
        request2.setReplyPattern(ReplyPattern.POLL);
        Assert.assertNull(engine.execute(request2));

        // send#1
        Assert.assertNull(engine.execute(request1));
        // poll#1
        Thread.sleep(3000);
        Assert.assertEquals("3", engine.poll(request1));
        // after polled
        Assert.assertNull(engine.poll(request1));

        // poll#2
        Thread.sleep(3000);
        Assert.assertEquals(new XInteger(5), engine.poll(request2));
    }

    @Test
    public void testExecuteQueryCallback() throws RemoteException, InterruptedException {
        final XQEngineClient engine = new XQEngineClient("//" + bindHost + ":1099/xbird/srv-01");

        final QueryRequest request1 = new QueryRequest("1+2", ReturnType.STRING);
        request1.setReplyPattern(ReplyPattern.CALLBACK);
        CallbackDetector callback = new CallbackDetector();
        Assert.assertNull(engine.execute(request1, callback));

        synchronized(callback) {
            callback.wait();
        }

        Assert.assertEquals("3", callback.result);
    }

    @SuppressWarnings("unused")
    private static final class CallbackDetector implements ResultHandler {

        private Object result = null;
        private boolean failed = false;
        private boolean isCalled = false;

        public CallbackDetector() {}

        public void handleError(Request request, String errMsg) throws RemoteException {
            this.isCalled = true;
            this.failed = true;
            synchronized(this) {
                this.notify();
            }
        }

        public void handleError(Request request, Throwable cause) throws RemoteException {
            this.isCalled = true;
            this.failed = true;
            synchronized(this) {
                this.notifyAll();
            }
        }

        public void handleResult(Object result) throws RemoteException {
            this.isCalled = true;
            this.result = result;
            synchronized(this) {
                this.notifyAll();
            }
        }

    }

}
