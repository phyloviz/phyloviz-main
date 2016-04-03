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

package net.phyloviz.upgmanjcore.visualization.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import net.phyloviz.upgmanjcore.visualization.GView;

/**
 *
 * @author martanascimento
 */
public class RescaleEdgesControlAction extends AbstractAction {

	private GView gv;
        private boolean selected;

	public RescaleEdgesControlAction(GView gv, boolean selected) {
		this.gv = gv;
                this.selected = selected;
	}

	public JMenuItem getMenuItem() {

		JCheckBoxMenuItem mi = new JCheckBoxMenuItem("Re-scale Edges(log distance)");
		mi.setToolTipText("Linear distance edges");
		mi.setMnemonic(KeyEvent.VK_R);
		mi.setSelected(selected);
		mi.addActionListener(this);
		return mi;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (gv == null) {
			((JCheckBoxMenuItem) e.getSource()).setSelected(false);
			return;
		}

		boolean status = ((JCheckBoxMenuItem) e.getSource()).isSelected();
		gv.setRescaleEdges(status);
	}

}

