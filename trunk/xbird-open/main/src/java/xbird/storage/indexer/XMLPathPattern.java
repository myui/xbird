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
package xbird.storage.indexer;

import java.util.Stack;

import xbird.util.collections.IntStack;
import xbird.util.string.StringUtils;
import xbird.util.xml.NamespaceBinder;
import xbird.xquery.misc.QNameUtil;
import xbird.xquery.misc.QNameTable.QualifiedName;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class XMLPathPattern {

    public static final int ELEMENT = 0x10, ATTRIBUTE = 0x20, TEXT = 0x40;
    private static final int MATCH = 0x01, WILDCARD = 0x02, SKIPPABLE = 0x04 | ELEMENT;
    private static final int ANYNODE = 0xf0 | WILDCARD, ELEM_WILDCARD = WILDCARD | ELEMENT,
            ATTR_WILDCARD = WILDCARD | ATTRIBUTE, TEXT_MATCH = MATCH | TEXT;

    private final IntStack types;
    private final Stack<QualifiedName> patterns;

    public XMLPathPattern() {
        this.types = new IntStack(8);
        this.patterns = new Stack<QualifiedName>();
    }

    public XMLPathPattern(final String pattern, final NamespaceBinder namespaces) {
        this();
        init(pattern, namespaces);
    }

    public void init(final String pattern, final NamespaceBinder namespaces) {
        final int ptnlen = pattern.length();
        final int last = ptnlen - 1;
        final StringBuilder tokens = new StringBuilder(ptnlen);
        for(int i = 0; i < ptnlen; i++) {
            final char c = pattern.charAt(i);
            if(c == '/') {
                if(i == last) {
                    throw new IllegalArgumentException("Illegal expression: " + pattern);
                }
                if(i != 0 && tokens.length() > 0) {
                    final String prev = tokens.toString();
                    tokens.setLength(0);
                    addComponent(prev, namespaces);
                }
                if(pattern.charAt(i + 1) == '/') { // handle '//'                    
                    addComponent(null, SKIPPABLE);
                    ++i;
                }
            } else {
                tokens.append(c);
            }
        }
        if(tokens.length() > 0) {
            addComponent(tokens.toString(), namespaces);
        }
    }

    public void addComponent(final QualifiedName qname, final int type) {
        types.push(type);
        patterns.push(qname);
    }

    private void addComponent(final String tokens, final NamespaceBinder namespaces) {
        final int tokenlen = tokens.length();
        if(tokenlen == 0) {
            throw new IllegalArgumentException();
        }
        if(tokens.charAt(0) == '@') {
            if(tokenlen < 2) {
                throw new IllegalArgumentException("Illegal as attribute pattern: " + tokens);
            }
            final String attname = tokens.substring(1);
            if(attname.equals("*")) {
                addComponent(null, ATTRIBUTE | WILDCARD);
            } else {
                QualifiedName qname = QNameUtil.parse(tokens, namespaces);
                addComponent(qname, ATTRIBUTE | MATCH);
            }
        } else {
            if(tokens.equals("*")) {
                addComponent(null, ELEM_WILDCARD | WILDCARD);
            } else if(tokens.equals("text()")) {
                addComponent(null, MATCH | TEXT);
            } else if(tokens.equals("node(")) {
                addComponent(null, ANYNODE);
            } else {
                QualifiedName qname = QNameUtil.parse(tokens, namespaces);
                addComponent(qname, MATCH | ELEMENT);
            }
        }
    }

    public void removeLastComponent() {
        types.pop();
        patterns.pop();
    }

    public boolean match(XMLPathPattern other) {
        return match(this, other, 0, 0);
    }

    private static boolean match(final XMLPathPattern verifyPattern, final XMLPathPattern targetPattern, int vi, int oi) {
        final IntStack verifyTypes = verifyPattern.types;
        final Stack<QualifiedName> verifyPatterns = verifyPattern.patterns;
        final IntStack otherTypes = targetPattern.types;
        final Stack<QualifiedName> otherPatterns = targetPattern.patterns;
        final int otherPtnlen = otherPatterns.size();
        final int verifyPtnlen = verifyPatterns.size();

        for(; vi < verifyPtnlen; vi++, oi++) {
            if(oi >= otherPtnlen) {
                return false;
            }
            final int verifyType = verifyTypes.elementAt(vi);
            if((verifyType & MATCH) == MATCH) {
                final int otherType = otherTypes.elementAt(oi);
                if((otherType & MATCH) != MATCH) {
                    throw new IllegalArgumentException("Comparing other pattern must be an AbstractPath"
                            + otherTypes.toString());
                }
                if(verifyType != otherType) {
                    return false;
                }
                if(verifyType == TEXT_MATCH) {
                    continue;
                }
                final QualifiedName otherName = otherPatterns.get(oi);
                final QualifiedName verifyName = verifyPatterns.get(vi);
                if(!verifyName.equals(otherName)) {
                    return false;
                }
            } else if(verifyType == ELEM_WILDCARD) {
                final int otherType = otherTypes.elementAt(oi);
                if((otherType & ELEMENT) != ELEMENT) {
                    return false;
                }
            } else if(verifyType == ATTR_WILDCARD) {
                final int otherType = otherTypes.elementAt(oi);
                if((otherType & ATTRIBUTE) != ATTRIBUTE) {
                    return false;
                }
            } else if(verifyType == SKIPPABLE) {
                if(++vi >= verifyPtnlen) {
                    return false;
                }
                for(; oi < otherPtnlen; oi++) {
                    if(match(verifyPattern, targetPattern, vi, oi)) {
                        return true;
                    }
                    // back track to ANY
                }
                return false;
            } else {
                throw new IllegalStateException("Illegal type: "
                        + StringUtils.toBitString(verifyType));
            }
        }
        return oi == otherPtnlen;
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder(128);
        final int ptnlen = patterns.size();
        for(int i = 0; i < ptnlen; i++) {
            buf.append('/');
            final QualifiedName qname = patterns.get(i);
            final int type = types.elementAt(i);
            if(qname == null) {
                if(type == SKIPPABLE) {
                    continue;
                } else if(type == TEXT) {
                    buf.append("text()");
                } else if(type == ELEM_WILDCARD) {
                    buf.append("*");
                } else if(type == ATTR_WILDCARD) {
                    buf.append("@*");
                } else {
                    throw new IllegalStateException("Illegal type was detected for a NULL qname: "
                            + StringUtils.toBitString(type));
                }
            } else {
                assert ((type & MATCH) == MATCH) : StringUtils.toBitString(type);
                buf.append(QNameUtil.toLexicalForm(qname));
            }
        }
        return buf.toString();
    }

}
