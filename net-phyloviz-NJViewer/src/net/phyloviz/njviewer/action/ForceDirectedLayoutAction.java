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

package net.phyloviz.njviewer.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import net.phyloviz.njviewer.ui.GraphView;
import net.phyloviz.upgmanjcore.visualization.GView;

public class ForceDirectedLayoutAction extends AbstractAction {

	private final GraphView gv;

	public ForceDirectedLayoutAction(GView gv) {
		this.gv =(GraphView) gv;
	}

	public JMenuItem getMenuItem() {
		JCheckBoxMenuItem mi = new JCheckBoxMenuItem("Force Directed Layout");
		mi.setMnemonic(KeyEvent.VK_R);
		mi.setSelected(false);
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
		gv.setForceDirectedLayout(status);
		gv.enableViewControl(!status);
	}

}
