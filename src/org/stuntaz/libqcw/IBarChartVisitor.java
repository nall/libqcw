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

package org.stuntaz.libqcw;

import org.stuntaz.libqcw.blobs.barchart.BarChartBlob;
import org.stuntaz.libqcw.blobs.barchart.BarChartWrapperBlob;
import org.stuntaz.libqcw.blobs.barchart.BasicLineBlob;
import org.stuntaz.libqcw.blobs.barchart.LineHeaderBlob;
import org.stuntaz.libqcw.blobs.barchart.StudyHeaderBlob;
import org.stuntaz.libqcw.blobs.barchart.StudyLayoutBlob;
import org.stuntaz.libqcw.blobs.barchart.StudyLineGroupBlob;
import org.stuntaz.libqcw.blobs.barchart.SymbolEntryBlob;
import org.stuntaz.libqcw.blobs.barchart.UnsupportedLineBlob;
import org.stuntaz.libqcw.blobs.barchart.UnsupportedStudyBlob;
import org.stuntaz.libqcw.blobs.barchart.VolumeStudyBlob;

/**
 * Interface for visiting a {@link BarChartBlob} object.
 * 
 * @author nall
 *
 */
public interface IBarChartVisitor
{
    /**
     * Visit the specified Bar Chart object
     * 
     * @param section the Bar Chart object to visit
     */
    public void visit(final BarChartBlob section);

    /**
     * Visit the specified Bar Chart wrapper object
     * 
     * @param wrapper the Bar Chart wrapper object to visit
     */
    public void visit(final BarChartWrapperBlob wrapper);

    /**
     * Visit the specified study layout object
     * 
     * @param studyLayout the study layout object to visit
     */
    public void visit(final StudyLayoutBlob studyLayout);

    /**
     * Visit the specified study header object
     * 
     * @param studyHeader the study header object to visit
     */
    public void visit(final StudyHeaderBlob studyHeader);

    /**
     * Visit the specified volume study object
     * 
     * @param volumeStudy the volume study object to visit
     */
    public void visit(final VolumeStudyBlob volumeStudy);

    /**
     * Visit the specified unsupported study object
     * 
     * @param study the unsupported study object to visit
     */
    public void visit(final UnsupportedStudyBlob study);

    /**
     * Visit the specified study line group object
     * 
     * @param lineGroup the study line group object to visit
     */
    public void visit(final StudyLineGroupBlob lineGroup);

    /**
     * Visit the specified symbol entry object
     * 
     * @param symbol the symbol entry object to visit
     */
    public void visit(final SymbolEntryBlob symbol);

    /**
     * Visit the specified line header object
     * 
     * @param header the line header object to visit
     */
    public void visit(final LineHeaderBlob header);

    /**
     * Visit the specified basic line object
     * 
     * @param line the basic line object to visit
     */
    public void visit(final BasicLineBlob line);

    /**
     * Visit the specified unsupported line object
     * 
     * @param line the unsupported line object to visit
     */
    public void visit(final UnsupportedLineBlob line);

}
