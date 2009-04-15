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

package org.stuntaz.libqcw.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.stuntaz.libqcw.BasicQCWVisitor;
import org.stuntaz.libqcw.QOptions;
import org.stuntaz.libqcw.blobs.barchart.BarChartBlob;
import org.stuntaz.libqcw.blobs.barchart.IStudyBlob;
import org.stuntaz.libqcw.blobs.barchart.LineHeaderBlob;
import org.stuntaz.libqcw.blobs.barchart.StudyHeaderBlob;
import org.stuntaz.libqcw.blobs.barchart.SymbolEntryBlob;
import org.stuntaz.libqcw.blobs.files.WorkspaceFileBlob;

/**
 * Basic tool to parse QCW files and dump all lines.
 * @author nall
 *
 */

public final class ParseTool
{

    /**
     * @param args command line arguments. arg[0] should be the filename to parse
     */
    public static void main(final String[] args)
    {
        try
        {
            QOptions.WORKSPACE_CONTEXT = true;

            if (args.length < 1 || args.length > 2)
            {
                System.err
                    .println("usage: ParseTool <input_qcw> [<output_qcw>]");
                System.exit(1);
            }

            final String fInName = args[0];
            final String fOutName = args.length == 2 ? args[1] : null;

            // This is how to read a workspace file
            final BufferedInputStream iStream = new BufferedInputStream(
                new FileInputStream(fInName));

            final WorkspaceFileBlob qcw = new WorkspaceFileBlob();

            qcw.parse(iStream);

            // This is how to write out a workspace
            if (fOutName != null)
            {
                final BufferedOutputStream oStream = new BufferedOutputStream(
                    new FileOutputStream(fOutName));
                qcw.write(oStream);
                oStream.close();
            }

            // This is how to iterate over studies and lines in a chart
            // This is done in an anonymous class below. More useful implementations
            // might use a named class to avoid all of the code sitting
            // in the middle of this function, cluttering things.
            new BasicQCWVisitor()
            {
                @Override
                public void visit(final BarChartBlob chart)
                {
                    System.out.println("Chart [" + chart.getChartTitleSymbol()
                        + "] [" + chart.getChartTimePeriod() + "]");
                    super.visit(chart);
                }

                @Override
                public void visit(final StudyHeaderBlob studyHeader)
                {
                    for (final IStudyBlob study : studyHeader.getStudies())
                    {
                        System.out.println("Study: " + study.getStudyType());
                    }
                    super.visit(studyHeader);
                }

                @Override
                public void visit(final SymbolEntryBlob symbol)
                {
                    System.out.println(symbol.getSymbolName() + " lines:");
                    super.visit(symbol);
                }

                @Override
                public void visit(final LineHeaderBlob line)
                {
                    System.out.println("\t" + line);
                    super.visit(line);
                }

            }.visit(qcw.getWorkspace());
        }
        catch (final IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
