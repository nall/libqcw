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

package org.stuntaz.qcwtool.dialogs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.stuntaz.libqcw.QCWException;
import org.stuntaz.libqcw.blobs.files.WorkspaceFileBlob;
import org.stuntaz.libqcw.blobs.workspace.WorkspaceBlob;
import org.stuntaz.libqcw.defines.QStudyType;
import org.stuntaz.qcwtool.tools.MergeDirective;

/**
 * Dialog for merging workspaces.
 * 
 * @author nall
 *
 */
public final class MergeToolDialog
    extends Dialog
{
    private WorkspaceBlob srcWorkspace;
    private WorkspaceBlob tgtWorkspace;

    private List<MergeDirective> results = new ArrayList<MergeDirective>();

    /**
     * Creates a new dialog
     * 
     * @param parent the SWT parent
     */
    public MergeToolDialog(final Shell parent)
    {
        super(parent, SWT.APPLICATION_MODAL);
    }

    /**
     * Returns the target workspace specified in the merge dialog
     * 
     * @return the user-specified target workspace
     */
    public WorkspaceBlob getTargetWorkspace()
    {
        return tgtWorkspace;
    }

    /**
     * Opens the dialog and blocks until the user has closed it.
     * 
     * @return a map of symbol name replacements
     */
    public List<MergeDirective> open()
    {
        srcWorkspace = null;
        tgtWorkspace = null;
        results.clear();

        final Shell parent = getParent();
        final Shell shell = new Shell(parent, SWT.DIALOG_TRIM
            | SWT.APPLICATION_MODAL);
        shell.setText("Merge Study Lines Between Workspaces");

        makeDialog(shell);

        shell.open();
        final Display display = parent.getDisplay();
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch()) display.sleep();
        }

        return results;
    }

    private void makeDialog(final Shell shell)
    {
        shell.setSize(505, 400);
        final GridLayout layout = new GridLayout();
        layout.numColumns = 5;
        shell.setLayout(layout);

        // ROW 1
        final Label lsrc = new Label(shell, SWT.NONE);
        lsrc.setText("Source QCW:");
        GridData data = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        lsrc.setLayoutData(data);

        final Text srcFile = new Text(shell, SWT.SINGLE | SWT.BORDER);
        srcFile.setEditable(false);
        data = new GridData(SWT.FILL, SWT.NONE, true, false);
        data.horizontalSpan = 3;
        srcFile.setLayoutData(data);

        final Button bLoadSrc = new Button(shell, SWT.PUSH);
        bLoadSrc.setText("Load...");
        data = new GridData(SWT.FILL, SWT.CENTER, false, false);
        bLoadSrc.setLayoutData(data);

        bLoadSrc.addSelectionListener(new SelectionListener()
        {

            public void widgetDefaultSelected(final SelectionEvent e)
            {
                widgetSelected(e);
            }

            public void widgetSelected(final SelectionEvent e)
            {
                final FileDialog fd = new FileDialog(shell, SWT.OPEN);
                fd.setText("Open Source QCW Workspace File");
                final String[] filterExt = { "*.qcw", "*.*" };
                fd.setFilterExtensions(filterExt);
                final String fileName = fd.open();

                final WorkspaceFileBlob wfb = new WorkspaceFileBlob();
                final File f = new File(fileName);
                try
                {
                    wfb.parse(new BufferedInputStream(new FileInputStream(f)));
                    srcWorkspace = wfb.getWorkspace();

                    srcFile.setText(f.getName());
                }
                catch (final FileNotFoundException fnfe)
                {
                    throw new QCWException(fnfe.getMessage());
                }
            }

        });

        // ROW 2
        final Label ltgt = new Label(shell, SWT.NONE);
        ltgt.setText("Target QCW:");
        data = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        ltgt.setLayoutData(data);

        final Text tgtFile = new Text(shell, SWT.SINGLE | SWT.BORDER);
        tgtFile.setEditable(false);
        data = new GridData(SWT.FILL, SWT.NONE, true, false);
        data.horizontalSpan = 3;
        tgtFile.setLayoutData(data);

        final Button bLoadTgt = new Button(shell, SWT.PUSH);
        bLoadTgt.setText("Load...");
        data = new GridData(SWT.FILL, SWT.CENTER, false, false);
        bLoadTgt.setLayoutData(data);
        bLoadTgt.addSelectionListener(new SelectionListener()
        {

            public void widgetDefaultSelected(final SelectionEvent e)
            {
                widgetSelected(e);
            }

            public void widgetSelected(final SelectionEvent e)
            {
                final FileDialog fd = new FileDialog(shell, SWT.OPEN);
                fd.setText("Open Target QCW Workspace File");
                final String[] filterExt = { "*.qcw", "*.*" };
                fd.setFilterExtensions(filterExt);
                final String fileName = fd.open();

                final WorkspaceFileBlob wfb = new WorkspaceFileBlob();
                final File f = new File(fileName);
                try
                {
                    wfb.parse(new BufferedInputStream(new FileInputStream(f)));
                    tgtWorkspace = wfb.getWorkspace();

                    tgtFile.setText(f.getName());
                }
                catch (final FileNotFoundException fnfe)
                {
                    throw new QCWException(fnfe.getMessage());
                }
            }

        });

        // ROW 3
        final Composite topTable = new Composite(shell, SWT.BORDER);
        topTable.setLayout(new FillLayout());
        data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.horizontalSpan = 4;
        data.verticalSpan = 4;
        data.verticalIndent = 25;
        topTable.setLayoutData(data);

        final Composite sComp = new Composite(topTable, SWT.NONE);
        final Table chartPairTable = new Table(sComp, SWT.FULL_SELECTION
            | SWT.MULTI | SWT.BORDER);
        final TableColumnLayout tLayout = new TableColumnLayout();
        sComp.setLayout(tLayout);

        final TableColumn c1 = new TableColumn(chartPairTable, SWT.LEFT);
        c1.setText("Source Chart");
        tLayout.setColumnData(c1, new ColumnWeightData(50, 150));
        final TableColumn c2 = new TableColumn(chartPairTable, SWT.LEFT);
        c2.setText("Target Chart");
        tLayout.setColumnData(c2, new ColumnWeightData(50, 150));

        chartPairTable.setHeaderVisible(true);
        chartPairTable.setLinesVisible(true);
        chartPairTable.pack();

        final Button addButton = new Button(shell, SWT.PUSH);
        addButton.setText("Add Pair...");
        data = new GridData(SWT.FILL, SWT.BOTTOM, false, false);
        data.verticalIndent = 25;
        addButton.setLayoutData(data);
        addButton.addSelectionListener(new SelectionListener()
        {
            public void widgetDefaultSelected(final SelectionEvent e)
            {
                widgetSelected(e);
            }

            public void widgetSelected(final SelectionEvent e)
            {
                if (srcWorkspace == null || tgtWorkspace == null)
                {
                    MessageDialog
                        .openError(shell, "Invalid workspaces",
                            "You must load a source and target workspace before continuing.");
                    return;
                }

                final ClassPairingDialog cpd = new ClassPairingDialog(shell);
                final MergeDirective results = new MergeDirective();
                final boolean validResults = cpd.open(srcWorkspace.getCharts(),
                    tgtWorkspace.getCharts(), results);
                if (validResults)
                {
                    final TableItem item = new TableItem(chartPairTable,
                        SWT.None);

                    final String[] pair = { results.source.getChartTitle(),
                        results.target.getChartTitle() };
                    item.setText(pair);
                    item.setData(results);
                }
            }
        });
        chartPairTable.addSelectionListener(new SelectionListener()
        {

            public void widgetDefaultSelected(final SelectionEvent e)
            {
                // This is a double click
                if (e.item instanceof TableItem)
                {
                    final TableItem ti = (TableItem) e.item;

                    if (srcWorkspace == null || tgtWorkspace == null)
                    {
                        MessageDialog
                            .openError(shell, "Invalid workspaces",
                                "You must load a source and target workspace before continuing.");
                        return;
                    }

                    final ClassPairingDialog cpd = new ClassPairingDialog(shell);
                    final MergeDirective results = (MergeDirective) ti
                        .getData();

                    cpd.open(srcWorkspace.getCharts(),
                        tgtWorkspace.getCharts(), results);
                    if (results != null)
                    {
                        final String[] pair = { results.source.getChartTitle(),
                            results.target.getChartTitle() };
                        ti.setText(pair);
                        ti.setData(results);
                    }
                }

            }

            public void widgetSelected(final SelectionEvent e)
            {
                // Do nothing on single click
            }
        });
        chartPairTable.addListener(SWT.MouseMove, new Listener()
        {

            public void handleEvent(final Event event)
            {
                chartPairTable.setToolTipText(null);
                final Point ep = new Point(event.x, event.y);
                for (final TableItem ti : chartPairTable.getItems())
                {
                    final Rectangle r = ti.getBounds(0);
                    r.add(ti.getBounds(1));
                    if (r.contains(ep))
                    {
                        final StringBuffer sb = new StringBuffer();
                        final MergeDirective results = (MergeDirective) ti
                            .getData();
                        if (results.priceEnabled)
                        {
                            sb.append("Price");
                        }
                        for (final QStudyType type : results.studies)
                        {
                            if (sb.length() > 0)
                            {
                                sb.append(", ");
                            }
                            sb.append(type.toString());
                        }

                        chartPairTable.setToolTipText(sb.toString());
                    }
                }
            }

        });

        // ROW 4
        final Button delButton = new Button(shell, SWT.PUSH);
        delButton.setText("Remove Pair(s)");
        data = new GridData(SWT.FILL, SWT.TOP, false, false);
        delButton.setLayoutData(data);
        delButton.addSelectionListener(new SelectionListener()
        {

            public void widgetDefaultSelected(final SelectionEvent e)
            {
                widgetSelected(e);
            }

            public void widgetSelected(final SelectionEvent e)
            {
                chartPairTable.remove(chartPairTable.getSelectionIndices());
            }
        });

        // ROW 5
        Composite spacer = new Composite(shell, SWT.NONE);
        data = new GridData(SWT.FILL, SWT.TOP, true, false);
        data.horizontalSpan = 1;
        spacer.setLayoutData(data);

        // ROW 6
        spacer = new Composite(shell, SWT.NONE);
        data = new GridData(SWT.FILL, SWT.TOP, true, false);
        data.horizontalSpan = 1;
        spacer.setLayoutData(data);

        // ROW 6
        final Label ltooltip = new Label(shell, SWT.WRAP);
        ltooltip
            .setText("Hover mouse over table entry to see studies that will be merged for those charts.");
        data = new GridData(SWT.FILL, SWT.TOP, true, false);
        data.horizontalSpan = 4;
        ltooltip.setLayoutData(data);

        final Button okButton = new Button(shell, SWT.PUSH);
        okButton.setText("Merge and Save...");
        data = new GridData(SWT.FILL, SWT.TOP, false, false);
        //        data.verticalIndent = 25;
        okButton.setLayoutData(data);
        okButton.addSelectionListener(new SelectionListener()
        {

            public void widgetDefaultSelected(final SelectionEvent e)
            {
                widgetSelected(e);
            }

            public void widgetSelected(final SelectionEvent e)
            {
                // Add all merge records from the table to results
                for (final TableItem ti : chartPairTable.getItems())
                {
                    final MergeDirective md = (MergeDirective) ti.getData();
                    results.add(md);
                }

                shell.dispose();
            }

        });

        // ROW 8
        spacer = new Composite(shell, SWT.NONE);
        data = new GridData(SWT.FILL, SWT.TOP, true, false);
        data.horizontalSpan = 4;
        spacer.setLayoutData(data);

        final Button cancelButton = new Button(shell, SWT.PUSH);
        cancelButton.setText("Cancel");
        data = new GridData(SWT.FILL, SWT.TOP, false, false);
        cancelButton.setLayoutData(data);
        cancelButton.addSelectionListener(new SelectionListener()
        {

            // Just close this dialog without doing anything
            public void widgetDefaultSelected(final SelectionEvent e)
            {
                widgetSelected(e);
            }

            public void widgetSelected(final SelectionEvent e)
            {
                shell.dispose();
            }
        });
    }
}
