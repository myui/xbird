/*
 * @(#)$Id: NotationValue.java 3619 2008-03-26 07:23:03Z yui $
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

import xbird.util.lang.ObjectUtils;
import xbird.xquery.dm.value.literal.XString;
import xbird.xquery.type.xs.NOTATIONType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class NotationValue extends XString {
    private static final long serialVersionUID = -5384640855108809050L;
    public static final int ID = 15;
    
    public NotationValue() {
        super();
    }
    
    public NotationValue(String literal) {
        super(literal, NOTATIONType.NOTATION);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this._lexicalValue = ObjectUtils.readString(in);
        this._type = NOTATIONType.NOTATION;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        ObjectUtils.writeString(out, _lexicalValue);
    }
    
    @Override
    public int getIdentifier() {
        return ID;
    }

}
