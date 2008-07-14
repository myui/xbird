/*
 * @(#)$Id: AttrNodeSequence.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.dm.value.sequence;

import java.util.HashSet;
import java.util.Set;

import xbird.xquery.DynamicError;
import xbird.xquery.XQRTException;
import xbird.xquery.dm.value.node.DMAttribute;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.misc.QNameTable.QualifiedName;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class AttrNodeSequence<T extends DMAttribute> extends NodeSequence<T> {
    private static final long serialVersionUID = 1L;
    
    private final Set<QualifiedName> attrSet = new HashSet<QualifiedName>(6);

    public AttrNodeSequence(DynamicContext dynEnv) {
        super(dynEnv);
    }

    /**
     * @throws DynamicError 
     * @link http://www.w3.org/TR/xquery/#ERRXQDY0025
     */
    @Override
    public void addItem(T attr) {
        QualifiedName qname = attr.nodeName();
        boolean uniq = attrSet.add(qname);
        if(!uniq) {
            throw new XQRTException("err:XQDY0025", "Duplicate attribute: " + qname);
        }
        super.addItem(attr);
    }

}
