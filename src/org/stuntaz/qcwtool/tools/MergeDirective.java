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

package org.stuntaz.qcwtool.tools;

import java.util.HashSet;
import java.util.Set;

import org.stuntaz.libqcw.blobs.barchart.BarChartBlob;
import org.stuntaz.libqcw.blobs.files.WorkspaceFileBlob;
import org.stuntaz.libqcw.defines.QStudyType;

/**
 * Represents instructions for merging a single bar chart.
 * 
 * @author nall
 *
 */
public class MergeDirective
{
    /**
     * The source bar chart
     */
    public BarChartBlob source = null;

    /**
     * The target bar chart
     */
    public BarChartBlob target = null;

    /**
     * The set of lower studies to be merged
     */
    public Set<QStudyType> studies = new HashSet<QStudyType>();

    /**
     * Specifies whether studies in the price window should be merged
     */
    public boolean priceEnabled = false;

    /**
     * The target workspace
     */
    public WorkspaceFileBlob targetWorkspace;
}