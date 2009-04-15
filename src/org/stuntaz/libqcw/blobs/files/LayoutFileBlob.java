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

package org.stuntaz.libqcw.blobs.files;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.stuntaz.libqcw.QCWException;
import org.stuntaz.libqcw.blobs.barchart.BarChartBlob;
import org.stuntaz.libqcw.blobs.barchart.BarChartWrapperBlob;

/**
 * This class represents a QCL file. 
 * 
 * TODO: This format probably is a generic wrapper format. Look into this.
 * 
 * @author nall
 *
 */
public final class LayoutFileBlob
{
    private BarChartWrapperBlob wrapper = new BarChartWrapperBlob();

    /**
     * Sets the Bar Chart for this QCL file.
     * 
     * @param layout the BarChart to be used in this file
     */
    public void setBarChart(final BarChartBlob layout)
    {
        this.wrapper.setLayout(layout);
    }

    /**
     * Gets the BarChart for this QCL file.
     * 
     * @return the BarChart defined in this file
     */
    public BarChartBlob getBarChart()
    {
        return this.wrapper.getChart();
    }

    /**
     * Writes this file to the specified output stream
     * 
     * @param output the stream to which to write
     * @throws IOException if an I/O error occurs while writing
     */
    public void write(final OutputStream output)
        throws IOException
    {
        // All files start with 01 00
        output.write(1);
        output.write(0);

        wrapper.write(output);
    }

    /**
     * Populates this object from the specified input stream
     * 
     * @param stream the stream from which to read
     * @throws IOException if an I/O error occurs while reading
     */
    public void parse(final InputStream stream)
        throws IOException
    {
        final int magic = stream.read() | (stream.read() << 8);

        if (magic != 0x0001)
        {
            throw new QCWException("Unexpected magic number in header");
        }

        wrapper = new BarChartWrapperBlob();
        wrapper.parse(stream);
    }
}
