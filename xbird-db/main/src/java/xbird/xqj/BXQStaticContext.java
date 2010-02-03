/*
 * @(#)$Id$
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
package xbird.xqj;

import static javax.xml.xquery.XQConstants.*;

import javax.xml.xquery.*;

import xbird.util.xml.NamespaceBinder;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.type.Type;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class BXQStaticContext implements XQStaticContext {

    private final StaticContext statEnv_;

    private int _bindingMode = BINDING_MODE_IMMEDIATE;
    private int _holdability = HOLDTYPE_HOLD_CURSORS_OVER_COMMIT; // REVIEWME
    private int _timeout = 0;
    private int _scrollability = SCROLLTYPE_FORWARD_ONLY;

    public BXQStaticContext(StaticContext statEnv) {
        this.statEnv_ = statEnv;
    }

    public void declareNamespace(String prefix, String uri) throws XQException {
        final NamespaceBinder nsmap = statEnv_.getStaticalyKnownNamespaces();
        nsmap.declarePrefix(prefix, uri);
    }

    public String getBaseURI() {
        return statEnv_.getBaseURIString();
    }

    public int getBindingMode() {
        return _bindingMode;
    }

    public int getBoundarySpacePolicy() {
        return statEnv_.isConstructionModeStrip() ? BOUNDARY_SPACE_STRIP : BOUNDARY_SPACE_PRESERVE;
    }

    public int getConstructionMode() {
        return statEnv_.isConstructionModeStrip() ? CONSTRUCTION_MODE_STRIP
                : CONSTRUCTION_MODE_PRESERVE;
    }

    public XQItemType getContextItemStaticType() {
        final Type rawType = statEnv_.getContextItemStaticType();
        return new BXQItemType(rawType);
    }

    public int getCopyNamespacesModeInherit() {
        return statEnv_.isInheritNamespace() ? COPY_NAMESPACES_MODE_INHERIT
                : COPY_NAMESPACES_MODE_NO_INHERIT;
    }

    public int getCopyNamespacesModePreserve() {
        return statEnv_.isPreserveNamespace() ? COPY_NAMESPACES_MODE_PRESERVE
                : COPY_NAMESPACES_MODE_NO_PRESERVE;
    }

    public String getDefaultCollation() {
        return statEnv_.getDefaultCollation();
    }

    public String getDefaultElementTypeNamespace() {
        return statEnv_.getDefaultElementNamespace();
    }

    public String getDefaultFunctionNamespace() {
        return statEnv_.getDefaultFunctionNamespace();
    }

    public int getDefaultOrderForEmptySequences() {
        return statEnv_.isEmptyLeast() ? DEFAULT_ORDER_FOR_EMPTY_SEQUENCES_LEAST
                : DEFAULT_ORDER_FOR_EMPTY_SEQUENCES_GREATEST;
    }

    public int getHoldability() {
        return _holdability;
    }

    public String[] getNamespacePrefixes() {
        final NamespaceBinder nsmap = statEnv_.getStaticalyKnownNamespaces();
        return nsmap.getAllPrefixesAsArray();
    }

    public String getNamespaceURI(String prefix) throws XQException {
        final NamespaceBinder nsmap = statEnv_.getStaticalyKnownNamespaces();
        return nsmap.getNamespaceURI(prefix);
    }

    public int getOrderingMode() {
        return statEnv_.isOrderingModeOrdered() ? ORDERING_MODE_ORDERED : ORDERING_MODE_UNORDERED;
    }

    public int getQueryLanguageTypeAndVersion() {
        return LANGTYPE_XQUERY;
    }

    public int getQueryTimeout() {
        return _timeout;
    }

    public int getScrollability() {
        return _scrollability;
    }

    public void setBaseURI(String baseUri) throws XQException {
        statEnv_.setBaseURI(baseUri);
    }

    public void setBindingMode(int bindingMode) throws XQException {
        if(bindingMode != BINDING_MODE_DEFERRED && bindingMode != BINDING_MODE_IMMEDIATE) {
            throw new XQException("Illegal argument: " + bindingMode, "err:XQJxxxx");
        }
        this._bindingMode = bindingMode;
    }

    public void setBoundarySpacePolicy(int policy) throws XQException {
        if(policy == BOUNDARY_SPACE_PRESERVE) {
            statEnv_.setStripBoundarySpace(false);
        } else if(policy == BOUNDARY_SPACE_STRIP) {
            statEnv_.setStripBoundarySpace(true);
        } else {
            throw new XQException("Illegal policy value: " + policy, "err:XQJxxxx");
        }
    }

    public void setConstructionMode(int mode) throws XQException {
        if(mode == CONSTRUCTION_MODE_PRESERVE) {
            statEnv_.setConstructionModeStrip(false);
        } else if(mode == CONSTRUCTION_MODE_STRIP) {
            statEnv_.setConstructionModeStrip(true);
        } else {
            throw new XQException("Illegal value as construction mode: " + mode, "err:XQJxxxx");
        }
    }

    public void setContextItemStaticType(XQItemType contextItemType) {
        if(contextItemType == null) {
            statEnv_.setContextItemStaticType(null);
        } else if(contextItemType instanceof BXQItemType) {
            Type type = ((BXQItemType) contextItemType).getInternalType();
            statEnv_.setContextItemStaticType(type);
        }
        throw new UnsupportedOperationException("Unexpected type: " + contextItemType);//TODO
    }

    public void setCopyNamespacesModeInherit(int mode) throws XQException {
        if(mode == COPY_NAMESPACES_MODE_INHERIT) {
            statEnv_.setInheritNamespace(true);
        } else if(mode == COPY_NAMESPACES_MODE_NO_INHERIT) {
            statEnv_.setInheritNamespace(false);
        } else {
            throw new XQException("Illegal value as inherit mode: " + mode, "err:XQJxxxx");
        }
    }

    public void setCopyNamespacesModePreserve(int mode) throws XQException {
        if(mode == COPY_NAMESPACES_MODE_PRESERVE) {
            statEnv_.setPreserveNamespace(true);
        } else if(mode == COPY_NAMESPACES_MODE_NO_PRESERVE) {
            statEnv_.setPreserveNamespace(false);
        } else {
            throw new XQException("Illegal value as the COPY_NAMESPACES_MODE: " + mode, "err:XQJxxxx");
        }
    }

    public void setDefaultCollation(String uri) throws XQException {
        if(uri == null) {
            throw new XQException("The namespace URI of the default collation should not be null.", "err:XQJxxxx");
        }
        statEnv_.setDefaultCollation(uri);
    }

    public void setDefaultElementTypeNamespace(String uri) throws XQException {
        if(uri == null) {
            throw new XQException("The URI of the default element/type namespace should not be null.", "err:XQJxxxx");
        }
        statEnv_.setDefaultElementNamespace(uri);
    }

    public void setDefaultFunctionNamespace(String uri) throws XQException {
        if(uri == null) {
            throw new XQException("The URI of the default function namespace should not be null.", "err:XQJxxxx");
        }
        statEnv_.setDefaultFunctionNamespace(uri);
    }

    public void setDefaultOrderForEmptySequences(int order) throws XQException {
        if(order == DEFAULT_ORDER_FOR_EMPTY_SEQUENCES_GREATEST) {
            statEnv_.setEmptyLeast(false);
        } else if(order == DEFAULT_ORDER_FOR_EMPTY_SEQUENCES_LEAST) {
            statEnv_.setEmptyLeast(true);
        } else {
            throw new XQException("Illegal value as the default order: " + order, "err:XQJxxxx");
        }
    }

    public void setHoldability(int holdability) throws XQException {
        if(holdability != HOLDTYPE_CLOSE_CURSORS_AT_COMMIT
                && holdability != HOLDTYPE_HOLD_CURSORS_OVER_COMMIT) {
            throw new XQException("Illegal holdability value: " + holdability, "err:XQJxxxx");
        }
        this._holdability = holdability;
    }

    public void setOrderingMode(int mode) throws XQException {
        if(mode == ORDERING_MODE_ORDERED) {
            statEnv_.setOrderingModeOrdered(true);
        } else if(mode == ORDERING_MODE_UNORDERED) {
            statEnv_.setOrderingModeOrdered(false);
        } else {
            throw new XQException("Illegal ordering mode: " + mode, "err:XQJxxxx");
        }
    }

    public void setQueryLanguageTypeAndVersion(int langType) throws XQException {
        if(langType != LANGTYPE_XQUERY) {
            throw new XQException("Language types other than XQUERY(" + LANGTYPE_XQUERY
                    + ") are not supported, but was " + langType, "err:XQJxxxx");
        }
    }

    public void setQueryTimeout(int seconds) throws XQException {
        if(seconds < 0) {
            throw new XQException("Illegal argument value: " + seconds, "err:XQJxxxx");
        }
        this._timeout = seconds;
    }

    public void setScrollability(int scrollability) throws XQException {
        if(scrollability != SCROLLTYPE_FORWARD_ONLY && scrollability != SCROLLTYPE_SCROLLABLE) {
            throw new XQException("Illegal argument value: " + scrollability, "err:XQJxxxx");
        }
        this._scrollability = scrollability; // TODO
    }

}
