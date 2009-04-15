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

/**
 * Class representing an 8 byte value as it exists in the file format. There
 * is an additional a header byte of type information.
 * 
 * @author nall
 *
 */
public final class QQword
    extends QPrimitive
{
    private long value = 0;

    /**
     * Creates a new, invalid QQword
     */
    public QQword()
    {
        // Do nothing
    }

    /**
     * Creates a new, valid QQword with the specified integer value
     * 
     * @param value the integer value for this QQword
     */
    public QQword(final long value)
    {
        setValue(value);
    }

    /**
     * Creates a new, valid QQword with the specified double value
     * 
     * @param value the double value for this QQword
     */
    public QQword(final double value)
    {
        this.value = Double.doubleToLongBits(value);
        setValid();
    }

    /**
     * Returns the value of this QQword object as a long.
     * 
     * @return this QQword value
     */
    public long getValue()
    {
        assert (isValid());
        return value;
    }

    /**
     * Returns the value of this QQword object as a double.
     * 
     * @return this QQword value
     */
    public double getDoubleValue()
    {
        assert (isValid());
        return Double.longBitsToDouble(value);
    }

    /**
     * Sets the value of this QQword.
     * 
     * @param value the value for this QQword
     */
    public void setValue(final long value)
    {
        this.value = value;
        setValid();
    }

    @Override
    public int getInternalSize()
    {
        return 8;
    }

    @Override
    public void write(final OutputStream output)
        throws IOException
    {
        if (!isValid()) return;

        QUtils.writeQQword(getValue(), output);
    }

    @Override
    protected char getHeaderType()
    {
        return QDefines.QQWORD_TYPE;
    }

    public void parse(final InputStream stream)
        throws IOException
    {
        setValue(QUtils.readQQword(stream));
    }
}