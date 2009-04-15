/*
 * libqcw - A library for parsing, manipulating, and writing QCharts (TM) 
 * workspace files.
 *
 * Copyright (C) 2008 Jon Nall
 * QCharts is a registered service mark of eSignal, Inc.
 * RagingBull is a registered service mark of eSignal, Inc.
 *
 * Licensed under the Open Software License version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You should
 * have received a copy of the License along with this software; if not, you
 * may obtain a copy of the License at
 *
 * http://opensource.org/licenses/osl-2.0.php
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.stuntaz.libqcw.defines;

import org.stuntaz.libqcw.QCWException;

/**
 * Enumeration of the various line types supported in QCW files
 * @author nall
 *
 */
public enum QLineType
{
    /**
     * A basic line, which may be a segment, ray, or extended line. Check the
     * {@link QBasicLineType} to determine that information.
     */
    BasicLine(0x80C5),

    /**
     * A retracement line.
     */
    Retracement(0x80CD),

    /**
     * A projection line.
     */
    Projection(0x820F),

    /**
     * A Fibonacci Extension line
     */
    Fibonacci_Extension(0x826E),

    /**
     * A Fibonacci Time Interval line
     */
    Fibonacci_Time_Interval(0x8237),

    /**
     * A Fibonacci Circle
     */
    Fibonacci_Circles(0x8226),

    /**
     * Fibonacci Time Cycles
     */
    Fibonacci_Time_Cycles(0x826B),

    /**
     * Time Cycles
     */
    Time_Cycles(0x826D),

    /**
     * A regression line
     */
    Regression_Line(0x80EC),

    /**
     * An Andrews Pitchfork
     */
    Andrews_Pitchfork(0x8217),

    /**
     * An Andrews Pitchfork Modified Schiff
     */
    Andrews_Pitchfork_Modified_Schiff(0x821B),

    /**
     * An Andrews Pitchfork Inside
     */
    Andrews_Pitchfork_Inside(0x821F),

    /**
     * A Pitchfan
     */
    Pitchfan(0x8223),

    /**
     * A text note
     */
    Note(0x80F0);

    private int value;

    QLineType(final int value)
    {
        this.value = value;
    }

    /**
     * Returns the QCW value that represents this type
     * 
     * @return the value used for this type in QCW files
     */
    public int getValue()
    {
        return value;
    }

    /**
     * Returns the line type represented by the specified value.
     * 
     * @param value the value for which to determine line type
     * @return the line type represented by {@code value}
     */
    public static QLineType forValue(final int value)
    {
        for (final QLineType lType : values())
        {
            if (lType.getValue() == value)
            {
                return lType;
            }
        }
        throw new QCWException("Unknown Line Type: 0x"
            + Integer.toHexString(value));
    }

}
