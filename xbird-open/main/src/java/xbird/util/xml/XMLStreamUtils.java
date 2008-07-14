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

import static javax.xml.stream.XMLStreamConstants.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class XMLStreamUtils {

    private static final String[] EVENT_NAMES = new String[16];
    private static final int UNKNOWN_EVENT = 0;
    static {
        EVENT_NAMES[UNKNOWN_EVENT] = "UNKNOWN";
        EVENT_NAMES[START_ELEMENT] = "START_ELEMENT";
        EVENT_NAMES[END_ELEMENT] = "END_ELEMENT";
        EVENT_NAMES[PROCESSING_INSTRUCTION] = "PROCESSING_INSTRUCTION";
        EVENT_NAMES[CHARACTERS] = "CHARACTERS";
        EVENT_NAMES[COMMENT] = "COMMENT";
        EVENT_NAMES[SPACE] = "SPACE";
        EVENT_NAMES[START_DOCUMENT] = "START_DOCUMENT";
        EVENT_NAMES[END_DOCUMENT] = "END_DOCUMENT";
        EVENT_NAMES[ENTITY_REFERENCE] = "ENTITY_REFERENCE";
        EVENT_NAMES[ATTRIBUTE] = "ATTRIBUTE";
        EVENT_NAMES[DTD] = "DTD";
        EVENT_NAMES[CDATA] = "CDATA";
        EVENT_NAMES[NAMESPACE] = "NAMESPACE";
        EVENT_NAMES[NOTATION_DECLARATION] = "NOTATION_DECLARATION";
        EVENT_NAMES[ENTITY_DECLARATION] = "ENTITY_DECLARATION";
    }

    private XMLStreamUtils() {}

    public static String resolveTypeName(final int type) {
        if(type > 0 || type < EVENT_NAMES.length) {
            return EVENT_NAMES[type];
        } else {
            return EVENT_NAMES[UNKNOWN_EVENT];
        }
    }

}
