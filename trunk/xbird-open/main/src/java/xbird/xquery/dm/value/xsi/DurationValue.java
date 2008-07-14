/*
 * @(#)$Id: DurationValue.java 3619 2008-03-26 07:23:03Z yui $
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
package xbird.xquery.dm.value.xsi;

import java.io.*;
import java.math.BigDecimal;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.*;

import xbird.util.lang.ObjectUtils;
import xbird.xquery.*;
import xbird.xquery.dm.value.AtomicValue;
import xbird.xquery.dm.value.Item;
import xbird.xquery.dm.value.literal.XString;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.misc.XsDatatypeFactory;
import xbird.xquery.type.AtomicType;
import xbird.xquery.type.TypeTable;
import xbird.xquery.type.xs.*;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 */
public final class DurationValue extends AtomicValue {
    private static final long serialVersionUID = 990190569696374961L;
    public static final int ID = 9;

    private static final GregorianCalendar EPOCH = new GregorianCalendar(TimeZone.getTimeZone("GMT"));

    private/* final */Duration value;
    private transient String _canonical = null;

    public DurationValue() {
        super();
    }

    public DurationValue(Duration d) {
        this(d, DurationType.DURATION);
    }

    public DurationValue(Duration d, DurationType type) {
        super(""/*dummy*/, type);
        this.value = XsDatatypeFactory.createDuration(d, type); // TODO REVIEWME
        update();
    }

    private DurationValue(String literal, Duration d, DurationType type) {
        super(literal, type);
        this.value = d;
        update();
    }

    public Duration getValue() {
        return value;
    }

    public DurationType getDurationType() {
        return (DurationType) _type;
    }

    public long getTimeInMillis() {
        return value.getTimeInMillis(EPOCH);
    }

    public Duration toJavaObject() throws XQueryException {
        return value;
    }

    public static DurationValue valueOf(String literal, DurationType type) {
        Duration d = XsDatatypeFactory.createDuration(literal, type);
        return new DurationValue(literal, d, type);
    }

    @Override
    public <T extends AtomicValue> T castAs(AtomicType type, DynamicContext dynEnv)
            throws XQueryException {
        assert (type != null);
        final int tid = type.getTypeId();
        final AtomicValue atomv;
        switch(tid) {
            case TypeTable.UNTYPED_ATOMIC_TID:
                atomv = new UntypedAtomicValue(stringValue());
                break;
            case TypeTable.STRING_TID:
                atomv = new XString(stringValue());
                break;
            case TypeTable.DURATION_TID:
                atomv = this;
                break;
            case TypeTable.YEAR_MONTH_DURATION_TID:
                atomv = new DurationValue(getYearMonthDuration(), YearMonthDurationType.YEARMONTH_DURATION);
                break;
            case TypeTable.DAYTIME_DURATION_TID:
                atomv = new DurationValue(getDayTimeDuration(), DayTimeDurationType.DAYTIME_DURATION);
                break;
            default:
                throw new TypeError("Could not cast '" + _type + "' as '" + type + "'");
        }
        return (T) atomv;
    }

    private final Duration getYearMonthDuration() {
        try {
            return DatatypeFactory.newInstance().newDurationYearMonth(value.getSign() >= 0, value.getYears(), value.getMonths());
        } catch (DatatypeConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    private final Duration getDayTimeDuration() {
        try {
            return DatatypeFactory.newInstance().newDurationDayTime(value.getSign() >= 0, value.getDays(), value.getHours(), value.getMinutes(), value.getSeconds());
        } catch (DatatypeConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    protected void update() {
        String sv = toString();
        setStringValue(sv);
    }

    @Override
    public String stringValue() {
        return this.toString();
    }

    public DurationValue multiply(double factor) {
        final int sign = value.getSign();
        if(sign == 0) {
            return this;
        }
        final Duration d;
        final int tid = _type.getTypeId();
        if(tid == TypeTable.YEAR_MONTH_DURATION_TID) {
            assert (sign != 0) : sign;
            boolean isNegativeFactor = factor < 0;
            boolean resIsPositive = (sign == -1) ? isNegativeFactor : !isNegativeFactor;
            int y = value.getYears(), m = value.getMonths();
            int months = y * 12 + m;
            int absRes = (int) (months * Math.abs(factor));
            int resYear = absRes / 12;
            int resMonth = absRes % 12;
            try {
                d = DatatypeFactory.newInstance().newDurationYearMonth(resIsPositive, resYear, resMonth);
            } catch (DatatypeConfigurationException e) {
                throw new IllegalStateException(e);
            }
        } else {
            d = value.multiply(BigDecimal.valueOf(factor));
        }
        return new DurationValue(d, getDurationType());
    }

    public int getYears() {
        int years = value.getYears();
        int months = Math.abs(getMonths());
        if(months >= 12) {
            int yadd = months / 12;
            years += yadd;
        }
        int sign = value.getSign();
        return sign == -1 ? -years : years;
    }

    public int getMonths() {
        int months = value.getMonths();
        int days = Math.abs(getDays());
        if(days > 31) {
            int madd = days / 31;
            months += madd;
        }
        int sign = value.getSign();
        return sign == -1 ? -months : months;
    }

    public int totalMonths() {
        int years = value.getYears();
        int months = (years * 12) + value.getMonths();
        int sign = value.getSign();
        return sign == -1 ? -months : months;
    }

    public int getDays() {
        int days = value.getDays();
        int hours = Math.abs(getHours());
        if(hours > 23) {
            int dadd = hours / 24;
            days += dadd;
        }
        int sign = value.getSign();
        return sign == -1 ? -days : days;
    }

    public int getHours() {
        int hours = value.getHours();
        int minutes = Math.abs(getMinutes());
        if(minutes > 60) {
            int hadd = minutes / 60;
            hours += hadd;
        }
        int sign = value.getSign();
        return sign == -1 ? -hours : hours;
    }

    public int getMinutes() {
        int minutes = value.getMinutes();
        int seconds = Math.abs(getSeconds());
        if(seconds > 60) {
            int madd = seconds / 60;
            minutes += madd;
        }
        int sign = value.getSign();
        return sign == -1 ? -minutes : minutes;
    }

    public int getSeconds() {
        int seconds = value.getSeconds();
        int sign = value.getSign();
        return sign == -1 ? -seconds : seconds;
    }

    private Number getSecondsField() {
        return value.getField(DatatypeConstants.SECONDS);
    }

    private double totalSeconds() {
        final int days = value.getDays();
        int hours = value.getHours();
        if(days != 0) {
            hours += days * 24;
        }
        int minutes = value.getMinutes();
        if(hours != 0) {
            minutes += hours * 60;
        }
        final Number secField = getSecondsField();
        final double seconds;
        if(secField == null) {
            seconds = minutes * 60;
        } else {
            if(minutes == 0) {
                seconds = secField.doubleValue();
            } else {
                seconds = secField.doubleValue() + (minutes * 60);
            }
        }
        final int sign = value.getSign();
        return (sign == -1) ? -seconds : seconds;
    }

    private boolean isYearMonthSet() {
        return value.isSet(DatatypeConstants.YEARS) | value.isSet(DatatypeConstants.MONTHS);
    }

    private boolean isDayTimeSet() {
        return value.isSet(DatatypeConstants.DAYS) | value.isSet(DatatypeConstants.HOURS)
                | value.isSet(DatatypeConstants.MINUTES) | value.isSet(DatatypeConstants.SECONDS);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) {
            return true;
        }
        if(o instanceof DurationValue) {
            final DurationValue dv = (DurationValue) o;
            if(isYearMonthSet() && !dv.isYearMonthSet()) {
                return totalMonths() == 0;
            } else if(isDayTimeSet() && !dv.isDayTimeSet()) {
                return totalSeconds() == 0;
            }
            final int diffm = totalMonths() - dv.totalMonths();
            if(diffm != 0) {
                return false;
            }
            final double diffs = totalSeconds() - dv.totalSeconds();
            if(diffs != 0) {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(Item trg) {
        if(!(trg instanceof DurationValue)) {
            throw new XQRTException("err:XPTY0004", "Imcomparable "
                    + trg.getClass().getSimpleName() + " with DurationValue");
        }
        final DurationValue dv = (DurationValue) trg;
        final int diffm = totalMonths() - dv.totalMonths();
        if(diffm != 0) {
            return diffm < 0 ? -1 : 1;
        }
        final double diffs = totalSeconds() - dv.totalSeconds();
        if(diffs != 0) {
            return diffs < 0 ? -1 : 1;
        }
        return 0;
    }

    @Override
    public String toString() {
        if(_canonical == null) {
            final StringBuilder buf = new StringBuilder(128);
            final int sign = value.getSign();
            boolean yset = value.isSet(DatatypeConstants.YEARS);
            boolean mset = value.isSet(DatatypeConstants.MONTHS);
            boolean isset = yset | mset | value.isSet(DatatypeConstants.DAYS)
                    | value.isSet(DatatypeConstants.HOURS) | value.isSet(DatatypeConstants.MINUTES)
                    | value.isSet(DatatypeConstants.SECONDS);
            if(sign == 0 && !isset) {
                this._canonical = "PT0S";
                return "PT0S";
            }
            if(sign == -1) {
                buf.append('-');
            }
            buf.append('P');
            int years = value.getYears();
            int months = value.getMonths();
            int days = value.getDays();
            int hours = value.getHours();
            int minutes = value.getMinutes();
            int seconds = value.getSeconds();
            if(seconds > 60) {
                int madd = seconds / 60;
                minutes += madd;
                seconds %= 60;
            }
            if(minutes > 60) {
                int hadd = minutes / 60;
                hours += hadd;
                minutes %= 60;
            }
            if(hours > 23) {
                int dadd = hours / 24;
                if(dadd != 0) {
                    days += dadd;
                }
                hours %= 24;
            }
            if(days > 31 && (mset | yset)) {
                int madd = days / 31;
                if(madd != 0) {
                    months += madd;
                }
                days = days % 31;
            }
            if(months >= 12) {
                int yadd = months / 12;
                if(yadd != 0) {
                    years += yadd;
                }
                months = months % 12;
            }
            if(years != 0) {
                buf.append(years);
                buf.append('Y');
            }
            final int tid = _type.getTypeId();
            if(months != 0 || (years == 0 && tid == TypeTable.YEAR_MONTH_DURATION_TID)) {
                buf.append(months);
                buf.append('M');
            }
            if(tid != TypeTable.YEAR_MONTH_DURATION_TID) {
                if(days != 0) {
                    buf.append(days);
                    buf.append('D');
                }
                buf.append('T');
                if(hours != 0) {
                    buf.append(hours);
                    buf.append('H');
                }
                if(minutes != 0) {
                    buf.append(minutes);
                    buf.append('M');
                }
                if(seconds != 0) {
                    Number sec = value.getField(DatatypeConstants.SECONDS);
                    String secStr = sec.toString();
                    String trimed = removeTrilingZeros(secStr);
                    buf.append(trimed);
                    buf.append('S');
                } else if(hours == 0 && minutes == 0) {
                    if(days != 0) {
                        buf.deleteCharAt(buf.length() - 1); // remove 'T'
                    } else {
                        buf.append("0S");
                    }
                }
            }
            final String res = buf.toString();
            this._canonical = res;
            return res;
        }
        return _canonical;
    }

    private static String removeTrilingZeros(final String src) {
        if(src.indexOf('.') == -1) {
            return src;
        } else {
            // remove trailing zeros
            int last = src.length();
            while(--last > 0) {
                if(src.charAt(last) != '0') {
                    break;
                }
            }
            if(src.charAt(last) == '.') { // for the case 'xxx.000'
                --last;
            }
            return src.substring(0, last + 1);
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this._lexicalValue = ObjectUtils.readString(in);
        this._type = AtomicType.readAtomicType(in);
        this.value = (Duration) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        ObjectUtils.writeString(out, _lexicalValue);
        _type.writeExternal(out);
        out.writeObject(value);
    }

    @Override
    public int getIdentifier() {
        return ID;
    }
}
