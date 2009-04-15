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
 * Enumeration of chart types
 * @author nall
 *
 */
public enum QChartType
{
    /**
     * Bar Chart
     */
    Bar(1),

    /**
     * Line Chart
     */
    Line(2),

    /**
     * Histogram
     */
    Histogram(3),

    // The value 4 seems to result in a line chart as well

    /**
     * Candle
     */
    Candle(5);

    QChartType(final int value)
    {
        assert ((value & 0xFFFF0000) == 0);
        this.value = (short) value;
    }

    /**
     * Gets the value for this chart type as defined in the file format
     * 
     * @return the QCW value for this type
     */
    public short getValue()
    {
        return value;
    }

    private final short value;

    /**
     * Returns the chart type for the specified value
     * 
     * @param value the value of the chart type
     * @return the chart type corresponding to value
     */
    public static QChartType forValue(final int value)
    {
        for (final QChartType cType : values())
        {
            if (cType.getValue() == value)
            {
                return cType;
            }
        }
        throw new QCWException("Unknown Chart Type: 0x"
            + Integer.toHexString(value));
    }

}
