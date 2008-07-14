/*
 * @(#)$Id: AnyURIValue.java 3619 2008-03-26 07:23:03Z yui $
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
import java.net.URI;

import xbird.util.lang.ObjectUtils;
import xbird.util.xml.XMLUtils;
import xbird.xquery.XQRTException;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.AtomicValue;
import xbird.xquery.dm.value.Item;
import xbird.xquery.type.xs.AnyURIType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class AnyURIValue extends AtomicValue {
    private static final long serialVersionUID = -1014398588671125866L;
    public static final int ID = 12;

    private/* final */URI value;

    public AnyURIValue() { // for Externalizable
        super();
        this._type = AnyURIType.ANYURI;
    }

    private AnyURIValue(String literal, URI uri) {
        super(literal, AnyURIType.ANYURI);
        this.value = uri;
    }

    public AnyURIValue(URI uri) {
        super(uri.toString(), AnyURIType.ANYURI);
        this.value = uri;
    }

    public URI toJavaObject() throws XQueryException {
        return value;
    }

    @Override
    public int compareTo(Item trg) {
        if(!(trg instanceof AnyURIValue)) {
            throw new XQRTException("err:XPTY0004", "Imcomparable "
                    + trg.getClass().getSimpleName() + " with AnyURIValue");
        }
        URI trgUri = ((AnyURIValue) trg).value;
        return value.compareTo(trgUri);
    }

    public static AnyURIValue valueOf(String uriLiteral) {
        final String escaped = XMLUtils.escapeUri(uriLiteral, true);
        final URI uri = URI.create(escaped);
        return new AnyURIValue(uriLiteral, uri);
    }

    public static AnyURIValue valueOf(URI uri) {
        return new AnyURIValue(uri);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this._lexicalValue = ObjectUtils.readString(in);
        this.value = (URI) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        ObjectUtils.writeString(out, _lexicalValue);
        out.writeObject(value);
    }

    public int getIdentifier() {
        return ID;
    }
}
