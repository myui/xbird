/*
 * @(#)$Id: FunctionManagerTest.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import xbird.util.lang.PrivilegedAccessor;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.literal.XDouble;
import xbird.xquery.expr.LiteralExpr;
import xbird.xquery.expr.var.VarRef;
import xbird.xquery.expr.var.Variable;
import xbird.xquery.func.Function;
import xbird.xquery.misc.FunctionManager;
import xbird.xquery.misc.QNameTable;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.type.xs.DoubleType;

public class FunctionManagerTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(FunctionManagerTest.class);
    }

    public FunctionManagerTest(String arg0) {
        super(arg0);
    }

    public void testLookupFunction() throws XQueryException, IllegalAccessException,
            NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        // params
        Variable v1 = new Variable.GlobalVariable(QNameTable.instantiate("", "p1"), new LiteralExpr(new XDouble(4)));
        v1.setType(DoubleType.DOUBLE);
        VarRef p1 = new VarRef(v1);
        List<VarRef> params = new LinkedList<VarRef>();
        params.add(p1);
        // function
        QualifiedName fname = QNameTable.instantiate("java:java.lang.Math", "sqrt");
        Function f = new FunctionManager().lookupFunction(fname, params);
        assertNotNull(f);
        f.staticAnalysis(null, params);

        // invoke binded java method.
        Method bindedMethod = (Method) PrivilegedAccessor.getValue(f, "bindedMethod");
        System.out.println("java.lang.Math.sqrt(4.0) = "
                + bindedMethod.invoke(bindedMethod, new Double(4)));
        System.out.println("java.lang.Math.sqrt(4.0) = " + bindedMethod.invoke(bindedMethod, 4.0));
        System.out.println("java.lang.Math.sqrt(4) = "
                + bindedMethod.invoke(bindedMethod, new Integer(4)));
        System.out.println("java.lang.Math.sqrt(4) = " + bindedMethod.invoke(bindedMethod, 4));
    }

}
