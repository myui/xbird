/*
 * @(#)$Id: DMDocument.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.dm.value.node;

import xbird.xquery.TypeError;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.NodeKind;
import xbird.xquery.dm.instance.XQueryDataModel;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.sequence.*;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.IFocus;

/**
 * Document Nodes encapsulate XML documents.
 * <DIV lang="en">
 * Documents have the following properties:
 * <ul>
 *  <li>base-uri, possibly empty.</li>
 *  <li>children, possibly empty.</li>
 *  <li>unparsed-entities, possibly empty.</li>
 *  <li>document-uri, possibly empty.</li>
 *  <li>string-value</li>
 *  <li>typed-value</li>
 * </ul>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xpath-datamodel/#DocumentNode
 * @link http://www.w3.org/TR/xquery/#doc-xquery-CompDocConstructor
 */
public class DMDocument extends DMNode {
    private static final long serialVersionUID = -831149877569544920L;

    private INodeSequence<DMNode> _children = NodeSequence.emptySequence();
    private final String _documentUri;

    public DMDocument() {//for serialization
        super();
        this._documentUri = null;
    }

    public DMDocument(String baseUri, String documentUri) {
        super();
        this._documentUri = documentUri;
        setDocumentId(getDataModel().nextDocumentId());
        setBaseUri(baseUri);
    }

    public DMDocument(String baseUri, Sequence contents) throws XQueryException {
        super();
        this._documentUri = null;
        this._children = processContents(contents);
        setBaseUri(baseUri);
    }

    protected DMDocument(int id, String baseUri, String documentUri) {
        super(id);
        this._documentUri = documentUri;
        setBaseUri(baseUri);
    }

    public void setChildren(INodeSequence<DMNode> childs) {
        this._children = childs;
    }

    //--------------------------------------------
    // Data Model accessors

    @Override
    public INodeSequence<DMNode> children() {
        return _children;
    }

    @Override
    public String documentUri() {
        return _documentUri;
    }

    public byte nodeKind() {
        return NodeKind.DOCUMENT;
    }

    @Override
    public String getContent() {
        throw new IllegalStateException(getClass().getSimpleName()
                + "#getContent() should not be called.");
    }

    @Override
    public void setParent(DMNode n) {
        throw new IllegalStateException("setParent for Document Node is not allowed.");
    }

    //--------------------------------------------
    // helpers

    /**
     * Adjacent text nodes in the content sequence are merged into a single text node 
     * by concatenating their contents, with no intervening blanks.
     * 
     * @link http://www.w3.org/TR/xquery/#doc-xquery-CompDocConstructor
     */
    private INodeSequence<DMNode> processContents(final Sequence<? extends Item> contents)
            throws XQueryException {
        assert (contents != null);
        final NodeSequence<DMNode> nodes = new LinkedNodeSequence<DMNode>(DynamicContext.DUMMY);
        DMText prevText = null;
        boolean prevAtom = false;
        final IFocus<? extends Item> itor = contents.iterator();
        for(Item i : itor) {
            if(i instanceof XQNode) {
                final DMNode dmnode = ((XQNode) i).asDMNode();
                byte nodekind = dmnode.nodeKind();
                if(nodekind == NodeKind.DOCUMENT) {
                    // If the content sequence contains a document node, the document node 
                    // is replaced in the content sequence by its children.
                    final INodeSequence<? extends DMNode> childs = ((DMNode) dmnode).children();
                    for(DMNode child : childs) {
                        DMNode c = child.clone();
                        nodekind = c.nodeKind();
                        if(nodekind == NodeKind.TEXT) {
                            String curText = c.getContent();
                            assert (curText != null);
                            if(curText.length() == 0) {
                                continue;
                            }
                            if(prevText != null) {
                                prevText.mergeContent(curText);
                                continue;
                            }
                            prevText = (DMText) c;
                        } else {
                            prevText = null;
                        }
                        nodes.addItem(c);
                    }
                } else if(nodekind == NodeKind.ATTRIBUTE) {
                    throw new TypeError("err:XPTY0004", "An attribute node in the content sequence is not allowed: "
                            + i);
                } else {
                    DMNode c = dmnode.clone();
                    if(nodekind == NodeKind.TEXT) {
                        String curText = c.getContent();
                        assert (curText != null);
                        if(curText.length() == 0) {
                            continue;
                        }
                        if(prevText != null) {
                            prevText.mergeContent(curText);
                        } else {
                            nodes.addItem(c);
                            prevText = (DMText) c;
                        }
                    } else {
                        nodes.addItem(c);
                        prevText = null;
                    }
                }
                prevAtom = false;
            } else {
                String sv = i.stringValue();
                DMText txt = XQueryDataModel.createText(sv);
                if(prevText != null) {
                    String ctxt = txt.getContent();
                    if(prevAtom) {
                        ctxt = ' ' + ctxt;
                    }
                    prevText.mergeContent(ctxt);
                } else {
                    if(sv.length() == 0) {
                        continue;
                    }
                    prevText = txt;
                    nodes.addItem(txt);
                }
                prevAtom = true;
            }
        }
        itor.closeQuietly();
        return nodes;
    }
}
