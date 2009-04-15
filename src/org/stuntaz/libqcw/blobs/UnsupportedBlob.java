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
import java.io.InputStream;
import java.io.OutputStream;

import org.stuntaz.libqcw.IWorkspaceVisitor;

/**
 * This class represents an unsupported object type. We know its length, but
 * that's about it.
 * 
 * @author nall
 *
 */
public class UnsupportedBlob
    extends QRecord
{
    private byte[] value = new byte[0];

    /**
     * Instantiates a new, invalid unsupported object.
     */
    public UnsupportedBlob()
    {
        // nothing
    }

    /**
     * Instantiates a new, valid unsupported object consisting of the specified
     * bytes.
     * 
     * @param value the value with which to initialize this object
     */
    public UnsupportedBlob(final byte[] value)
    {
        setValue(value);
    }

    /**
     * Sets the content of this object.
     * 
     * @param value the bytes that should comprise this object
     */
    public final void setValue(final byte[] value)
    {
        this.value = value;
        setValid();
    }

    /**
     * Returns the bytes associated with this unsupported object.
     * 
     * @return a byte array of this object's contents
     */
    public final byte[] getValue()
    {
        assert (isValid());
        return value;
    }

    @Override
    public final int getInternalSize()
    {
        return value.length;
    }

    @Override
    public final void write(final OutputStream output)
        throws IOException
    {
        if (!isValid()) return;

        super.write(output);
        output.write(value);
    }

    public final void parse(final InputStream stream)
        throws IOException
    {
        final int size = parseRecordHeader(stream);

        final byte[] value = new byte[size];

        final int readSize = stream.read(value, 0, size);
        assert (readSize == size);

        setValue(value);

        assert (size == getInternalSize());
    }

    @Override
    public String toString()
    {
        String s = "UnsupportedBlob valid: " + isValid();
        if (isValid())
        {
            s += "; [length = 0x" + Integer.toHexString(value.length) + "]";
        }
        return s;
    }

    /**
     * Accepts the specified visitor in accordance with the visitor pattern.
     * 
     * @param visitor the visitor to accept
     */
    public void accept(final IWorkspaceVisitor visitor)
    {
        visitor.visit(this);
    }

}
