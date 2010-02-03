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
package xbird.xquery.server;

import java.net.MalformedURLException;
import java.rmi.*;

import xbird.config.Settings;
import xbird.util.net.NetUtils;
import xbird.xquery.dm.value.Sequence;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class XQEngineClient {

    private static final int _localRegistryPort = Integer.parseInt(Settings.get("xbird.rmi.registry.local.port"));

    private static final String _localRegistryUrl = "//" + NetUtils.getLocalHostName() + ":" + _localRegistryPort + '/'
            + Settings.get("xbird.rmi.serv.name");

    public XQEngineClient() {}

    public static void main(String[] args) throws MalformedURLException, RemoteException,
            NotBoundException {
        final XQEngine serv = (XQEngine) Naming.lookup(_localRegistryUrl);
        QueryRequest request = new QueryRequest("1 + 2");
        Sequence result = (Sequence) serv.execute(request);
        System.out.println(result.toString());
    }

}
