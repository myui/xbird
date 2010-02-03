/*
 * @(#)$Id$
 *
 * Copyright 2008 Makoto YUI
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
package xbird.util.distribution;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class ZipfianDistTest {

    @Test
    public void testZipf() {
        Distribution dist = new ZipfianDist(100, 0.5);
        long[] values = new long[100];
        for(int i = 0; i < 100; i++) {
            values[i] = dist.nextRandomValue().longValue();
        }
        Arrays.sort(values);
        long oldVal = 0;
        int count = 1;
        ArrayList<Integer> v = new ArrayList<Integer>();

        for(int i = 0; i < 100; i++) {
            if(values[i] != oldVal) {
                oldVal = values[i];
                v.add(count);
                count = 1;
            } else {
                count++;
            }
        }

        int size = v.size();
        System.out.println(size);

        int[] va = new int[size];
        for(int i = 0; i < size; i++) {
            va[i] = v.get(i).intValue();
        }
        Arrays.sort(va);

        for(int i = size - 1; i >= 0; i--) {
            System.out.print(va[i] + " ");
        }
    }
}
