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
package xbird.xqj;

import javax.xml.xquery.XQItemType;
import javax.xml.xquery.XQSequenceType;

import xbird.xquery.type.ItemType;
import xbird.xquery.type.SequenceType;
import xbird.xquery.type.Type.Occurrence;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class BXQSequenceType implements XQSequenceType {

    private final SequenceType type_;

    public BXQSequenceType(SequenceType type) {
        this.type_ = type;
    }

    public int getItemOccurrence() {
        final Occurrence occ = type_.quantifier();
        switch(occ) {
            case OCC_EXACTLY_ONE:
                return OCC_EXACTLY_ONE;
            case OCC_MORE:
            case OCC_ONE_OR_MORE:
                return OCC_ONE_OR_MORE;
            case OCC_ZERO:
                return OCC_EMPTY;
            case OCC_ZERO_OR_MORE:
                return OCC_ZERO_OR_MORE;
            case OCC_ZERO_OR_ONE:
                return OCC_ZERO_OR_ONE;
            default:
                throw new IllegalStateException("Unexpected occurrence: " + occ);
        }
    }

    public XQItemType getItemType() {
        final ItemType primeType = type_.prime();
        return new BXQItemType(primeType);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof BXQSequenceType)) {
            return false;
        }
        BXQSequenceType trgType = (BXQSequenceType) obj;
        final int trgOcc = trgType.getItemOccurrence();
        final int myOcc = getItemOccurrence();
        if(myOcc != trgOcc) {
            return false;
        }
        if(myOcc == OCC_EMPTY) {
            return true;
        }
        XQItemType trgItemType = trgType.getItemType();
        XQItemType myItemType = getItemType();
        return myItemType.equals(trgItemType);
    }

    @Override
    public int hashCode() {
        final int hashCode;
        if(getItemOccurrence() == OCC_EMPTY) {
            hashCode = 1;
        } else {
            hashCode = getItemOccurrence() * 31 + getItemType().hashCode();
        }
        return hashCode;
    }

    @Override
    public String toString() {
        return type_.toString();
    }

}
