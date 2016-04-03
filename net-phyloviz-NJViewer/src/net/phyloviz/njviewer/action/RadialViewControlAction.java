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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import net.phyloviz.njviewer.action.control.JRadialViewControlPanel;
import net.phyloviz.upgmanjcore.visualization.GView;
import net.phyloviz.njviewer.ui.GraphView;
import org.openide.util.ImageUtilities;
import org.openide.windows.TopComponent;

public class RadialViewControlAction implements ActionListener {

	private GraphView gv;
	private JDialog control;
        private JRadialViewControlPanel radialPanel;

	public RadialViewControlAction(GView gv, JRadialViewControlPanel panel) {
		this.gv =(GraphView) gv;
                this.radialPanel = panel;
		gv.addAncestorListener(new AncestorListener() {

			@Override
			public void ancestorAdded(AncestorEvent event) {
			}

			@Override
			public void ancestorRemoved(AncestorEvent event) {
				if (control != null) {
					control.setVisible(false);
					control.dispose();
				}
			}

			@Override
			public void ancestorMoved(AncestorEvent event) {
			}
		});
	}

	public JMenuItem getMenuItem() {
		JMenuItem mi = new JMenuItem("Tree View Control");
		mi.setToolTipText("Rendering engine control");
		mi.setMnemonic(KeyEvent.VK_C);
		mi.addActionListener(this);
		return mi;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (control != null && control.isShowing())
			control.requestFocus();
		else
			enableViewControl();
	}

	public void enableViewControl() {

		control = new JDialog();
		control.setName("View Control");
		control.setTitle("View Control");
		control.setIconImage(ImageUtilities.loadImage("net/phyloviz/core/DataSetIcon.png"));
		control.add(radialPanel, BorderLayout.CENTER);
		control.setLocationRelativeTo(TopComponent.getRegistry().getActivated());
		control.pack();
		control.setVisible(true);
		control.setResizable(false);
	}
        
}
