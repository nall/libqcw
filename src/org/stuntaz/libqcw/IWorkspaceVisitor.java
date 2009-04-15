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

import org.stuntaz.libqcw.blobs.UnsupportedBlob;
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
import org.stuntaz.libqcw.blobs.workspace.SectionContainerBlob;
import org.stuntaz.libqcw.blobs.workspace.SectionHeaderBlob;
import org.stuntaz.libqcw.blobs.workspace.SectionHeaderInfoBlob;
import org.stuntaz.libqcw.blobs.workspace.SectionNumberBlob;
import org.stuntaz.libqcw.blobs.workspace.WorkspaceBlob;

/**
 * An interface representing the Visitor pattern that visits the object
 * model for a workspace
 * 
 * @author nall
 *
 */
public interface IWorkspaceVisitor
{
    /**
     * Visit the specified workspace
     * 
     * @param workspace the workspace to visit
     */
    public void visit(final WorkspaceBlob workspace);

    /**
     * Visit the specified section number object
     * 
     * @param sectionNumber the section number object to visit
     */
    public void visit(final SectionNumberBlob sectionNumber);

    /**
     * Visit the specified section header object
     * 
     * @param sectionHeader the section header object to visit
     */
    public void visit(final SectionHeaderBlob sectionHeader);

    /**
     * Visit the specified section header info object
     * 
     * @param sectionHeader the section header info object to visit
     */
    public void visit(final SectionHeaderInfoBlob sectionHeader);

    /**
     * Visit the specified section container object
     * 
     * @param sectionContainer the section container object to visit
     */
    public void visit(final SectionContainerBlob sectionContainer);

    /**
     * Visit the an unsupported object
     * 
     * @param object the unsupported object to visit
     */
    public void visit(final UnsupportedBlob object);

    /**
     * Visit the specified browser object
     * 
     * @param browser the browser object to visit
     */
    public void visit(final BrowserBlob browser);

    /**
     * Visit the specified Hot List object
     * 
     * @param hotList the Hot List object to visit
     */
    public void visit(final HotlistBlob hotList);

    /**
     * Visit the specified Island Book object
     * 
     * @param islandBook the Island Book object to visit
     */
    public void visit(final IslandBookBlob islandBook);

    /**
     * Visit the specified Level II object
     * 
     * @param level2 the Level II object to visit
     */
    public void visit(final Level2Blob level2);

    /**
     * Visit the specified Options Montage object
     * 
     * @param montage the Options Montage object to visit
     */
    public void visit(final OptionsMontageBlob montage);

    /**
     * Visit the specified Quote Sheet object
     * 
     * @param sheet the quote sheet object to visit
     */
    public void visit(final QuotesheetBlob sheet);

    /**
     * Visit the specified Raging Bull Message Board object
     * 
     * @param bull the Raging Bull object to visit
     */
    public void visit(final RagingBullBlob bull);

    /**
     * Visit the specified Single Quote object
     * 
     * @param quote the Single Quote object to visit
     */
    public void visit(final SingleQuoteBlob quote);

    /**
     * Visit the specified Tabular Bar object
     * 
     * @param tabularBar the Tabular Bar object to visit
     */
    public void visit(final TabularBarBlob tabularBar);

    /**
     * Visit the specified Time and Sales object
     * 
     * @param timeAndSales the Time and Sales object to visit
     */
    public void visit(final TimeAndSalesBlob timeAndSales);

    /**
     * Visit the specified Bar Chart object
     * 
     * @param barChart the Bar Chart object to visit
     */
    public void visit(final BarChartSectionBlob barChart);

}
