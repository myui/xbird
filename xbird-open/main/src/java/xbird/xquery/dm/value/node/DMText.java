/*
 * @(#)$Id: DMText.java 3619 2008-03-26 07:23:03Z yui $
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

import xbird.xquery.dm.NodeKind;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.type.xs.UntypedAtomicType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class DMText extends DMNode {
    private static final long serialVersionUID = 6039677854456642046L;

    public static final DMText EMPTY_SEQ = new DMText("") {
        public boolean next() {
            return false;
        }
    };

    public static final DMText SPACE = new DMText(" ");

    public DMText() {//for serialization
        super();
    }
    
    public DMText(String content) {
        super();
        setContent(content);
    }

    protected DMText(long _id, String content) {
        super(_id);
        setContent(content);
    }

    //--------------------------------------------
    // DM interfaces

    public byte nodeKind() {
        return NodeKind.TEXT;
    }

    @Override
    public QualifiedName typeName() {
        return UntypedAtomicType.UNTYPED_ATOMIC.getTypeName();
    }

    @Override
    public DMText clone() {
        return new DMText(_id, getContent());
    }

    // --------------------------------------------
    // extension

    public void mergeContent(String content) {
        String prev = getContent();
        if(prev == null || prev.length() == 0) {
            setContent(content);
        } else {
            setContent(prev + content);
        }
    }

}
