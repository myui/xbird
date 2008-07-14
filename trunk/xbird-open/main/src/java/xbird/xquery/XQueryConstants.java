/*
 * @(#)$Id: XQueryConstants.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public interface XQueryConstants {

    public static final String XQUERY_VERSION = "1.0";   
    
    public static final String XML = "xml";
    public static final String XML_URI = "http://www.w3.org/XML/1998/namespace";

    public static final String XMLNS = "xmlns";
    public static final String XMLNS_URI = "http://www.w3.org/2000/xmlns";
    
    public static final String XS = "xs";
    public static final String XS_URI = "http://www.w3.org/2001/XMLSchema";

    public static final String XSI = "xsi";
    public static final String XSI_URI = "http://www.w3.org/2001/XMLSchema-instance";

    public static final String FN = "fn";
    public static final String FN_URI = "http://www.w3.org/2005/04/xpath-functions";

    public static final String XDT = "xdt";
    public static final String XDT_URI = "http://www.w3.org/2005/04/xpath-datatypes";

    public static final String LOCAL = "local";
    public static final String LOCAL_URI = "http://www.w3.org/2005/04/xquery-local-functions";
    
    public static final String OP = "op";
    public static final String OP_URI = "http://www.w3.org/TR/xpath-functions";
    
    public static final String FS = "fs";
    public static final String FS_URI = "http://www.w3.org/TR/xquery-semantics";
    
    public static final String XQT_ERR = "err";
    public static final String XQT_ERR_URI = "http://www.w3.org/2005/xqt-errors";
    
    public static final String UNICODE_CODEPOINT_COLLATION = "http://www.w3.org/2005/04/xpath-functions/collation/codepoint";

}