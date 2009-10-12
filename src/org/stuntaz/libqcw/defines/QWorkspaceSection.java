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
 * Enumeration of supported workspace sections in QCW files
 * 
 * @author nall
 *
 */
public enum QWorkspaceSection
{
    /**
     * A bar chart section
     */
    BarChart(1),

    /**
     * A time and sales section
     */
    TimeAndSales(2),

    /**
     * A tabular bar sections
     */
    TabularBar(3),

    /**
     * A quote sheet section
     */
    QuoteSheet(4),

    // TODO: What is QWorkspaceSection 5?
    //
    /**
     * An internet browser secton
     */
    Browser(6),

    /**
     * A Level II section
     */
    LevelII(7),

    /**
     * A hot list section
     */
    HotList(8),

    /**
     * A single quote section
     */
    SingleQuote(9),

    /**
     * An Island Book section
     */
    IslandBook(10),

    //
    // TODO: What is QWorkspaceSection 11?
    // 

    /**
     * An options montage section
     */
    OptionsMontage(12),

    /**
     * A Raging Bull message board section
     */
    RagingBull(13);

    private int value;

    QWorkspaceSection(final int value)
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
     * Returns the workspace section type represented by the specified value.
     * 
     * @param value the value for which to determine workspace section type
     * @return the workspace section type represented by {@code value}
     */
    public static QWorkspaceSection forValue(final int value)
    {
        for (final QWorkspaceSection wsType : values())
        {
            if (wsType.getValue() == value)
            {
                return wsType;
            }
        }
        throw new QCWException("Unknown Workspace Section: 0x"
            + Integer.toHexString(value));
    }

}
