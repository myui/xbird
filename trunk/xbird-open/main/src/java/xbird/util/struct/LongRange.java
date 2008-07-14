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
package xbird.util.struct;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public class LongRange implements Externalizable, Comparable<LongRange> {
    private static final long serialVersionUID = 6867907859871402394L;

    private long start, end;

    public LongRange() {}
    
    public LongRange(long start, long end) {
        if(start > end) {
            throw new IllegalArgumentException("Illegal range: " + start + " to " + end);
        }
        this.start = start;
        this.end = end;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public long getEnd() {
        return end;
    }

    public long getStart() {
        return start;
    }

    public boolean contains(LongRange other, boolean partially) {
        if(partially) {
            return other.start >= start && other.start <= end;
        } else {
            return start <= other.start && end >= other.end;
        }
    }

    public boolean contains(long o) {
        return o >= start && o <= end;
    }

    public LongRange mergeWith(LongRange other) {
        if(!contains(other, true)) {
            return null;
        }
        long newStart = Math.min(start, other.start);
        long newEnd = Math.max(end, other.end);
        return new LongRange(newStart, newEnd);
    }

    public int compareTo(LongRange other) {
        if(start < other.start) {
            return -1;
        } else if(start > other.start) {
            return 1;
        } else if(end == other.end) {
            return 0;
        } else {
            return end < other.end ? -1 : 1;
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.start = in.readLong();
        this.end = in.readLong();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(start);
        out.writeLong(end);
    }

    public static LongRange readFrom(ObjectInput in) throws IOException {
        long start = in.readLong();
        long end = in.readLong();
        return new LongRange(start, end);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof LongRange)) {
            return false;
        }
        LongRange other = (LongRange) obj;
        return other.start == start && other.end == end;
    }

    @Override
    public int hashCode() {
        return (int) (start ^ end);
    }

    @Override
    public String toString() {
        return Long.toString(start) + "-" + Long.toString(end);
    }

}
