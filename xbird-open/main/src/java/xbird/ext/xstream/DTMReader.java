/*
 * @(#)$Id: codetemplate_xbird.xml 3792 2008-04-21 21:39:23Z yui $
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
package xbird.ext.xstream;

import xbird.xquery.dm.instance.DocumentTableModel.DTMAttribute;
import xbird.xquery.dm.instance.DocumentTableModel.DTMDocument;
import xbird.xquery.dm.instance.DocumentTableModel.DTMElement;
import xbird.xquery.dm.instance.DocumentTableModel.DTMNodeBase;

import com.thoughtworks.xstream.io.xml.AbstractDocumentReader;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class DTMReader extends AbstractDocumentReader {

    private DTMElement currentElement;

    public DTMReader(DTMDocument docmentRoot) {
        this((DTMElement) docmentRoot.firstChild(), new XmlFriendlyReplacer());
    }

    public DTMReader(DTMDocument docmentRoot, XmlFriendlyReplacer replacer) {
        this((DTMElement) docmentRoot.firstChild(), replacer);
    }

    public DTMReader(DTMElement rootElement) {
        super(rootElement, new XmlFriendlyReplacer());
    }

    public DTMReader(DTMElement rootElement, XmlFriendlyReplacer replacer) {
        super(rootElement, replacer);
    }

    public String getNodeName() {
        String rawName = currentElement.nodeName().getLocalPart();
        return unescapeXmlName(rawName);
    }

    public String getValue() {
        return currentElement.stringValue();
    }

    public String getAttribute(String attributeName) {
        DTMAttribute attr = currentElement.getAttribute(null, attributeName);
        return attr == null ? null : attr.getContent();
    }

    public String getAttribute(int index) {
        DTMAttribute attr = currentElement.attribute(index);
        return attr == null ? null : attr.getContent();
    }

    public int getAttributeCount() {
        return currentElement.getAttributesCount();
    }

    public String getAttributeName(int index) {
        DTMAttribute attr = currentElement.attribute(index);
        if(attr == null) {
            return null;
        }
        String rawName = attr.nodeName().getLocalPart();
        return unescapeXmlName(rawName);
    }

    @Override
    protected DTMElement getParent() {
        return currentElement.parent();
    }

    @Override
    protected DTMNodeBase getChild(int index) {
        DTMNodeBase child = currentElement.firstChild();
        for(int i = 0; child != null; i++) {
            if(i == index) {
                return child;
            }
            child = child.nextSibling();
        }
        return null;
    }

    @Override
    protected int getChildCount() {
        DTMNodeBase child = currentElement.firstChild();
        int count = 0;
        while(child != null) {
            child = child.nextSibling();
            count++;
        }
        return count;
    }

    @Override
    protected void reassignCurrentElement(Object element) {
        this.currentElement = (DTMElement) element;
    }

}
