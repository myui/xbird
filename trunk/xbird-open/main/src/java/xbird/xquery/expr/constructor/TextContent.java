/*
 * @(#)$Id: TextContent.java 3619 2008-03-26 07:23:03Z yui $
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

import xbird.xquery.type.node.NodeType;
import xbird.xquery.expr.LiteralExpr;
import xbird.xquery.dm.value.literal.XString;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class TextContent extends LiteralExpr implements NodeConstructor {
    private static final long serialVersionUID = 1L;
    
    private final boolean isCData;

    public TextContent(String value) {
        this(value, false);
    }

    public TextContent(String value, boolean isCData) {
        super(value);
        this.isCData = isCData;
        this._type = NodeType.TEXT;
    }

    public boolean isCData() {
        return isCData;
    }

    public void appendText(String data) {
        String s = ((XString) value).getValue();
        this.value = new XString(s.concat(data));
    }

    public static final class TrimedText extends TextContent {
        private static final long serialVersionUID = -1741622404527796144L;
        
        public static final TextContent INSTANCE = new TrimedText();        
        
        private TrimedText() {
            super("");
        }        
    }
}
