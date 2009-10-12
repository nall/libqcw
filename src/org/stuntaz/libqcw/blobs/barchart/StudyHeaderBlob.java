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
import org.stuntaz.libqcw.blobs.QDword;
import org.stuntaz.libqcw.blobs.QRecord;
import org.stuntaz.libqcw.blobs.QUtils;
import org.stuntaz.libqcw.defines.QStudyType;

/**
 * Represents a header section for a bar chart study.
 * 
 * @author nall
 *
 */
public final class StudyHeaderBlob
    extends QRecord
{
    private final List<IStudyBlob> studies = new ArrayList<IStudyBlob>();
    private final boolean isPriceStudy;

    /**
     * Creates a new StudyHeaderBlob.
     * 
     * @param isPriceStudy true if this study represents an "upper study" which
     *      should be drawn in the chart's price area
     */
    public StudyHeaderBlob(final boolean isPriceStudy)
    {
        this.isPriceStudy = isPriceStudy;
    }

    /**
     * Adds the specified study to 
     * @param study
     */
    public void addStudy(final IStudyBlob study)
    {
        studies.add(study);
    }

    /**
     * Returns true if this study is an "upper study", false otherwise.
     * 
     * @return true if this study is one that is drawn in the price window
     */
    public boolean isPriceStudy()
    {
        return this.isPriceStudy;
    }

    /**
     * Get the list of studies defined in this header.
     * 
     * @return a list of studies.
     */
    public List<IStudyBlob> getStudies()
    {
        return studies;
    }

    @Override
    protected int getInternalSize()
    {
        int totalBytes = QUtils.getSize(QDword.class); // studies.size()

        for (final IStudyBlob study : studies)
        {
            totalBytes += QUtils.getSize(QDword.class); // type DWORD
            totalBytes += study.getSize();
            totalBytes += study.getSubstudiesSize();
        }

        return totalBytes;
    }

    @Override
    public void write(final OutputStream output)
        throws IOException
    {
        super.write(output);

        QUtils.writeQDword(studies.size(), output);

        for (final IStudyBlob study : studies)
        {
            final QStudyType type = study.getStudyType();
            QUtils.writeQDword(type.getValue(), output);

            study.write(output);
        }
    }

    public void parse(final InputStream stream)
        throws IOException
    {
        final int size = parseRecordHeader(stream);
        final int numTypes = QUtils.readQDword(stream);

        for (int i = 0; i < numTypes; ++i)
        {
            final QStudyType typeID = QStudyType.forValue(QUtils
                .readQDword(stream));

            final IStudyBlob study;
            if (typeID == QStudyType.Volume)
            {
                study = new VolumeStudyBlob();
            }
            else
            {
                study = new UnsupportedStudyBlob(typeID);
            }
            study.parse(stream);
            addStudy(study);

            // Only Volume can have sub-studies and as best I can tell
            // the only way to know is if we haven't reached blobSize
            // yet.
            final boolean hasSubstudies = (typeID == QStudyType.Volume)
                && (getInternalSize() < size);
            if (hasSubstudies)
            {
                assert (study instanceof VolumeStudyBlob);
                final VolumeStudyBlob s = (VolumeStudyBlob) study;
                s.parseSubstudies(stream);
            }
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
