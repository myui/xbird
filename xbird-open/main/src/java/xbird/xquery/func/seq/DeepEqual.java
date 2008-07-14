/*
 * @(#)$Id: DeepEqual.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.func.seq;

import java.text.Collator;
import java.util.Iterator;

import xbird.util.resource.CollationUtils;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.NodeKind;
import xbird.xquery.dm.instance.DocumentTableModel.DTMAttribute;
import xbird.xquery.dm.instance.DocumentTableModel.DTMElement;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.node.DMAttribute;
import xbird.xquery.dm.value.node.DMElement;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.dm.value.xsi.BooleanValue;
import xbird.xquery.expr.comp.ValueComp;
import xbird.xquery.expr.func.DirectFunctionCall;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.misc.TypeUtil;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.type.*;
import xbird.xquery.type.xs.BooleanType;
import xbird.xquery.type.xs.StringType;

/**
 * fn:deep-equal.
 * <DIV lang="en">
 * This function assesses whether two sequences are deep-equal to each other. 
 * <ul>
 * <li>fn:deep-equal($parameter1 as item()*, $parameter2 as item()*) as xs:boolean</li>
 * <li>fn:deep-equal($parameter1 as item()*, $parameter2 as item()*, $collation as string) as xs:boolean</li>
 * </ul>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-deep-equal
 */
public final class DeepEqual extends BuiltInFunction {
    private static final long serialVersionUID = 5614122631562133090L;
    public static final String SYMBOL = "fn:deep-equal";

    public DeepEqual() {
        super(SYMBOL, BooleanType.BOOLEAN);
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[2];
        s[0] = new FunctionSignature(getName(), new Type[] { SequenceType.ANY_ITEMS,
                SequenceType.ANY_ITEMS });
        s[1] = new FunctionSignature(getName(), new Type[] { SequenceType.ANY_ITEMS,
                SequenceType.ANY_ITEMS, StringType.STRING });
        return s;
    }

    /**
     * @see DirectFunctionCall#eval(Sequence, DynamicContext)
     */
    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        // If the two sequences are both empty, the function returns true.        
        Item param1 = argv.getItem(0); // convention to be SingleCollection
        Item param2 = argv.getItem(1);
        if(param1.isEmpty() && param2.isEmpty()) {
            return BooleanValue.TRUE;
        }
        final Collator collator;
        final int arglen = argv.size();
        if(arglen == 3) {
            Item third = argv.getItem(2);
            final String collation = third.stringValue();
            collator = CollationUtils.resolve(collation, dynEnv.getStaticContext());
        } else {
            collator = null;
        }
        Iterator<? extends Item> param1Itor = param1.iterator();
        Iterator<? extends Item> param2Itor = param2.iterator();
        int p1ctr = 0, p2ctr = 0;
        final Type anyatomicType = TypeRegistry.safeGet("xs:anyAtomicType*");
        outer: while(param1Itor.hasNext()) {
            ++p1ctr;
            Item p1 = param1Itor.next();
            while(param2Itor.hasNext()) {
                ++p2ctr;
                Item p2 = param2Itor.next();
                Type p1type = p1.getType();
                Type p2type = p2.getType();
                if(TypeUtil.subtypeOf(p1type, anyatomicType)
                        && TypeUtil.subtypeOf(p2type, anyatomicType)) {
                    // If $i1 and $i2 are both atomic values, they are deep-equal
                    // if and only if ($i1 eq $i2) is true.
                    if(ValueComp.effectiveBooleanValue(ValueComp.Operator.EQ, p1, p2, dynEnv)) {
                        continue outer;
                    } else {
                        return BooleanValue.FALSE;
                    }
                } else if(p1 instanceof XQNode && p2 instanceof XQNode) {
                    // If $i1 and $i2 are both nodes..
                    XQNode n1 = (XQNode) p1, n2 = (XQNode) p2;
                    // #2 If the two nodes are both document nodes then they are deep-equal
                    //   if and only if the sequence $i1/(*|text()) is deep-equal to the sequence
                    //   $i2/(*|text()).
                    if(deepEqual(n1, n2, collator, false)) {
                        continue outer;
                    } else {
                        return BooleanValue.FALSE;
                    }
                } else {
                    // The result of fn:deep-equal(1, current-dateTime()) is false; 
                    // it does not raise an error.
                    return BooleanValue.FALSE;
                }
            }
        }
        // If the two sequences are of different lengths, the function returns false.
        return (p1ctr == p2ctr) ? BooleanValue.TRUE : BooleanValue.FALSE;
    }

    private static boolean deepEqual(XQNode n1, XQNode n2, Collator collator, boolean descOfItem) {
        // TODO performance improvement!
        // #1 If the two nodes are of different kinds, the result is false.
        final long n1pos = n1.getPosition(), n2pos = n2.getPosition();
        if(n1pos != -1 && n1pos == n2pos) {
            return true;
        }
        final byte n1kind = n1.nodeKind(), n2kind = n2.nodeKind();
        if(n1kind != n2kind) {
            return false;
        }
        switch(n1kind) {
            case NodeKind.DOCUMENT: {
                // If the two nodes are both document nodes then they are deep-equal 
                // if and only if the sequence $i1/(*|text()) is deep-equal to 
                // the sequence $i2/(*|text()).
                XQNode n1child = n1.firstChild();
                XQNode n2child = n2.firstChild();
                if(n1child == n2child) {
                    break;
                } else if(n1child == null && n2child != null) {
                    return false;
                }
                int n1cnt = 0, n2cnt = 0;
                for(; n1child != null; n1child = n1child.nextSibling()) {// childs match
                    ++n1cnt;
                    n2child = null;
                    // siblings match
                    for(; n2child != null; n2child = n2child.nextSibling()) {
                        ++n2cnt;
                        if(!deepEqual(n1child, n2child, collator, true)) {
                            return false;
                        }
                        n1child = n1child.nextSibling();
                        if(n1child != null) {
                            ++n1cnt;
                        }
                    }
                    if(n1child == null) {
                        if(n2child != null) {
                            return false;
                        }
                        break;
                    }
                }
                if(n1cnt != n2cnt) {
                    return false;
                }
                break;
            }
            case NodeKind.ELEMENT: {
                // #1 the two nodes have the same name, that is (node-name($i1) eq node-name($i2)).                
                if(!n1.nodeName().equals(n2.nodeName())) {
                    return false;
                }
                // #2 the two nodes are both annotated as having simple content or 
                //   both nodes are annotated as having complex content.
                // TODO aware schema (typed value)
                XQNode n1child = n1.firstChild();
                XQNode n2child = n2.firstChild();
                if(n1child == null) {
                    if(n2child != null) {
                        return false;
                    }
                } else if(n1child != n2child) {
                    int n1cnt = 0, n2cnt = 0;
                    for(; n1child != null; n1child = n1child.nextSibling()) {// childs match
                        ++n1cnt;
                        // siblings match
                        for(; n2child != null && n1child != null; n2child = n2child.nextSibling()) {
                            ++n2cnt;
                            if(!deepEqual(n1child, n2child, collator, true)) {
                                return false;
                            }
                            n1child = n1child.nextSibling();
                            if(n1child != null) {
                                ++n1cnt;
                            }
                        }
                        if(n1child == null) {
                            if(n2child != null) {
                                return false;
                            }
                            break;
                        }
                    }
                    if(n1cnt != n2cnt) {
                        return false;
                    }
                }
                // #3 check if the two nodes have the same number of attributes
                if(!attributesEqual(n1, n2, collator)) {
                    return false;
                }
                break;
            }
            case NodeKind.PROCESSING_INSTRUCTION:
                // The content of a comment or processing instruction that appears as a descendant of an item
                // in one of the sequences being compared does not affect the result.
                if(descOfItem) {
                    break;
                }
            case NodeKind.ATTRIBUTE:
            case NodeKind.NAMESPACE:
                if(!n1.nodeName().equals(n2.nodeName())) {
                    return false;
                }
                if(!n1.getContent().equals(n2.getContent())) {
                    return false;
                }
                break;
            case NodeKind.COMMENT:
                if(descOfItem) {
                    break;
                }
            case NodeKind.TEXT:
            case NodeKind.CDATA: {
                final String n1content = n1.getContent();
                final String n2content = n2.getContent();
                if(n1content == null) {
                    if(n2content != null) {
                        return false;
                    }
                } else {
                    if(!n1content.equals(n2content)) {
                        return false;
                    }
                }
                break;
            }
            default:
                throw new IllegalStateException("Invalid node kind was detected: "
                        + NodeKind.resolveName(n1kind));
        }
        return true;
    }

    private static boolean attributesEqual(XQNode n1, XQNode n2, Collator collator) {
        assert (n1.nodeKind() == NodeKind.ELEMENT && n2.nodeKind() == NodeKind.ELEMENT);
        if(n1 instanceof DMElement) {
            assert (n2 instanceof DMElement);
            DMElement e1 = (DMElement) n1, e2 = (DMElement) n2;
            Iterator<DMAttribute> a1Itor = e1.attribute().iterator();
            Iterator<DMAttribute> a2Itor = e2.attribute().iterator();
            while(a1Itor.hasNext()) {
                if(!a2Itor.hasNext()) {
                    return false;
                }
                DMAttribute a1 = a1Itor.next();
                DMAttribute a2 = a2Itor.next();
                QualifiedName a1name = a1.nodeName();
                QualifiedName a2name = a2.nodeName();
                if(!a1name.equals(a2name)) {
                    return false;
                }
                String a1val = a1.getContent();
                String a2val = a2.getContent();
                assert (a1val != null);
                if(collator != null) {
                    if(!collator.equals(a1val, a2val)) {
                        return false;
                    }
                } else {
                    if(!a1val.equals(a2val)) {
                        return false;
                    }
                }
            }
        } else {
            // TODO performance improvement!
            assert (n1 instanceof DTMElement && n2 instanceof DTMElement);
            DTMElement e1 = (DTMElement) n1, e2 = (DTMElement) n2;
            DTMAttribute att1 = null;
            for(int i = 0; (att1 = e1.attribute(i)) != null; i++) {
                DTMAttribute att2 = e2.attribute(i);
                if(!att1.nodeName().equals(att2.nodeName())) {
                    return false;
                }
                final String att1val = att1.getContent();
                final String att2val = att2.getContent();
                if(collator != null) {
                    if(!collator.equals(att1val, att2val)) {
                        return false;
                    }
                } else {
                    if(!att1val.equals(att2.getContent())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
