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

package org.stuntaz.libqcw.blobs.workspace;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.stuntaz.libqcw.IWorkspaceVisitor;
import org.stuntaz.libqcw.blobs.QRecord;

/**
 * Represents the section header blob that contains multiple entries of
 * {@link SectionHeaderInfoBlob} instances. 
 * 
 * @author nall
 *
 */
public final class SectionHeaderBlob
    extends QRecord
{
    private final int numSections;
    List<SectionHeaderInfoBlob> infos = new ArrayList<SectionHeaderInfoBlob>();

    /**
     * Creates a new SectionHeaderBlob with the specified number of sections.
     * 
     * @param numSections the number of sections in this blob
     */
    public SectionHeaderBlob(final int numSections)
    {
        this.numSections = numSections;
    }

    /**
     * Returns the number of sections in this section header
     * 
     * @return the number of sections defined in this header
     */
    public int getSectionCount()
    {
        return this.numSections;
    }

    /**
     * Returns the {@link SectionHeaderInfoBlob} instances contained in this
     * header.
     * 
     * @return a list of section header info objects
     */
    public List<SectionHeaderInfoBlob> getSections()
    {
        return infos;
    }

    @Override
    protected int getInternalSize()
    {
        int totalBytes = 0;
        for (final SectionHeaderInfoBlob info : infos)
        {
            totalBytes += info.getSize();
        }

        return totalBytes;
    }

    public void parse(final InputStream stream)
        throws IOException
    {
        final int size = parseRecordHeader(stream);

        for (int i = 0; i < numSections; ++i)
        {
            final SectionHeaderInfoBlob info = new SectionHeaderInfoBlob();
            info.parse(stream);
            infos.add(info);
        }

        setValid();
        assert (size == getInternalSize());
    }

    @Override
    public void write(final OutputStream output)
        throws IOException
    {
        super.write(output);

        for (final SectionHeaderInfoBlob info : infos)
        {
            info.write(output);
        }
    }

    /**
     * Accepts the specified workspace visitor
     * 
     * @param visitor the visitor to accept
     */
    public void accept(final IWorkspaceVisitor visitor)
    {
        visitor.visit(this);
    }
}
