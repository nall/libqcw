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

package org.stuntaz.libqcw.blobs;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This class represents a base class for all primitive types as they exist
 * in the file format.
 *  
 * @author nall
 *
 */
abstract class QPrimitive
    implements IBlob
{
    private boolean valid = false;

    protected void setValid()
    {
        valid = true;
    }

    protected boolean isValid()
    {
        return valid;
    }

    protected abstract char getHeaderType();

    /**
     * Returns the size of this QPrimitive without header information
     */
    protected abstract int getInternalSize();

    /**
     * Returns the number of bytes this object requires in a QCW/QCL format
     * 
     * @return the size required for this object in bytes.
     */
    public int getSize()
    {
        // Basic QPrimitives have their size + 1 byte of type
        return valid ? getInternalSize() + 1 : 0;
    }

    public void write(final OutputStream output)
        throws IOException
    {
        output.write(getHeaderType());
    }
}
