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
import org.stuntaz.libqcw.blobs.QByteBuffer;
import org.stuntaz.libqcw.blobs.QRecord;
import org.stuntaz.libqcw.blobs.QUtils;
import org.stuntaz.libqcw.blobs.QWord;
import org.stuntaz.libqcw.defines.QWorkspaceSection;

/**
 * Represents the blob which specifies information about a single section 
 * present in the workspace.
 * 
 * @author nall
 *
 */
public final class SectionHeaderInfoBlob
    extends QRecord
{
    private QWorkspaceSection sectionType;
    private QByteBuffer unknown1 = null;

    /**
     * Returns the type of this section.
     * 
     * @return the type of the section
     */
    public QWorkspaceSection getSectionType()
    {
        assert (isValid());
        return sectionType;
    }

    @Override
    protected int getInternalSize()
    {
        return QUtils.getSize(QWord.class) + unknown1.getSize();
    }

    public void parse(final InputStream stream)
        throws IOException
    {
        final int size = parseRecordHeader(stream);

        sectionType = QWorkspaceSection.forValue(QUtils.readQWord(stream));
        unknown1 = new QByteBuffer(QUtils.readQByteBuffer(stream));

        setValid();
        assert (size == getInternalSize());
    }

    @Override
    public void write(final OutputStream output)
        throws IOException
    {
        super.write(output);

        QUtils.writeQWord(sectionType.getValue(), output);
        unknown1.write(output);
    }

    /**
     * Accepts the specified workspace visitor.
     * 
     * @param visitor the visitor to accept.
     */
    public void accept(final IWorkspaceVisitor visitor)
    {
        visitor.visit(this);
    }
}
