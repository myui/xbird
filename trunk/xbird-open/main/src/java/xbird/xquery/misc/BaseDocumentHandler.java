/*
 * @(#)$Id: BaseDocumentHandler.java 3619 2008-03-26 07:23:03Z yui $
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

import org.xml.sax.*;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.NamespaceSupport;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public abstract class BaseDocumentHandler implements ContentHandler, LexicalHandler {

    protected Locator _locator = null;

    protected final NamespaceSupport _namespace = new NamespaceSupport();

    public void setDocumentLocator(Locator locator) {
        this._locator = locator;
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        // regist namespace resolver
        _namespace.pushContext();
        _namespace.declarePrefix(prefix, uri);
    }

    public void endPrefixMapping(String prefix) throws SAXException {
        _namespace.popContext();
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {}

    public void skippedEntity(String name) throws SAXException {}

    public void startDTD(String name, String publicId, String systemId) throws SAXException {}

    public void endDTD() throws SAXException {}

    public void startEntity(String name) throws SAXException {}

    public void endEntity(String name) throws SAXException {}

}
