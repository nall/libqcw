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

package org.stuntaz.libqcw.blobs.barchart;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.stuntaz.libqcw.IBarChartVisitor;
import org.stuntaz.libqcw.blobs.QDword;
import org.stuntaz.libqcw.blobs.QRecord;
import org.stuntaz.libqcw.blobs.QUtils;

/**
 * Represents the layout characteristics for a study. This includes the percentage
 * of the bar chart window allocated for this study.
 * 
 * @author nall
 *
 */
public final class StudyLayoutBlob
    extends QRecord
{
    private int unknown1;
    private int verticalPct;

    private void setUnknown1(final int value)
    {
        this.unknown1 = value;
    }

    /**
     * Sets the percentage of the bar chart window that should used to display
     * this study.
     * 
     * @param value the vertical percentage of the bar chart this study should use
     */
    public void setVerticalPct(final int value)
    {
        this.verticalPct = value;
    }

    /**
     * Gets the percentage of the bar chart window that this study uses. For
     * example, a value of 33 would mean this study takes up 33% of the bar
     * chart's veritical space.
     * 
     * @return the percentage of the bar chart window used to display this study
     */
    public int getVerticalPct()
    {
        return verticalPct;
    }

    @Override
    protected int getInternalSize()
    {
        return (2 * QUtils.getSize(QDword.class));
    }

    @Override
    public void write(final OutputStream output)
        throws IOException
    {
        super.write(output);
        QUtils.writeQDword(unknown1, output);
        QUtils.writeQDword(verticalPct, output);
    }

    public void parse(final InputStream stream)
        throws IOException
    {
        final int size = parseRecordHeader(stream);

        setUnknown1(QUtils.readQDword(stream));
        setVerticalPct(QUtils.readQDword(stream));

        setValid();
        assert (size == getInternalSize());
    }

    /**
     * Accept the specified bar chart visitor.
     * 
     * @param visitor the visitor to accept
     */
    public void accept(final IBarChartVisitor visitor)
    {
        visitor.visit(this);
    }
}
