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

package org.stuntaz.libqcw.blobs.barchart;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.stuntaz.libqcw.IWorkspaceVisitor;
import org.stuntaz.libqcw.blobs.QSection;
import org.stuntaz.libqcw.blobs.UnsupportedBlob;
import org.stuntaz.libqcw.defines.QWorkspaceSection;

/**
 * Represents a bar chart section. This is a thin wrapper around a
 * {@link BarChartWrapperBlob}.
 * 
 * TODO: BarChartSectionBlob: determine if unknown1 is required and remove this layer if not
 *  
 * @author nall
 *
 */
public final class BarChartSectionBlob
    extends QSection
{
    UnsupportedBlob unknown1 = new UnsupportedBlob();
    BarChartWrapperBlob wrapper = new BarChartWrapperBlob();

    @Override
    protected int getInternalSize()
    {
        return unknown1.getSize() + wrapper.getSize();
    }

    @Override
    public QWorkspaceSection getSectionType()
    {
        return QWorkspaceSection.BarChart;
    }

    /**
     * Get the bar chart layout wrapper for this bar chart section
     * 
     * @return the bar chart wrapper for this section
     */
    public BarChartWrapperBlob getLayoutWrapper()
    {
        return wrapper;
    }

    public void parse(final InputStream stream)
        throws IOException
    {
        unknown1 = new UnsupportedBlob();
        unknown1.parse(stream);

        wrapper = new BarChartWrapperBlob();
        wrapper.parse(stream);

        setValid();
    }

    @Override
    public void write(final OutputStream output)
        throws IOException
    {
        unknown1.write(output);

        wrapper.write(output);
    }

    @Override
    public void accept(final IWorkspaceVisitor visitor)
    {
        visitor.visit(this);
    }
}
