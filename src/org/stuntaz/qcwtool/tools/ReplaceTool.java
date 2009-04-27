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

package org.stuntaz.qcwtool.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.stuntaz.libqcw.BasicQCWVisitor;
import org.stuntaz.libqcw.blobs.barchart.StudyLineGroupBlob;
import org.stuntaz.libqcw.blobs.barchart.SymbolEntryBlob;
import org.stuntaz.libqcw.blobs.files.WorkspaceFileBlob;
import org.stuntaz.qcwtool.dialogs.MergeConflictDialog;
import org.stuntaz.qcwtool.dialogs.ReplaceDialog;

/**
 * Tool that allows symbol replacement in QCW files.
 * 
 * @author nall
 *
 */
public final class ReplaceTool
    extends AbstractTool
{
    @Override
    public String getName()
    {
        return "Replace";
    }

    @Override
    public String getDescription()
    {
        return "This allows you to move lines and notes associated with one "
            + "symbol to another symbol. Note that this only moves lines in "
            + "chart layouts and will not modify any other "
            + "windows, such as QuoteSheets";
    }

    @Override
    public String getShortDescription()
    {
        return "Moves the lines/notes associated with one symbol to another symbol";
    }

    @Override
    public void run()
    {
        final ReplaceDialog rd = new ReplaceDialog(getComposite().getShell());

        final List<ReplaceDialog.MappingEntry> mapping = rd.open();
        final String fileName = rd.getFilename();
        final boolean removeOldSyms = rd.getRemoveOldEntries();

        if (mapping.size() == 0)
        {
            System.out
                .println("No entries in map -- error condition or cancel...");
            // User closed w/o properly filling out the dialog
            return;
        }

        final Map<StudyLineGroupBlob, Set<SymbolEntryBlob>> removals = new HashMap<StudyLineGroupBlob, Set<SymbolEntryBlob>>();
        final Map<StudyLineGroupBlob, Set<SymbolEntryBlob>> adds = new HashMap<StudyLineGroupBlob, Set<SymbolEntryBlob>>();

        try
        {
            final BufferedInputStream input = new BufferedInputStream(
                new FileInputStream(fileName));
            final WorkspaceFileBlob wfile = new WorkspaceFileBlob();
            wfile.parse(input);
            input.close();

            final boolean[] madeChanges = { false };

            new BasicQCWVisitor()
            {
                private StudyLineGroupBlob curLines = null;
                private boolean needConflictPolicy = true;
                private MergeResolutionType mergePolicy = MergeResolutionType.MergeSkip;

                @Override
                public void visit(final StudyLineGroupBlob lines)
                {
                    curLines = lines;
                    super.visit(lines);
                }

                @Override
                public void visit(final SymbolEntryBlob oldSym)
                {
                    for (final ReplaceDialog.MappingEntry entry : mapping)
                    {
                        final String oldSymName = entry.key;
                        final String newSymName = entry.value;

                        if (oldSym.getSymbolName().equals(oldSymName))
                        {
                            // 1. Check if new symbol exists by checking all
                            //    symbol names in the current line set
                            //    against the new symbol name
                            boolean conflict = false;
                            SymbolEntryBlob newSym = null;
                            for (final SymbolEntryBlob s : curLines
                                .getSymbols())
                            {
                                if (s.getSymbolName().equals(newSymName))
                                {
                                    newSym = s;
                                    conflict = true;
                                }
                            }

                            if (conflict)
                            {
                                if (needConflictPolicy)
                                {
                                    final MergeConflictDialog mcd = new MergeConflictDialog(
                                        getComposite().getShell(), oldSymName,
                                        newSymName);
                                    mcd.open();
                                    mergePolicy = mcd.getMergeType();
                                    needConflictPolicy = !mcd
                                        .useResultsHenceforth();
                                }

                                switch (mergePolicy)
                                {
                                case MergeUnion:
                                {
                                    // Copy old lines to new symbol
                                    // Remove old symbol if needed
                                    newSym.getLineHeaders().addAll(
                                        oldSym.getLineHeaders());
                                    break;
                                }
                                case MergeKeepOld:
                                {
                                    // Replace new lines w/ old lines
                                    // Remove old symbol if needed
                                    newSym.setLineHeaders(oldSym
                                        .getLineHeaders());
                                    break;
                                }
                                case MergeKeepNew:
                                case MergeSkip:
                                {
                                    // No modifications to the new Symbol's entry
                                    break;
                                }
                                }

                                if (mergePolicy != MergeResolutionType.MergeSkip)
                                {
                                    if (removeOldSyms)
                                    {
                                        Set<SymbolEntryBlob> curSet = removals
                                            .get(curLines);
                                        if (curSet == null)
                                        {
                                            curSet = new HashSet<SymbolEntryBlob>();
                                            removals.put(curLines, curSet);
                                        }
                                        curSet.add(oldSym);
                                    }
                                    else if (mergePolicy != MergeResolutionType.MergeKeepNew)
                                    {
                                        // KeepNew && !Skip && !removeOldSyms
                                        // means no change
                                        madeChanges[0] = true;
                                    }
                                }
                            }
                            else
                            {
                                // Copy/Move symbol as needed                                
                                if (removeOldSyms)
                                {
                                    // We can just rename
                                    oldSym.setSymbolName(newSymName);
                                }
                                else
                                {
                                    newSym = new SymbolEntryBlob(newSymName,
                                        oldSym.getLineHeaders());

                                    Set<SymbolEntryBlob> curSet = adds
                                        .get(curLines);
                                    if (curSet == null)
                                    {
                                        curSet = new HashSet<SymbolEntryBlob>();
                                        adds.put(curLines, curSet);
                                    }
                                    curSet.add(newSym);
                                }

                                madeChanges[0] = true;
                            }
                        }
                    }

                    super.visit(oldSym);
                }
            }.visit(wfile.getWorkspace());

            // Do this after the visit to avoid a ConcurrentModificationException
            for (final StudyLineGroupBlob group : removals.keySet())
            {
                for (final SymbolEntryBlob sym : removals.get(group))
                {
                    group.removeSymbol(sym.getSymbolName());
                }
            }
            for (final StudyLineGroupBlob group : adds.keySet())
            {
                for (final SymbolEntryBlob sym : adds.get(group))
                {
                    group.addSymbolEntry(sym);
                }
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
                        "Error Saving File",
                        "Error writing new workspace file: " + e.getMessage());
                }
            }

            else
            {
                MessageDialog.openInformation(getComposite().getShell(),
                    "No Changes", "No changes were needed in the workspace");
            }
        }
        catch (final Exception e)
        {
            MessageDialog.openError(getComposite().getShell(),
                "Error Opening File", "Error processing file: \n"
                    + e.getClass() + ": " + e.getMessage());
        }
    }
}
