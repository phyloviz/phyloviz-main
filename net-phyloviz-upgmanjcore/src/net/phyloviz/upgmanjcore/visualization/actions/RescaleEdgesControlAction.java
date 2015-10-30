/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

