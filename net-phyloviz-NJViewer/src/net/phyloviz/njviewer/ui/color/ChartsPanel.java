/*-
 * Copyright (c) 2016, PHYLOViZ Team <phyloviz@gmail.com>
 * All rights reserved.
 * 
 * This file is part of PHYLOViZ <http://www.phyloviz.net/>.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.phyloviz.njviewer.ui.color;

import java.awt.GridLayout;
import org.openide.util.HelpCtx;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Cátia Vaz
 */
public class ChartsPanel extends TopComponent {

    public ChartsPanel(int charts) {
        this.setLayout(new GridLayout(1, charts));

    }

    @Override
    public void open() {
        Mode m = WindowManager.getDefault().findMode("output");
        m.dockInto(this);
        super.open();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

    @Override
    public int getPersistenceType() {
        return PERSISTENCE_NEVER;
    }

    @Override
    protected String preferredID() {
        return "ChartLegend";
    }
}
