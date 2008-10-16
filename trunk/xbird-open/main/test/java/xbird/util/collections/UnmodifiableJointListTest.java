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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class UnmodifiableJointListTest {

    @Test
    public void testGetInt() {
        final List<Integer>[] lists = new ArrayList[3];
        final Random rand = new Random(444354534232L);
        int i = 0;
        for(int a = 0; a < lists.length; a++) {
            final int size = rand.nextInt(1000);
            List<Integer> list = new ArrayList<Integer>(size);
            for(int j = 0; j < size; j++) {
                list.add(i++);
            }
            lists[a] = list;
        }

        UnmodifiableJointList<Integer> jointList = new UnmodifiableJointList<Integer>(lists);
        Assert.assertEquals(i, jointList.size());

        for(int n = 0; n < jointList.size(); n++) {
            Assert.assertEquals(n, jointList.get(n).intValue());
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAdd1() {
        UnmodifiableJointList<Integer> jointList1 = new UnmodifiableJointList<Integer>();
        jointList1.add(0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAdd2() {
        final List<Integer>[] lists = new ArrayList[2];
        final Random rand = new Random(444354534232L);
        int i = 0;
        for(int a = 0; a < lists.length; a++) {
            final int size = rand.nextInt(1000);
            List<Integer> list = new ArrayList<Integer>(size);
            for(int j = 0; j < size; j++) {
                list.add(i++);
            }
            lists[a] = list;
        }
        UnmodifiableJointList<Integer> jointList2 = new UnmodifiableJointList<Integer>(lists);
        jointList2.add(999);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetInt2() {
        final List<Integer>[] lists = new ArrayList[3];
        final Random rand = new Random(444354534232L);
        int i = 0;
        for(int a = 0; a < lists.length; a++) {
            final int size = rand.nextInt(1000);
            List<Integer> list = new ArrayList<Integer>(size);
            for(int j = 0; j < size; j++) {
                list.add(i++);
            }
            lists[a] = list;
        }

        UnmodifiableJointList<Integer> jointList = new UnmodifiableJointList<Integer>(lists);
        Assert.assertEquals(i, jointList.size());

        for(int n = 0; n <= jointList.size(); n++) {
            Assert.assertEquals(n, jointList.get(n).intValue());
        }
    }


}
