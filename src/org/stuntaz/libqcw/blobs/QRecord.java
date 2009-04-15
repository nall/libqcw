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

package org.stuntaz.libqcw.blobs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class represents the base class of the record format. It is the class
 * from which all useful object model classes are derived.
 * 
 * @author nall
 *
 */
public abstract class QRecord
    extends QPrimitive
{
    @Override
    public int getSize()
    {
        return isValid() ? (getInternalSize() + QDefines.QRECORD_HEADER_SIZE)
            : 0;
    }

    @Override
    public void write(final OutputStream output)
        throws IOException
    {
        super.write(output);

        QUtils.writeDword(getInternalSize(), output);
    }

    protected int parseRecordHeader(final InputStream stream)
        throws IOException
    {
        final int type = QUtils.readByte(stream);
        assert (type == getHeaderType());

        final int size = (int) QUtils.readVal(4, stream);
        return size;
    }

    @Override
    protected char getHeaderType()
    {
        return QDefines.QRECORD_TYPE;
    }
}
