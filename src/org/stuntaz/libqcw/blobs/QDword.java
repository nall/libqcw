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
 * Class representing a 4 byte value as it exists in the file format. There
 * is an additional a header byte of type information.
 * 
 * @author nall
 *
 */
public final class QDword
    extends QPrimitive
{
    private int value = 0;

    /**
     * Creates a new, invalid QDword
     */
    public QDword()
    {
        // Do nothing
    }

    /**
     * Creates a new, valid QDword with the specified value
     * 
     * @param value the value for this QDword
     */
    public QDword(final int value)
    {
        setValue(value);
    }

    /**
     * Returns the value of this QDword object. Note that an int is returned,
     * although this may be an unsigned quantity.
     * 
     * TODO: change this to long and assert appropriately.
     * 
     * @return this QDword value
     */
    public int getValue()
    {
        assert (isValid());
        return value;
    }

    /**
     * Sets the value of this QDWord.
     * 
     * TODO: Change this to long and assert appropriately
     * 
     * @param value the value for this QByte, which should be between 0 and 255
     */
    public void setValue(final int value)
    {
        this.value = value;
        setValid();
    }

    @Override
    public int getInternalSize()
    {
        return 4;
    }

    @Override
    public void write(final OutputStream output)
        throws IOException
    {
        if (!isValid()) return;

        QUtils.writeQDword(getValue(), output);
    }

    @Override
    protected char getHeaderType()
    {
        return QDefines.QDWORD_TYPE;
    }

    public void parse(final InputStream stream)
        throws IOException
    {
        setValue(QUtils.readQDword(stream));
    }

}