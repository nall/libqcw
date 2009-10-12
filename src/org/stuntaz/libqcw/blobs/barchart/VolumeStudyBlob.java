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
import org.stuntaz.libqcw.blobs.QDword;
import org.stuntaz.libqcw.blobs.QRecord;
import org.stuntaz.libqcw.blobs.QUtils;
import org.stuntaz.libqcw.blobs.UnsupportedBlob;
import org.stuntaz.libqcw.defines.QStudyType;

/**
 * Represents a volume study, including potential Volume substudies.
 * 
 * @author nall
 *
 */
public final class VolumeStudyBlob
    extends QRecord
    implements IStudyBlob
{
    private int unknown1;
    private final UnsupportedBlob unknown2 = new UnsupportedBlob();
    private final UnsupportedBlob substudies = new UnsupportedBlob();

    @Override
    protected int getInternalSize()
    {
        return QUtils.getSize(QDword.class) + unknown2.getSize();
    }

    public int getSubstudiesSize()
    {
        return substudies.getSize();
    }

    public QStudyType getStudyType()
    {
        return QStudyType.Volume;
    }

    @Override
    public void write(final OutputStream output)
        throws IOException
    {
        super.write(output);

        QUtils.writeQDword(unknown1, output);
        unknown2.write(output);
        substudies.write(output);
    }

    public void parse(final InputStream stream)
        throws IOException
    {
        final int size = parseRecordHeader(stream);

        setUnknown1(QUtils.readQDword(stream));
        unknown2.parse(stream);

        setValid();
        assert (size == getInternalSize());
    }

    /**
     * Parses the specified input stream for volume substudies.
     * 
     * @param stream the stream to parse
     * @throws IOException if there are errors while parsing
     */
    public void parseSubstudies(final InputStream stream)
        throws IOException
    {
        substudies.parse(stream);
    }

    private void setUnknown1(final int value)
    {
        unknown1 = value;
    }

    public void accept(final IBarChartVisitor visitor)
    {
        visitor.visit(this);
    }
}
