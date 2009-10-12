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

package org.stuntaz.libqcw.blobs.files;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.stuntaz.libqcw.QCWException;
import org.stuntaz.libqcw.blobs.workspace.WorkspaceBlob;

/**
 * This class represents a QCW file.
 * 
 * TODO: Check for commonality between this and the QCL file wrapper.
 * 
 * @author nall
 *
 */
public final class WorkspaceFileBlob
{
    private WorkspaceBlob workspace = new WorkspaceBlob();

    /**
     * Sets the workspace included by this file wrapper.
     * 
     * @param workspace the new workspace
     */
    public void setWorkspace(final WorkspaceBlob workspace)
    {
        this.workspace = workspace;
    }

    /**
     * Gets the workspace associated with this file wrapper
     * 
     * @return the current workspace
     */
    public WorkspaceBlob getWorkspace()
    {
        return this.workspace;
    }

    /**
     * Returns the size, in bytes, of this file.
     * 
     * @return the size of this file
     */
    public int getSize()
    {
        return this.workspace.getSize() + 2;
    }

    /**
     * Writes this file to the specified output stream
     * 
     * @param output the stream to which to write
     * @throws IOException if an I/O error occurs while writing
     */
    public void write(final OutputStream output)
    {
        try
        {

            // All files start with 01 00
            output.write(1);
            output.write(0);

            getWorkspace().write(output);
        }
        catch (final IOException e)
        {
            throw new QCWException("I/O error detected while writing file: "
                + e.getMessage());
        }
    }

    /**
     * Populates this object from the specified input stream
     * 
     * @param stream the stream from which to read
     * @throws IOException if an I/O error occurs while reading
     */
    public void parse(final InputStream stream)
    {
        try
        {
            final int magic = stream.read() | (stream.read() << 8);

            if (magic != 0x0001)
            {
                throw new QCWException("Unexpected magic number in header");
            }

            setWorkspace(new WorkspaceBlob());
            getWorkspace().parse(stream);
        }
        catch (final IOException e)
        {
            throw new QCWException("I/O error detected while parsing file: "
                + e.getMessage());
        }
    }
}
