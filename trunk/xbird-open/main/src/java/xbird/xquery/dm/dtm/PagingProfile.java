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
package xbird.xquery.dm.dtm;

import xbird.util.collections.CyclicQueue;
import xbird.xquery.meta.Profiler;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class PagingProfile {

    private static boolean ENV_DISABLE_CHANGE_STRATEGY = System.getProperty("xbird.paging.fix") != null;

    private static final int RF_FORWARD = Integer.getInteger("xbird.paging.rf_fwd", 32);
    private static final int RB_FORWARD = 0;
    private static final int RF_REVERSE = 1;
    private static final int RB_REVERSE = 31;
    private static final int RF_INDEX = Integer.getInteger("xbird.paging.rf_idx", 4);
    private static final int RB_INDEX = 0;
    private static final int RF_BUILKLOAD = 6;
    private static final int RB_BUILKLOAD = 2;
    private static final int RF_SERIALIZE = Integer.getInteger("xbird.paging.rf_ser", 64);
    private static final int RB_SERIALIZE = 0;
    private static final int RF_NEXTSIB = Integer.getInteger("xbird.paging.rf_nextsib", 8);
    private static final int RB_NEXTSIB = 0;

    private int readForwards = RF_FORWARD;
    private int readBackwards = RB_FORWARD;

    // statistics stuff
    private final CyclicQueue<Integer> lastReads = new CyclicQueue<Integer>(10);
    private Strategy strategy = Strategy.forward;

    private int forwardLimit = -1;

    private Profiler profiler = null;
    private int totalReadIOs = 0;
    private int totalReadBlocks = 0;

    public PagingProfile() {}

    public void setProfiler(Profiler profiler) {
        this.profiler = profiler;
    }

    public int getReadBackwards() {
        return readBackwards;
    }

    public int getReadForwards() {
        return readForwards;
    }

    @Deprecated
    public int getTotalReads() {
        return readBackwards + readForwards;
    }

    public void setReadBackwards(int readBackwards) {
        this.readBackwards = readBackwards;
    }

    public void setReadForwards(int readForwards) {
        this.readForwards = readForwards;
    }

    public void lastReadAccess(int pageNum, int readBlocks) {
        lastReads.offer(pageNum);
        ++totalReadIOs;
        totalReadBlocks += readBlocks;
        if(profiler != null) {
            profiler.incrDTMReads();
            profiler.incrReadDTMBlocks(readBlocks);
        }
    }

    public int getForwardLimit() {
        return forwardLimit;
    }

    public void setForwardLimit(int forwardLimit) {
        this.forwardLimit = forwardLimit;
    }

    public void setStrategy(Strategy s) {
        if(ENV_DISABLE_CHANGE_STRATEGY) {
            return;
        }
        if(this.strategy != s) {
            switch(s) {
                case forward:
                    this.readForwards = RF_FORWARD;
                    this.readBackwards = RB_FORWARD;
                    break;
                case index:
                    this.readForwards = RF_INDEX;
                    this.readBackwards = RB_INDEX;
                    break;
                case serialization:
                    this.readForwards = RF_SERIALIZE;
                    this.readBackwards = RB_SERIALIZE;
                    break;
                case nextsib:
                    this.readForwards = RF_NEXTSIB;
                    this.readBackwards = RB_NEXTSIB;
                    break;
                case bulkload:
                    this.readForwards = RF_BUILKLOAD;
                    this.readBackwards = RB_BUILKLOAD;
                    break;
                case reverse:
                    this.readForwards = RF_REVERSE;
                    this.readBackwards = RB_REVERSE;
                    break;
                default:
                    break;
            }
        }
        this.strategy = s;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public enum Strategy {
        forward /* default */, reverse, nextsib, index, bulkload, serialization
    }

    public void recordPurgation(final int key) {
        if(profiler != null) {
            profiler.incrPurgation();
        }
    }

}
