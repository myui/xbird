/*
 * @(#)$Id: TypeFactory.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.misc;

import xbird.xquery.StaticError;
import xbird.xquery.meta.StaticContext;
import xbird.xquery.misc.QNameTable.QualifiedName;
import xbird.xquery.type.*;
import xbird.xquery.type.Type.Occurrence;
import xbird.xquery.type.node.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class TypeFactory {

    public static DocumentTest createDocumentTest(ItemType nodeType) {
        return new DocumentTest(nodeType);
    }

    public static ElementTest createElementTest(QualifiedName elementName, QualifiedName typeName, boolean isNillable) {
        return new ElementTest(elementName, typeName, isNillable);
    }

    public static PITest createPITest(String name) {
        return new PITest(name);
    }

    public static AttributeTest createAttributeTest(QualifiedName attributeName, QualifiedName typeName) {
        return new AttributeTest(attributeName, typeName);
    }

    public static SchemaElementTest createSchemaElementTest(QualifiedName elementName) {
        return new SchemaElementTest(elementName);
    }

    public static SchemaAttributeTest createSchemaAttributeTest(QualifiedName attributeName) {
        return new SchemaAttributeTest(attributeName);
    }

    public static AtomicType createAtomicType(QualifiedName itemName, StaticContext ctxt)
            throws StaticError {
        final Type t = ctxt.getSchemaType(itemName);
        if(!(t instanceof AtomicType)) {
            throw new StaticError("err:XPST0051", "Type not found: " + itemName.toString());
        }
        return (AtomicType) t;
    }

    public static Type createSequenceType(ItemType itemType, Occurrence occurrence) {
        if(occurrence == Occurrence.OCC_EXACTLY_ONE) {
            return itemType;
        } else {
            return new SequenceType(itemType, occurrence);
        }
    }

}