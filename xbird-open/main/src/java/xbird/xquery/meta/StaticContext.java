/*
 * @(#)$Id: StaticContext.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.meta;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;

import xbird.util.resource.ResourceUtils;
import xbird.util.xml.NamespaceBinder;
import xbird.util.xml.XMLUtils;
import xbird.xquery.StaticError;
import xbird.xquery.XQueryConstants;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.instance.DocumentTableModel.DTMDocument;
import xbird.xquery.expr.opt.PathVariable;
import xbird.xquery.expr.opt.ThreadedVariable;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.ext.JavaFunction;
import xbird.xquery.misc.FunctionManager;
import xbird.xquery.misc.ModuleManager;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.parser.SyntaxError;
import xbird.xquery.type.Type;
import xbird.xquery.type.TypeRegistry;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery/#dt-static-context
 * @link http://www.w3.org/TR/xquery/#id-xq-static-context-components
 */
public class StaticContext implements XQueryContext {
    private static final long serialVersionUID = 7173419309150108985L;

    public static final String DEFAULT_NAMESPACE = "none";

    private transient final ModuleManager moduleManager;

    /**
     * @link http://www.w3.org/TR/xquery-semantics/#xq_ns_env
     */
    private NamespaceBinder staticalyKnownNamespaces = new NamespaceBinder();

    /**
     * @link http://www.w3.org/TR/xquery-semantics/#xq_default_elem_ns_env
     */
    private String defaultElemNamespace = null;

    /**
     * @link http://www.w3.org/TR/xquery-semantics/#xq_default_fn_ns_env
     */
    private String defaultFunctionNamespace = XQueryConstants.FN_URI;

    /**
     * @link http://www.w3.org/TR/xquery-semantics/#xq_default_collation_env
     */
    private String defaultCollation = XQueryConstants.UNICODE_CODEPOINT_COLLATION;

    /**
     * @link http://www.w3.org/TR/xquery-semantics/#xq_construction_mode_env
     */
    private boolean constructionModeStrip = false;

    /**
     * @link http://www.w3.org/TR/xquery-semantics/#xq_ordering_mode_env
     */
    private boolean orderingModeOrdered = false;

    /**
     * @link http://www.w3.org/TR/xquery-semantics/#xq_default_empty_order
     */
    private boolean emptyLeast = false;

    /**
     * @link http://www.w3.org/TR/xquery-semantics/#xq_boundary_space
     */
    private boolean stripBoundarySpace = true;

    /**
     * @link http://www.w3.org/TR/xquery-semantics/#xq_copy_namespaces_mode
     */
    private boolean preserveNamespace = false;

    /**
     * @link http://www.w3.org/TR/xquery-semantics/#xq_copy_namespaces_mode
     */
    private boolean inheritNamespace = false;

    private String baseURIString = null;

    private final Map<String, PathVariable> pathVariableMap = new HashMap<String, PathVariable>();

    /**
     * @link http://www.w3.org/TR/xquery-semantics/#xq_base_uri_env
     */
    private URI baseURI = null;

    private Map<QualifiedName, String> options = null;

    // -------------------------------------------
    // extention

    public static final String SYSTEM_BASE_URI = ResourceUtils.toURIString(new File(System.getProperty("user.dir").replace(File.separatorChar, '/')));

    private transient URI systemBaseURI = null;

    /** just a place holder */
    private transient FunctionManager funcMgr = null;

    private transient List<ThreadedVariable> threadedVariables;

    private int loopDepth = 0;

    private Type contextItemType = null;

    private boolean useIndices = true;

    private transient Map<String, DTMDocument> defaultCollection = null;

    //--------------------------------------------

    public StaticContext() {
        this.moduleManager = new ModuleManager(this);
        resetStaticalyKnownNamespaces();
    }

    private void resetStaticalyKnownNamespaces() {
        staticalyKnownNamespaces.declarePrefix(XQueryConstants.XML, XQueryConstants.XML_URI);
        staticalyKnownNamespaces.declarePrefix(XQueryConstants.XS, XQueryConstants.XS_URI);
        staticalyKnownNamespaces.declarePrefix(XQueryConstants.XSI, XQueryConstants.XSI_URI);
        staticalyKnownNamespaces.declarePrefix(XQueryConstants.FN, XQueryConstants.FN_URI);
        staticalyKnownNamespaces.declarePrefix(XQueryConstants.XDT, XQueryConstants.XDT_URI);
        staticalyKnownNamespaces.declarePrefix(XQueryConstants.LOCAL, XQueryConstants.LOCAL_URI);
        staticalyKnownNamespaces.declarePrefix(XMLConstants.DEFAULT_NS_PREFIX, XMLUtils.NULL_NS_URI);
        staticalyKnownNamespaces.declarePrefix(JavaFunction.PROTOCOL, JavaFunction.PROTOCOL);
        staticalyKnownNamespaces.declarePrefix(BuiltInFunction.EXT_NSPREFIX, BuiltInFunction.EXT_NAMESPACE_URI);
    }

    //--------------------------------------------
    // Getter/Setter

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public NamespaceBinder getStaticalyKnownNamespaces() {
        return staticalyKnownNamespaces;
    }

    public String getDefaultElementNamespace() {
        return defaultElemNamespace;
    }

    public void setDefaultElementNamespace(String defaultElementNamespace) {
        this.defaultElemNamespace = defaultElementNamespace;
    }

    public String getDefaultFunctionNamespace() {
        return defaultFunctionNamespace;
    }

    public void setDefaultFunctionNamespace(String defaultFunctionNamespace) {
        this.defaultFunctionNamespace = defaultFunctionNamespace;
    }

    public String getDefaultCollation() {
        return defaultCollation;
    }

    public void setDefaultCollation(String uri) {
        this.defaultCollation = uri;
    }

    public boolean isConstructionModeStrip() {
        return constructionModeStrip;
    }

    public void setConstructionModeStrip(boolean constructionModeStrip) {
        this.constructionModeStrip = constructionModeStrip;
    }

    public boolean isEmptyLeast() {
        return emptyLeast;
    }

    public void setEmptyLeast(boolean emptyLeast) {
        this.emptyLeast = emptyLeast;
    }

    public boolean isOrderingModeOrdered() {
        return orderingModeOrdered;
    }

    public void setOrderingModeOrdered(boolean orderingModeOrdered) {
        this.orderingModeOrdered = orderingModeOrdered;
    }

    public boolean isStripBoundarySpace() {
        return stripBoundarySpace;
    }

    public void setStripBoundarySpace(boolean stripBoundarySpace) {
        this.stripBoundarySpace = stripBoundarySpace;
    }

    public boolean isPreserveNamespace() {
        return preserveNamespace;
    }

    public void setPreserveNamespace(boolean preserveNamespace) {
        this.preserveNamespace = preserveNamespace;
    }

    public boolean isInheritNamespace() {
        return inheritNamespace;
    }

    public void setInheritNamespace(boolean inheritNamespace) {
        this.inheritNamespace = inheritNamespace;
    }

    public String getBaseURIString() {
        return baseURIString; // TODO FIXME
    }

    public URI getBaseURI() throws XQueryException {
        if(baseURI != null) { // TODO FIXME
            return baseURI;
        } else {
            if(baseURIString == null) {
                return null;
            }
            final URI uri;
            try {
                uri = new URI(baseURIString);
            } catch (URISyntaxException e) {
                throw new SyntaxError("err:XQST0046", e);
            }
            this.baseURI = uri;
            return uri;
        }
    }

    public void setBaseURI(String baseURI) {
        this.baseURIString = baseURI;
    }

    public void setBaseURI(URI baseURI) {
        this.baseURI = baseURI;
        this.baseURIString = baseURI.toString();
    }

    public synchronized void putOption(QualifiedName key, String value) {
        if(options == null) {
            this.options = new HashMap<QualifiedName, String>();
        }
        options.put(key, value);
    }

    public String getOption(QualifiedName key) {
        if(options == null) {
            return null;
        }
        return options.get(key);
    }

    /**
     * @link http://www.w3.org/TR/xquery-semantics/#xq_type_defn
     */
    public Type getSchemaType(QualifiedName type) throws StaticError {
        return TypeRegistry.get(type); // TODO
    }

    public Map<String, PathVariable> getDeclaredPathVariables() {
        return pathVariableMap;
    }

    // ---------------------------------------------------------
    // proprietary extension

    public URI getSystemBaseURI() {
        if(systemBaseURI == null) {
            this.systemBaseURI = URI.create(SYSTEM_BASE_URI);
        }
        return systemBaseURI;
    }

    public void setSystemBaseURI(URI systemBaseURI) {
        this.systemBaseURI = systemBaseURI;
    }

    public void setFunctionManager(FunctionManager funcMgr) {
        this.funcMgr = funcMgr;
    }

    public FunctionManager getFunctionManager() {
        return funcMgr;
    }

    public void addThreadedVariable(ThreadedVariable var) {// does not called concurrently
        if(threadedVariables == null) {
            this.threadedVariables = new ArrayList<ThreadedVariable>(4);
        }
        threadedVariables.add(var);
    }

    public List<ThreadedVariable> getThreadedVariables() {// does not called concurrently
        return (threadedVariables == null) ? Collections.<ThreadedVariable> emptyList()
                : threadedVariables;
    }

    public int getLoopDepth() {
        return loopDepth;
    }

    public void setLoopDepth(int d) {
        this.loopDepth = d;
    }

    public int incrLoopDepth() {
        return ++loopDepth;
    }

    public Type getContextItemStaticType() {
        return contextItemType;
    }

    public void setContextItemStaticType(Type type) {
        this.contextItemType = type;
    }

    public boolean isIndicesAccessible() {
        return useIndices;
    }

    public void setIndicesAccessible(boolean useIndices) {
        this.useIndices = useIndices;
    }

    public Map<String, DTMDocument> getDefaultCollection() {
        return defaultCollection;
    }

    public void setDefaultCollection(Map<String, DTMDocument> defaultCollection) {
        this.defaultCollection = defaultCollection;
    }

}