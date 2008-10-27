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
package xbird.util.concurrent.counter;

import xbird.config.Settings;
import xbird.util.system.SystemUtils;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class CounterProvider {

    private static final CounterType counterType;
    private static final int NSTRIPE;
    static {
        final String counter = System.getProperty("xbird.counter");
        if("cliffc".equals(counter)) {
            counterType = CounterType.cliffc;
        } else if("stripe".equals(counter)) {
            counterType = CounterType.stripe;
        } else if("stripe2".equals(counter)) {
            counterType = CounterType.stripe2;
        } else if("stripe3".equals(counter)) {
            counterType = CounterType.stripe3;
        } else if("atomic".equals(counter)) {
            counterType = CounterType.atomic;
        } else {
            counterType = CounterType.auto;
        }
        int nstripe = Integer.parseInt(Settings.get("xbird.util.counter.nstripe", "-1"));
        if(nstripe == -1) {
            int procs = SystemUtils.availableProcessors();
            nstripe = Math.max(4, procs >> 3); // 4 < x < 8
        }
        NSTRIPE = nstripe;
    }

    public enum CounterType {
        cliffc, stripe, stripe2,stripe3, atomic, auto
    }

    private CounterProvider() {}

    public static CounterType getCounterType() {
        return counterType;
    }

    public static ICounter createIntCounter() {
        return createIntCounter(NSTRIPE);
    }
    
    public static ICounter createIntCounter(int nstripe) {
        final int procs = SystemUtils.availableProcessors();
        switch(counterType) {
            case cliffc:
                return new HighScalableIntCounter();
            case stripe:
                return new StripeAtomicIntCounter(nstripe);
            case stripe2:
                return new StripeAtomicIntCounter2(nstripe);
            case stripe3:
                return new StripeAtomicIntCounter3(nstripe);
            case atomic:
                return new AtomicIntCounter();
            case auto:
            default:
                if(procs > 64) {
                    return new HighScalableIntCounter();
                } else if(procs > 32) {
                    return new StripeAtomicIntCounter(nstripe);
                } else {
                    return new AtomicIntCounter();
                }
        }
    }

}
