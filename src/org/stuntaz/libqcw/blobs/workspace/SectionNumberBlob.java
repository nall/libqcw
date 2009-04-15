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

package org.stuntaz.libqcw.blobs.workspace;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.stuntaz.libqcw.IWorkspaceVisitor;
import org.stuntaz.libqcw.blobs.QRecord;
import org.stuntaz.libqcw.blobs.QUtils;
import org.stuntaz.libqcw.blobs.QWord;

/**
 * Represents the blob which specifies the number of sections in a workspace
 * blob.
 * 
 * @author nall
 *
 */
public final class SectionNumberBlob
    extends QRecord
{
    private QWord numSections = new QWord();

    /**
     * Returns the number of sections in this workspace blob.
     * @return the number of sections
     */
    public int getSectionCount()
    {
        assert (isValid());
        return numSections.getValue();
    }

    @Override
    protected int getInternalSize()
    {
        return numSections.getSize();
    }

    public void parse(final InputStream stream)
        throws IOException
    {
        final int size = parseRecordHeader(stream);
        numSections = new QWord(QUtils.readQWord(stream));

        setValid();
        assert (size == getInternalSize());
    }

    @Override
    public void write(final OutputStream output)
        throws IOException
    {
        super.write(output);
        numSections.write(output);
    }

    /**
     * Accepts the specified workspace visitor.
     * @param visitor the visitor to accept
     */
    public void accept(final IWorkspaceVisitor visitor)
    {
        visitor.visit(this);
    }
}
