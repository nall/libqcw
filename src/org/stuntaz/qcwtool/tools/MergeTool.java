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

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.stuntaz.libqcw.BasicQCWVisitor;
import org.stuntaz.libqcw.blobs.barchart.BarChartBlob;
import org.stuntaz.libqcw.blobs.barchart.StudyLineGroupBlob;
import org.stuntaz.libqcw.blobs.barchart.SymbolEntryBlob;
import org.stuntaz.libqcw.blobs.files.WorkspaceFileBlob;
import org.stuntaz.libqcw.defines.QStudyType;
import org.stuntaz.qcwtool.dialogs.MergeConflictDialog;
import org.stuntaz.qcwtool.dialogs.MergeToolDialog;

/**
 * Merge tool.
 * 
 * @author nall
 *
 */
public final class MergeTool
    extends AbstractTool
{
    private MergeResolutionType mergePolicy = MergeResolutionType.MergeSkip;
    private boolean needConflictPolicy = true;

    @Override
    public String getName()
    {
        return "Merge";
    }

    @Override
    public String getShortDescription()
    {
        return "Merges the lines from a source workspace into a target workspace, generating a new workspace";
    }

    @Override
    public void run()
    {
        mergePolicy = MergeResolutionType.MergeSkip;
        needConflictPolicy = true;

        final MergeToolDialog mtd = new MergeToolDialog(getComposite()
            .getShell());
        final List<MergeDirective> merges = mtd.open();

        if (merges.isEmpty())
        {
            return;
        }

        final boolean[] madeChanges = { false };

        final WorkspaceFileBlob wfile = new WorkspaceFileBlob();
        wfile.setWorkspace(mtd.getTargetWorkspace());

        // For each merge directive, merge the requested studies
        for (final MergeDirective md : merges)
        {
            wfile.getWorkspace().accept(new BasicQCWVisitor()
            {
                @Override
                public void visit(final BarChartBlob chart)
                {
                    if (chart == md.target)
                    {
                        madeChanges[0] |= mergeCharts(md);
                    }
                }
            });
        }

        if (madeChanges[0])
        {
            final FileDialog fd = new FileDialog(getComposite().getShell(),
                SWT.SAVE);
            fd.setText("Save Workspace File");
            final String[] filterExt = { "*.qcw", "*.*" };
            fd.setFilterExtensions(filterExt);
            final String saveFileName = fd.open();

            try
            {
                if (saveFileName != null)
                {

                    final BufferedOutputStream output = new BufferedOutputStream(
                        new FileOutputStream(saveFileName));
                    wfile.write(output);
                    output.flush();
                    output.close();
                }
            }
            catch (final IOException e)
            {
                MessageDialog.openError(getComposite().getShell(),
                    "Error Saving File", "Error writing new workspace file: "
                        + e.getMessage());
            }
        }

        else
        {
            MessageDialog.openInformation(getComposite().getShell(),
                "No Changes", "The merge resulted in no changes");
        }

    }

    private boolean mergeCharts(final MergeDirective md)
    {
        boolean madeChanges = false;

        for (final QStudyType type : md.studies)
        {
            // Find target study lines
            final StudyLineGroupBlob srcLines = getLines(md.source, type,
                md.priceEnabled);
            final StudyLineGroupBlob tgtLines = getLines(md.target, type,
                md.priceEnabled);

            for (final SymbolEntryBlob srcSym : srcLines.getSymbols())
            {
                SymbolEntryBlob conflictingSym = null;
                for (final SymbolEntryBlob tgtSym : tgtLines.getSymbols())
                {
                    if (tgtSym.getSymbolName().equals(srcSym.getSymbolName()))
                    {
                        conflictingSym = tgtSym;
                        break;
                    }
                }

                // No conflict -- just add an entry
                if (conflictingSym == null)
                {
                    tgtLines.addSymbolEntry(srcSym);
                    madeChanges = true;
                }
                else
                {
                    // conflict resolution
                    if (needConflictPolicy)
                    {
                        final MergeConflictDialog mcd = new MergeConflictDialog(
                            getComposite().getShell(),
                            conflictingSym.getSymbolName(), conflictingSym.getSymbolName());
                        mcd.open();
                        mergePolicy = mcd.getMergeType();
                        needConflictPolicy = !mcd.useResultsHenceforth();
                    }

                    switch (mergePolicy)
                    {
                    case MergeKeepNew:
                    case MergeSkip:
                    {
                        // Do nothing
                        break;
                    }
                    case MergeUnion:
                    {
                        conflictingSym.getLineHeaders().addAll(
                            srcSym.getLineHeaders());
                        madeChanges = true;
                        break;
                    }
                    case MergeKeepOld:
                    {
                        conflictingSym.setLineHeaders(srcSym.getLineHeaders());
                        madeChanges = true;
                        break;
                    }
                    }
                }
            }
        }

        return madeChanges;
    }

    private StudyLineGroupBlob getLines(
        final BarChartBlob chart,
        final QStudyType type,
        final boolean getLinesForPrice)
    {
        for (int i = 0; i < chart.getStudies().size(); ++i)
        {
            if (chart.getStudies().get(i).isPriceStudy() && getLinesForPrice)
            {
                return chart.getStudyLines().get(i);
            }
            else if (chart.getStudies().get(i).getStudies().get(0)
                .getStudyType() == type)
            {
                return chart.getStudyLines().get(i);
            }
        }

        return null;
    }
}
