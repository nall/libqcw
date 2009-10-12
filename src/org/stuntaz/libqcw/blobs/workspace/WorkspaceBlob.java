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

package org.stuntaz.libqcw.blobs.workspace;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.stuntaz.libqcw.IWorkspaceVisitor;
import org.stuntaz.libqcw.blobs.QSection;
import org.stuntaz.libqcw.blobs.barchart.BarChartBlob;
import org.stuntaz.libqcw.blobs.barchart.BarChartSectionBlob;
import org.stuntaz.libqcw.defines.QWorkspaceSection;

/**
 * Represents a workspace blob
 * 
 * TODO: Create easier-to-use workspace section API
 * Reasoning: The various blobs that comprise the workspace section are currently
 * implemented very literally (one per actual blob). From the user's 
 * perspective, this could probably be simplified a good deal. T
 * 
 * @author nall
 *
 */
public final class WorkspaceBlob
{
    private SectionNumberBlob sectionNumber = null;
    private SectionHeaderBlob sectionHeader = null;
    private SectionContainerBlob sectionContainer = null;

    /**
     * Get the blob representing the number of sections in this workspace
     * 
     * @return the workspace section number blob
     */
    public SectionNumberBlob getSectionNumber()
    {
        return sectionNumber;
    }

    /**
     * Get the section header blob for this workspace
     * 
     * @return the workspace section header blob
     */
    public SectionHeaderBlob getSectionHeader()
    {
        return sectionHeader;
    }

    /**
     * Get the sections included in this workspace
     * 
     * @return a list of sections included in this workspace
     */
    public List<QSection> getSections()
    {
        return sectionContainer.getSections();
    }

    /**
     * Get the charts in this workspace. This is a convenience method that is
     * equivalent to iterating over the results of {@link #getSections()} and
     * returning all charts found in any {@link QWorkspaceSection#BarChart} sections.
     * 
     * @return a set of bar charts found in the workspace
     */
    public Set<BarChartBlob> getCharts()
    {
        final Set<BarChartBlob> charts = new HashSet<BarChartBlob>();

        for (final QSection s : getSections())
        {
            if (s.getSectionType() == QWorkspaceSection.BarChart)
            {
                charts.add(((BarChartSectionBlob) s).getLayoutWrapper()
                    .getChart());
            }
        }

        return charts;
    }

    /**
     * Returns the size, in bytes of this workspace blob
     * 
     * @return the size of this workspace
     */
    public int getSize()
    {
        final int totalBytes = sectionNumber.getSize()
            + sectionHeader.getSize() + sectionContainer.getSize();
        return totalBytes;

    }

    /**
     * Parse a workspace blob from the specified input stream.
     * 
     * @param stream the stream from which to read
     * @throws IOException if an error is encountered while parsing
     */
    public void parse(final InputStream stream)
        throws IOException
    {
        sectionNumber = new SectionNumberBlob();
        sectionNumber.parse(stream);

        sectionHeader = new SectionHeaderBlob(sectionNumber.getSectionCount());
        sectionHeader.parse(stream);

        sectionContainer = new SectionContainerBlob(sectionHeader);
        sectionContainer.parse(stream);
    }

    /**
     * Writes this workspace blob to the specified output stream.
     * 
     * @param output the stream to which to write
     * @throws IOException if an error is encountered while writing
     */
    public void write(final OutputStream output)
        throws IOException
    {
        sectionNumber.write(output);
        sectionHeader.write(output);
        sectionContainer.write(output);
    }

    /**
     * Accepts the specified workspace visitor.
     * 
     * @param visitor the visitor to accept
     */
    public void accept(final IWorkspaceVisitor visitor)
    {
        visitor.visit(this);
    }
}
