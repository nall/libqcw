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

import org.stuntaz.libqcw.IBarChartVisitor;
import org.stuntaz.libqcw.blobs.UnsupportedBlob;
import org.stuntaz.libqcw.defines.QStudyType;

/**
 * Represents a currently unsupported study type.
 * 
 * @author nall
 *
 */
public class UnsupportedStudyBlob
    extends UnsupportedBlob
    implements IStudyBlob
{
    private final QStudyType studyType;

    /**
     * Creates a new UnsupportedStudyBlob with the specified study type
     * 
     * @param studyType the study type of this unsupported line
     */
    public UnsupportedStudyBlob(final QStudyType studyType)
    {
        this.studyType = studyType;
    }

    public QStudyType getStudyType()
    {
        return studyType;
    }

    public int getSubstudiesSize()
    {
        return 0;
    }

    public void accept(final IBarChartVisitor visitor)
    {
        visitor.visit(this);
    }
}
