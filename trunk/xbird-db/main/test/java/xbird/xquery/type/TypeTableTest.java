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
package xbird.xquery.type;

import junit.framework.TestCase;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class TypeTableTest extends TestCase {

    @org.junit.Test
    public void testCastingTable() {
        final byte[][] castingTable = TypeTable.castingTable;
        assert (castingTable.length == 23);
        for(int i = 0; i < 23; i++) {
            if(castingTable[i].length != 23) {
                throw new IllegalStateException("Illegal size of castingTable[" + i + "].length="
                        + castingTable[i].length);
            }
        }
    }
}
