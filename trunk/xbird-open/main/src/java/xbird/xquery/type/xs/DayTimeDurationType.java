/*
 * @(#)$Id: DayTimeDurationType.java 3619 2008-03-26 07:23:03Z yui $
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

import xbird.xqj.XQJConstants;
import xbird.xquery.type.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class DayTimeDurationType extends DurationType {
    private static final long serialVersionUID = -5599431649051662107L;
    public static final String SYMBOL = "xs:dayTimeDuration";

    public static final DayTimeDurationType DAYTIME_DURATION = new DayTimeDurationType();

    public DayTimeDurationType() {
        super(TypeTable.DAYTIME_DURATION_TID, SYMBOL);
    }

    @Override
    protected boolean isSuperTypeOf(final AtomicType expected) {
        return expected instanceof DayTimeDurationType;
    }

    @Override
    public int getXQJBaseType() {
        return XQJConstants.XQBASETYPE_DAYTIMEDURATION;
    }

}
