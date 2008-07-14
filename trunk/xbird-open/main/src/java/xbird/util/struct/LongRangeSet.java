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
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xbird.util.collections.LongQueue;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class LongRangeSet extends AbstractSet<Long> implements Externalizable {
    private static final long serialVersionUID = 4553694380835101053L;

    private final ArrayList<LongRange> _ranges;

    public LongRangeSet() {
        this._ranges = new ArrayList<LongRange>();
    }

    public LongRange getRangeOf(long v) {
        final int idx = getRangeIndexOf(v);
        return idx >= 0 ? _ranges.get(idx) : null;
    }

    private int getRangeIndexOf(long v) {
        final int size = _ranges.size();
        if(size == 0) {
            return -1;
        }
        int lo = 0;
        int hi = size - 1;
        while(lo <= hi) {
            int mid = (lo + hi) / 2;
            LongRange r = _ranges.get(mid);
            if(r.contains(v)) {
                return mid;
            } else if(v < r.getStart()) {
                hi = mid - 1;
            } else {
                lo = mid + 1;
            }
        }
        return -(lo + 1);
    }

    private void normalize(int index) {
        while(index < _ranges.size() - 1) {
            LongRange r1 = _ranges.get(index);
            LongRange r2 = _ranges.get(index + 1);
            LongRange merged = r1.mergeWith(r2);
            if(merged == null) {
                break;
            }
            _ranges.set(index, merged);
            _ranges.remove(index + 1);
        }
    }

    public int insertRange(LongRange other) {
        int lo = 0;
        int hi = _ranges.size() - 1;
        while(lo <= hi) {
            int mid = (lo + hi) / 2;
            LongRange r = _ranges.get(mid);
            final int compare = other.compareTo(r);
            if(compare == 0) {
                return -1;
            } else if(compare < 0) {
                hi = mid - 1;
            } else {
                lo = mid + 1;
            }
        }
        _ranges.add(lo, other);
        return lo;
    }

    public boolean addRange(long start, long end) {
        if(contains(start, end)) {
            return false;
        }
        final int insertPos = insertRange(new LongRange(start, end));
        if(insertPos != -1) {
            normalize(Math.max(insertPos - 1, 0));
            return true;
        }
        return false;
    }

    public boolean containsAll(LongRange range) {
        final LongRange r = getRangeOf(range.getStart());
        return (r != null) ? r.contains(range.getEnd()) : false;
    }

    public boolean containsAll(long start, long end) {
        final LongRange r = getRangeOf(start);
        return (r != null) ? r.contains(end) : false;
    }

    public long[] listNotContained(LongRange range) {
        return listNotContained(range.getStart(), range.getEnd());
    }

    public long[] listNotContained(long start, long end) {
        final LongQueue list = new LongQueue();
        for(long i = start; i <= end; i++) {
            if(!contains(i)) {
                list.add(i);
            }
        }
        return list.toArray();
    }

    // --------------------------------------------------

    @Override
    public boolean add(Long e) {
        return add(e.longValue());
    }

    public boolean add(long v) {
        int index = getRangeIndexOf(v);
        if(index >= 0) {
            return false;
        }
        int insertionIndex = -index - 1;
        _ranges.add(insertionIndex, new LongRange(v, v));
        if(insertionIndex > 0) {
            insertionIndex--;
        }
        normalize(insertionIndex);
        return true;
    }

    @Override
    public boolean contains(Object o) {
        if(!(o instanceof Long)) {
            return false;
        }
        final long v = ((Long) o).longValue();
        return contains(v);
    }

    public boolean contains(long v) {
        return getRangeIndexOf(v) >= 0;
    }

    public boolean contains(long start, long end) {
        return getRangeIndexOf(start) >= 0 && getRangeIndexOf(end) >= 0;
    }

    /**
     * not supported.
     */
    @Override
    public Iterator<Long> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return _ranges.size();
    }

    // --------------------------------------------------
    // override for efficiency    

    @Override
    public void clear() {
        _ranges.clear();
    }

    @Override
    public boolean isEmpty() {
        return _ranges.isEmpty();
    }

    @Override
    public boolean remove(Object o) {
        if(!(o instanceof Long)) {
            return false;
        }
        final long v = ((Long) o).longValue();
        return remove(v);
    }

    public boolean remove(long v) {
        final int index = getRangeIndexOf(v);
        if(index < 0) {
            return false;
        }
        final LongRange r = _ranges.get(index);
        if(v == r.getStart()) {
            if(r.getEnd() == v) {
                _ranges.remove(index);
            } else {
                r.setStart(r.getStart() + 1);
            }
        } else if(v == r.getEnd()) {
            r.setEnd(r.getEnd());
        } else {
            LongRange r1 = new LongRange(r.getStart(), (v - 1));
            LongRange r2 = new LongRange((v + 1), r.getEnd());
            _ranges.set(index, r1);
            _ranges.add(index + 1, r2);
        }
        return true;
    }

    // --------------------------------------------------

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        final List<LongRange> ranges = _ranges;
        final int size = in.readInt();
        for(int i = 0; i < size; i++) {
            ranges.add(LongRange.readFrom(in));
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        final List<LongRange> ranges = _ranges;
        int size = ranges.size();
        out.writeInt(size);
        for(int i = 0; i < size; i++) {
            ranges.get(i).writeExternal(out);
        }
    }

    public static LongRangeSet readFrom(ObjectInput in) throws IOException, ClassNotFoundException {
        LongRangeSet set = new LongRangeSet();
        set.readExternal(in);
        return set;
    }

    @Override
    public String toString() {
        return _ranges.toString();
    }
}
