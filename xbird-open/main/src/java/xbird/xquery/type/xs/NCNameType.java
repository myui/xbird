/*
 * @(#)$Id: NCNameType.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.type.xs;

import xbird.util.xml.XMLUtils;
import xbird.xqj.XQJConstants;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class NCNameType extends NameType {
    private static final long serialVersionUID = -7280608993971169321L;
    public static final String SYMBOL = "xs:NCName";

    public static final NCNameType NCNAME = new NCNameType();

    public NCNameType() {
        super(SYMBOL);
    }

    protected NCNameType(String type) {
        super(type);
    }

    public boolean isValid(String literal) {
        return XMLUtils.isNCName(literal);
    }

    @Override
    public int getXQJBaseType() {
        return XQJConstants.XQBASETYPE_NCNAME;
    }

}
