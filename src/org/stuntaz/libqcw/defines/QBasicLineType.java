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
 * Describes the sub-types of the {@link QLineType#BasicLine}
 * 
 * @author nall
 *
 */
public enum QBasicLineType
{
    /**
     * Normal line with 2 fixed endpoints
     */
    Normal(0),

    /**
     * A ray, with one fixed endpoint which extends to infinity in the other direction
     */
    Ray(1),

    /**
     * An extended line, with both ends going to infinity
     */
    Extended(2),

    /**
     * A textual note. These are handled like lines, though there is no
     * actual line drawn on the chart.
     */
    Note(4);

    private int value;

    QBasicLineType(final int value)
    {
        this.value = value;
    }

    /**
     * Gets the value for this type
     * 
     * @return the subtype value
     */
    public int getValue()
    {
        return value;
    }

    /**
     * Gets the subtype for the specified value
     * 
     * @param value the value to check
     * @return the type corresponding to value
     */
    public static QBasicLineType forValue(final int value)
    {
        for (final QBasicLineType sType : values())
        {
            if (sType.getValue() == value)
            {
                return sType;
            }
        }
        throw new QCWException("Unknown Line Subtype: 0x"
            + Integer.toHexString(value));
    }
}
