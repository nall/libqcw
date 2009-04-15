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

package org.stuntaz.libqcw.blobs.workspace;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.stuntaz.libqcw.IWorkspaceVisitor;
import org.stuntaz.libqcw.blobs.QRecord;
import org.stuntaz.libqcw.blobs.QSection;
import org.stuntaz.libqcw.blobs.barchart.BarChartSectionBlob;
import org.stuntaz.libqcw.blobs.browser.BrowserBlob;
import org.stuntaz.libqcw.blobs.hotlist.HotlistBlob;
import org.stuntaz.libqcw.blobs.islandbook.IslandBookBlob;
import org.stuntaz.libqcw.blobs.level2.Level2Blob;
import org.stuntaz.libqcw.blobs.optionsmontage.OptionsMontageBlob;
import org.stuntaz.libqcw.blobs.quotesheet.QuotesheetBlob;
import org.stuntaz.libqcw.blobs.ragingbull.RagingBullBlob;
import org.stuntaz.libqcw.blobs.singlequote.SingleQuoteBlob;
import org.stuntaz.libqcw.blobs.tabularbar.TabularBarBlob;
import org.stuntaz.libqcw.blobs.timeandsales.TimeAndSalesBlob;

/**
 * Represents a section container blob. This object is a thin wrapper around
 * the actual {@link QSection} objects.
 * 
 * @author nall
 *
 */
public final class SectionContainerBlob
    extends QRecord
{
    private final SectionHeaderBlob headers;
    private final List<QSection> sections = new ArrayList<QSection>();

    /**
     * Creates a new SectionContainerBlob with the specified headers.
     * 
     * @param headers the section header blob to use in initialization
     */
    public SectionContainerBlob(final SectionHeaderBlob headers)
    {
        this.headers = headers;
    }

    /**
     * Returns the {@link QSection} instances contained in this blob.
     * 
     * @return a list of {@link QSection} instances.
     */
    public List<QSection> getSections()
    {
        return sections;
    }

    @Override
    protected int getInternalSize()
    {
        int totalBytes = 0;
        for (final QSection section : sections)
        {
            totalBytes += section.getSize();
        }

        return totalBytes;
    }

    public void parse(final InputStream stream)
        throws IOException
    {
        final int size = parseRecordHeader(stream);

        final List<SectionHeaderInfoBlob> infos = headers.getSections();
        for (int i = 0; i < headers.getSectionCount(); ++i)
        {
            QSection section = null;
            switch (infos.get(i).getSectionType())
            {
            case BarChart:
                section = new BarChartSectionBlob();
                break;
            case TimeAndSales:
                section = new TimeAndSalesBlob();
                break;
            case TabularBar:
                section = new TabularBarBlob();
                break;
            case QuoteSheet:
                section = new QuotesheetBlob();
                break;
            case Browser:
                section = new BrowserBlob();
                break;
            case LevelII:
                section = new Level2Blob();
                break;
            case HotList:
                section = new HotlistBlob();
                break;
            case SingleQuote:
                section = new SingleQuoteBlob();
                break;
            case IslandBook:
                section = new IslandBookBlob();
                break;
            case OptionsMontage:
                section = new OptionsMontageBlob();
                break;
            case RagingBull:
                section = new RagingBullBlob();
                break;
            }

            section.parse(stream);
            sections.add(section);
        }

        setValid();
        assert (size == getInternalSize());
    }

    @Override
    public void write(final OutputStream output)
        throws IOException
    {
        super.write(output);

        for (final QSection section : sections)
        {
            section.write(output);
        }
    }

    /**
     * Accepts a workspace visitor.
     * 
     * @param visitor the visitor to accept
     */
    public void accept(final IWorkspaceVisitor visitor)
    {
        visitor.visit(this);
    }
}
