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
package xbird.util.hashes;

import junit.framework.TestCase;


/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class FNVHashTest extends TestCase {
    
    public void testXorFolding() {
        System.out.println(FNVHash.xorFolding(100, 7));
        System.out.println(FNVHash.xorFolding(127, 7));
        System.out.println(FNVHash.xorFolding(128, 7));
        System.out.println(FNVHash.xorFolding(129, 7));
        System.out.println(FNVHash.xorFolding(256, 7));
        System.out.println(FNVHash.xorFolding(257, 7));
        System.out.println(FNVHash.xorFolding(100, 8));
        System.out.println(FNVHash.xorFolding(256, 8));
        System.out.println(FNVHash.xorFolding(257, 8));
        System.out.println(FNVHash.xorFolding(-100, 8));
        System.out.println(FNVHash.xorFolding(-256, 8));
        System.out.println(FNVHash.xorFolding(-257, 8));
        System.out.println(FNVHash.xorFolding(1, 31));
        System.out.println(FNVHash.xorFolding(-1, 31));
        System.out.println(FNVHash.xorFolding(Integer.MAX_VALUE, 31));       
        System.out.println(FNVHash.xorFolding(Integer.MIN_VALUE, 31));
    }

}
