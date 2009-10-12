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

package org.stuntaz.libqcw.blobs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class represents a generic object as parsed in QCW files. These objects
 * are an array of bytes whose size is known.
 * 
 * http://en.wikipedia.org/wiki/Binary_blob
 * 
 * @author nall
 *
 */
public interface IBlob
{
    /**
     * Gets the size of this object in bytes. This includes any header and size
     * bytes.
     * 
     * @return the total number of bytes this object consumes in its file format
     */
    public abstract int getSize();

    /**
     * Parse this object from the given InputStream.
     * 
     * @param stream the stream from which to read
     * @throws IOException if an I/O error occurs while reading
     */
    public abstract void parse(final InputStream stream)
        throws IOException;

    /**
     * Writes this object to the given OutputStream.
     * 
     * @param output the stream to which to write
     * @throws IOException if an I/O error occurs while writing
     */
    public abstract void write(final OutputStream output)
        throws IOException;
}