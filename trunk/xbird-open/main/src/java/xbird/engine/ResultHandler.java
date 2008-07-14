/*
 * @(#)$Id: ResultHandler.java 3619 2008-03-26 07:23:03Z yui $
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

import java.rmi.Remote;
import java.rmi.RemoteException;

import xbird.engine.Request.ReturnType;
import xbird.engine.remote.RemoteSequence;
import xbird.engine.remote.RemoteSequenceProxy;

/**
 * 
 * <DIV lang="en">
 * To wait for the callback, your client code will be as following.
 * <pre>
 * class YourCallbackHandler implements ResultHandler {
 *     ..
 *     ..
 *     public void handleResult(Object result) throws RemoteException {
 *        // process result
 *        this.notifyAll();
 *     }
 * }
 * 
 * ResultHandler handler = new YourCallbackHandler(..);
 * synchronized(handler) {
 *    handler.wait(); // wait for callback
 *    // or handler.wait(timeoutInMillSec);
 * }
 * </pre>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405 AT gmail.com)
 */
public interface ResultHandler extends Remote {

    /**
     * Note that {@link ReturnType#REMOTE_SEQUENCE} returns {@link RemoteSequenceProxy},
     * so wrap the result with {@link RemoteSequence} in this handler if needed.
     */
    public void handleResult(Object result) throws RemoteException;

    public void handleError(Request request, String errMsg) throws RemoteException;

    public void handleError(Request request, Throwable cause) throws RemoteException;

}
