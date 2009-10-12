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

package org.stuntaz.qcwtool;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/**
 * RCP workbench window advisor
 * 
 * @author nall
 *
 */
public class ApplicationWorkbenchWindowAdvisor
    extends WorkbenchWindowAdvisor
{

    /**
     * Creates a new advisor
     * 
     * @param configurer the configurer for this advisor
     */
    public ApplicationWorkbenchWindowAdvisor(
        final IWorkbenchWindowConfigurer configurer)
    {
        super(configurer);
    }

    @Override
    public ActionBarAdvisor createActionBarAdvisor(
        final IActionBarConfigurer configurer)
    {
        return new ApplicationActionBarAdvisor(configurer);
    }

    @Override
    public void preWindowOpen()
    {
        final IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setInitialSize(new Point(600, 300));
        configurer.setTitle("QCWTool");

        // Don't let the user resize this guy
        configurer.setShellStyle(configurer.getShellStyle() & ~(SWT.RESIZE));

        configurer.setShowCoolBar(false);
        configurer.setShowStatusLine(false);
    }
}
