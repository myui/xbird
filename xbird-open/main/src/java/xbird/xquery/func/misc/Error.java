/*
 * @(#)$Id: Error.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.func.misc;

import java.util.LinkedList;
import java.util.List;

import xbird.xquery.*;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.Sequence;
import xbird.xquery.dm.value.literal.XString;
import xbird.xquery.dm.value.sequence.ValueSequence;
import xbird.xquery.dm.value.xsi.QNameValue;
import xbird.xquery.func.BuiltInFunction;
import xbird.xquery.func.FunctionSignature;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.meta.IFocus;
import xbird.xquery.misc.QNameUtil;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.type.*;
import xbird.xquery.type.xs.QNameType;
import xbird.xquery.type.xs.StringType;

/**
 * The Error Function.
 * <DIV lang="en">
 * <ul>
 * <li>fn:error() as none</li>
 * <li>fn:error($error as xs:QName) as none</li>
 * <li>fn:error($error as xs:QName?, $description as xs:string) as none</li>
 * <li>fn:error($error as xs:QName?, $description as xs:string, $error-object as item()*) as none</li>
 * </ul>
 * </DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#func-error
 */
public final class Error extends BuiltInFunction {
    private static final long serialVersionUID = -6957170935059577190L;

    public static final String SYMBOL = "fn:error";
    private static final String DEFAULT_ERR_CODE = "err:FOER0000";
    private static final QualifiedName DEFAULT_ERR_NAME = QNameUtil.parse(DEFAULT_ERR_CODE, XQueryConstants.XQT_ERR_URI);

    public Error() {
        super(SYMBOL, Type.NONE);
    }

    protected FunctionSignature[] signatures() {
        final FunctionSignature[] s = new FunctionSignature[4];
        s[0] = new FunctionSignature(getName());
        s[1] = new FunctionSignature(getName(), new Type[] { QNameType.QNAME });
        s[2] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:QName?"),
                StringType.STRING });
        s[3] = new FunctionSignature(getName(), new Type[] { TypeRegistry.safeGet("xs:QName?"),
                StringType.STRING, SequenceType.ANY_ITEMS });
        return s;
    }

    public Sequence eval(Sequence<? extends Item> contextSeq, ValueSequence argv, DynamicContext dynEnv)
            throws XQueryException {
        final QualifiedName errorName;
        String desc = null;
        String[] errObjects = null;
        if(argv == null) {
            errorName = DEFAULT_ERR_NAME;
        } else {
            final int arglen = argv.size();
            final Item firstItem = argv.getItem(0);
            if(arglen == 1) {
                QNameValue err = (QNameValue) firstItem;
                errorName = err.getValue();
                final String trace = (new Throwable()).getStackTrace()[1].toString();
                desc = "fn:error is called at " + trace;
            } else {
                if(arglen == 3) {
                    final Item thirdItem = argv.getItem(2);
                    final List<String> errList = new LinkedList<String>();
                    final IFocus<Item> thirdItemItor = thirdItem.iterator();
                    for(Item err : thirdItemItor) {
                        errList.add(err.stringValue());
                    }
                    thirdItemItor.closeQuietly();
                    errObjects = errList.toArray(new String[errList.size()]);
                }
                if(firstItem.isEmpty()) {
                    errorName = DEFAULT_ERR_NAME;
                } else {
                    QNameValue err = (QNameValue) firstItem;
                    errorName = err.getValue();
                }
                Item secondItem = argv.getItem(1);
                assert (secondItem instanceof XString);
                desc = secondItem.stringValue();
            }
        }
        if(errObjects != null) {
            final StackTraceElement[] st = (new Throwable()).getStackTrace();
            errObjects = new String[st.length];
            for(int i = 0; i < st.length; i++) {
                StackTraceElement e = st[i];
                errObjects[i] = e.toString();
            }
        }
        assert (errorName != null && desc != null && errObjects != null);
        throw new XQError(errorName, desc, errObjects);
    }

    public static final class XQError extends DynamicError {
        private static final long serialVersionUID = 7003903979202149656L;

        public XQError(QualifiedName errorName, String desc, String[] errObjects) {
            super(QNameUtil.toLexicalForm(errorName), createMessage(desc, errObjects));
        }

        private static String createMessage(String desc, String[] errObjects) {
            assert (desc != null);
            final StringBuilder buf = new StringBuilder(256);
            buf.append("[fn:error] ");
            buf.append(desc);
            if(errObjects != null && errObjects.length > 0) {
                buf.append("\n-------- error-objects --------");
                for(String e : errObjects) {
                    buf.append('\n');
                    buf.append(e);
                }
                buf.append("\n------------------------------- \n");
            }
            return buf.toString();
        }

    }

}
