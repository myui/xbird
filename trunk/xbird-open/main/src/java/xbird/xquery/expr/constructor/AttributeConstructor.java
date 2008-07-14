/*
 * @(#)$Id: AttributeConstructor.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.expr.constructor;

import java.util.ArrayList;
import java.util.List;

import xbird.xquery.XQueryConstants;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.XQEventReceiver;
import xbird.xquery.dm.instance.XQueryDataModel;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.node.DMAttribute;
import xbird.xquery.dm.value.sequence.AtomizedSequence;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.meta.*;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.node.AttributeTest;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class AttributeConstructor extends AttributeConstructorBase {
    private static final long serialVersionUID = 1939114435839918721L;

    private final QualifiedName name; // avoid null for ComputedAttributeConstructor
    private final List<XQExpression> valueExprs;

    public AttributeConstructor(QualifiedName name) {
        this(name, new ArrayList<XQExpression>(4));
    }

    public AttributeConstructor(XQExpression nameExpr) {
        this(null, new ArrayList<XQExpression>(4));
        this._nameExpr = nameExpr;
    }

    public AttributeConstructor(QualifiedName name, List<XQExpression> value) {
        this.name = name;
        this.valueExprs = value;
        this._type = AttributeTest.ANY_ATTRIBUTE;
    }

    public XQExpression visit(XQueryParserVisitor visitor, XQueryContext ctxt)
            throws XQueryException {
        return visitor.visit(this, ctxt);
    }

    //--------------------------------------------
    // getter/setter

    public void addValue(XQExpression values) {
        valueExprs.add(values);
    }

    public QualifiedName getName() {
        return this.name;
    }

    public List<XQExpression> getValueExprs() {
        return this.valueExprs;
    }

    //--------------------------------------------
    // static analysis/dynamic evaluation

    public XQExpression staticAnalysis(StaticContext statEnv) throws XQueryException {
        if(!_analyzed) {
            this._analyzed = true;
            if(name == null) {
                this._nameExpr = _nameExpr.staticAnalysis(statEnv);
            }
            if(_value == null) {
                for(int i = 0; i < valueExprs.size(); i++) {
                    XQExpression v = valueExprs.get(i);
                    valueExprs.set(i, v.staticAnalysis(statEnv));
                }
            }
        }
        return this;
    }

    /**
     * @link http://www.w3.org/TR/xquery/#id-computedAttributes
     */
    public DMAttribute eval(Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        // process name
        QualifiedName instName = this.name;
        if(instName == null) { // note that should not cache it
            instName = processNameExpr(contextSeq, dynEnv);
        }
        if(XQueryConstants.XMLNS_URI.equals(instName.getNamespaceURI())
                || (instName.getNamespaceURI().length() == 0 && XQueryConstants.XMLNS.equals(instName.getLocalPart()))) {
            reportError("err:XQDY0044", "Invalid namespace format.. " + instName);
        }
        // process content
        String instValue = this._value;
        if(instValue == null) {
            instValue = processContentExpr(contextSeq, dynEnv);
        }
        return XQueryDataModel.createAttribute(instName, instValue);
    }

    @Override
    public void evalAsEvents(XQEventReceiver handler, Sequence<? extends Item> contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        // process name
        QualifiedName instName = this.name;
        if(instName == null) { // note that should not cache it
            instName = processNameExpr(contextSeq, dynEnv);
        }
        if(XQueryConstants.XMLNS_URI.equals(instName.getNamespaceURI())
                || (instName.getNamespaceURI().length() == 0 && XQueryConstants.XMLNS.equals(instName.getLocalPart()))) {
            reportError("err:XQDY0044", "Invalid namespace format.. " + instName);
        }
        // process content
        String instValue = this._value;
        if(instValue == null) {
            instValue = processContentExpr(contextSeq, dynEnv);
        }
        handler.evAttribute(instName, instValue);
    }

    private String processContentExpr(Sequence contextSeq, DynamicContext dynEnv)
            throws XQueryException {
        final int size = valueExprs.size();
        if(size == 0) {
            return "";
        }
        final StringBuilder buf = new StringBuilder(128);
        for(int i = 0; i < size; i++) {
            final XQExpression v = valueExprs.get(i);
            final Sequence rawseq = v.eval(contextSeq, dynEnv);
            final Sequence seq = AtomizedSequence.wrap(rawseq, dynEnv);
            final IFocus<Item> itor = seq.iterator();
            if(itor.hasNext()) {
                while(true) {
                    final Item it = itor.next();
                    buf.append(it.stringValue());
                    if(itor.hasNext()) {
                        buf.append(' ');
                    } else {
                        break;
                    }
                }
            }
            itor.closeQuietly();
        }
        return buf.toString();
    }

}
