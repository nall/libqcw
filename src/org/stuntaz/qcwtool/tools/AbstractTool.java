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

import org.eclipse.swt.widgets.Composite;

/**
 * Defines the interface for a QCWTool sub-tool.
 * 
 * @author nall
 *
 */
public abstract class AbstractTool
{
    private Composite composite;

    protected AbstractTool()
    {
        ToolRegistry.registerTool(this);
    }

    /**
     * Returns the long description of the tool.
     * 
     * @return a String containing the long description
     */
    public String getDescription()
    {
        return getShortDescription();
    }

    /**
     * Returns whether or not this tool is enabled.
     * 
     * @return true if the tool is enabled, false otherwise
     */
    public boolean isEnabled()
    {
        return true;
    }

    /**
     * Sets the {@link Composite} for this tool.
     * 
     * @param composite the composite to use
     */
    public void setComposite(final Composite composite)
    {
        this.composite = composite;
    }

    protected final Composite getComposite()
    {
        return this.composite;
    }

    /**
     * Gets the short name for this tool
     * 
     * @return the tool's name
     */
    public abstract String getName();

    /**
     * Gets the short description for this tool.
     * 
     * @return the short description
     */
    public abstract String getShortDescription();

    /**
     * Runs the tool.
     */
    public abstract void run();
}