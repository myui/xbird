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
package xbird.util.collections;

import junit.framework.TestCase;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class IndexedSetTest extends TestCase {

    public void testAdd() {
        IndexedSet<String> index = new IndexedSet<String>(200);
        for(int i = 0; i <= 100; i++) {
            int at = index.addIndexOf(String.valueOf(i));
            assertEquals(i, at);
        }
        for(int i = 100; i >= 0; i--) {
            int at = index.addIndexOf(String.valueOf(i));
            assertEquals(i, at);
        }
        assertEquals(101, index.size());
        for(int i = 0; i <= 100; i++) {
            String v = index.get(i);
            assertEquals(String.valueOf(i), v);
        }
    }

}
