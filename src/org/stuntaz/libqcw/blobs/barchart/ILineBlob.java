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
import java.io.OutputStream;

import org.stuntaz.libqcw.IBarChartVisitor;
import org.stuntaz.libqcw.blobs.QRecord;
import org.stuntaz.libqcw.defines.QLineType;

/**
 * Interface to be implemented by all line types.
 * 
 * @author nall
 *
 */
public interface ILineBlob
{
    /**
     * Returns the type of line.
     * 
     * @return the line's type
     */
    public QLineType getLineType();

    /**
     * @see QRecord#getSize()
     * 
     * @return the QCW/QCL size required for this line in bytes
     */
    public int getSize();

    /**
     * @see QRecord#write(OutputStream)
     * 
     * @param output the output stream to which to write
     * @throws IOException if there is an error while writing
     */
    public void write(final OutputStream output)
        throws IOException;

    /**
     * Accept the specified bar chart visitor.
     * 
     * @param visitor the visitor to accept
     */
    public void accept(IBarChartVisitor visitor);
}
