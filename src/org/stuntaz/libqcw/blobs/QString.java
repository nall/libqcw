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
import java.nio.ByteBuffer;

import org.stuntaz.libqcw.QCWException;

/**
 * This class represents strings as they exist in the file format. Strings
 * are not an atomic primitive, but are made up of a {@link QWord} of length
 * and a {@link ByteBuffer} of data. They are treated as primitives as a
 * convenience to the user of the API.
 * 
 * Some strings include the trailing null in the length, while others do not.
 * I can find no rhyme or reason as to which do and which don't, so I allow
 * strings to have a "goofy mode". When in this mode, the length does not 
 * include the NULL. This is the exceptional case and it is up to the user of
 * QStrings to make sure this mode is set appropriately.
 * 
 * @author nall
 *
 */
public final class QString
    extends QPrimitive
{
    private int qlength;
    private IBlob qbuf;
    private boolean goofyLengthMode = false;

    /**
     * Creates a new, invalid QString
     */
    public QString()
    {
        // Do nothing
    }

    /**
     * Creates a new, invalid string with the specified mode
     * 
     * @param goofyLengthMode true for goofy mode, false otherwise.
     */
    public QString(final boolean goofyLengthMode)
    {
        this.goofyLengthMode = goofyLengthMode;
    }

    /**
     * Creates a new, valid string with the specified mode
     * 
     * @param value the value for this QString
     * @param goofyLengthMode true for goofy mode, false otherwise.
     */
    public QString(final String value, final boolean goofyLengthMode)
    {
        setValue(value);
        this.goofyLengthMode = goofyLengthMode;
    }

    /**
     * Creates a new, valid string in non-goofy mode. This is the equivalent
     * of calling {@code QString(value, false)}}
     * 
     * @param value the value for this QString
     */
    public QString(final String value)
    {
        setValue(value);
    }

    /**
     * Returns the value of this string. If the string is empty, but contains a
     * trailing NULL, the empty string ("") is returned. If it is empty, but 
     * doesn't contain a trailing null, <code>null</code> is returned. Otherwise
     * a valid String object is returned.
     * 
     * @return the String represented by this QString
     */
    public String getValue()
    {
        assert (isValid());

        if (qbuf instanceof QByte)
        {
            return "";
        }
        else if (qlength == 0)
        {
            return null;
        }
        else
        {
            assert (qbuf instanceof QByteBuffer);
            final QByteBuffer qbb = (QByteBuffer) qbuf;

            final byte[] bytes = qbb.getValue();

            // Remove the NULL for the outside world
            assert (bytes[bytes.length - 1] == 0);
            return new String(bytes, 0, bytes.length - 1);
        }
    }

    /**
     * Sets the value of this QString. Depending on the mode with which this
     * QString was instantiated, the length will be set appropriately. Note
     * that in goofy mode, a value of "" and null result in different lengths.
     * In non-goofy mode, they both have a length of 1 (the trailing null).
     * 
     * @param value The string value for this QString
     */
    public void setValue(final String value)
    {
        if (goofyLengthMode)
        {
            if (value == null)
            {
                qlength = 0;
                qbuf = null;
            }
            else if (value.length() == 0)
            {
                qlength = 1;
                qbuf = new QByte(0);
            }
            else
            {
                final byte[] bytes = new byte[value.length() + 1];
                System.arraycopy(value.getBytes(), 0, bytes, 0, value.length());
                bytes[bytes.length - 1] = 0;

                qlength = value.length() + 1;
                qbuf = new QByteBuffer(bytes);
            }
        }
        else
        {
            if (value == null || value.length() == 0)
            {
                qlength = 1;
                qbuf = new QByte(0);
            }
            else
            {
                final byte[] bytes = new byte[value.length() + 1];
                System.arraycopy(value.getBytes(), 0, bytes, 0, value.length());
                bytes[bytes.length - 1] = 0;

                qlength = value.length() + 1;
                qbuf = new QByteBuffer(bytes);
            }
        }
        setValid();
    }

    @Override
    public int getSize()
    {
        return getInternalSize();
    }

    @Override
    public int getInternalSize()
    {
        // QStrings are comprised of a DWord and a ByteBuffer
        assert (isValid());

        final int bufSize = (goofyLengthMode && qlength == 0) ? 0 : qbuf
            .getSize();
        return QUtils.getSize(QWord.class) + bufSize;
    }

    @Override
    public void write(final OutputStream output)
        throws IOException
    {
        if (!isValid()) return;

        QUtils.writeQWord(qlength, output);
        if (goofyLengthMode)
        {
            if (qlength > 0)
            {
                qbuf.write(output);
            }
            if (qlength == 1 && !(qbuf instanceof QByte))
                throw new RuntimeException("hit");
        }
        else
        {
            assert (qlength > 0);
            qbuf.write(output);
        }
    }

    @Override
    protected char getHeaderType()
    {
        throw new QCWException("QStrings do not have header types");
    }

    public void parse(final InputStream stream)
        throws IOException
    {
        qlength = QUtils.readQWord(stream);

        if (goofyLengthMode)
        {
            if (qlength == 0)
            {
                // There is no actual string data
                qbuf = null;
            }
            else if (qlength == 1)
            {
                // Just a NULL byte
                final QByte qb = new QByte(QUtils.readQByte(stream));
                assert (qb.getValue() == 0);
                qbuf = qb;
            }
            else
            {
                final QByteBuffer qbb = new QByteBuffer(QUtils
                    .readQByteBuffer(stream));
                final int byteLength = (qbb.getInternalSize() - qbb
                    .getLengthByteCount());

                if ((qlength + 1) != byteLength)
                {
                    throw new QCWException(
                        "Unexpected difference while parsing QString [Goofy]");
                }
                qbuf = qbb;
            }
        }
        else
        {
            assert (qlength > 0);
            if (qlength == 1)
            {
                // Just a NULL byte
                final QByte qb = new QByte(QUtils.readQByte(stream));
                assert (qb.getValue() == 0);
                qbuf = qb;
            }
            else
            {
                final QByteBuffer qbb = new QByteBuffer(QUtils
                    .readQByteBuffer(stream));
                final int byteLength = (qbb.getInternalSize() - qbb
                    .getLengthByteCount());

                if (qlength != byteLength)
                {
                    throw new QCWException(
                        "Unexpected difference while parsing QString [Normal]");
                }
                qbuf = qbb;
            }
        }

        setValid();
    }
}