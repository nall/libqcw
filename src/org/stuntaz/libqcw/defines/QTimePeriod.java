/*
 * libqcw - A library for parsing, manipulating, and writing QCharts (TM) 
 * workspace files.
 *
 * Copyright (C) 2008-2009 Jon Nall
 * QCharts is a registered service mark of eSignal, Inc.
 * RagingBull is a registered service mark of eSignal, Inc.
 *
 * Licensed under the Open Software License version 3.0 (the "License"); you
 * may not use this file except in compliance with the License. You should
 * have received a copy of the License along with this software; if not, you
 * may obtain a copy of the License at
 *
 * http://opensource.org/licenses/osl-3.0.php
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.stuntaz.libqcw.defines;

import org.stuntaz.libqcw.QCWException;

/**
 * Represents the time frame used by a bar chart.
 * @author nall
 *
 */
public final class QTimePeriod
    implements Comparable<QTimePeriod>
{
    private final boolean allSessions;
    private final int minutes;
    private final QTimePeriodType timePeriod;

    private QTimePeriod(
        final QTimePeriodType tp,
        final int minutes,
        final boolean allSessions)
    {
        this.timePeriod = tp;
        this.minutes = minutes;
        this.allSessions = allSessions;

        if (allSessions)
        {
            assert (tp == QTimePeriodType.Minutely || tp == QTimePeriodType.Tick);
        }
    }

    /**
     * Returns the time frame represented by the specified value.
     * 
     * @param value the value for which to determine time frame
     * @return the time frame represented by {@code value}
     */
    public static QTimePeriod forValue(final String value)
    {
        final int[] minuteValue = { -1 };
        final boolean[] allSessions = { false };
        final QTimePeriodType tp = QTimePeriodType.forValue(value, minuteValue,
            allSessions);
        return new QTimePeriod(tp, minuteValue[0], allSessions[0]);
    }

    /**
     * Returns the type of time period represented by this object.
     * 
     * @return the time period type
     */
    public QTimePeriodType getTimePeriodType()
    {
        return timePeriod;
    }

    /**
     * Returns whether this time period is marked as "All Sessions"
     * 
     * @return true for "All Sessions" time periods, false otherwise
     */
    public boolean isAllSessions()
    {
        return allSessions;
    }

    /**
     * Returns the time period in minutes of this time period. This method should
     * only by called if {@link #getTimePeriodType()} is {@link QTimePeriodType#Minutely}.
     * 
     * @return the period of time time frame in minutes
     * @throws QCWException if called on a non-minutely chart
     */
    public int getMinutelyValue()
    {
        if (timePeriod != QTimePeriodType.Minutely)
        {
            throw new QCWException(
                "Cannot get minutely value for Time Period: " + timePeriod);
        }

        return minutes;
    }

    @Override
    public int hashCode()
    {
        return timePeriod.hashCode() ^ minutes;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        else if (!(o instanceof QTimePeriod))
        {
            return false;
        }
        else
        {
            final QTimePeriod qtp = (QTimePeriod) o;
            return this.timePeriod == qtp.timePeriod
                && this.minutes == qtp.minutes
                && this.allSessions == qtp.allSessions;
        }
    }

    @Override
    public String toString()
    {
        String s = timePeriod.toString();
        if (timePeriod == QTimePeriodType.Minutely)
        {
            s += "[" + minutes + "]";
        }
        if (allSessions)
        {
            s += "[All Sessions]";
        }
        return s;
    }

    /**
     * An enumeration of time period types in QCW files.
     * 
     * @author nall
     *
     */
    public enum QTimePeriodType
    {
        /**
         * Yearly time period
         */
        Yearly,

        /**
         * Quarterly time period
         */
        Quarterly,

        /**
         * Monthly time period
         */
        Monthly,

        /**
         * Weekly time period
         */
        Weekly,

        /**
         * Daily time period
         */
        Daily,

        /**
         * Time period specified in ticks
         */
        Tick,

        /**
         * Time period specified in minutes
         */
        Minutely;

        /**
         * Returns the time period type represented by the specified value.
         * 
         * @param value the value for which to determine time period type
         * @param minuteValue a holder variable for the minute value. This is filled in with
         *      a valid value when the type is {@link #Tick} or {@link #Minutely}.
         * @param allSessions a holder variable for all sessions state. This is filled in
         *      with true or false as appropriate and is always valid upon return.
         *      
         * @return the time period type represented by {@code value}
         */
        public static QTimePeriodType forValue(
            String value,
            final int[] minuteValue,
            final boolean[] allSessions)
        {
            allSessions[0] = false;

            final String[] pieces = value.split(":");
            if (pieces.length > 1)
            {
                value = pieces[0];
                assert (pieces[1].equals("255"));
                allSessions[0] = true;
            }

            value = value.toLowerCase();
            if (value.equals("y"))
            {
                return Yearly;
            }
            else if (value.equals("q"))
            {
                return Quarterly;
            }
            else if (value.equals("m"))
            {
                return Monthly;
            }
            else if (value.equals("w"))
            {
                return Weekly;
            }
            else if (value.equals("d"))
            {
                return Daily;
            }
            else if (value.equals("0") || value.equals("t"))
            {
                // Values are never stored as "T", but it's allowed in the
                // expression bar, so we'll parse it.
                minuteValue[0] = 0;
                return Tick;
            }
            else
            {
                try
                {
                    minuteValue[0] = Integer.parseInt(value);
                    return Minutely;
                }
                catch (final NumberFormatException e)
                {
                    throw new QCWException("Unknown Time Period: " + value);
                }
            }
        }
    }

    public int compareTo(final QTimePeriod o)
    {
        int value1 = 0;
        int value2 = 0;
        switch (this.timePeriod)
        {
        case Tick:
            value1 = 0;
            break;
        case Minutely:
            value1 = this.getMinutelyValue();
            break;
        case Daily:
            value1 = 1440;
            break;
        case Weekly:
            value1 = 10080;
            break;
        case Monthly:
            value1 = 302400;
            break;
        case Quarterly:
            value1 = 907200;
            break;
        case Yearly:
            value1 = 3628800;
            break;
        }

        switch (o.timePeriod)
        {
        case Tick:
            value2 = 0;
            break;
        case Minutely:
            value2 = o.getMinutelyValue();
            break;
        case Daily:
            value2 = 1440;
            break;
        case Weekly:
            value2 = 10080;
            break;
        case Monthly:
            value2 = 302400;
            break;
        case Quarterly:
            value2 = 907200;
            break;
        case Yearly:
            value2 = 3628800;
            break;
        }

        return value1 - value2;
    }
}
