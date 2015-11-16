/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.phyloviz.gtview.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import net.phyloviz.upgmanjcore.visualization.GView;

/**
 *
 * @author Morrighan
 */
public class EdgePercentageLabelAction extends AbstractAction {

	private GView gv;

	public EdgePercentageLabelAction(GView gv) {
		this.gv = gv;
	}

	public JMenuItem getMenuItem() {

		JCheckBoxMenuItem mi = new JCheckBoxMenuItem("Edge Percentage labels");
		mi.setToolTipText("Label edges with percentage value");
		mi.setMnemonic(KeyEvent.VK_V); //O que e' VK_V ?
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
		gv.setEdgePercentageLabel(status);
	}

}
