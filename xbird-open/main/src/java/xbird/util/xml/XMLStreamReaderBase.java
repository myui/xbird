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
package xbird.util.xml;

import javax.xml.stream.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class XMLStreamReaderBase implements XMLStreamReader {

    private final String encoding;

    public XMLStreamReaderBase() {
        this(null);
    }

    public XMLStreamReaderBase(String encoding) {
        this.encoding = encoding;
    }

    public String getAttributeLocalName(int index) {
        return getAttributeName(index).getLocalPart();
    }

    public String getAttributeNamespace(int index) {
        return getAttributeName(index).getNamespaceURI();
    }

    public String getAttributePrefix(int index) {
        return getAttributeName(index).getPrefix();
    }

    public String getElementText() throws XMLStreamException {
        if(getEventType() != XMLStreamConstants.START_ELEMENT) {
            throw new XMLStreamException("parser must be on START_ELEMENT to read next text, but found "
                    + getEventTypeName() + getEventTypeName(), getLocation());
        }
        StringBuilder content = null;
        for(int eventType = next(); eventType != END_ELEMENT; eventType = next()) {
            if(hasText()) {
                if(content == null) {
                    content = new StringBuilder();
                }
                content.append(getText());
            } else if(eventType == PROCESSING_INSTRUCTION || eventType == COMMENT) {
                continue;
            } else {
                throw new XMLStreamException("Encountered " + getEventTypeName()
                        + " unexpected event within text-only element", getLocation());
            }
        }
        return (content == null) ? "" : content.toString();
    }

    public String getEncoding() {
        return encoding;
    }

    public Location getLocation() {
        return UnknownLocation.instance;
    }

    public String getNamespaceURI() {
        switch(getEventType()) {
            case START_ELEMENT:
            case END_ELEMENT:
                return getName().getNamespaceURI();
            default:
                throw new IllegalStreamStateException("Expected START_ELEMENT or END_ELEMENT state, but found "
                        + getEventTypeName(), getLocation());
        }
    }

    public String getNamespaceURI(String prefix) {
        if(prefix == null) {
            throw new IllegalArgumentException("Namespace prefix was null");
        }
        return getNamespaceContext().getNamespaceURI(prefix);
    }

    public String getPrefix() {
        switch(getEventType()) {
            case XMLStreamConstants.START_ELEMENT:
            case XMLStreamConstants.END_ELEMENT:
                return getName().getPrefix();
            default:
                throw new IllegalStreamStateException("Expected START_ELEMENT or END_ELEMENT, but found "
                        + getEventTypeName(), getLocation());
        }
    }

    public char[] getTextCharacters() {
        final String text = getText();
        return (text == null) ? new char[0] : text.toCharArray();
    }

    public int getTextLength() {
        final String text = getText();
        return (text == null) ? 0 : text.length();
    }

    public String getVersion() {
        return "1.0";
    }

    public boolean hasName() {
        switch(getEventType()) {
            case START_ELEMENT:
            case END_ELEMENT:
                return true;
            default:
                return false;
        }
    }

    public boolean hasText() {
        switch(getEventType()) {
            case SPACE:
            case CHARACTERS:
            case COMMENT:
            case CDATA:
            case ENTITY_REFERENCE:
                return true;
            default:
                return false;
        }
    }

    public boolean isCharacters() {
        return getEventType() == CHARACTERS;
    }

    public boolean isEndElement() {
        return getEventType() == END_ELEMENT;
    }

    public boolean isStartElement() {
        return getEventType() == START_ELEMENT;
    }

    public boolean isWhiteSpace() {
        return getEventType() == SPACE;
    }

    public int nextTag() throws XMLStreamException {
        for(int eventType = next(); hasNext(); eventType = next()) {
            switch(eventType) {
                case START_ELEMENT:
                case END_ELEMENT:
                    return eventType;
                case CHARACTERS:
                case CDATA:
                    if(!isWhiteSpace()) {
                        break;
                    }
                case SPACE:
                case PROCESSING_INSTRUCTION:
                case COMMENT:
                    continue;
                default:
                    break;
            }
        }
        throw new XMLStreamException("Encountered " + getEventTypeName()
                + " when expecting START_ELEMENT or END_ELEMENT", getLocation());
    }

    public void require(int type, String namespaceURI, String localName) throws XMLStreamException {
        final int currType = getEventType();
        if(currType != type) {
            throw new XMLStreamException("Expected " + XMLStreamUtils.resolveTypeName(type)
                    + ", but found " + XMLStreamUtils.resolveTypeName(currType), getLocation());
        } else {
            // If the namespaceURI is null it is not checked for equality
            final String actualURI = getNamespaceURI();
            if(namespaceURI != null && namespaceURI.equals(actualURI)) {
                throw new XMLStreamException("Expected namespaceURI is " + namespaceURI
                        + ", but was " + actualURI);
            }
            // if the localName is null it is not checked for equality
            final String actualLocalName = getLocalName();
            if(localName != null && localName.equals(actualLocalName)) {
                throw new XMLStreamException("Expected localName is " + localName + ", but was "
                        + actualLocalName);
            }
        }
    }

    public boolean isAttributeSpecified(int index) {
        checkConditionForAttribute();
        int count = getAttributeCount();
        return index < count;
    }

    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length)
            throws XMLStreamException {
        if(target == null) {
            throw new NullPointerException("Target must be non-null value");
        }
        if(targetStart < 0 || length < 0 || (targetStart + length) > target.length) {
            throw new IndexOutOfBoundsException("Illegal arguments were specified");
        }
        final char[] source = getTextCharacters();
        assert (source != null);
        int max = source.length - sourceStart;
        if(max < 0) {
            return 0;
        }
        if(max < length) {
            length = max;
        }
        System.arraycopy(source, sourceStart, target, targetStart, length);
        return length;
    }

    public String getAttributeType(int index) {
        if(isAttributeSpecified(index)) {
            return "CDATA"; // TODO
        }
        throw new IllegalStreamStateException("no attribute on the given index: " + index, getLocation());
    }

    public int getTextStart() {
        checkConditionForText();
        return 0;
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        if(name == null) {
            throw new IllegalArgumentException();
        }
        return null;
    }

    public boolean isStandalone() {
        throw new UnsupportedOperationException();
    }

    public boolean standaloneSet() {
        throw new UnsupportedOperationException();
    }

    public String getCharacterEncodingScheme() {
        throw new UnsupportedOperationException();
    }

    protected final String getEventTypeName() {
        return XMLStreamUtils.resolveTypeName(getEventType());
    }

    protected void checkConditionForAttribute() {
        final int type = getEventType();
        if(type != START_ELEMENT && type != ATTRIBUTE) {
            throw new IllegalStreamStateException("Unexpected event: "
                    + XMLStreamUtils.resolveTypeName(type), getLocation());
        }
    }

    protected void checkConditionForElement() {
        final int type = getEventType();
        if(type != START_ELEMENT && type != END_ELEMENT && type != ENTITY_REFERENCE) {
            throw new IllegalStreamStateException("Unexpected event: "
                    + XMLStreamUtils.resolveTypeName(type), getLocation());
        }
    }

    protected void checkConditionForNamespace() {
        final int type = getEventType();
        if(type != START_ELEMENT && type != END_ELEMENT && type != NAMESPACE) {
            throw new IllegalStreamStateException("Unexpected event: "
                    + XMLStreamUtils.resolveTypeName(type), getLocation());
        }
    }

    protected void checkConditionForText() {
        final int type = getEventType();
        if(type != CHARACTERS && type != COMMENT && type != CDATA && type != SPACE) {
            throw new IllegalStreamStateException("Unexpected event: "
                    + XMLStreamUtils.resolveTypeName(type), getLocation());
        }
    }

    public static final class IllegalStreamStateException extends IllegalStateException {
        private static final long serialVersionUID = 3936288422696709550L;

        private final Location location;

        public IllegalStreamStateException(String s, Location location) {
            super(s);
            this.location = location;
        }

        public Location getLocation() {
            return location;
        }
    }

    protected static final class UnknownLocation implements Location {
        static final Location instance = new UnknownLocation();

        private UnknownLocation() {}

        public int getCharacterOffset() {
            return -1;
        }

        public int getColumnNumber() {
            return -1;
        }

        public int getLineNumber() {
            return -1;
        }

        public String getPublicId() {
            return null;
        }

        public String getSystemId() {
            return null;
        }

    }
}
