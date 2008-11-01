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

import java.util.Arrays;
import java.util.Random;

import junit.framework.TestCase;
import xbird.util.lang.ArrayUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class SortedListTest extends TestCase {

    public void testSortedArrayList() {
        runTest(new SortedArrayList<Integer>());
    }

    public void testSortedLinkedList() {
        runTest(new SortedLinkedList<Integer>());
    }

    private static void runTest(final SortedList<Integer> listImpl) {
        final int[] orig = new int[10000];
        final Random rand = new Random(235423009912312L);
        for(int i = 0; i < orig.length; i++) {
            orig[i] = rand.nextInt();
        }
        final int[] sorted = ArrayUtils.copy(orig);
        Arrays.sort(sorted);

        // round 1
        for(int i = 0; i < orig.length; i++) {
            listImpl.add(orig[i]);
        }
        assertEquals(sorted.length, listImpl.size());
        for(int i = 0; i < sorted.length; i++) {
            assertEquals(sorted[i], listImpl.get(i).intValue());
        }

        // round 2
        listImpl.clear();
        final int[] shuffled = ArrayUtils.copy(orig);
        ArrayUtils.shuffle(orig);
        for(int i = 0; i < orig.length; i++) {
            listImpl.add(shuffled[i]);
        }
        assertEquals(sorted.length, listImpl.size());
        for(int i = 0; i < sorted.length; i++) {
            assertEquals(sorted[i], listImpl.get(i).intValue());
        }
    }

}
