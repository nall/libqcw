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

package org.stuntaz.qcwtool;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.stuntaz.qcwtool.tools.AbstractTool;
import org.stuntaz.qcwtool.tools.MergeTool;
import org.stuntaz.qcwtool.tools.ReplaceTool;
import org.stuntaz.qcwtool.tools.ToolRegistry;
import org.stuntaz.qcwtool.tools.VerifyTool;

/**
 * The main view for QCWTool
 * 
 * @author nall
 *
 */
public class IntroView
    extends ViewPart
{
    /**
     * The ID for this view
     */
    public static final String ID = "org.stuntaz.qcwtool.introview";

    private Composite composite;
    private AbstractTool curTool;
    private final String okText = "Run Tool...";

    // Instantiate the tools -- this causes them to be added to the ToolRegistry
    @SuppressWarnings("unused")
    private final VerifyTool verifyTool = new VerifyTool();
    @SuppressWarnings("unused")
    private final ReplaceTool replaceTool = new ReplaceTool();
    @SuppressWarnings("unused")
    private final MergeTool mergeTool = new MergeTool();

    class QSelectionListener
        implements SelectionListener
    {

        public void widgetDefaultSelected(final SelectionEvent e)
        {
            widgetSelected(e);
        }

        public void widgetSelected(final SelectionEvent e)
        {
            assert (e.getSource() instanceof Button);
            final Button b = (Button) e.getSource();

            if (b.getData() == null)
            {
                assert (b.getText().equals(okText));
                curTool.run();
            }
            else
            {
                assert (b.getData() instanceof AbstractTool);
                curTool = (AbstractTool) b.getData();
            }
        }
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize
     * it.
     */
    @Override
    public void createPartControl(final Composite parent)
    {
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        parent.setLayout(layout);

        composite = new Composite(parent, SWT.NORMAL);
        layout = new GridLayout();
        layout.numColumns = 1;
        layout.verticalSpacing = 30;
        composite.setLayout(layout);

        final GridData data = new GridData(SWT.CENTER, SWT.CENTER, true, true);
        composite.setLayoutData(data);

        boolean isFirst = true;
        for (final AbstractTool tool : ToolRegistry.getRegisteredTools())
        {
            tool.setComposite(parent);
            final Button b = new Button(composite, SWT.RADIO);
            b.setText(tool.getName() + ": " + tool.getShortDescription());
            b.setData(tool);
            b.addSelectionListener(new QSelectionListener());
            b.setEnabled(tool.isEnabled());
            b.setToolTipText(tool.getDescription());

            if (isFirst)
            {
                b.setSelection(true);
                curTool = tool;
                isFirst = false;
            }
        }

        final Composite buttonBar = new Composite(parent, SWT.NORMAL);
        final RowLayout rLayout = new RowLayout();
        rLayout.wrap = false;
        rLayout.marginLeft = 450;
        buttonBar.setLayout(rLayout);

        final Button b = new Button(buttonBar, SWT.PUSH);
        b.setText(okText);
        b.addSelectionListener(new QSelectionListener());
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    @Override
    public void setFocus()
    {
        composite.setFocus();
    }
}