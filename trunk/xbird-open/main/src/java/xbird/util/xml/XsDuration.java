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
package xbird.util.xml;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xbird.xquery.DynamicError;
import xbird.xquery.XQueryException;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class XsDuration implements Externalizable, Comparable<XsDuration> {
    private static final long serialVersionUID = -3948239212882681887L;
    private static final long SECONDS_PER_DATE_L = 24 * 60 * 60;
    private static final Pattern DURATION_PATTERN = Pattern.compile("(-?)P(([0-9]+)Y)?(([0-9]+)M)?(([0-9]+)D)?"
            + "(T(([0-9]+)H)?(([0-9]+)M)?(([0-9]+)(\\.[0-9]+)?S)?)?");

    private boolean _negate;
    private int _months;
    private long _seconds;
    private double _milsec;

    private boolean _hasYearMonth;
    private boolean _hasDayTime;

    private boolean _dirty = true;
    private String _literal = null;
    
    public XsDuration() {}

    private XsDuration(boolean negate, int months, long seconds, double milsec) {
        this._negate = negate;
        this._months = months;
        this._seconds = seconds;
        this._milsec = milsec;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }
        if(obj instanceof XsDuration) {
            return compareTo((XsDuration) obj) == 0;
        }
        return false;
    }

    public int compareTo(XsDuration o) {
        return 0;
    }
    
    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        if(_dirty) {
            
            this._dirty = false;
        }
        return _literal;
    }

    public static XsDuration valueOf(String literal) throws XQueryException {
        final Matcher match = DURATION_PATTERN.matcher(literal);
        if(!match.matches()) {
            throw new DynamicError("err:FORG0001", "Illegal representation as xs:duration: "
                    + literal);
        }

        final String y, mo, d;
        int year = ((y = match.group(2)) == null) ? 0 : Integer.parseInt(y);
        int month = ((mo = match.group(4)) == null) ? 0 : Integer.parseInt(mo);
        int day = ((d = match.group(6)) == null) ? 0 : Integer.parseInt(d);
        String x;
        int hour = ((x = match.group(9)) == null) ? 0 : Integer.parseInt(x);
        int minute = ((x = match.group(11)) == null) ? 0 : Integer.parseInt(x);
        int sec = ((x = match.group(13)) == null) ? 0 : Integer.parseInt(x);

        final boolean neg = (match.group(1) != null);
        final int months = year * 12 + month;
        final long seconds = (day * SECONDS_PER_DATE_L) + (hour * 3600) + (minute * 60) + sec;
        final double milsec = ((x = match.group(15)) == null) ? 0d : Double.parseDouble(x);

        final XsDuration dur = new XsDuration(neg, months, seconds, milsec);
        dur._hasYearMonth = (y != null) || (mo != null);
        dur._hasDayTime = (d != null) || (match.group(8) != null);
        return dur;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {}

    public void writeExternal(ObjectOutput out) throws IOException {}

}
