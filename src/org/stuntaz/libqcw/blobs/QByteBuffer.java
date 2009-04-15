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

import org.stuntaz.libqcw.QCWException;

/**
 * Class representing a byte buffer as it exists in the file format. There
 * is an additional byte for header type information and a length.
 * 
 * @author nall
 *
 */
public final class QByteBuffer
    extends QPrimitive
{
    private byte[] value = null;

    /**
     * Creates a new, invalid QByteBuffer
     */
    public QByteBuffer()
    {
        // Do nothing
    }

    /**
     * Creates a new, valid QByteBuffer with the specified value
     * 
     * @param value the value for this QByteBuffer
     */
    public QByteBuffer(final byte[] value)
    {
        setValue(value);
    }

    /**
     * Returns the value of this QByteBuffer object. 
     * 
     * @return this QByteBuffer's value
     */
    public byte[] getValue()
    {
        assert (isValid());
        if (value == null) return value;

        final byte[] copy = new byte[value.length];
        System.arraycopy(this.value, 0, copy, 0, this.value.length);
        return copy;
    }

    /**
     * Sets the value of this QByteBuffer.
     * 
     * @param value the value for this QByteBuffer
     */
    public void setValue(final byte[] value)
    {
        if (value == null)
        {
            this.value = null;
        }
        else
        {
            this.value = new byte[value.length];
            System.arraycopy(value, 0, this.value, 0, value.length);
        }
        setValid();
    }

    @Override
    public int getInternalSize()
    {
        return getLengthByteCount() + value.length;
    }

    int getLengthByteCount()
    {
        // Header Type determines number of length bytes
        return (getHeaderType() == QDefines.QBYTE_BUFFER_TYPE8) ? 1 : 2;
    }

    @Override
    public void write(final OutputStream output)
        throws IOException
    {
        if (!isValid()) return;

        super.write(output);

        final int byteCount;
        if (getHeaderType() == QDefines.QBYTE_BUFFER_TYPE8)
        {
            QUtils.writeByte(value.length, output);
            byteCount = getInternalSize() - 1;
        }
        else
        {
            assert (getHeaderType() == QDefines.QBYTE_BUFFER_TYPE16);
            QUtils.writeWord(value.length, output);
            byteCount = getInternalSize() - 2;
        }

        output.write(value, 0, byteCount);
    }

    @Override
    protected char getHeaderType()
    {
        assert (isValid());
        if (value.length > 0 && value.length <= 0xFF)
        {
            return QDefines.QBYTE_BUFFER_TYPE8;
        }
        else
        {
            return QDefines.QBYTE_BUFFER_TYPE16;
        }
    }

    public void parse(final InputStream stream)
        throws IOException
    {
        final int type = QUtils.readByte(stream);

        final int length;
        if (type == QDefines.QBYTE_BUFFER_TYPE8)
        {
            length = QUtils.readByte(stream);
        }
        else if (type == QDefines.QBYTE_BUFFER_TYPE16)
        {
            length = QUtils.readWord(stream);
        }
        else
        {
            throw new QCWException("Unsupported Byte Buffer Type: 0x"
                + Integer.toHexString(type));
        }

        final byte[] bytes = new byte[length];
        final int readBytes = stream.read(bytes, 0, length);
        assert (readBytes == length);

        setValue(bytes);
    }
}
