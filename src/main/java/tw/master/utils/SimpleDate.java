/*
 *  This file is part of the EvolutionBrain project.
 *
 *  Copyright (c) 2011 by Thomas Welsch (ttww@gmx.de). All rights reserved.
 *
 *  EvolutionBrain is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  EvolutionBrain is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with EvolutionBrain.  If not, see <http://www.gnu.org/licenses/>.
 */

package tw.master.utils;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * This class represents a simple to use date object .
 * <p>
 * It's based on a GregorianCalendar object inside, but gives a simpler interface for access.
 * 
 * @author Thomas Welsch
 */
public class SimpleDate implements Serializable {

    private static final long       serialVersionUID = 1L;

    private final GregorianCalendar calendar;

    private String                  lastToTimeString;

    private String                  lastToDateString;

    private String                  lastToString;

    // ---------------------------------------------------------------------------------------------

    /**
     * Create an new {@link SimpleDate} object with actual date and time with the default {@link Locale}.
     *
     */
    public SimpleDate() {
        calendar = new GregorianCalendar();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Create an new {@link SimpleDate} object with given ms with the default {@link Locale}.
     *
     * @param millis
     *            Milliseconds since epoch.
     */
    public SimpleDate(long millis) {
        calendar = new GregorianCalendar();
        calendar.setTimeInMillis(millis);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Create an new SimpleDate object with hour/minute/second set to 0.
     * <p>
     *
     * @param year
     *            The year with base 0 (not to base 1900 or 1970...)
     * @param month
     *            Month, starting with January as month 1
     * @param dayOfMonth
     *            Day of month, starting at 1
     */
    public SimpleDate(int year, int month, int dayOfMonth) throws NumberFormatException {
        checkDateValues(year, month, dayOfMonth, 0, 0, 0);

        calendar = new GregorianCalendar(year, month - 1, dayOfMonth);
    }

    public SimpleDate(Date date) {
        this(date.getTime());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Create an new SimpleDate object with date and time.
     * <p>
     *
     * @param year
     *            The year with base 0 (not to base 1900 or 1970...)
     * @param month
     *            Month, starting with January as month 1
     * @param dayOfMonth
     *            Day of month, starting at 1
     * @param hourOfDay
     *            The hour from 0..23
     * @param minute
     *            The minute from 0..59
     * @param second
     *            The second from 0..59
     */
    public SimpleDate(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second)
    throws NumberFormatException {

        checkDateValues(year, month, dayOfMonth, hourOfDay, minute, second);

        calendar = new GregorianCalendar(year, month - 1, dayOfMonth, hourOfDay, minute, second);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Parse String with format dd.mm.yyyy[ hh:mm[:ss]] to a SimpleDate object. The time part is optional.
     *
     * @param date
     */
    public SimpleDate(String date) throws NumberFormatException {

        // -----------------------------------------------------------------------------------------
        // Change every none numeric character to {
        // -----------------------------------------------------------------------------------------
        String s = date.trim().replace(':', ',');
        s = s.replace(' ', ',');
        s = s.replace('.', ',');
        String[] sa = s.split(",");

        // -----------------------------------------------------------------------------------------
        // Fetch the fields:
        // -----------------------------------------------------------------------------------------
        int hourOfDay = 0;
        int minute = 0;
        int second = 0;

        int dayOfMonth = Integer.parseInt(sa[0]);
        int month = Integer.parseInt(sa[1]);
        int year = Integer.parseInt(sa[2]);

        if (sa.length > 3) {
            hourOfDay = Integer.parseInt(sa[3]);
            minute = Integer.parseInt(sa[4]);
            if (sa.length > 5)
                second = Integer.parseInt(sa[5]);
        }

        checkDateValues(year, month, dayOfMonth, hourOfDay, minute, second);

        calendar = new GregorianCalendar(year, month - 1, dayOfMonth, hourOfDay, minute, second);
    }

    private void checkDateValues(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second)
    throws NumberFormatException {
        // -----------------------------------------------------------------------------------------
        // Check values:
        // -----------------------------------------------------------------------------------------
        String error = null;
        if (hourOfDay < 0 || hourOfDay > 23) error = "Hour " + hourOfDay + " out of range (0..23)";
        if (minute < 0 || minute > 59) error = "Minute " + minute + " out of range (0..59)";
        if (second < 0 || second > 59) error = "Second " + second + " out of range (0..59)";
        if (dayOfMonth < 1 || dayOfMonth > 31) error = "Day " + dayOfMonth + " out of range (1..31)";
        if (month < 1 || month > 12) error = "Month " + month + " out of range (1..12)";
        if (year < 1900 || year > 3000) error = "Year " + year + " out of range (1900..3000)";

        if (error != null) throw new NumberFormatException("Bad date: " + error);
    }

    /**
     * Tests if this date is before the specified date.
     *
     * @param when
     *            a date.
     * @return <code>true</code> if and only if the instant of time represented by this <tt>Date</tt> object is strictly earlier than the
     *         instant represented by <tt>when</tt>; <code>false</code> otherwise.
     */
    public boolean before(SimpleDate when) {
        return toMillis() < when.toMillis();
    }

    /**
     * Tests if this date is after the specified date.
     *
     * @param when
     *            a date.
     * @return <code>true</code> if and only if the instant represented by this <tt>Date</tt> object is strictly later than the instant
     *         represented by <tt>when</tt>; <code>false</code> otherwise.
     */
    public boolean after(SimpleDate when) {
        return toMillis() > when.toMillis();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the year part of this date.
     *
     * @return the year, eg 1967
     */
    public int getYear() {
        return calendar.get(Calendar.YEAR);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the month part of this date.
     *
     * @return the month (1..12)
     */
    public int getMonth() {
        return calendar.get(Calendar.MONTH) + 1;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the day part of this date.
     *
     * @return the day (1..31)
     */
    public int getDay() {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the hour part of this date.
     *
     * @return the hour (0..23)
     */
    public int getHour() {
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the minute part of this date.
     *
     * @return the minute (0..59)
     */
    public int getMinute() {
        return calendar.get(Calendar.MINUTE);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the seconds part of this date.
     *
     * @return the second (0..59)
     */
    public int getSecond() {
        return calendar.get(Calendar.SECOND);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the milliseconds represented by of this date.
     *
     * @return the milliseconds, eg. 239288172312
     */
    public long getMillisecond() {
        return calendar.getTimeInMillis();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns string in the Spontech standard date format "dd.mm.yyyy".
     */
    public String toDateString() {

        if (lastToDateString == null) {

            StringBuffer sb = new StringBuffer();

            int d = getDay();
            int m = getMonth();
            int y = getYear();

            if (d < 10)
                sb.append('0');
            sb.append(d);

            sb.append('.');

            if (m < 10)
                sb.append('0');
            sb.append(m);

            sb.append('.');

            sb.append(y);

            lastToDateString = sb.toString();
        }
        return lastToDateString;
    }

    // ----------------------------------------------------------------------------------------------

    /**
     * Formats this object with the Spontech standard time format "hh:mm:ss".
     */
    public String toTimeString() {

        if (lastToTimeString == null) {

            StringBuffer sb = new StringBuffer();

            int h = getHour();
            int m = getMinute();
            int s = getSecond();

            if (h < 10)
                sb.append('0');
            sb.append(h);

            sb.append(':');

            if (m < 10)
                sb.append('0');
            sb.append(m);

            sb.append(':');

            if (s < 10)
                sb.append('0');
            sb.append(s);

            lastToTimeString = sb.toString();
        }
        return lastToTimeString;
    }

    // ----------------------------------------------------------------------------------------------

    /**
     * Formats this object with the Spontech standard format "dd.mm.yyyy hh:mm:ss".
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (lastToString == null)
            lastToString = toDateString() + " " + toTimeString();
        return lastToString;
    }

    /**
     *
     * @see Calendar#hashCode()
     */
    @Override
    public int hashCode() {

        return calendar.hashCode();
    }

    /**
     *
     * @see Calendar#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SimpleDate)
            return calendar.equals(((SimpleDate) obj).calendar);
        return false;
    }

    // ----------------------------------------------------------------------------------------------

    /**
     * Returns this date for a given locale as string.
     *
     * @param locale
     * @return this date formated for the given locale....
     */
    public String toDateString(Locale locale) {
        String ret = DateFormat.getDateInstance(DateFormat.MEDIUM, locale).format(calendar.getTime());
        return ret;
    }

    // ----------------------------------------------------------------------------------------------

    /**
     * Returns this date/time value in milliseconds.
     *
     * @return the date/time as UTC milliseconds from the epoch.
     */
    public long toMillis() {
        return calendar.getTimeInMillis();
    }

    // ----------------------------------------------------------------------------------------------

    /**
     * Check of the given date is at the same day.
     *
     * @param date
     *            to check, if null then the return is always false.
     *
     * @return true/false...
     */
    public boolean isSameDay(SimpleDate date) {
        if (date == null)
            return false;

        if (getYear() != date.getYear())
            return false;
        if (getMonth() != date.getMonth())
            return false;
        if (getDay() != date.getDay())
            return false;

        return true;
    }

}
