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

package org.stuntaz.libqcw.blobs.barchart;

import org.stuntaz.libqcw.IBarChartVisitor;
import org.stuntaz.libqcw.blobs.UnsupportedBlob;
import org.stuntaz.libqcw.defines.QLineType;

/**
 * Represents a currently unsupported line type.
 * 
 * @author nall
 *
 */
public final class UnsupportedLineBlob
    extends UnsupportedBlob
    implements ILineBlob
{
    private final QLineType lineType;

    /**
     * Creates a new UnsupportedLineBlob with the specified line type
     * 
     * @param lineType the line type of this unsupported line
     */
    public UnsupportedLineBlob(final QLineType lineType)
    {
        this.lineType = lineType;
    }

    public QLineType getLineType()
    {
        return lineType;
    }

    public void accept(final IBarChartVisitor visitor)
    {
        visitor.visit(this);
    }
}
