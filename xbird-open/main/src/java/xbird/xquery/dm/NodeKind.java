/*
 * @(#)$Id: NodeKind.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.dm;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xpath-datamodel/
 */
public final class NodeKind {
   
    public static final byte ANY = 0;
    
    public static final byte DOCUMENT = 1;
    public static final byte ELEMENT = 2;
    public static final byte ATTRIBUTE = 3;
    public static final byte NAMESPACE = 4;
    public static final byte PROCESSING_INSTRUCTION = 5;
    public static final byte COMMENT = 6;
    public static final byte TEXT = 7;
    public static final byte CDATA = 8;

    public static final String resolveName(byte type) {
        switch (type) {
            case ANY:                
                return "";
            case DOCUMENT:
                return "document";
            case ELEMENT:
                return "element";
            case ATTRIBUTE:
                return "attribute";
            case NAMESPACE:
                return "namespace";
            case PROCESSING_INSTRUCTION:
                return "pi";
            case COMMENT:
                return "comment";
            case TEXT:
                return "text";
            case CDATA:
                return "cdata";
            default:
                throw new IllegalArgumentException("Illegal argument.. " + type);
        }
    }
    
}