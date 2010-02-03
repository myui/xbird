/*
 * @(#)$Id: DocumentRepository.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.server.repository;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import xbird.config.Settings;
import xbird.server.*;
import xbird.server.ServerConstants.ReturnType;
import xbird.util.PrintUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class DocumentRepository extends RemoteServerBase implements IDocumentRepository {

    private static final int _globalRegistryPort = Integer.parseInt(Settings.get("xbird.rmi.registry.global.port"));

    private static final String _repositoryUrl = "//localhost:" + _globalRegistryPort + '/'
            + Settings.get("xbird.rmi.repository.name");

    private static final int _repositoryPort = Integer.parseInt(Settings.get("xbird.rmi.repository.port", "0"));

    public DocumentRepository() {
        super(_repositoryUrl, _repositoryPort);
    }

    @Override
    protected void start() throws ServerSideException {
        try {// startup rmiregistry(global repository) on localhost
            LocateRegistry.createRegistry(_globalRegistryPort);
        } catch (RemoteException e) {
            throw new ServerSideException("failed to launch registry on port "
                    + _globalRegistryPort, e);
        }
        super.start();
    }

    public Object execute(Request request, ReturnType returnType) throws RemoteException {
        return null;
    }

    public static void main(String[] args) {
        try {
            new DocumentRepository().run(args);
        } catch (Throwable e) {
            PrintUtils.prettyPrintStackTrace(e, System.err);
            System.exit(1);
        }
    }
}
