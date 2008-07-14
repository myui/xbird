/*
 * @(#)$Id: QNameValue.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.dm.value.xsi;

import java.io.*;

import javax.xml.namespace.QName;

import xbird.util.lang.ObjectUtils;
import xbird.xquery.XQRTException;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.AtomicValue;
import xbird.xquery.dm.value.Item;
import xbird.xquery.misc.QNameUtil;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.type.xs.QNameType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class QNameValue extends AtomicValue {
    private static final long serialVersionUID = 7686947492583230440L;
    public static final int ID = 8;

    private/* final */QualifiedName value;

    public QNameValue() {
        super();
    }

    public QNameValue(QualifiedName value) {
        super(QNameUtil.toLexicalForm(value), QNameType.QNAME);
        if(value == null) {
            throw new IllegalArgumentException();
        }
        this.value = value;
    }

    public QualifiedName getValue() {
        return value;
    }

    public QName toJavaObject() throws XQueryException {
        return QualifiedName.toJavaxQName(value);
    }

    @Override
    public int compareTo(Item trg) {
        if(!(trg instanceof QNameValue)) {
            throw new XQRTException("err:XPTY0004", "Imcomparable "
                    + trg.getClass().getSimpleName() + " with QNameValue");
        }
        final QNameValue qname = (QNameValue) trg;
        final QualifiedName targetName = qname.getValue();
        if(value.equals(targetName)) {
            return 0;
        } else {
            return value.identity() > qname.value.identity() ? 1 : -1;
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this._lexicalValue = ObjectUtils.readString(in);
        this._type = QNameType.QNAME;
        this.value = QualifiedName.readFrom(in);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        ObjectUtils.writeString(out, _lexicalValue);
        value.writeExternal(out);
    }

    @Override
    public int getIdentifier() {
        return ID;
    }

}
