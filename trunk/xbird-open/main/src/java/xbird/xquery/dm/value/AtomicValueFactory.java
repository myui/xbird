/*
 * @(#)$Id$
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
package xbird.xquery.dm.value;

import xbird.xquery.dm.value.literal.*;
import xbird.xquery.dm.value.xsi.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class AtomicValueFactory {

    private AtomicValueFactory() {}

    public static AtomicValue createAtomicValue(final int id) {
        switch(id) {
            case UntypedAtomicValue.ID:
                return new UntypedAtomicValue();
            case XString.ID:
                return new XString();
            case XDouble.ID:
                return new XDouble();
            case XInteger.ID:
                return new XInteger();
            case XFloat.ID:
                return new XFloat();
            case BooleanValue.ID:
                return new BooleanValue();
            case DateTimeValue.ID:
                return new DateTimeValue();
            case QNameValue.ID:
                return new QNameValue();
            case DurationValue.ID:
                return new DurationValue();
            case XDecimal.ID:
                return new XDecimal();
            case GregorianDateTimeValue.ID:
                return new GregorianDateTimeValue();
            case AnyURIValue.ID:
                return new AnyURIValue();
            case Base64BinaryValue.ID:
                return new Base64BinaryValue();
            case HexBinaryValue.ID:
                return new HexBinaryValue();
            case NotationValue.ID:
                return new NotationValue();
            default:
                throw new IllegalStateException("Illegal AtomicValue id: " + id);
        }
    }

}
