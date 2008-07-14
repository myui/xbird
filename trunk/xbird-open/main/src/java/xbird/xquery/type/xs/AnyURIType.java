/*
 * @(#)$Id: AnyURIType.java 3619 2008-03-26 07:23:03Z yui $
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
/*
 * Part of this code is inspired by Kohsuke KAWAGUCHI's xsdlib.
 * 
 * Copyright (c) 2002-2006 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and  use in  source and binary  forms, with  or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * - Redistributions  of  source code  must  retain  the above  copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * - Redistribution  in binary  form must  reproduct the  above copyright
 * notice, this list of conditions  and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * Neither  the  name   of  Sun  Microsystems,  Inc.  or   the  names  of
 * contributors may be  used to endorse or promote  products derived from
 * this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS  OR   IMPLIED  CONDITIONS,  REPRESENTATIONS   AND  WARRANTIES,
 * INCLUDING  ANY  IMPLIED WARRANTY  OF  MERCHANTABILITY,  FITNESS FOR  A
 * PARTICULAR PURPOSE  OR NON-INFRINGEMENT, ARE HEREBY  EXCLUDED. SUN AND
 * ITS  LICENSORS SHALL  NOT BE  LIABLE  FOR ANY  DAMAGES OR  LIABILITIES
 * SUFFERED BY LICENSEE  AS A RESULT OF OR  RELATING TO USE, MODIFICATION
 * OR DISTRIBUTION OF  THE SOFTWARE OR ITS DERIVATIVES.  IN NO EVENT WILL
 * SUN OR ITS  LICENSORS BE LIABLE FOR ANY LOST  REVENUE, PROFIT OR DATA,
 * OR  FOR  DIRECT,   INDIRECT,  SPECIAL,  CONSEQUENTIAL,  INCIDENTAL  OR
 * PUNITIVE  DAMAGES, HOWEVER  CAUSED  AND REGARDLESS  OF  THE THEORY  OF
 * LIABILITY, ARISING  OUT OF  THE USE OF  OR INABILITY TO  USE SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that Software is not designed,licensed or intended for
 * use  in the  design,  construction, operation  or  maintenance of  any
 * nuclear facility.
 */
package xbird.xquery.type.xs;

import java.net.URI;

import xbird.xqj.XQJConstants;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.xsi.AnyURIValue;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#casting-to-anyuri
 * @link http://www.w3.org/TR/xmlschema-2/#built-in-datatypes
 */
public final class AnyURIType extends AtomicType {
    private static final long serialVersionUID = 4291451031828335983L;

    public static final String SYMBOL = "xs:anyURI";

    public static final AnyURIType ANYURI = new AnyURIType();

    public AnyURIType() {
        super(TypeTable.ANY_URI_TID, SYMBOL);
    }

    public Class getJavaObjectType() {
        return URI.class;
    }

    public AnyURIValue createInstance(String literal, AtomicType srcType, DynamicContext dynEnv)
            throws XQueryException {
        return AnyURIValue.valueOf(literal);
    }

    @Override
    protected boolean isSuperTypeOf(final AtomicType expected) {
        return expected instanceof AnyURIType;
    }

    @Override
    public int getXQJBaseType() {
        return XQJConstants.XQBASETYPE_ANYURI;
    }

}
