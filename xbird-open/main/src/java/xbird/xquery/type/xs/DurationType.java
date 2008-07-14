/*
 * @(#)$Id: DurationType.java 3619 2008-03-26 07:23:03Z yui $
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

import javax.xml.datatype.Duration;

import xbird.xqj.XQJConstants;
import xbird.xquery.XQueryException;
import xbird.xquery.dm.value.xsi.DurationValue;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.type.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery-operators/#casting-to-durations
 */
public class DurationType extends AtomicType {
    private static final long serialVersionUID = 8679650559522595959L;

    public static final String SYMBOL = "xs:duration";
    public static final DurationType DURATION = new DurationType();

    public DurationType() {
        super(TypeTable.DURATION_TID, SYMBOL);
    }

    protected DurationType(final int typeId, final String type) {
        super(typeId, type);
    }

    public Class getJavaObjectType() {
        return Duration.class;
    }

    public DurationValue createInstance(String literal, AtomicType srcType, DynamicContext dynEnv)
            throws XQueryException {
        return DurationValue.valueOf(literal, this);
    }

    @Override
    protected boolean isSuperTypeOf(final AtomicType expected) {
        return expected instanceof DurationType;
    }

    @Override
    public int getXQJBaseType() {
        return XQJConstants.XQBASETYPE_DURATION;
    }
}
