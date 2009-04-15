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
 * Class representing a 2 byte value as it exists in the file format. There
 * is an additional a header byte of type information.
 * 
 * @author nall
 *
 */
public final class QWord
    extends QPrimitive
{
    private int value = 0;

    /**
     * Creates a new, invalid QWord
     */
    public QWord()
    {
        // Do nothing
    }

    /**
     * Creates a new, valid QWord with the specified value, which should be
     * between 0 and 65,536
     * 
     * @param value the value for this QWord
     */
    public QWord(final int value)
    {
        setValue(value);
    }

    /**
     * Gets the value of this QWord which will be between 0 and 65,536
     * 
     * @return the value of this QWord
     */
    public int getValue()
    {
        assert (isValid());
        return value;
    }

    /**
     * Sets the value of this QWord. <code>value</code> should be between 0
     * and 65,536.
     * 
     * @param value the value to which to set this QWord
     */
    public void setValue(final int value)
    {
        assert ((value & 0xFFFF0000) == 0);
        this.value = value;
        setValid();
    }

    @Override
    public int getInternalSize()
    {
        return 2;
    }

    @Override
    public void write(final OutputStream output)
        throws IOException
    {
        if (!isValid()) return;

        QUtils.writeQWord(getValue(), output);
    }

    @Override
    protected char getHeaderType()
    {
        return QDefines.QWORD_TYPE;
    }

    public void parse(final InputStream stream)
        throws IOException
    {
        setValue(QUtils.readQWord(stream));
    }
}
