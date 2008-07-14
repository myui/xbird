/*
 * @(#)$Id: DateTimeValue.java 3619 2008-03-26 07:23:03Z yui $
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

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import xbird.util.lang.ObjectUtils;
import xbird.xquery.*;
import xbird.xquery.dm.value.AtomicValue;
import xbird.xquery.dm.value.Item;
import xbird.xquery.meta.DynamicContext;
import xbird.xquery.misc.XsDatatypeFactory;
import xbird.xquery.type.AtomicType;
import xbird.xquery.type.TypeTable;
import xbird.xquery.type.xs.DateTimeBaseType;

/**
 * 
 * <DIV lang="en"></DIV>
 * <DIV lang="ja"></DIV>
 * 
 * @author Makoto YUI (yuin405+xbird@gmail.com)
 * @link http://www.w3.org/TR/xmlschema-2/#dateTime
 */
public class DateTimeValue extends AtomicValue {
    private static final long serialVersionUID = 2749362266774252238L;
    public static final int ID = 7;

    protected/* final */XMLGregorianCalendar value;
    private transient String _canonical = null;

    public DateTimeValue() {
        super();
    }

    public DateTimeValue(String literal, DateTimeBaseType type) {
        super(literal, type);
        this.value = XsDatatypeFactory.createXMLGregorianCalendar(literal);
    }

    public DateTimeValue(XMLGregorianCalendar value, DateTimeBaseType type) {
        super(value.toXMLFormat(), type);
        this.value = value;
    }

    public XMLGregorianCalendar getValue() {
        return value;
    }

    public DateTimeBaseType getDateTimeType() {
        return (DateTimeBaseType) _type;
    }

    public XMLGregorianCalendar toJavaObject() throws XQueryException {
        return value;
    }

    @Override
    public int compareTo(Item trg) {
        if(!(trg instanceof DateTimeValue)) {
            throw new XQRTException("err:XPTY0004", "Imcomparable "
                    + trg.getClass().getSimpleName() + " with DateTimeValue");
        }
        XMLGregorianCalendar trgUri = ((DateTimeValue) trg).value;
        return value.compare(trgUri);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends AtomicValue> T castAs(AtomicType trgType, DynamicContext dynEnv)
            throws XQueryException {
        final int ttid = trgType.getTypeId();
        if(ttid == TypeTable.NOTATION_TID || ttid == TypeTable.ANY_ATOM_TID) {
            throw new StaticError("err:XPST0080", "Illegal target type: " + trgType);
        }
        final AtomicType type = _type;
        if(type == trgType) {
            return (T) this;
        }
        final boolean mayCastable = TypeTable.isMayCastable(type, trgType);
        if(mayCastable) {
            if(trgType instanceof DateTimeBaseType) {
                final DateTimeBaseType dtType = (DateTimeBaseType) trgType;
                return (T) dtType.createInstance(value);
            } else {
                final String sv = stringValue();
                final String literal = trgType.processWhitespace(sv);
                try {
                    return (T) trgType.createInstance(literal, type, dynEnv);
                } catch (RuntimeException e) {
                    throw new DynamicError("err:FORG0001", "cast to '" + trgType + "' failed: "
                            + sv, e);
                }
            }
        }
        throw new DynamicError("err:XPTY0004", "failed to cast '" + _type + "' as '" + trgType
                + "': " + _lexicalValue);
    }

    @Override
    public final String stringValue() {
        return this.toString();
    }

    @Override
    public String toString() {
        if(_canonical == null) {
            final DateTimeBaseType targetType = getDateTimeType();
            final boolean settime = targetType.isTimeSet();
            if(settime) {
                final int hour = value.getHour();
                if(hour == 24 && value.getMinute() == 0 && value.getSecond() == 0) {
                    // 2004-03-31T24:00:00" => "2004-04-01T00:00:00"
                    value.add(XsDatatypeFactory.getDatatypeFactory().newDuration(true, 0, 0, 0, 0, 0, 1));
                    value.setSecond(0);
                }
            }
            final StringBuilder buf = new StringBuilder(128);
            if(targetType.isDateSet()) {
                final boolean yset = targetType.isYearSet();
                final boolean mset = targetType.isMonthSet();
                final boolean dset = targetType.isDaySet();
                if(yset) {
                    final int year = value.getYear();
                    if(year != DatatypeConstants.FIELD_UNDEFINED) {
                        if(year == 0) {// There is no year 0, and '0000' is not a valid lexical representation
                            appendPaddedZeros(1, 4, buf);
                        } else if(year < 0) {
                            buf.append('-');
                            appendPaddedZeros(-year, 4, buf);
                        } else {
                            appendPaddedZeros(year, 4, buf);
                        }
                    } else {
                        buf.append('-');
                    }
                } else if(mset || dset) {
                    buf.append('-');
                }
                if(mset) {
                    buf.append('-');
                    int month = value.getMonth();
                    if(month != DatatypeConstants.FIELD_UNDEFINED) {
                        appendPaddedZeros(month, 2, buf);
                    }
                } else if(dset) {
                    buf.append('-');
                }
                if(dset) {
                    buf.append('-');
                    int day = value.getDay();
                    if(day != DatatypeConstants.FIELD_UNDEFINED) {
                        appendPaddedZeros(day, 2, buf);
                    }
                }
                if(settime) {
                    buf.append('T');
                }
            }
            if(settime) {
                final int hour = value.getHour();
                if(hour == DatatypeConstants.FIELD_UNDEFINED) {
                    buf.append("00:00:00");
                } else {
                    appendPaddedZeros(hour, 2, buf);
                    buf.append(':');
                    final int minute = value.getMinute();
                    assert (minute != DatatypeConstants.FIELD_UNDEFINED);
                    appendPaddedZeros(minute, 2, buf);
                    buf.append(':');
                    final int second = value.getSecond();
                    assert (second != DatatypeConstants.FIELD_UNDEFINED);
                    appendPaddedZeros(second, 2, buf);
                    int millisec = value.getMillisecond();
                    if(millisec != DatatypeConstants.FIELD_UNDEFINED && millisec != 0) {
                        buf.append('.');
                        String ms = Integer.toString(millisec);
                        // remove trailing zeros
                        int last = ms.length();
                        while(--last > 0) {
                            if(ms.charAt(last) != '0') {
                                break;
                            }
                        }
                        String trimed = ms.substring(0, last + 1);
                        buf.append(trimed);
                    }
                }
            }
            int tz = value.getTimezone();
            if(tz != DatatypeConstants.FIELD_UNDEFINED) {
                if(tz == 0) {
                    buf.append('Z');
                } else if(tz != DatatypeConstants.FIELD_UNDEFINED) {
                    if(tz < 0) {
                        buf.append('-');
                        tz *= -1;
                    } else {
                        buf.append('+');
                    }
                    appendPaddedZeros(tz / 60, 2, buf);
                    buf.append(':');
                    appendPaddedZeros(tz % 60, 2, buf);
                }
            }
            String res = buf.toString();
            this._canonical = res;
            return res;
        }
        return _canonical;
    }

    private static void appendPaddedZeros(final int value, final int digits, final StringBuilder buf) {
        final String s = Integer.toString(value);
        for(int i = s.length(); i < digits; i++) {//padding
            buf.append('0');
        }
        buf.append(s);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this._lexicalValue = ObjectUtils.readString(in);
        this._type = AtomicType.readAtomicType(in);
        this.value = (XMLGregorianCalendar) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        ObjectUtils.writeString(out, _lexicalValue);
        _type.writeExternal(out);
        out.writeObject(value); //TODO XMLGregorianCalendar might not be Serializable
    }

    @Override
    public int getIdentifier() {
        return ID;
    }

}
