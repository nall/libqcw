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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.stuntaz.libqcw.QCWException;
import org.stuntaz.libqcw.blobs.barchart.BarChartBlob;
import org.stuntaz.libqcw.blobs.barchart.IStudyBlob;
import org.stuntaz.libqcw.blobs.barchart.StudyHeaderBlob;
import org.stuntaz.libqcw.defines.QStudyType;
import org.stuntaz.qcwtool.tools.MergeDirective;

/**
 * Dialog to allow pairing between workspaces
 * 
 * @author nall
 *
 */
public final class ClassPairingDialog
    extends Dialog
    implements SelectionListener
{
    private List<Button> studies = new ArrayList<Button>(
        QStudyType.values().length);
    private Combo srcCombo;
    private Combo tgtCombo;
    private Map<String, BarChartBlob> srcCharts = new HashMap<String, BarChartBlob>();
    private Map<String, BarChartBlob> tgtCharts = new HashMap<String, BarChartBlob>();
    private String srcSelection = "";
    private String tgtSelection = "";

    /**
     * Creates a new dialog
     * 
     * @param parentShell the SWT shell
     */
    public ClassPairingDialog(final Shell parentShell)
    {
        super(parentShell, SWT.APPLICATION_MODAL);
    }

    /**
     * Opens the dialog and blocks until the user closes it
     * 
     * @param ws1 a set of {@link BarChartBlob} objects from the source workspace
     * @param ws2 a set of {@link BarChartBlob} objects from the destination workspace
     * @param results the data structure holding the results of the pairing operation
     * @return true if OK was clicked, false otherwise
     */
    public boolean open(
        final Set<BarChartBlob> ws1,
        final Set<BarChartBlob> ws2,
        final MergeDirective results)
    {
        for (final BarChartBlob c : ws1)
        {
            srcCharts.put(c.getChartTitle(), c);
        }
        for (final BarChartBlob c : ws2)
        {
            tgtCharts.put(c.getChartTitle(), c);
        }

        final boolean[] okClicked = { false };
        final Shell parent = getParent();
        final Shell area = new Shell(parent, SWT.DIALOG_TRIM
            | SWT.APPLICATION_MODAL);
        area.setSize(400, 450);
        area.setText("Pair Workspace Charts");
        final GridLayout layout = new GridLayout();
        final int numColumns = 4;
        layout.numColumns = numColumns;
        layout.verticalSpacing = 2;
        area.setLayout(layout);

        GridData data = null;

        final Label l0 = new Label(area, SWT.NONE);
        l0
            .setText("Merge lines in the selected studies from Source Chart to Target Chart.");
        data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.horizontalSpan = 4;
        l0.setLayoutData(data);

        final Label l1 = new Label(area, SWT.NONE);
        l1.setText("Source Chart:");
        data = new GridData(SWT.FILL, SWT.CENTER, false, true);
        data.horizontalSpan = 2;
        l1.setLayoutData(data);

        final Label l2 = new Label(area, SWT.NONE);
        l2.setText("Target Chart:");
        data = new GridData(SWT.LEFT, SWT.CENTER, false, true);
        data.horizontalSpan = 2;
        l2.setLayoutData(data);

        srcCombo = new Combo(area, SWT.DROP_DOWN | SWT.READ_ONLY);
        for (final BarChartBlob chart : sortByPeriod(ws1))
        {
            srcCombo.add(chart.getChartTitle());

            if (chart == results.source)
            {
                srcCombo.select(srcCombo.getItems().length - 1);
                srcSelection = chart.getChartTitle();
            }
        }
        if (srcSelection == "")
        {
            srcCombo.select(0);
            srcSelection = srcCombo.getItem(0);
        }
        srcCombo.addSelectionListener(this);
        data = new GridData(SWT.LEFT, SWT.TOP, false, true);
        data.horizontalSpan = 2;
        srcCombo.setLayoutData(data);

        tgtCombo = new Combo(area, SWT.DROP_DOWN | SWT.READ_ONLY);
        for (final BarChartBlob chart : sortByPeriod(ws2))
        {
            tgtCombo.add(chart.getChartTitle());
            if (chart == results.target)
            {
                tgtCombo.select(tgtCombo.getItems().length - 1);
                tgtSelection = chart.getChartTitle();
            }
        }
        if (tgtSelection == "")
        {
            tgtCombo.select(0);
            tgtSelection = tgtCombo.getItem(0);
        }
        tgtCombo.addSelectionListener(this);

        data = new GridData(SWT.LEFT, SWT.TOP, false, true);
        data.horizontalSpan = 2;
        tgtCombo.setLayoutData(data);

        // Studies
        final Group group = new Group(area, SWT.NONE);
        final GridLayout gLayout = new GridLayout();
        final int gNumColumns = 2;
        gLayout.numColumns = gNumColumns;
        group.setLayout(gLayout);
        group
            .setText("Lines will be merged from Source to Target in the selected studies");
        data = new GridData(SWT.FILL, SWT.CENTER, true, true);
        data.horizontalSpan = 4;
        group.setLayoutData(data);

        // Add Price explicitly
        Button b = new Button(group, SWT.CHECK);
        b.setText("Price Window");
        b.setData(null);
        b.setSelection(results.priceEnabled);
        data = new GridData(SWT.FILL, SWT.CENTER, true, true);
        data.horizontalSpan = 1;
        b.setLayoutData(data);
        studies.add(b);

        for (int i = 0; i < QStudyType.values().length; ++i)
        {
            if (QStudyType.values()[i].isUpperStudy())
            {
                continue;
            }

            b = new Button(group, SWT.CHECK);
            b.setText(QStudyType.values()[i].name());
            b.setData(QStudyType.values()[i]);
            b.setSelection(results.studies.contains(b.getData()));
            data = new GridData(SWT.FILL, SWT.CENTER, true, true);
            data.horizontalSpan = 1;
            b.setLayoutData(data);
            studies.add(b);
        }

        Composite spacer;
        if (studies.size() % 2 != 0)
        {
            spacer = new Composite(group, SWT.NONE);
            data = new GridData(SWT.FILL, SWT.FILL, true, true);
            data.horizontalSpan = 1;
            spacer.setLayoutData(data);
        }

        final Label lnote = new Label(area, SWT.WRAP);
        lnote.setText("NOTE: Only studies common to both charts are enabled.");
        data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.horizontalSpan = 4;
        lnote.setLayoutData(data);

        updateStudies();

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
                results.source = srcCharts.get(srcSelection);
                results.target = tgtCharts.get(tgtSelection);

                results.priceEnabled = false;
                results.studies.clear();
                for (final Button b : studies)
                {
                    if (b.getSelection())
                    {
                        if (b.getData() == null)
                        {
                            results.priceEnabled = true;

                            // Add an upper study since that will grab price lines
                            results.studies.add(QStudyType.MovingAverage);
                        }
                        else
                        {
                            results.studies.add((QStudyType) b.getData());
                        }
                    }
                }

                if (results.priceEnabled == false && results.studies.isEmpty())
                {
                    MessageDialog.openError(area,
                        "No studies selected for merge",
                        "Please select at least one study to merge.");
                }
                else
                {
                    okClicked[0] = true;
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

        return okClicked[0];
    }

    private void updateStudies()
    {
        // Find common studies of selected charts
        // and enable those checkboxes only common to both

        final BarChartBlob c1 = srcCharts.get(srcSelection);
        final BarChartBlob c2 = tgtCharts.get(tgtSelection);
        final Set<QStudyType> ws1studies = new HashSet<QStudyType>();
        final Set<QStudyType> ws2studies = new HashSet<QStudyType>();

        for (final StudyHeaderBlob h : c1.getStudies())
        {
            for (final IStudyBlob s : h.getStudies())
            {
                ws1studies.add(s.getStudyType());
            }
        }

        for (final StudyHeaderBlob h : c2.getStudies())
        {
            for (final IStudyBlob s : h.getStudies())
            {
                ws2studies.add(s.getStudyType());
            }
        }

        ws1studies.retainAll(ws2studies);

        for (final Button b : studies)
        {
            final QStudyType type = (QStudyType) b.getData();
            b.setEnabled(ws1studies.contains(type) ? true : false);
        }

        // Always enable price
        studies.get(0).setEnabled(true);
    }

    public void widgetDefaultSelected(final SelectionEvent e)
    {
        widgetSelected(e);
    }

    public void widgetSelected(final SelectionEvent e)
    {
        assert (e.getSource() instanceof Combo);
        final Combo c = (Combo) e.getSource();
        final String text = c.getItem(c.getSelectionIndex());

        if (c == srcCombo)
        {
            srcSelection = text;
        }
        else if (c == tgtCombo)
        {
            tgtSelection = text;
        }
        else
        {
            throw new QCWException("Unexpected selection source: " + c);
        }

        updateStudies();
    }

    private List<BarChartBlob> sortByPeriod(final Set<BarChartBlob> s)
    {
        List<BarChartBlob> chartList = new LinkedList<BarChartBlob>();
        chartList.addAll(s);

        final Comparator<BarChartBlob> comparator = new Comparator<BarChartBlob>()
        {

            public int compare(BarChartBlob o1, BarChartBlob o2)
            {
                return o1.getChartTimePeriod().compareTo(
                    o2.getChartTimePeriod());
            }

        };

        BarChartBlob sortedArray[] = new BarChartBlob[chartList.size()];
        sortedArray = chartList.toArray(sortedArray);
        Arrays.sort(sortedArray, comparator);

        chartList = Arrays.asList(sortedArray);
        return chartList;
    }
}
