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
package xbird.xquery.dm;

import java.io.Serializable;
import java.util.Map;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public interface IDocument extends Serializable {

    public void setDeclaredNamespaces(Map<String, String> nsmap);

    public long openNode(byte ev);

    public void closeNode();

    public long putAttribute(byte ev, long parent, int index, int attsSize);

    public void setAttributeName(long attid, int i);

    public int setAttributeName(long nsidx, String nsuri, String localName);

    public int setAttributeName(long attidx, String nsuri, String localName, String prefix);

    public void setName(long at, int code);

    public int setName(long idx, String nsuri, String localName);

    public int setName(long elemidx, String nsuri, String localName, String prefix);

    public long setTextAt(long index, char[] ch, int start, int length);

    public long setTextAt(long nsidx, String str);

}