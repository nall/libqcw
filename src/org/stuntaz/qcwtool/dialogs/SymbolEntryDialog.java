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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Manual symbol replacement entry dialog
 * 
 * @author nall
 *
 */
public final class SymbolEntryDialog
    extends Dialog
{

    /**
     * Creates a new dialog
     * 
     * @param parentShell the SWT shell
     */
    public SymbolEntryDialog(final Shell parentShell)
    {
        super(parentShell, SWT.APPLICATION_MODAL);
    }

    /**
     * Opens the dialog and blocks until the user closes it
     * 
     * @return An array of strings. This array has two elements -- the old 
     *      symbol and the new symbol
     */
    public String[] open()
    {
        final String[] pair = { null, null };
        final Shell parent = getParent();
        final Shell area = new Shell(parent, SWT.DIALOG_TRIM
            | SWT.APPLICATION_MODAL);
        area.setSize(400, 125);
        area.setText("Add Symbol Mapping");
        final GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        layout.verticalSpacing = 2;
        area.setLayout(layout);

        final Label l1 = new Label(area, SWT.NONE);
        l1.setText("Old Symbol:");
        GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
        l1.setLayoutData(data);

        Composite spacer = new Composite(area, SWT.NONE);
        data = new GridData(SWT.FILL, SWT.FILL, true, true);
        spacer.setLayoutData(data);

        final Label l2 = new Label(area, SWT.NONE);
        l2.setText("New Symbol:");
        data = new GridData(SWT.FILL, SWT.CENTER, true, false);
        l2.setLayoutData(data);

        spacer = new Composite(area, SWT.NONE);
        data = new GridData(SWT.FILL, SWT.FILL, true, true);
        spacer.setLayoutData(data);

        final Text t1 = new Text(area, SWT.SINGLE | SWT.BORDER);
        data = new GridData(SWT.FILL, SWT.CENTER, true, false);
        data.horizontalSpan = 2;
        t1.setLayoutData(data);

        final Text t2 = new Text(area, SWT.SINGLE | SWT.BORDER);
        data = new GridData(SWT.FILL, SWT.CENTER, true, false);
        data.horizontalSpan = 2;
        t2.setLayoutData(data);

        spacer = new Composite(area, SWT.NONE);
        data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.horizontalSpan = 2;
        spacer.setLayoutData(data);

        final Button cancelButton = new Button(area, SWT.PUSH);
        cancelButton.setText("Cancel");
        data = new GridData(SWT.FILL, SWT.CENTER, false, false);
        cancelButton.setLayoutData(data);
        cancelButton.addSelectionListener(new SelectionListener()
        {

            public void widgetDefaultSelected(final SelectionEvent e)
            {
                widgetSelected(e);
            }

            public void widgetSelected(final SelectionEvent e)
            {
                // Dispose this dialog on Cancel
                area.dispose();
            }

        });

        final Button okButton = new Button(area, SWT.PUSH);
        okButton.setText("OK");
        data = new GridData(SWT.FILL, SWT.CENTER, false, false);
        okButton.setLayoutData(data);
        okButton.addSelectionListener(new SelectionListener()
        {

            public void widgetDefaultSelected(final SelectionEvent e)
            {
                widgetSelected(e);
            }

            public void widgetSelected(final SelectionEvent e)
            {
                pair[0] = t1.getText().trim();
                pair[1] = t2.getText().trim();

                if (pair[0].length() == 0 || pair[1].length() == 0)
                {
                    MessageDialog.openError(area, "Invalid Symbol",
                        "Invalid Symbol(s): Symbol cannot be empty");
                }
                else
                {
                    area.dispose();
                }
            }

        });

        area.open();
        final Display display = parent.getDisplay();
        while (!area.isDisposed())
        {
            if (!display.readAndDispatch()) display.sleep();
        }

        return pair;
    }
}
