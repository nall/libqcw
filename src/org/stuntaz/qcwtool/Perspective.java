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

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * RCP Perspective
 * 
 * @author nall
 *
 */
public class Perspective
    implements IPerspectiveFactory
{

    public void createInitialLayout(final IPageLayout layout)
    {
        final String editorArea = layout.getEditorArea();
        layout.setEditorAreaVisible(false);
        layout.setFixed(true);

        layout.addStandaloneView(IntroView.ID, false, IPageLayout.LEFT, 1.0f,
            editorArea);
    }

}
