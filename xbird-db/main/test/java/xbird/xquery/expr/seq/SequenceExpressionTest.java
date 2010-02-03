/*
 * @(#)$Id: SequenceExpressionTest.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.expr.seq;

import java.util.Iterator;

import junit.framework.TestCase;

import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.*;
import xbird.xquery.dm.value.literal.XString;
import xbird.xquery.expr.LiteralExpr;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.StaticContext;

public class SequenceExpressionTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(SequenceExpressionTest.class);
    }

    public SequenceExpressionTest(String arg0) {
        super(arg0);
    }

    public void testEval() throws XQueryException {
        SequenceExpression seq = new SequenceExpression();
        AtomicValue it1 = new XString("1");
        AtomicValue it2 = new XString("2");
        seq.addExpression(new LiteralExpr(it1));
        seq.addExpression(new LiteralExpr(it2));
        Sequence<Item> res = (Sequence<Item>) seq.eval(null, new DynamicContext(new StaticContext()));
        Iterator<Item> resItor = res.iterator();
        assertTrue(resItor.hasNext());
        assertSame(it1, resItor.next());
        assertTrue(resItor.hasNext());
        assertSame(it2, resItor.next());
        assertFalse(resItor.hasNext());
    }

}
