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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.stuntaz.qcwtool.tools.MergeResolutionType;

/**
 * Dialog box for merge resolution conflict input.
 * 
 * @author nall
 *
 */
public final class MergeConflictDialog
    extends Dialog
{
    private boolean useResultsHenceforth;
    private MergeResolutionType resolutionType = MergeResolutionType.MergeSkip;
    private String oldSym;
    private String newSym;

    /**
     * Constructs a new MergeConflictDialog.
     * 
     * @param parent the SWT parent
     * @param oldSym the old Symbol name
     * @param newSym the new Symbol name
     */
    public MergeConflictDialog(
        final Shell parent,
        final String oldSym,
        final String newSym)
    {
        super(parent, SWT.APPLICATION_MODAL);
        this.oldSym = oldSym;
        this.newSym = newSym;
    }

    protected Point getInitialSize()
    {
        return new Point(400, 250);
    }

    /**
     * Get the resulting conflict resolution type.
     * 
     * @return the conflict resolution type
     */
    public MergeResolutionType getMergeType()
    {
        return resolutionType;
    }

    /**
     * Returns true if the user wants to use the current merge type for any
     * further conflicts found during this merge operation.
     * 
     * @return true if the current merge type should be used for future conflicts.
     */
    public boolean useResultsHenceforth()
    {
        return useResultsHenceforth;
    }

    protected Control createContents(final Composite area)
    {
        //   final Composite area = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        area.setLayout(layout);

        // Top Row
        final Label l = new Label(area, SWT.WRAP);
        l.setText("Notes and/or Lines for \"" + newSym
            + "\" already exist in this workspace.");
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        l.setLayoutData(data);

        // Middle Row
        final Group group = new Group(area, SWT.NONE);
        layout = new GridLayout();
        layout.numColumns = 2;
        group.setLayout(layout);
        group.setText("Choose a Merge Policy");
        data = new GridData(SWT.FILL, SWT.CENTER, true, false);
        group.setLayoutData(data);

        final Button[] policies = new Button[4];
        final Button m1 = new Button(group, SWT.RADIO);
        m1.setData(MergeResolutionType.MergeUnion);
        data = new GridData(SWT.RIGHT, SWT.FILL, false, true);
        m1.setLayoutData(data);
        policies[0] = m1;

        final Label l1 = new Label(group, SWT.WRAP);
        l1.setText("Use all lines from both source symbol (" + oldSym
            + ") and target symbol (" + newSym + ")");
        data = new GridData(SWT.FILL, SWT.FILL, true, true);
        l1.setLayoutData(data);

        final Button m2 = new Button(group, SWT.RADIO);
        m2.setData(MergeResolutionType.MergeKeepOld);
        data = new GridData(SWT.NONE, SWT.NONE, false, false);
        m2.setLayoutData(data);
        policies[1] = m2;

        final Label l2 = new Label(group, SWT.WRAP);
        l2.setText("Use lines from source symbol (" + oldSym
            + "), discarding any existing lines from target symbol (" + newSym
            + ")");
        data = new GridData(SWT.FILL, SWT.FILL, true, true);
        l2.setLayoutData(data);

        final Button m3 = new Button(group, SWT.RADIO);
        m3.setData(MergeResolutionType.MergeKeepNew);
        data = new GridData(SWT.FILL, SWT.FILL, true, true);
        m3.setLayoutData(data);
        policies[2] = m3;

        final Label l3 = new Label(group, SWT.WRAP);
        l3.setText("Use lines from target symbol (" + newSym
            + "), ignoring any existing lines from source symbol (" + oldSym
            + ")");
        data = new GridData(SWT.FILL, SWT.FILL, true, true);
        l3.setLayoutData(data);

        final Button m4 = new Button(group, SWT.RADIO);
        m4.setData(MergeResolutionType.MergeSkip);
        m4.setSelection(true);
        data = new GridData(SWT.FILL, SWT.FILL, true, true);
        m4.setLayoutData(data);
        policies[3] = m4;

        final Label l4 = new Label(group, SWT.WRAP);
        l4.setText("Skip this replacement");
        data = new GridData(SWT.FILL, SWT.FILL, true, true);
        l4.setLayoutData(data);

        // Bottom Row
        final Composite bottomPane = new Composite(area, SWT.NONE);
        final GridLayout gLayout = new GridLayout();
        gLayout.numColumns = 6;
        bottomPane.setLayout(gLayout);

        final Button check = new Button(bottomPane, SWT.CHECK);
        check.setText("Use this merge policy for all future conflicts");
        check.setSelection(false);
        GridData gData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gData.horizontalSpan = 5;
        check.setLayoutData(gData);

        final Button okButton = new Button(bottomPane, SWT.PUSH);
        okButton.setText("OK");
        gData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        gData.horizontalSpan = 1;
        gData.horizontalIndent = 60;
        okButton.setLayoutData(gData);
        okButton.addSelectionListener(new SelectionListener()
        {

            public void widgetDefaultSelected(final SelectionEvent e)
            {
                widgetSelected(e);
            }

            public void widgetSelected(final SelectionEvent e)
            {
                for (int i = 0; i < MergeResolutionType.values().length; ++i)
                {
                    if (policies[i].getSelection())
                    {
                        resolutionType = (MergeResolutionType) policies[i]
                            .getData();
                    }
                }
                useResultsHenceforth = check.getSelection();
                area.dispose();
            }

        });

        return area;
    }

    /**
     * Opens this dialog and blocks until it has been closed.
     * 
     */
    public void open()
    {
        final Shell parent = getParent();
        final Shell shell = new Shell(parent, SWT.DIALOG_TRIM
            | SWT.APPLICATION_MODAL);
        shell.setText("Resolve Merge Conflict");
        shell.setSize(getInitialSize());

        createContents(shell);

        shell.open();
        final Display display = parent.getDisplay();
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch()) display.sleep();
        }
    }

}
