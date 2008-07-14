/*
 * @(#)$Id: SequenceType.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.type;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xquery/#id-matching-value
 */
public class SequenceType extends Type {
    private static final long serialVersionUID = -3912918517248609209L;
    
    /** SequenceType of "empty" sequence. */
    public static final SequenceType EMPTY = new Empty();
    /** item()*, the default result/parameter type. */
    public static final SequenceType ANY_ITEMS = new SequenceType(ItemType.ANY_ITEM, Occurrence.OCC_ZERO_OR_MORE);

    /** may be null with  empty type */
    private final ItemType itemType;
    private final Occurrence occurrence;

    private SequenceType(Occurrence occurrence) {
        this.itemType = null;
        this.occurrence = occurrence;
    }

    public SequenceType(ItemType itemType) {
        this(itemType, Occurrence.OCC_EXACTLY_ONE);
    }

    public SequenceType(ItemType itemType, Occurrence occurrence) {
        assert (itemType != null);
        this.itemType = itemType;
        this.occurrence = occurrence;
    }

    public ItemType prime() {
        return itemType;
    }

    public Occurrence quantifier() {
        return occurrence;
    }

    public boolean accepts(Type expected) {
        if(this == expected) {
            return true;
        }
        if(expected == null || expected == EMPTY) {
            return occurrence.accepts(Occurrence.OCC_ZERO.getAlignment());
        }
        if(itemType == null) {
            return (expected instanceof Empty);
        }
        final Occurrence expectedOcc = expected.quantifier();
        if(occurrence.accepts(expectedOcc.getAlignment())) {
            if(itemType.accepts(expected.prime())) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        if(itemType == null) {
            return "empty()";
        }
        final StringBuilder b = new StringBuilder(128);
        b.append(itemType.toString());
        switch(occurrence) {
            case OCC_ZERO:
                return "empty()";
            case OCC_EXACTLY_ONE:
                break;
            case OCC_ZERO_OR_ONE:
                b.append("?");
                break;
            case OCC_ZERO_OR_MORE:
                b.append("*");
                break;
            case OCC_ONE_OR_MORE:
                b.append("+");
                break;
            default:
                throw new IllegalStateException();
        }
        return b.toString();
    }

    public Class getJavaObjectType() {
        return occurrence == Occurrence.OCC_EXACTLY_ONE ? Object.class : Object[].class;
    }

    private static class Empty extends SequenceType {
        private static final long serialVersionUID = -8273327402370118846L;

        public Empty() {
            super(Occurrence.OCC_ZERO);
        }

        @Override
        public String toString() {
            return "void()";
        }

        @Override
        public Class getJavaObjectType() {
            return void.class; // TODO how to express empty?
        }

    }

}