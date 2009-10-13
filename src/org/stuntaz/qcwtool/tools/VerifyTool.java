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

package org.stuntaz.qcwtool.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.stuntaz.libqcw.BasicQCWVisitor;
import org.stuntaz.libqcw.QCWException;
import org.stuntaz.libqcw.blobs.QSection;
import org.stuntaz.libqcw.blobs.barchart.BarChartSectionBlob;
import org.stuntaz.libqcw.blobs.barchart.LineHeaderBlob;
import org.stuntaz.libqcw.blobs.browser.BrowserBlob;
import org.stuntaz.libqcw.blobs.files.WorkspaceFileBlob;
import org.stuntaz.libqcw.blobs.hotlist.HotlistBlob;
import org.stuntaz.libqcw.blobs.islandbook.IslandBookBlob;
import org.stuntaz.libqcw.blobs.level2.Level2Blob;
import org.stuntaz.libqcw.blobs.optionsmontage.OptionsMontageBlob;
import org.stuntaz.libqcw.blobs.quotesheet.QuotesheetBlob;
import org.stuntaz.libqcw.blobs.ragingbull.RagingBullBlob;
import org.stuntaz.libqcw.blobs.singlequote.SingleQuoteBlob;
import org.stuntaz.libqcw.blobs.tabularbar.TabularBarBlob;
import org.stuntaz.libqcw.blobs.timeandsales.TimeAndSalesBlob;
import org.stuntaz.libqcw.blobs.workspace.WorkspaceBlob;
import org.stuntaz.libqcw.defines.QWorkspaceSection;

/**
 * Tool to parse a QCW file, then write it, and verify that what was written
 * is identical to what was read.
 * 
 * @author nall
 *
 */
public final class VerifyTool
    extends AbstractTool
{
    @Override
    public String getDescription()
    {
        return "This tool reads in a QCW workspace file, and writes it back out. "
            + "It then checks that what was written is identical to what was read.";
    }

    @Override
    public String getName()
    {
        return "Verify";
    }

    @Override
    public String getShortDescription()
    {
        return "Tests that a given workspace file can be read and written correctly";
    }

    @Override
    public void run()
    {
        final FileDialog fd = new FileDialog(getComposite().getShell(),
            SWT.OPEN);
        fd.setText("Open Workspace File to Verify");
        final String[] filterExt = { "*.qcw", "*.*" };
        fd.setFilterExtensions(filterExt);
        final String fileName = fd.open();
        verify(fileName);
    }

    private void verify(final String fileName)
    {
        try
        {
            final FileInputStream fStream = new FileInputStream(fileName);
            final BufferedInputStream streamBuf = new BufferedInputStream(
                fStream);
            final byte[] input = new byte[(int) fStream.getChannel().size()];
            final int size = streamBuf.read(input);
            assert (size == fStream.getChannel().size());

            final ByteArrayInputStream stream = new ByteArrayInputStream(input);
            final WorkspaceFileBlob qcw = new WorkspaceFileBlob();

            qcw.parse(stream);

            final ByteArrayOutputStream ostream = new ByteArrayOutputStream(qcw
                .getWorkspace().getSize());
            final BufferedOutputStream ostreamBuf = new BufferedOutputStream(
                ostream);

            qcw.write(ostreamBuf);

            ostreamBuf.flush();

            if (Arrays.equals(input, ostream.toByteArray()))
            {
                final String[][] stats = getStatsTableEntries(qcw
                    .getWorkspace());
                final MessageDialog md = new MessageDialog(getComposite()
                    .getShell(), "Verify Successful", null,
                    "File verified successfully!", MessageDialog.INFORMATION,
                    new String[] { "OK" }, 0)
                {
                    @Override
                    protected Control createCustomArea(Composite parent)
                    {
                        parent.setLayout(new GridLayout());
                        final Table table = new Table(parent,
                            SWT.FULL_SELECTION | SWT.BORDER);
                        GridData gridData = new GridData(SWT.CENTER,
                            SWT.CENTER, true, false);
                        table.setLayoutData(gridData);

                        TableColumn tc1 = new TableColumn(table, SWT.LEFT);
                        tc1.setText("Section Type");
                        tc1.setWidth(100);
                        tc1.setMoveable(false);
                        tc1.setResizable(false);

                        TableColumn tc2 = new TableColumn(table, SWT.LEFT);
                        tc2.setText("Count");
                        tc2.setWidth(100);
                        tc2.setMoveable(false);
                        tc2.setResizable(false);

                        TableColumn tc3 = new TableColumn(table, SWT.RIGHT);
                        tc3.setText("Size (In Bytes)");
                        tc3.setWidth(100);
                        tc3.setMoveable(false);
                        tc3.setResizable(false);

                        table.setHeaderVisible(true);
                        for (final String[] data : stats)
                        {
                            TableItem item = new TableItem(table, SWT.NORMAL);
                            item.setText(data);
                        }
                        table.pack();
                        return table;
                    }
                };
                md.open();
            }
            else
            {
                System.out.println("l1: " + input.length);
                System.out.println("l2: " + ostream.toByteArray().length);
                for (int i = 0; i < input.length; ++i)
                {
                    if (input[i] != ostream.toByteArray()[i])
                    {
                        System.out.println("MISMATCH at offset 0x"
                            + Integer.toHexString(i));
                    }
                }
                final MessageDialog md = new MessageDialog(getComposite()
                    .getShell(), "Verify Failed", null,
                    "File verification failed!", MessageDialog.ERROR,
                    new String[] { "OK" }, 0);
                md.open();
            }
        }
        catch (final QCWException e)
        {
            final MessageDialog md = new MessageDialog(getComposite()
                .getShell(), "Verify Failed", null,
                "Error occurred while reading file: " + e.getMessage(),
                MessageDialog.ERROR, new String[] { "OK" }, 0);
            md.open();
        }
        catch (final IOException e)
        {
            final MessageDialog md = new MessageDialog(getComposite()
                .getShell(), "I/O error", null,
                "An unknown I/O error occurred!", MessageDialog.ERROR,
                new String[] { "OK" }, 0);
            md.open();
        }
        catch(final AssertionError ass)
        {
        	StringBuffer buf = new StringBuffer();
        	for(final StackTraceElement elt : ass.getStackTrace())
        	{
        		buf.append(elt.toString() + "\n");
        	}
            final MessageDialog md = new MessageDialog(getComposite()
                    .getShell(), "Assertion Failed", null,
                    "An assertion failed: " + buf, MessageDialog.ERROR,
                    new String[] { "OK" }, 0);
                md.open();        	
        }
    }

    private String[][] getStatsTableEntries(final WorkspaceBlob workspace)
    {
        final int[] counts = new int[QWorkspaceSection.values().length];
        final int[] sizes = new int[QWorkspaceSection.values().length];
        final int[] numLines = { 0 };

        new BasicQCWVisitor()
        {
            private void updateStats(final QSection section)
            {
                ++counts[section.getSectionType().ordinal()];
                sizes[section.getSectionType().ordinal()] += section.getSize();
            }

            @Override
            public void visit(final BarChartSectionBlob section)
            {
                updateStats(section);
                super.visit(section);
            }

            @Override
            public void visit(final TimeAndSalesBlob section)
            {
                updateStats(section);
            }

            @Override
            public void visit(final TabularBarBlob section)
            {
                updateStats(section);
            }

            @Override
            public void visit(final QuotesheetBlob section)
            {
                updateStats(section);
            }

            @Override
            public void visit(final BrowserBlob section)
            {
                updateStats(section);
            }

            @Override
            public void visit(final Level2Blob section)
            {
                updateStats(section);
            }

            @Override
            public void visit(final HotlistBlob section)
            {
                updateStats(section);
            }

            @Override
            public void visit(final SingleQuoteBlob section)
            {
                updateStats(section);
            }

            @Override
            public void visit(final IslandBookBlob section)
            {
                updateStats(section);
            }

            @Override
            public void visit(final RagingBullBlob section)
            {
                updateStats(section);
            }

            @Override
            public void visit(final OptionsMontageBlob section)
            {
                updateStats(section);
            }

            @Override
            public void visit(final LineHeaderBlob line)
            {
                ++numLines[0];
            }
        }.visit(workspace);

        // A line for each section + 1 for the number of lines
        final String[][] stats = new String[counts.length + 1][];
        int i = 0;
        for (final QWorkspaceSection type : QWorkspaceSection.values())
        {
            stats[i++] = new String[] { type.name(),
                Integer.toString(counts[type.ordinal()]),
                Integer.toString(sizes[type.ordinal()]) };
            if (type == QWorkspaceSection.BarChart)
            {
                stats[i++] = new String[] { "    Total Lines",
                    Integer.toString(numLines[0]), "" };
            }
        }

        return stats;
    }
}
