/*
 * @(#)$Id: ComplexType.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.type.schema;

import xbird.xquery.type.Type;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class ComplexType extends Type implements SchemaType {
    private static final long serialVersionUID = -5786783608331802325L;

    public enum ContentType {
        ANY((byte) 127), MixedContent((byte) 1), SimpleContent((byte) 2), EmptyContent((byte) 4);
        private final byte bit;

        ContentType(byte b) {
            bit = b;
        }

        public boolean isType(ContentType type) {
            final byte mask = type.getValue();
            return (bit & mask) == mask;
        }

        public byte getValue() {
            return bit;
        }
    }

    protected final ContentType _contentType;

    public ComplexType() {
        this(ContentType.ANY);
    }

    public ComplexType(ContentType type) {
        super();
        this._contentType = type;
    }

    public boolean accepts(Type expected) {
        return false;
    }

    public Class getJavaObjectType() {
        return null;
    }

    public Occurrence quantifier() {
        return Occurrence.OCC_ZERO_OR_MORE;
    }

    public ContentType getContentType() {
        return _contentType;
    }

    public Type prime() {
        return this; // FIXME
    }

    public String toString() {
        throw new UnsupportedOperationException(ComplexType.class.getSimpleName()
                + "#toString() is not implementated."); // FIXME
    }

}
