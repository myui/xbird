/*
 * @(#)$Id: JavaFunction.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.func.ext;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xbird.util.lang.PrivilegedAccessor;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.expr.XQExpression;
import xbird.xquery.func.*;
import xbird.xquery.meta.*;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.parser.XQueryParserVisitor;
import xbird.xquery.type.*;

/**
 * Function to invoke a Java method.
 * <DIV lang="en">
 * <pre>
 * Ex).
 *  declare namespace math = "java:java.lang.Math";
 *  math:log(4)
 * </pre>
 * <u>Limitation:</u>
 * <ul>
 * <li>The number of arguments is restricted up to 5.</li>
 * </ul>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class JavaFunction extends BuiltInFunction {
    private static final long serialVersionUID = 4172568702626131690L;
    private static final Log LOG = LogFactory.getLog(JavaFunction.class);
    public static final String PROTOCOL = "java:";

    private transient Method bindedMethod = null; //TODO FIXME

    public JavaFunction(QualifiedName funcName) {
        super(funcName, SequenceType.ANY_ITEMS);
    }
    
    public JavaFunction() {
        super();
    }// for Externalizable

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[5];
        s[0] = new FunctionSignature(getName(), new Type[] { AtomicType.ANY_ATOMIC_TYPE });
        s[1] = new FunctionSignature(getName(), new Type[] { AtomicType.ANY_ATOMIC_TYPE,
                AtomicType.ANY_ATOMIC_TYPE });
        s[2] = new FunctionSignature(getName(), new Type[] { AtomicType.ANY_ATOMIC_TYPE,
                AtomicType.ANY_ATOMIC_TYPE, AtomicType.ANY_ATOMIC_TYPE });
        s[3] = new FunctionSignature(getName(), new Type[] { AtomicType.ANY_ATOMIC_TYPE,
                AtomicType.ANY_ATOMIC_TYPE, AtomicType.ANY_ATOMIC_TYPE });
        s[4] = new FunctionSignature(getName(), new Type[] { AtomicType.ANY_ATOMIC_TYPE,
                AtomicType.ANY_ATOMIC_TYPE, AtomicType.ANY_ATOMIC_TYPE });
        return s;
    }

    @Override
    public JavaFunction staticAnalysis(StaticContext context, List<? extends XQExpression> params)
            throws XQueryException {
        QualifiedName qname = getName();
        String clazz = toCamelCase(qname.getNamespaceURI().substring(5));
        String methodname = qname.getLocalPart();

        Class[] types = getArgTypes(params);
        final Method m;
        try {
            Class primeClass = Class.forName(clazz);
            assert (primeClass != null);
            m = PrivilegedAccessor.getMethod(primeClass, methodname, types);
        } catch (Exception e) {
            final StringBuilder msg = new StringBuilder();
            msg.append("method not found: " + clazz + '#' + methodname + '(');
            for(int i = 0; i < types.length; i++) {
                if(i != 0) {
                    msg.append(',');
                }
                msg.append(types[i].getSimpleName());
            }
            msg.append(')');
            throw new XQueryException(msg.toString(), e);
        }
        if(Modifier.isStatic(m.getModifiers()) == false) {
            throw new XQueryException("Method '" + m.getName() + "' is not a static method.");
        }
        m.setAccessible(true);
        if(LOG.isDebugEnabled()) {
            LOG.debug("Found Java method: " + m.toString());
        }
        // TODO return type setting.
        Class retType = m.getReturnType();

        this.bindedMethod = m;
        return this;
    }

    private static Class[] getArgTypes(List<? extends XQExpression> params) {
        Class c[] = new Class[params.size()];
        for(int i = 0; i < params.size(); i++) {
            c[i] = params.get(i).getType().getJavaObjectType();
        }
        return c;
    }

    /**
     * Converts method name with camel case.
     * <pre>
     * e.g)
     *  invoke-remote-servece -> invokeRemoteService
     * </pre>
     */
    private static String toCamelCase(String method) {
        String n[] = method.split("-");
        if(n.length > 1) {
            StringBuilder buf = new StringBuilder(32);
            buf.append(n[0]);
            for(int i = 1; i < n.length; i++) {
                buf.append(Character.toUpperCase(n[i].charAt(0)));
                if(n[i].length() > 1) {
                    buf.append(n[i].substring(1));
                }
            }
            return buf.toString();
        } else {
            return n[0];
        }
    }

    @Override
    public void visit(XQueryParserVisitor visitor, XQueryContext ctxt) throws XQueryException {
        visitor.visit(this, ctxt);
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        assert (bindedMethod != null);
        throw new UnsupportedOperationException();
    }

}
