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

package org.stuntaz.libqcw;

import org.stuntaz.libqcw.blobs.QSection;
import org.stuntaz.libqcw.blobs.UnsupportedBlob;
import org.stuntaz.libqcw.blobs.barchart.BarChartBlob;
import org.stuntaz.libqcw.blobs.barchart.BarChartSectionBlob;
import org.stuntaz.libqcw.blobs.barchart.BarChartWrapperBlob;
import org.stuntaz.libqcw.blobs.barchart.BasicLineBlob;
import org.stuntaz.libqcw.blobs.barchart.ILineBlob;
import org.stuntaz.libqcw.blobs.barchart.IStudyBlob;
import org.stuntaz.libqcw.blobs.barchart.LineHeaderBlob;
import org.stuntaz.libqcw.blobs.barchart.StudyHeaderBlob;
import org.stuntaz.libqcw.blobs.barchart.StudyLayoutBlob;
import org.stuntaz.libqcw.blobs.barchart.StudyLineGroupBlob;
import org.stuntaz.libqcw.blobs.barchart.SymbolEntryBlob;
import org.stuntaz.libqcw.blobs.barchart.UnsupportedLineBlob;
import org.stuntaz.libqcw.blobs.barchart.UnsupportedStudyBlob;
import org.stuntaz.libqcw.blobs.barchart.VolumeStudyBlob;
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
import org.stuntaz.libqcw.blobs.workspace.SectionContainerBlob;
import org.stuntaz.libqcw.blobs.workspace.SectionHeaderBlob;
import org.stuntaz.libqcw.blobs.workspace.SectionHeaderInfoBlob;
import org.stuntaz.libqcw.blobs.workspace.SectionNumberBlob;
import org.stuntaz.libqcw.blobs.workspace.WorkspaceBlob;

/**
 * Class that implements a basic IQCWVisitor. This implementation merely 
 * traverses the entire model and is meant to serve as a base class.
 * 
 * @author nall
 *
 */
public class BasicQCWVisitor
    implements IWorkspaceVisitor, IBarChartVisitor
{

    /**
     * @see org.stuntaz.libqcw.IWorkspaceVisitor#visit(org.stuntaz.libqcw.blobs.workspace.WorkspaceBlob)
     */
    public void visit(final WorkspaceBlob workspace)
    {
        workspace.getSectionNumber().accept(this);
        workspace.getSectionHeader().accept(this);
        for (final QSection section : workspace.getSections())
        {
            section.accept(this);
        }
    }

    /**
     * @see org.stuntaz.libqcw.IWorkspaceVisitor#visit(org.stuntaz.libqcw.blobs.workspace.SectionNumberBlob)
     */
    public void visit(final SectionNumberBlob sectionNumber)
    {
        // Nothing to traverse
    }

    /**
     * @see org.stuntaz.libqcw.IWorkspaceVisitor#visit(org.stuntaz.libqcw.blobs.workspace.SectionHeaderBlob)
     */
    public void visit(final SectionHeaderBlob sectionHeader)
    {
        for (final SectionHeaderInfoBlob info : sectionHeader.getSections())
        {
            info.accept(this);
        }
    }

    /**
     * @see org.stuntaz.libqcw.IWorkspaceVisitor#visit(org.stuntaz.libqcw.blobs.workspace.SectionHeaderInfoBlob)
     */
    public void visit(final SectionHeaderInfoBlob sectionHeader)
    {
        // Nothing to traverse
    }

    /**
     * @see org.stuntaz.libqcw.IWorkspaceVisitor#visit(org.stuntaz.libqcw.blobs.workspace.SectionContainerBlob)
     */
    public void visit(final SectionContainerBlob sectionContainer)
    {
        for (final QSection section : sectionContainer.getSections())
        {
            section.accept(this);
        }
    }

    /**
     * @see org.stuntaz.libqcw.IWorkspaceVisitor#visit(org.stuntaz.libqcw.blobs.UnsupportedBlob)
     */
    public void visit(final UnsupportedBlob object)
    {
        // Nothing to traverse
    }

    /**
     * @see org.stuntaz.libqcw.IWorkspaceVisitor#visit(org.stuntaz.libqcw.blobs.browser.BrowserBlob)
     */
    public void visit(final BrowserBlob browser)
    {
        // Nothing to traverse
    }

    /**
     * @see org.stuntaz.libqcw.IWorkspaceVisitor#visit(org.stuntaz.libqcw.blobs.hotlist.HotlistBlob)
     */
    public void visit(final HotlistBlob hotList)
    {
        // Nothing to traverse
    }

    /**
     * @see org.stuntaz.libqcw.IWorkspaceVisitor#visit(org.stuntaz.libqcw.blobs.islandbook.IslandBookBlob)
     */
    public void visit(final IslandBookBlob islandBook)
    {
        // Nothing to traverse
    }

    /**
     * @see org.stuntaz.libqcw.IWorkspaceVisitor#visit(org.stuntaz.libqcw.blobs.level2.Level2Blob)
     */
    public void visit(final Level2Blob level2)
    {
        // Nothing to traverse
    }

    /**
     * @see org.stuntaz.libqcw.IWorkspaceVisitor#visit(org.stuntaz.libqcw.blobs.optionsmontage.OptionsMontageBlob)
     */
    public void visit(final OptionsMontageBlob montage)
    {
        // Nothing to traverse
    }

    /**
     * @see org.stuntaz.libqcw.IWorkspaceVisitor#visit(org.stuntaz.libqcw.blobs.quotesheet.QuotesheetBlob)
     */
    public void visit(final QuotesheetBlob sheet)
    {
        // Nothing to traverse
    }

    /**
     * @see org.stuntaz.libqcw.IWorkspaceVisitor#visit(org.stuntaz.libqcw.blobs.ragingbull.RagingBullBlob)
     */
    public void visit(final RagingBullBlob bull)
    {
        // Nothing to traverse
    }

    /**
     * @see org.stuntaz.libqcw.IWorkspaceVisitor#visit(org.stuntaz.libqcw.blobs.singlequote.SingleQuoteBlob)
     */
    public void visit(final SingleQuoteBlob quote)
    {
        // Nothing to traverse
    }

    /**
     * @see org.stuntaz.libqcw.IWorkspaceVisitor#visit(org.stuntaz.libqcw.blobs.tabularbar.TabularBarBlob)
     */
    public void visit(final TabularBarBlob tabularBar)
    {
        // Nothing to traverse
    }

    /**
     * @see org.stuntaz.libqcw.IWorkspaceVisitor#visit(org.stuntaz.libqcw.blobs.timeandsales.TimeAndSalesBlob)
     */
    public void visit(final TimeAndSalesBlob timeAndSales)
    {
        // Nothing to traverse
    }

    /**
     * @see IBarChartVisitor#visit(BarChartBlob)
     */
    public void visit(final BarChartBlob barChart)
    {
        assert (barChart.getStudyLayouts().size() == barChart.getStudies()
            .size());
        assert (barChart.getStudyLayouts().size() == barChart.getStudyLines()
            .size());

        for (int i = 0; i < barChart.getStudies().size(); ++i)
        {
            barChart.getStudyLayouts().get(i).accept(this);
            barChart.getStudies().get(i).accept(this);
            barChart.getStudyLines().get(i).accept(this);
        }
    }

    /**
     * @see org.stuntaz.libqcw.IBarChartVisitor#visit(org.stuntaz.libqcw.blobs.barchart.BarChartWrapperBlob)
     */
    public void visit(final BarChartWrapperBlob layoutWrapper)
    {
        layoutWrapper.getChart().accept(this);
    }

    /**
     * @see IWorkspaceVisitor#visit(BarChartSectionBlob)
     */
    public void visit(final BarChartSectionBlob layoutSection)
    {
        layoutSection.getLayoutWrapper().accept(this);
    }

    /**
     * @see org.stuntaz.libqcw.IBarChartVisitor#visit(org.stuntaz.libqcw.blobs.barchart.StudyLayoutBlob)
     */
    public void visit(final StudyLayoutBlob studyLayout)
    {
        // Nothing to traverse
    }

    /**
     * @see org.stuntaz.libqcw.IBarChartVisitor#visit(org.stuntaz.libqcw.blobs.barchart.StudyHeaderBlob)
     */
    public void visit(final StudyHeaderBlob studyHeader)
    {
        for (final IStudyBlob study : studyHeader.getStudies())
        {
            study.accept(this);
        }
    }

    /**
     * @see org.stuntaz.libqcw.IBarChartVisitor#visit(org.stuntaz.libqcw.blobs.barchart.VolumeStudyBlob)
     */
    public void visit(final VolumeStudyBlob volumeStudy)
    {
        // Nothing to traverse
    }

    /**
     * @see org.stuntaz.libqcw.IBarChartVisitor#visit(org.stuntaz.libqcw.blobs.barchart.UnsupportedStudyBlob)
     */
    public void visit(final UnsupportedStudyBlob study)
    {
        // Nothing to traverse
    }

    /**
     * @see org.stuntaz.libqcw.IBarChartVisitor#visit(org.stuntaz.libqcw.blobs.barchart.StudyLineGroupBlob)
     */
    public void visit(final StudyLineGroupBlob linesGroup)
    {
        for (final SymbolEntryBlob symbol : linesGroup.getSymbols())
        {
            symbol.accept(this);
        }
    }

    /**
     * @see org.stuntaz.libqcw.IBarChartVisitor#visit(org.stuntaz.libqcw.blobs.barchart.SymbolEntryBlob)
     */
    public void visit(final SymbolEntryBlob symbol)
    {
        for (final ILineBlob line : symbol.getLineHeaders())
        {
            line.accept(this);
        }
    }

    /**
     * @see org.stuntaz.libqcw.IBarChartVisitor#visit(org.stuntaz.libqcw.blobs.barchart.LineHeaderBlob)
     */
    public void visit(final LineHeaderBlob header)
    {
        // Nothing to traverse
    }

    /**
     * @see org.stuntaz.libqcw.IBarChartVisitor#visit(org.stuntaz.libqcw.blobs.barchart.BasicLineBlob)
     */
    public void visit(final BasicLineBlob line)
    {
        // Nothing to traverse
    }

    /**
     * @see org.stuntaz.libqcw.IBarChartVisitor#visit(org.stuntaz.libqcw.blobs.barchart.UnsupportedLineBlob)
     */
    public void visit(final UnsupportedLineBlob line)
    {
        // Nothing to traverse
    }

}
