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

/**
 * Types that describe how to resolve merge conflicts.
 * 
 * @author nall
 *
 */
public enum MergeResolutionType
{
    /**
     * Skip this merge, doing nothing.
     */
    MergeSkip,

    /**
     * Merge the symbols by using the data from both
     */
    MergeUnion,

    /**
     * Merge the symbols, using only the new symbol's data
     */
    MergeKeepNew,

    /**
     * Merge the symbols, using only the old symbol's data
     */
    MergeKeepOld;
}
