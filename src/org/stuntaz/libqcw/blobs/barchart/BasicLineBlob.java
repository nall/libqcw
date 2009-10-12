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

package org.stuntaz.libqcw.blobs.barchart;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Date;

import org.stuntaz.libqcw.IBarChartVisitor;
import org.stuntaz.libqcw.blobs.QDword;
import org.stuntaz.libqcw.blobs.QQword;
import org.stuntaz.libqcw.blobs.QRecord;
import org.stuntaz.libqcw.blobs.QUtils;
import org.stuntaz.libqcw.defines.QBasicLineType;

/**
 * Represents a basic line drawn on a bar chart. This line may be a segment,
 * ray, extended line, or note.
 * 
 * @author nall
 *
 */
public final class BasicLineBlob
    extends QRecord
{
    private QBasicLineType lineSubtype;
    private long options;
    private int width;
    private long rgb;

    private Date timestamp1;
    private Date timestamp2;

    private double endpoint1;
    private double endpoint2;

    private long unknown1;
    private long unknown2;
    private Integer unknown3 = null; // This DWORD is optional

    /**
     * Returns this line's subtype.
     * 
     * @return the subtype of this basic line
     */
    public QBasicLineType getLineSubtype()
    {
        return lineSubtype;
    }

    /**
     * Returns the width of this line.
     * 
     * @return this line's width in pixels
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Returns the RGB information for this line.
     * 
     * @return this line's RGB information
     */
    public long getRBG()
    {
        return rgb;
    }

    /**
     * Returns the starting timestamp for this line
     * 
     * @return this line's starting timestamp
     */
    public Date getStartTimestamp()
    {
        return timestamp1;
    }

    /**
     * Returns the ending timestamp for this line
     * 
     * @return this line's starting ending
     */
    public Date getEndTimestamp()
    {
        return timestamp2;
    }

    /**
     * Returns the starting value for this line
     * 
     * @return this line's starting value
     */
    public double getStartValue()
    {
        return endpoint1;
    }

    /**
     * Returns the ending value for this line
     * 
     * @return this line's ending value
     */
    public double getEndValue()
    {
        return endpoint2;
    }

    /**
     * Sets the subtype of this line.
     * 
     * @param type the subtype for this line
     */
    public void setLineSubtype(final QBasicLineType type)
    {
        lineSubtype = type;
    }

    /**
     * Sets the options for this line.
     * 
     * TODO: decode line options
     * 
     * @param options the options for this line
     */
    public void setOptions(final long options)
    {
        this.options = options;
    }

    /**
     * Sets the width for this line.
     * 
     * @param width the width for this line in pixels
     */
    public void setWidth(final int width)
    {
        this.width = width;
    }

    /**
     * Sets the RGB information for this line.
     * 
     * @param rgb the RGB information for this line.
     */
    public void setRGB(final long rgb)
    {
        this.rgb = rgb;
    }

    /**
     * Sets the starting timestamp for this line.
     * 
     * @param tstamp the line's starting timestamp
     */
    public void setStartTimestamp(final Date tstamp)
    {
        this.timestamp1 = tstamp;
    }

    /**
     * Sets the ending timestamp for this line.
     * 
     * @param tstamp the line's ending timestamp
     */
    public void setEndTimestamp(final Date tstamp)
    {
        this.timestamp2 = tstamp;
    }

    /**
     * Sets the starting value for this line.
     * 
     * @param value the line's starting value
     */
    public void setStartValue(final double value)
    {
        this.endpoint1 = value;
    }

    /**
     * Sets the ending value for this line.
     * 
     * @param value the line's ending value
     */
    public void setEndValue(final double value)
    {
        this.endpoint2 = value;
    }

    private void setUnknown1(final long value)
    {
        this.unknown1 = value;
    }

    private void setUnknown2(final long value)
    {
        this.unknown2 = value;
    }

    private void setUnknown3(final Integer value)
    {
        this.unknown3 = value;
    }

    @Override
    protected int getInternalSize()
    {
        final int numDwords = 6 + ((unknown3 == null) ? 0 : 1);

        return (numDwords * QUtils.getSize(QDword.class))
            + (4 * QUtils.getSize(QQword.class));
    }

    @Override
    public void write(final OutputStream output)
        throws IOException
    {
        super.write(output);

        QUtils.writeQDword(lineSubtype.getValue(), output);
        QUtils.writeQDword(options, output);
        QUtils.writeQDword(width, output);
        QUtils.writeQDword(rgb, output);
        QUtils.writeQDword(QUtils.dateToQTime(timestamp1), output);
        QUtils.writeQQword(Double.doubleToLongBits(endpoint1), output);
        QUtils.writeQDword(QUtils.dateToQTime(timestamp2), output);
        QUtils.writeQQword(Double.doubleToLongBits(endpoint2), output);
        QUtils.writeQQword(unknown1, output);
        QUtils.writeQQword(unknown2, output);
        QUtils.writeQDword(unknown3, output);
    }

    public void parse(final InputStream stream)
        throws IOException
    {
        final int size = parseRecordHeader(stream);

        setLineSubtype(QBasicLineType.forValue(QUtils.readQDword(stream)));
        setOptions(QUtils.readQDword(stream));
        setWidth(QUtils.readQDword(stream));
        setRGB(QUtils.readQDword(stream));

        setStartTimestamp(QUtils.qtimeToDate(QUtils.readQDword(stream)));
        setStartValue(Double.longBitsToDouble(QUtils.readQQword(stream)));
        setEndTimestamp(QUtils.qtimeToDate(QUtils.readQDword(stream)));
        setEndValue(Double.longBitsToDouble(QUtils.readQQword(stream)));

        setUnknown1(QUtils.readQQword(stream));
        setUnknown2(QUtils.readQQword(stream));

        if (getInternalSize() != size)
        {
            setUnknown3(QUtils.readQDword(stream));
        }

        setValid();
        assert (size == getInternalSize());
    }

    @Override
    public String toString()
    {
        final DecimalFormat fmt = new DecimalFormat("0.00");
        final String s = "Type: BasicLine; Subtype: " + lineSubtype
            + "; Options: 0x" + Long.toHexString(options) + "; Width: " + width
            + "; RGB: 0x" + Long.toHexString(rgb) + "; Coordinates("
            + fmt.format(endpoint1) + " @ [" + timestamp1 + "] x "
            + fmt.format(endpoint2) + " @ [" + timestamp2 + "])";
        return s;
    }

    /**
     * Accept the specified bar chart visitor.
     * 
     * @param visitor the visitor to accept
     */
    public void accept(final IBarChartVisitor visitor)
    {
        visitor.visit(this);
    }
}
