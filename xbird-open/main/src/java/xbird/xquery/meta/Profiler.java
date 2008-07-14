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
package xbird.xquery.meta;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class Profiler {

    private int io_dtm_reads = 0;
    private int dtm_read_blocks = 0;
    private int purgation = 0;

    public Profiler() {}

    public void incrDTMReads() {
        ++io_dtm_reads;
    }

    public int getDTMReads() {
        return io_dtm_reads;
    }

    public void incrReadDTMBlocks(int blocks) {
        this.dtm_read_blocks += blocks;
    }

    public int getReadDTMBlocks() {
        return dtm_read_blocks;
    }

    public void incrPurgation() {
        ++purgation;
    }

    public int getPurgations() {
        return purgation;
    }

    @Override
    public String toString() {
        return "total operations of DTM read: " + io_dtm_reads + ", read blocks: "
                + dtm_read_blocks + ", purgations of DTM blocks: " + purgation;
    }
}
