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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;

import org.stuntaz.libqcw.QCWException;
import org.stuntaz.libqcw.QOptions;

/**
 * A collection of static utility methods for helping to read, write, and
 * decode QCW files.
 * 
 * @author nall
 *
 */
public final class QUtils
{
    private static byte[] readBuf = new byte[8];
    private static byte[] writeBuf = new byte[8];

    private static QByte _qbyte = new QByte(0);
    private static QWord _qword = new QWord(0);
    private static QDword _qdword = new QDword(0);
    private static QQword _qqword = new QQword(0);

    private static long qEpochSeconds;
    static
    {
        final Calendar baseDate = Calendar.getInstance();
        baseDate.clear();
        baseDate.set(1801, Calendar.JANUARY, 1, 0, 0);
        qEpochSeconds = Math.abs(baseDate.getTimeInMillis() / 1000);
    }

    /**
     * Returns the size, in bytes, required by the specified class as it exists
     * in the file format.
     * 
     * @param type the class to check
     * @return the number of bytes required by type in the file format
     */
    @SuppressWarnings("unchecked")
    public static int getSize(final Class type)
    {
        if (type == QByte.class)
        {
            return _qbyte.getSize();
        }
        else if (type == QWord.class)
        {
            return _qword.getSize();
        }
        else if (type == QDword.class)
        {
            return _qdword.getSize();
        }
        else if (type == QQword.class)
        {
            return _qqword.getSize();
        }
        else
        {
            throw new QCWException("No size information know for type: "
                + type.getName());
        }
    }

    /**
     * Returns the size of the QString resulting from the specified value. This
     * size is calculated in "non-goofy" mode (see {@link QString} for a 
     * description of goofy mode).
     * 
     * @param s the string to check
     * @return the size of the resulting QString in bytes as it would exist in
     *         the file
     */
    public static int getQStringSize(final String s)
    {
        final QString _qstring = new QString(s);
        return _qstring.getSize();
    }

    /**
     * Reads the specified number of bytes from the input stream and returns
     * the value. This assumes a little endian format on the input stream.
     * 
     * @param numBytes the number of bytes to read
     * @param stream the stream from which to read
     * @return the value read
     * @throws IOException if an I/O error occurs while reading the file
     */
    public static long readVal(final int numBytes, final InputStream stream)
        throws IOException
    {
        if (QOptions.DEBUG_READ)
        {
            System.out.print("READING "
                + numBytes);
            if(stream instanceof FileInputStream)
            {
            	System.out.println(" at 0x"
                + Long.toHexString(((FileInputStream) stream).getChannel()
                    .position()));
            }
            else
            {
            	System.out.println();
            }
        }

        long value = 0;

        final int size = stream.read(readBuf, 0, numBytes);
        assert (size == numBytes);

        for (int i = 0; i < numBytes; ++i)
        {
            long tmp = (readBuf[i] & 0xFF);
            tmp <<= (i * 8);
            value |= tmp;
        }

        if (QOptions.DEBUG_READ)
        {
            System.out.println("\t 0x" + Long.toHexString(value));
        }

        return value;
    }

    /**
     * Writes the specified value in the specified number of bytes to the output
     * stream. This writes the value in little endian format.
     * 
     * @param value the value to write
     * @param numBytes the number of bytes to write
     * @param output the stream on which to write
     * @throws IOException if an I/O error occurs while writing the file
     */
    public static void writeVal(
        final long value,
        final int numBytes,
        final OutputStream output)
        throws IOException
    {
        if (QOptions.DEBUG_WRITE)
        {
            System.out.println("WRITING "
                + numBytes
                + " at 0x"
                + Long.toHexString(((FileOutputStream) output).getChannel()
                    .position()) + " Value: 0x" + Long.toHexString(value));
        }

        for (int i = 0; i < numBytes; ++i)
        {
            writeBuf[i] = (byte) ((value >> (i * 8)) & 0xFF);
        }

        output.write(writeBuf, 0, numBytes);
    }

    /**
     * Read 1 bare (non-QByte) byte from the input stream.
     * 
     * @param stream the stream from which to read
     * @return the value read
     * @throws IOException if an I/O exception occurs while reading
     */
    public static int readByte(final InputStream stream)
        throws IOException
    {
        return (int) readVal(1, stream);
    }

    /**
     * Read 1 bare (non-QWord) word from the input stream.
     * 
     * @param stream the stream from which to read
     * @return the value read
     * @throws IOException if an I/O exception occurs while reading
     */
    public static int readWord(final InputStream stream)
        throws IOException
    {
        return (int) readVal(2, stream);
    }

    static void writeByte(final long value, final OutputStream output)
        throws IOException
    {
        writeVal(value, 1, output);
    }

    static void writeWord(final long value, final OutputStream output)
        throws IOException
    {
        writeVal(value, 2, output);
    }

    static void writeDword(final long value, final OutputStream output)
        throws IOException
    {
        writeVal(value, 4, output);
    }

    static void writeQword(final long value, final OutputStream output)
        throws IOException
    {
        writeVal(value, 8, output);
    }

    /**
     * Writes the value as a {@link QByte} to the specified output stream.
     * 
     * @param value the value to write
     * @param output the stream on which to write
     * @throws IOException if an I/O exception occurs while writing
     */
    public static void writeQByte(final long value, final OutputStream output)
        throws IOException
    {
        writeByte(QDefines.QBYTE_TYPE, output);
        writeVal(value, 1, output);
    }

    /**
     * Writes the value as a {@link QWord} to the specified output stream.
     * 
     * @param value the value to write
     * @param output the stream on which to write
     * @throws IOException if an I/O exception occurs while writing
     */
    public static void writeQWord(final long value, final OutputStream output)
        throws IOException
    {
        writeByte(QDefines.QWORD_TYPE, output);
        writeVal(value, 2, output);
    }

    /**
     * Writes the value as a {@link QDword} to the specified output stream.
     * 
     * @param value the value to write
     * @param output the stream on which to write
     * @throws IOException if an I/O exception occurs while writing
     */
    public static void writeQDword(final long value, final OutputStream output)
        throws IOException
    {
        writeByte(QDefines.QDWORD_TYPE, output);
        writeVal(value, 4, output);
    }

    /**
     * Writes the value as a {@link QQword} to the specified output stream.
     * 
     * @param value the value to write
     * @param output the stream on which to write
     * @throws IOException if an I/O exception occurs while writing
     */
    public static void writeQQword(final long value, final OutputStream output)
        throws IOException
    {
        writeByte(QDefines.QQWORD_TYPE, output);
        writeVal(value, 8, output);
    }

    /**
     * Writes the value as a {@link QString} to the specified output stream.
     * 
     * @param value the value to write
     * @param output the stream on which to write
     * @param goofyLengthMode whether to use the QString goofy mode when calculating
     *        the length
     * @throws IOException if an I/O exception occurs while writing
     */
    public static void writeQString(
        final String value,
        final OutputStream output,
        final boolean goofyLengthMode)
        throws IOException
    {
        final QString string = new QString(value, goofyLengthMode);
        string.write(output);
    }

    /**
     * This is equivalent to {@link QUtils#writeQString(String, OutputStream, boolean)}
     * with <code>goofyLengthMode</code> set to false.
     * 
     * @param value the value to write
     * @param output the output stream on which to write
     * @throws IOException if an I/O exception occurs while writing
     */
    public static void writeQString(
        final String value,
        final OutputStream output)
        throws IOException
    {
        writeQString(value, output, false);
    }

    /**
     * Reads a {@link QByte} from the input stream.
     * 
     * @param stream the stream from which to read
     * @return the value read
     * @throws IOException if an I/O exception occurs while reading
     */
    public static int readQByte(final InputStream stream)
        throws IOException
    {
        final int type = QUtils.readByte(stream);
        assert (type == QDefines.QBYTE_TYPE);

        return (int) readVal(1, stream);
    }

    /**
     * Reads a {@link QWord} from the input stream.
     * 
     * @param stream the stream from which to read
     * @return the value read
     * @throws IOException if an I/O exception occurs while reading
     */
    public static int readQWord(final InputStream stream)
        throws IOException
    {
        final int type = QUtils.readByte(stream);
        assert (type == QDefines.QWORD_TYPE);

        return (int) readVal(2, stream);
    }

    /**
     * Reads a {@link QDword} from the input stream.
     * 
     * @param stream the stream from which to read
     * @return the value read
     * @throws IOException if an I/O exception occurs while reading
     */
    public static int readQDword(final InputStream stream)
        throws IOException
    {
        final int type = QUtils.readByte(stream);
        assert (type == QDefines.QDWORD_TYPE);

        return (int) readVal(4, stream);
    }

    /**
     * Reads a {@link QQword} from the input stream.
     * 
     * @param stream the stream from which to read
     * @return the value read
     * @throws IOException if an I/O exception occurs while reading
     */
    public static long readQQword(final InputStream stream)
        throws IOException
    {
        final int type = QUtils.readByte(stream);
        assert (type == QDefines.QQWORD_TYPE);

        return readVal(8, stream);
    }

    /**
     * Reads a {@link QByteBuffer} from the input stream.
     * 
     * @param stream the stream from which to read
     * @return the buffer read
     * @throws IOException if an I/O exception occurs while reading
     */
    public static byte[] readQByteBuffer(final InputStream stream)
        throws IOException
    {
        final QByteBuffer val = new QByteBuffer();
        val.parse(stream);
        return val.getValue();
    }

    /**
     * Reads a {@link QString} from the input stream.
     * 
     * @param stream the stream from which to read
     * @param goofyLengthMode whether to read the length in goofy mode or not
     * @return the value read
     * @throws IOException if an I/O exception occurs while reading
     */
    public static String readQString(
        final InputStream stream,
        final boolean goofyLengthMode)
        throws IOException
    {
        final QString val = new QString(goofyLengthMode);
        val.parse(stream);
        return val.getValue();
    }

    /**
     * Reads a {@link QString} from the input stream. The QString read will
     * not use goofy mode.
     * 
     * @param stream the stream from which to read
     * @return the value read
     * @throws IOException if an I/O exception occurs while reading
     */
    public static String readQString(final InputStream stream)
        throws IOException
    {
        return readQString(stream, false);
    }

    /**
     * Converts a 32-bit time as present in the file format to a {@link Date}
     * object.
     * 
     * Note that while a long is used, the value should fit in 32 bits.
     * 
     * @param qtime the time to convert
     * @return the equivalent Date object
     */
    public static Date qtimeToDate(long qtime)
    {
        // Dates are stored as the # of 2 second increments since Jan 1, 1801 00:00
        // Make sure we're not negative
        qtime &= (0x0FFFFFFFFL);

        // Get # seconds
        // Subtract out baseTime
        long seconds = (qtime * 2);
        if (seconds < qEpochSeconds)
        {
            // Date between 1801 and 1970, so subtract it from Qepoch
            seconds = (qEpochSeconds - seconds) * -1;
        }
        else
        {
            // Date >= 1970, so subtract out epoch seconds
            seconds -= qEpochSeconds;
        }

        final Calendar c = Calendar.getInstance();
        c.clear();
        c.setTimeInMillis(seconds * 1000);

        if (c.getTimeZone().inDaylightTime(c.getTime()))
        {
            // Need to subtract an hour
            c.add(Calendar.HOUR_OF_DAY, -1);
        }

        return c.getTime();
    }

    /**
     * Converts a {@link Date} object to a 32-bit value that be used in the
     * file format.
     * 
     * Note that while a long is returned, the value will fit in 32 bits.
     * 
     * @param date the date to convert
     * @return the equivalent 32-bit value
     */
    public static long dateToQTime(final Date date)
    {
        final Calendar c = Calendar.getInstance();
        c.clear();
        c.setTime(date);
        if (c.getTimeZone().inDaylightTime(c.getTime()))
        {
            c.add(Calendar.HOUR_OF_DAY, 1);
        }

        final long seconds = c.getTimeInMillis() / 1000;

        long result;
        if (seconds >= 0)
        {
            // Post-1970. Add qEpoch
            result = seconds + qEpochSeconds;
        }
        else
        {
            // Pre-1970.
            result = qEpochSeconds - (-seconds);
        }
        result /= 2;

        return result;
    }
}
