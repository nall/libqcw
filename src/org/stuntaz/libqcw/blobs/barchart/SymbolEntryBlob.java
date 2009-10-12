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
import java.util.ArrayList;
import java.util.List;

import org.stuntaz.libqcw.IBarChartVisitor;
import org.stuntaz.libqcw.QCWException;
import org.stuntaz.libqcw.blobs.QDword;
import org.stuntaz.libqcw.blobs.QRecord;
import org.stuntaz.libqcw.blobs.QUtils;
import org.stuntaz.libqcw.defines.QLineType;

/**
 * Represents a symbol entry that exists within a study. This symbol includes a
 * name and a (potentially empty) collection of lines.
 * 
 * @author nall
 *
 */
public final class SymbolEntryBlob
    extends QRecord
{
    private String sybolName;

    List<ILineBlob> headers = new ArrayList<ILineBlob>();

    /**
     * Creates a new SymbolEntryBlob with the specified name and collection of lines.
     * 
     * @param name the qualified symbol name (e.g. INDEX:OEX.X)
     * @param headers a list of line headers
     */
    public SymbolEntryBlob(final String name, final List<ILineBlob> headers)
    {
        this.sybolName = name;
        this.headers.addAll(headers);
        setValid();
    }

    /**
     * Creates a new SymbolEntryBlob by parsing the specified InputStream.
     * 
     * @param input the stream to parse
     * @throws IOException if there is an error during parsing
     */
    public SymbolEntryBlob(final InputStream input) throws IOException
    {
        parse(input);
    }

    /**
     * Sets the symbol name.
     * 
     * @param name the fully qualified symbol name (e.g. INDEX:OEX.X)
     */
    public void setSymbolName(final String name)
    {
        this.sybolName = name;
    }

    /**
     * Gets the symbol name for this entry
     * 
     * @return this entry's symbol name
     */
    public String getSymbolName()
    {
        return sybolName;
    }

    /**
     * Adds the specified line to this symbol's entry
     * @param line
     */
    public void addLine(final ILineBlob line)
    {
        headers.add(line);
    }

    /**
     * Gets the list of lines associated with this symbol.
     * 
     * @return this symbol's list of lines
     */
    public List<ILineBlob> getLineHeaders()
    {
        return headers;
    }

    /**
     * Sets the line headers for this symbol, removing any previous lines.
     * 
     * @param headers the lines for this symbol entry
     */
    public void setLineHeaders(final List<ILineBlob> headers)
    {
        this.headers = headers;
    }

    @Override
    protected int getInternalSize()
    {
        int totalBytes = 0;
        for (final ILineBlob header : headers)
        {
            totalBytes += QUtils.getSize(QDword.class); // header.getLineType()
            totalBytes += header.getSize();
        }

        // Terminator
        totalBytes += QUtils.getSize(QDword.class); // terminator

        // Number of custom lines
        totalBytes += QUtils.getSize(QDword.class); // headers.size()

        // Symbol name
        totalBytes += QUtils.getQStringSize(sybolName);

        return totalBytes;
    }

    @Override
    public void write(final OutputStream output)
        throws IOException
    {
        super.write(output);

        QUtils.writeQString(sybolName, output);
        new QDword(headers.size()).write(output);

        for (final ILineBlob header : headers)
        {
            QUtils.writeQDword(header.getLineType().getValue(), output);
            header.write(output);
        }

        // Terminator
        QUtils.writeQDword(0, output); // terminator
    }

    public void parse(final InputStream stream)
        throws IOException
    {
        final int size = parseRecordHeader(stream);

        setSymbolName(QUtils.readQString(stream));

        final int numLines = QUtils.readQDword(stream);

        for (int i = 0; i < numLines; ++i)
        {
            final QLineType type = QLineType
                .forValue(QUtils.readQDword(stream));

            switch (type)
            {
            case BasicLine:
            case Note:
            {
                final LineHeaderBlob line = new LineHeaderBlob(type);
                line.parse(stream);
                addLine(line);
                break;
            }

            case Retracement:
            case Projection:
            case Fibonacci_Extension:
            case Fibonacci_Time_Interval:
            case Fibonacci_Circles:
            case Fibonacci_Time_Cycles:
            case Time_Cycles:
            case Regression_Line:
            case Andrews_Pitchfork:
            case Andrews_Pitchfork_Modified_Schiff:
            case Andrews_Pitchfork_Inside:
            case Pitchfan:
            {
                final UnsupportedLineBlob line = new UnsupportedLineBlob(type);
                line.parse(stream);
                addLine(line);
                break;
            }
            default:
                throw new QCWException("Unknown line type: " + type + " in "
                    + getSymbolName());
            }
        }

        final int terminator = QUtils.readQDword(stream);
        assert (terminator == 0);

        setValid();
        assert (size == getInternalSize());
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
