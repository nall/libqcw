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

/**
 * Static constants used when parsing and writing files
 * @author nall
 *
 */
final class QDefines
{
    /**
     * The size of a QRecord header. This includes 1 byte of type and 4 bytes
     * of length.
     */
    static final int QRECORD_HEADER_SIZE = 5;

    /**
     * The data type for a QByte (1 byte)
     */
    static final char QBYTE_TYPE = 0;

    /**
     * The data type for a QWord (2 bytes)
     */
    static final char QWORD_TYPE = 1;

    /**
     * The data type for a QDword (4 bytes)
     */
    static final char QDWORD_TYPE = 2;

    /**
     * The data type for a QQword (8 bytes)
     */
    static final char QQWORD_TYPE = 4;

    /**
     * The data type for a QByteBuffer of length <= 255
     */
    static final char QBYTE_BUFFER_TYPE8 = 8;

    /**
     * The data type for a QByteBuffer of length > 255
     */
    static final char QBYTE_BUFFER_TYPE16 = 0x10;

    /**
     * The data type for a QRecord
     */
    static final char QRECORD_TYPE = 0xB8;

}
