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
import org.stuntaz.libqcw.blobs.QByte;
import org.stuntaz.libqcw.blobs.QRecord;
import org.stuntaz.libqcw.blobs.QUtils;

/**
 * A thin wrapper containing the core bar chart data structure
 * 
 * TODO: BarChartWrapperBlob remove this layer as all that's here is the bar chart blob
 * 
 * @author nall
 *
 */
public final class BarChartWrapperBlob
    extends QRecord
{
    private BarChartBlob chart = new BarChartBlob();
    private QByte unknown1 = new QByte();

    /**
     * Sets the bar chart for this wrapper
     * 
     * @param chart the bar chart wrapped by this wrapper.
     */
    public void setLayout(final BarChartBlob chart)
    {
        this.chart = chart;
    }

    /**
     * Returns the bar chart wrapped by this wrapper
     * 
     * @return the core bar chart data structure
     */
    public BarChartBlob getChart()
    {
        return this.chart;
    }

    private void setUnknown1(final int value)
    {
        this.unknown1 = new QByte(value);
    }

    @Override
    protected int getInternalSize()
    {
    	final int extra61size = chart.isQC61() ? unknown1.getSize() : 0;    	
        // layout
    	return chart.getSize() + extra61size;
        
    }

    @Override
    public void write(final OutputStream output)
        throws IOException
    {
        super.write(output);
        chart.write(output);
        
        if(chart.isQC61())
        {
        	unknown1.write(output);
        }
    }

    public void parse(final InputStream stream)
        throws IOException
    {
        final int size = parseRecordHeader(stream);
        setLayout(new BarChartBlob());
        getChart().parse(stream);
        
        if(getChart().isQC61())
        {
        	setUnknown1(QUtils.readQByte(stream));
        }

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