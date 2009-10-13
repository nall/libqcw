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
import org.stuntaz.libqcw.QOptions;
import org.stuntaz.libqcw.blobs.QByteBuffer;
import org.stuntaz.libqcw.blobs.QDword;
import org.stuntaz.libqcw.blobs.QRecord;
import org.stuntaz.libqcw.blobs.QString;
import org.stuntaz.libqcw.blobs.QUtils;
import org.stuntaz.libqcw.blobs.QWord;
import org.stuntaz.libqcw.blobs.UnsupportedBlob;
import org.stuntaz.libqcw.defines.QChartType;
import org.stuntaz.libqcw.defines.QTimePeriod;

/**
 * Represents a bar chart section in a QCW/QCL file. A bar chart section contains
 * information on a per-study basis. This information includes how the study
 * is laid out in the chart window, study header information (parameters, etc),
 * and any lines drawn on that study.
 * 
 * This data structure also contains the basic configuration of the chart
 * including colors, fonts, etc.
 * 
 * It should always be the case that the lists returned from {@link #getStudyLayouts()},
 * {@link #getStudies()}, and {@link #getStudyLines()} should contain the same
 * number of elements.
 * 
 * @author nall
 *
 */
public final class BarChartBlob
    extends QRecord
{
	private static final int QC_MAGIC5 = 0x0190;
	private static final int QC_MAGIC61 = 0x025B;
	
    private QWord magic = new QWord();
    private QString chartTitle = new QString(true);
    private QByteBuffer chartFont = new QByteBuffer();

    private QDword unknown1 = new QDword();
    private QDword unknown2 = new QDword();
    private QDword unknown3 = new QDword();
    private QChartType chartType = QChartType.Line; // QChartType
    private QDword unknown4 = new QDword(); // likely a color
    private QDword unknown5 = new QDword(); // likely a color
    private QDword unknown6 = new QDword(); // likely a color
    private QDword unknown7 = new QDword(); // likely a color
    private QDword unknown8 = new QDword(); // likely a color
    private QDword unknown9 = new QDword(); // likely a color
    private QDword unknown10 = new QDword(); // likely a color
    private QDword unknown11 = new QDword(); // likely a color
    private QDword unknown12 = new QDword();

    private final List<StudyLayoutBlob> studyLayouts = new ArrayList<StudyLayoutBlob>();
    private final List<StudyHeaderBlob> studies = new ArrayList<StudyHeaderBlob>();
    private final List<StudyLineGroupBlob> customLines = new ArrayList<StudyLineGroupBlob>();

    private UnsupportedBlob unknown13 = new UnsupportedBlob(); // 3 16 bit values

    private UnsupportedBlob dailySnapshot = new UnsupportedBlob();
    private UnsupportedBlob dataWindow = new UnsupportedBlob();
    private UnsupportedBlob unknown14 = new UnsupportedBlob();

    private QString fileName; // Can be a QByte = 0 or a QString

    private QDword unknown15 = new QDword();
    private QDword unknown16 = new QDword();

    // These are valid in Workspace Layouts
    private QDword unknown17 = new QDword();
    private QDword unknown18 = new QDword();
    private QDword unknown19 = new QDword();

    /**
     * Returns true if this is a QC-6.1 style chart, false otherwise
     * 
     * @return true if a 6.1 chart, false if earlier
     */
    public boolean isQC61()
    {
    	return magic.getValue() == QC_MAGIC61;
    }
    
    /**
     * Returns the study layouts for this chart
     * 
     * @return a list of the study layouts in this chart.
     */
    public List<StudyLayoutBlob> getStudyLayouts()
    {
        return studyLayouts;
    }

    /**
     * Returns the studies for this chart
     * 
     * @return a list of the studies in this chart.
     */
    public List<StudyHeaderBlob> getStudies()
    {
        return studies;
    }

    /**
     * Returns the per-study lines for this chart
     * 
     * @return a list of the per-study lines in this chart.
     */
    public List<StudyLineGroupBlob> getStudyLines()
    {
        return customLines;
    }

    /**
     * Sets the font to be used in this chart.
     * 
     * @param font the chart's default font
     */
    public void setChartFont(final byte[] font)
    {
        this.chartFont = new QByteBuffer(font);
    }

    /**
     * Returns the title for this chart. This value is only valid when
     * {@link QOptions#WORKSPACE_CONTEXT} is {@code true}.
     * 
     * @return a String containing the title of this chart
     */
    public String getChartTitle()
    {
        assert (QOptions.WORKSPACE_CONTEXT);

        return this.chartTitle.getValue();
    }

    /**
     * Sets the title for this chart.
     * 
     * @param title the title to use for this chart
     */
    public void setChartTitle(final String title)
    {
        // This particular string uses the wrong length
        this.chartTitle = new QString(title, true);
    }

    /**
     * Returns the symbol contained in this chart's title. This value is only
     * valid when {@link QOptions#WORKSPACE_CONTEXT} is {@code true}.
     * 
     * @return the symbol contained in the title of this chart
     */
    public String getChartTitleSymbol()
    {
        assert (QOptions.WORKSPACE_CONTEXT);

        final String title = chartTitle.getValue();

        if (title == null) return null;

        final String[] pieces = title.split(",");
        assert (pieces.length == 2);
        return pieces[0];
    }

    /**
     * Return this chart's time period. This value is only valid when
     * {@link QOptions#WORKSPACE_CONTEXT} is {@code true}.
     * 
     * @return the time period specified in the title of this chart
     */
    public QTimePeriod getChartTimePeriod()
    {
        assert (QOptions.WORKSPACE_CONTEXT);

        final String title = chartTitle.getValue();

        if (title == null) return null;

        final String[] pieces = title.split(",");
        assert (pieces.length == 2);
        return QTimePeriod.forValue(pieces[1]);
    }

    /**
     * Sets the type of this bar chart (line, candle, etc).
     * 
     * @param type the type for this bar chart
     */
    public void setChartType(final QChartType type)
    {
        this.chartType = type;
    }

    private void setDailySnapshot(final UnsupportedBlob value)
    {
        dailySnapshot = value;
    }

    private void setDataWindow(final UnsupportedBlob value)
    {
        dataWindow = value;
    }

    private void setMagic(final int magic)
    {
        assert (magic == QC_MAGIC5  || magic == QC_MAGIC61);
        this.magic = new QWord(magic);
    }

    private void setUnknown1(final int value)
    {
        this.unknown1 = new QDword(value);
    }

    private void setUnknown2(final int value)
    {
        this.unknown2 = new QDword(value);
    }

    private void setUnknown3(final int value)
    {
        this.unknown3 = new QDword(value);
    }

    private void setUnknown4(final int value)
    {
        this.unknown4 = new QDword(value);
    }

    private void setUnknown5(final int value)
    {
        this.unknown5 = new QDword(value);
    }

    private void setUnknown6(final int value)
    {
        this.unknown6 = new QDword(value);
    }

    private void setUnknown7(final int value)
    {
        this.unknown7 = new QDword(value);
    }

    private void setUnknown8(final int value)
    {
        this.unknown8 = new QDword(value);
    }

    private void setUnknown9(final int value)
    {
        this.unknown9 = new QDword(value);
    }

    private void setUnknown10(final int value)
    {
        this.unknown10 = new QDword(value);
    }

    private void setUnknown11(final int value)
    {
        this.unknown11 = new QDword(value);
    }

    private void setUnknown12(final int value)
    {
        this.unknown12 = new QDword(value);
    }

    private void setUnknown13(final UnsupportedBlob value)
    {
        this.unknown13 = value;
    }

    private void setUnknown14(final UnsupportedBlob value)
    {
        this.unknown14 = value;
    }

    private void setUnknown15(final int value)
    {
        this.unknown15 = new QDword(value);
    }

    private void setUnknown16(final int value)
    {
        this.unknown16 = new QDword(value);
    }

    private void setUnknown17(final int value)
    {
        this.unknown17 = new QDword(value);
    }

    private void setUnknown18(final int value)
    {
        this.unknown18 = new QDword(value);
    }

    private void setUnknown19(final int value)
    {
        this.unknown19 = new QDword(value);
    }

    @Override
    protected int getInternalSize()
    {
        int totalStudyBytes = 0;
        assert (studyLayouts.size() == studies.size());
        assert (studyLayouts.size() == customLines.size());

        final QDword terminator = new QDword(0);
        for (int i = 0; i < studyLayouts.size(); ++i)
        {
            totalStudyBytes += studyLayouts.get(i).getSize();
            totalStudyBytes += studies.get(i).getSize();
            totalStudyBytes += customLines.get(i).getSize();

            // No terminator for the first pane
            if (i != 0)
            {
                totalStudyBytes += terminator.getSize();
            }

            // No terminator written for the last pane
            final boolean lastPane = (i + 1) >= studyLayouts.size();
            if (!lastPane)
            {
                totalStudyBytes += terminator.getSize();
            }
        }

        if (QOptions.WORKSPACE_CONTEXT)
        {
            final int chartTitleSize = chartTitle == null ? 0 : chartTitle
                .getSize();
            final int fileNameSize = fileName == null ? 0 : fileName.getSize();

            totalStudyBytes += chartTitleSize + fileNameSize;
        }
        final int totalBytes = magic.getSize() + chartFont.getSize()
            + unknown1.getSize() + unknown2.getSize() + unknown3.getSize()
            + new QWord(chartType.getValue()).getSize() + unknown4.getSize()
            + unknown5.getSize() + unknown6.getSize() + unknown7.getSize()
            + unknown8.getSize() + unknown9.getSize() + unknown10.getSize()
            + unknown11.getSize() + new QDword(studyLayouts.size()).getSize()
            + unknown12.getSize() + unknown13.getSize()
            + dailySnapshot.getSize() + dataWindow.getSize()
            + unknown14.getSize() + unknown15.getSize() + unknown16.getSize()
            + unknown17.getSize() + unknown18.getSize() + unknown19.getSize()
            + totalStudyBytes;

        return totalBytes;
    }

    @Override
    public void write(final OutputStream output)
        throws IOException
    {
        super.write(output);

        magic.write(output);

        if (QOptions.WORKSPACE_CONTEXT)
        {
            chartTitle.write(output);
        }

        chartFont.write(output);
        unknown1.write(output);
        unknown2.write(output);
        unknown3.write(output);
        new QWord(chartType.getValue()).write(output);
        unknown4.write(output);
        unknown5.write(output);
        unknown6.write(output);
        unknown7.write(output);
        unknown8.write(output);
        unknown9.write(output);
        unknown10.write(output);
        unknown11.write(output);
        new QDword(studyLayouts.size()).write(output);
        unknown12.write(output);

        assert (studyLayouts.size() == studies.size());
        assert (studyLayouts.size() == customLines.size());

        final QDword terminator = new QDword(0);
        for (int i = 0; i < studyLayouts.size(); ++i)
        {
            studyLayouts.get(i).write(output);
            studies.get(i).write(output);

            // No terminator written for the first pane
            if (i != 0)
            {
                terminator.write(output);
            }

            customLines.get(i).write(output);

            // No terminator written for the last pane
            final boolean lastPane = (i + 1) >= studyLayouts.size();
            if (!lastPane)
            {
                terminator.write(output);
            }

        }

        unknown13.write(output);
        dailySnapshot.write(output);
        dataWindow.write(output);
        unknown14.write(output);

        if (QOptions.WORKSPACE_CONTEXT)
        {
            fileName.write(output);
        }

        unknown15.write(output);
        unknown16.write(output);

        if (QOptions.WORKSPACE_CONTEXT)
        {
            unknown17.write(output);
            unknown18.write(output);
            unknown19.write(output);
        }
    }

    public void parse(final InputStream stream)
        throws IOException
    {
        final int size = parseRecordHeader(stream);

        setMagic(QUtils.readQWord(stream));

        if (QOptions.WORKSPACE_CONTEXT)
        {
            chartTitle = new QString(true);
            chartTitle.parse(stream);
        }

        setChartFont(QUtils.readQByteBuffer(stream));
        setUnknown1(QUtils.readQDword(stream));
        setUnknown2(QUtils.readQDword(stream));
        setUnknown3(QUtils.readQDword(stream));
        setChartType(QChartType.forValue(QUtils.readQWord(stream)));
        setUnknown4(QUtils.readQDword(stream));
        setUnknown5(QUtils.readQDword(stream));
        setUnknown6(QUtils.readQDword(stream));
        setUnknown7(QUtils.readQDword(stream));
        setUnknown8(QUtils.readQDword(stream));
        setUnknown9(QUtils.readQDword(stream));
        setUnknown10(QUtils.readQDword(stream));
        setUnknown11(QUtils.readQDword(stream));

        final int numStudies = QUtils.readQDword(stream);

        setUnknown12(QUtils.readQDword(stream));

        for (int i = 0; i < numStudies; ++i)
        {
            final StudyLayoutBlob layout = new StudyLayoutBlob();
            layout.parse(stream);
            studyLayouts.add(layout);

            final StudyHeaderBlob header = new StudyHeaderBlob(i == 0);
            header.parse(stream);
            studies.add(header);

            if (i != 0)
            {
                // Terminator
                final int t = QUtils.readQDword(stream);
                assert (t == 0);
            }

            final StudyLineGroupBlob lines = new StudyLineGroupBlob();
            lines.parse(stream);
            customLines.add(lines);

            final boolean lastPane = (i + 1) >= numStudies;
            if (!lastPane)
            {
                final int t = QUtils.readQDword(stream);
                assert (t == 0);
            }

        }

        UnsupportedBlob blob = new UnsupportedBlob();
        blob.parse(stream);
        setUnknown13(blob);

        blob = new UnsupportedBlob();
        blob.parse(stream);
        setDailySnapshot(blob);

        blob = new UnsupportedBlob();
        blob.parse(stream);
        setDataWindow(blob);

        blob = new UnsupportedBlob();
        blob.parse(stream);
        setUnknown14(blob);

        if (QOptions.WORKSPACE_CONTEXT)
        {
            fileName = new QString();
            fileName.parse(stream);
        }

        setUnknown15(QUtils.readQDword(stream));
        setUnknown16(QUtils.readQDword(stream));

        if (QOptions.WORKSPACE_CONTEXT)
        {
            setUnknown17(QUtils.readQDword(stream));
            setUnknown18(QUtils.readQDword(stream));
            setUnknown19(QUtils.readQDword(stream));
        }

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
