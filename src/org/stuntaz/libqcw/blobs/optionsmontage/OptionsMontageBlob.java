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

package org.stuntaz.libqcw.blobs.optionsmontage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.stuntaz.libqcw.IWorkspaceVisitor;
import org.stuntaz.libqcw.blobs.QSection;
import org.stuntaz.libqcw.blobs.UnsupportedBlob;
import org.stuntaz.libqcw.defines.QWorkspaceSection;

/**
 * Represents an Options Montage section from a workspace file. This section is
 * currently unsupported (that is, undecoded).
 * 
 * @author nall
 *
 */
public final class OptionsMontageBlob
    extends QSection
{
    private final UnsupportedBlob unknown1 = new UnsupportedBlob();
    private final UnsupportedBlob unknown2 = new UnsupportedBlob();
    private final UnsupportedBlob unknown3 = new UnsupportedBlob();
    private final UnsupportedBlob unknown4 = new UnsupportedBlob();
    private final UnsupportedBlob unknown5 = new UnsupportedBlob();
    private final UnsupportedBlob unknown6 = new UnsupportedBlob();

    @Override
    protected int getInternalSize()
    {
        return unknown1.getSize() + unknown2.getSize() + unknown3.getSize()
            + unknown4.getSize() + unknown5.getSize() + unknown6.getSize();
    }

    @Override
    public QWorkspaceSection getSectionType()
    {
        return QWorkspaceSection.OptionsMontage;
    }

    public void parse(final InputStream stream)
        throws IOException
    {
        unknown1.parse(stream);
        unknown2.parse(stream);
        unknown3.parse(stream);
        unknown4.parse(stream);
        unknown5.parse(stream);
        unknown6.parse(stream);

        setValid();
    }

    @Override
    public void write(final OutputStream output)
        throws IOException
    {
        unknown1.write(output);
        unknown2.write(output);
        unknown3.write(output);
        unknown4.write(output);
        unknown5.write(output);
        unknown6.write(output);
    }

    @Override
    public void accept(final IWorkspaceVisitor visitor)
    {
        visitor.visit(this);
    }
}
