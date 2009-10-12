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

import org.stuntaz.libqcw.IBarChartVisitor;
import org.stuntaz.libqcw.blobs.QByteBuffer;
import org.stuntaz.libqcw.blobs.QDword;
import org.stuntaz.libqcw.blobs.QRecord;
import org.stuntaz.libqcw.blobs.QUtils;
import org.stuntaz.libqcw.defines.QLineType;

/**
 * Represents a line header. This contains line type information as well as
 * font and text information for notes.
 * 
 * @author nall
 *
 */
public final class LineHeaderBlob
    extends QRecord
    implements ILineBlob
{
    private final QLineType lineType;

    private QByteBuffer noteFont;
    private String noteText;
    private int unknown2;
    private int unknown3;

    private final BasicLineBlob lineInfo = new BasicLineBlob();

    /**
     * Creates a new LineHeaderBlob with the specified line type.
     * 
     * @param type the line type for this header
     */
    public LineHeaderBlob(final QLineType type)
    {
        this.lineType = type;
    }

    public QLineType getLineType()
    {
        return lineType;
    }

    /**
     * Returns the basic line information for this header.

     * @return the basic line information
     */
    public BasicLineBlob getLineInfo()
    {
        return lineInfo;
    }

    /**
     * Returns the text of a note. This should only be called if 
     * {@link #getLineType()} is {@link QLineType#Note}.
     * 
     * @return the text of the note
     */
    public String getNoteText()
    {
        assert (getLineType() == QLineType.Note);
        return noteText;
    }

    /**
     * Sets the font to use for the note. This should only be called if 
     * {@link #getLineType()} is {@link QLineType#Note}.
     * 
     * @param noteFont the font to use when displaying the note
     */
    public void setNoteFont(final byte[] noteFont)
    {
        this.noteFont = new QByteBuffer(noteFont);
    }

    /**
     * Sets the text of the note. This should only be called if 
     * {@link #getLineType()} is {@link QLineType#Note}.
     * 
     * @param noteText the text of the note
     */
    public void setNoteText(final String noteText)
    {
        this.noteText = noteText;
    }

    private void setUnknown2(final int value)
    {
        this.unknown2 = value;
    }

    private void setUnknown3(final int value)
    {
        this.unknown3 = value;
    }

    @Override
    protected int getInternalSize()
    {
        int totalBytes = lineInfo.getSize()
            + (2 * QUtils.getSize(QDword.class));

        if (lineType == QLineType.Note)
        {
            totalBytes += noteFont.getSize() + QUtils.getQStringSize(noteText);
        }

        return totalBytes;
    }

    @Override
    public void write(final OutputStream output)
        throws IOException
    {
        super.write(output);

        if (lineType == QLineType.Note)
        {
            noteFont.write(output);
            QUtils.writeQString(noteText, output);
        }

        QUtils.writeQDword(unknown2, output);
        QUtils.writeQDword(unknown3, output);
        lineInfo.write(output);
    }

    public void parse(final InputStream stream)
        throws IOException
    {
        final int size = parseRecordHeader(stream);

        if (lineType == QLineType.Note)
        {
            setNoteFont(QUtils.readQByteBuffer(stream));
            setNoteText(QUtils.readQString(stream));
        }

        setUnknown2(QUtils.readQDword(stream));
        setUnknown3(QUtils.readQDword(stream));

        lineInfo.parse(stream);

        setValid();
        assert (size == getInternalSize());
    }

    @Override
    public String toString()
    {
        String s = "";
        if (lineType == QLineType.Note)
        {
            s += "Note: [" + noteText + "] ";
        }
        s += lineInfo.toString();

        return s;
    }

    public void accept(final IBarChartVisitor visitor)
    {
        visitor.visit(this);
    }
}
