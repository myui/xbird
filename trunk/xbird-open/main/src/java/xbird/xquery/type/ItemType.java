/*
 * @(#)$Id: ItemType.java 3619 2008-03-26 07:23:03Z yui $
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

import java.util.List;

import xbird.xqj.XQJConstants;
import xbird.xquery.type.node.NodeType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class ItemType extends Type {

    public static final ItemType ANY_ITEM = new ItemType() {
        private static final long serialVersionUID = -5645490398229265243L;

        public Class getJavaObjectType() {
            return Object.class;
        }

        @Override
        public int getXQJBaseType() {
            return XQJConstants.XQBASETYPE_ANYTYPE; // REVIEWME
        }
    };

    public ItemType() {}

    public boolean accepts(Type expected) {
        if(expected == null) {
            return false;
        }
        if(this == expected) {
            return true;
        }
        final Type prime = expected.prime();
        if(prime instanceof ChoiceType) {
            final List<Type> choice = ((ChoiceType) prime).getTypes();
            for(Type t : choice) {
                if(accepts(t)) {
                    return true;
                }
            }
            return false;
        } else if(this instanceof NodeType && prime instanceof NodeType) {
            return ((NodeType) this).acceptNodeType((NodeType) prime);
        } else {
            return this.getClass().isAssignableFrom(expected.getClass());
        }
    }

    public Occurrence quantifier() {
        return Occurrence.OCC_EXACTLY_ONE;
    }

    public Type prime() {
        return this;
    }

    public String toString() {
        return "item()";
    }

}
