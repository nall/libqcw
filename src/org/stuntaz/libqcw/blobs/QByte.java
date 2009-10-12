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
 * Class representing a 1 byte value as it exists in the file format. There
 * is an additional byte of header type information.
 * 
 * @author nall
 *
 */
public final class QByte
    extends QPrimitive
{
    private int value = 0;

    /**
     * Creates a new, invalid QByte
     */
    public QByte()
    {
        // Do nothing
    }

    /**
     * Creates a new, valid QByte with the specified value. It should be a value
     * between 0 and 255.
     * 
     * @param value the value for this QByte
     */
    public QByte(final int value)
    {
        setValue(value);
    }

    /**
     * Returns the value of this QByte object. It should be a value between
     * 0 and 255.
     * 
     * @return this QByte's value
     */
    public int getValue()
    {
        assert (isValid());
        return value;
    }

    /**
     * Sets the value of this QByte.
     * 
     * @param value the value for this QByte, which should be between 0 to 255
     */
    public void setValue(final int value)
    {
        assert ((value & 0xFFFFFF00) == 0);

        this.value = value;
        setValid();
    }

    @Override
    public int getInternalSize()
    {
        return 1;
    }

    @Override
    public void write(final OutputStream output)
        throws IOException
    {
        if (!isValid()) return;

        QUtils.writeQByte(getValue(), output);
    }

    @Override
    protected char getHeaderType()
    {
        return QDefines.QBYTE_TYPE;
    }

    public void parse(final InputStream stream)
        throws IOException
    {
        setValue(QUtils.readQByte(stream));
    }
}
