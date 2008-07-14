/*
 * @(#)$Id: codetemplate_xbird.xml 943 2006-09-13 07:03:37Z yui $
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
package xbird.util.concurrent;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class AtomicUtils {

    private AtomicUtils() {}

    public static boolean tryIncrementIfLessThan(final AtomicInteger capacity, final int atmost) {
        int capa;
        do {
            capa = capacity.get();
            if(capa >= atmost) {
                return false;
            }
        } while(!capacity.compareAndSet(capa, capa + 1));
        return true;
    }

    public static int tryIncrementAndGetIfLessThan(final AtomicInteger capacity, final int upperbound) {
        for(;;) {
            final int current = capacity.get();
            if(current >= upperbound) {
                return upperbound;
            }
            final int next = current + 1;
            if(capacity.compareAndSet(current, next)) {
                return next;
            }
        }
    }

    public static boolean tryIncrementIfGreaterThan(final AtomicInteger capacity, final int least) {
        int capa;
        do {
            capa = capacity.get();
            if(capa <= least) {
                return false;
            }
        } while(!capacity.compareAndSet(capa, capa + 1));
        return true;
    }

    public static boolean tryDecrementIfGreaterThan(final AtomicInteger capacity, final int least) {
        int capa;
        do {
            capa = capacity.get();
            if(capa <= least) {
                return false;
            }
        } while(!capacity.compareAndSet(capa, capa - 1));
        return true;
    }

    public static int tryDecrementAndGetIfGreaterThan(final AtomicInteger capacity, final int least) {
        for(;;) {
            final int current = capacity.get();
            if(current <= least) {
                return current;
            }
            final int next = current - 1;
            if(capacity.compareAndSet(current, next)) {
                return next;
            }
        }
    }

}
