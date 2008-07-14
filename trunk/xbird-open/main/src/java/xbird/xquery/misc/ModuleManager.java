/*
 * @(#)$Id: ModuleManager.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.misc;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.*;

import xbird.xquery.*;
import xbird.xquery.ext.JavaModule;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.parser.ParseException;
import xbird.xquery.parser.XQueryParser;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class ModuleManager {

    private final Map<String, Module> moduleTable = new HashMap<String, Module>(4);

    private final StaticContext _statEnv;
    private ModuleResolver _resolver = null;

    public ModuleManager(StaticContext statEnv) {
        this._statEnv = statEnv;
    }

    public void setModuleResolver(ModuleResolver resolver) {
        this._resolver = resolver;
    }

    public Module loadModule(String targetNamespace) throws XQueryException {
        return loadModule(targetNamespace, targetNamespace);
    }

    public Module loadModule(String targetNamespace, String location) throws XQueryException {
        if(moduleTable.containsKey(targetNamespace)) {
            return moduleTable.get(targetNamespace); // found in the cache
        }
        if(targetNamespace.startsWith(JavaModule.SCHEME)) {
            final int from = JavaModule.SCHEME.length() + 1;
            return new JavaModule(targetNamespace.substring(from));
        } else if(targetNamespace.startsWith("wsdl:")) { // TODO rmi
            throw new UnsupportedOperationException("WSDL style module is not supported yet.");
        }
        if(_resolver != null) {
            location = _resolver.resolveLocation(location);
        }
        InputStream is = openResource(location);
        XQueryModule module = obtainModule(is, _statEnv);
        // TODO should confirm namespace between library-module and target-namespace?
        final String modns = module.getNamespace();
        if(modns == null || modns.equals(targetNamespace) == false) {
            throw new StaticError("XQST0059", "target namespace '" + targetNamespace
                    + "' does not much to the library module namespace '" + modns + "'.");
        }
        module.setLocation(location);
        putModule(module);
        return module;
    }

    private InputStream openResource(String targetNamespace) throws XQueryException {
        final URI u;
        try {
            u = new URI(targetNamespace);
        } catch (URISyntaxException e) {
            throw new StaticError("err:XQST0046", "Illegal xs:anyURI format: " + targetNamespace, e);
        }
        URI baseURI = _statEnv.getBaseURI();
        if(baseURI == null) { // TODO REVIEWME workaround
            baseURI = _statEnv.getSystemBaseURI();
        }
        URI targetURI = (baseURI == null) ? u : baseURI.resolve(u);
        final URL targetURL;
        try {
            targetURL = targetURI.toURL();
        } catch (Exception e) {
            throw new StaticError("err:XQST0046", "targetNamespace is not a valid URL: "
                    + targetNamespace, e);
        }
        final InputStream is;
        try {
            is = targetURL.openStream();
        } catch (IOException e) {
            throw new StaticError("err:XQST0059", "Opening stream failed: " + targetURL, e);
        }
        return is;
    }

    private static XQueryModule obtainModule(final InputStream target, final StaticContext statEnv)
            throws XQueryException {
        final XQueryParser parser = new XQueryParser(target);
        parser.setStaticContext(statEnv);
        final XQueryModule module;
        try {
            module = parser.parse();
        } catch (XQueryException e) {
            final Throwable cause = e.getCause();
            if(cause instanceof ParseException) {
                throw new StaticError("err:XQST0059", "parse failed.", cause);
            } else {
                throw e;
            }
        }
        return module;
    }

    public void putModule(Module module) throws XQueryException {
        final String moduleNamespace = module.getNamespace();
        if(moduleNamespace == null) {
            throw new StaticError("err:XQST0048", "Invalid as library module: "
                    + module.getLocation());
        }
        moduleTable.put(moduleNamespace, module);
    }

    public Module getModule(String namespace) {
        return moduleTable.get(namespace);
    }

    public Collection<Module> getModules() {
        return moduleTable.values();
    }

}