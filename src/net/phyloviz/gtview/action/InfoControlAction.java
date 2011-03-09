package net.phyloviz.gtview.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import net.phyloviz.gtview.ui.GView;

public class InfoControlAction extends AbstractAction {

	private GView gv;

	public InfoControlAction(GView gv) {
		this.gv = gv;
	}

	public JMenuItem getMenuItem() {
		JCheckBoxMenuItem mi = new JCheckBoxMenuItem("Info");
		mi.setToolTipText("Info Panel");
		mi.setMnemonic(KeyEvent.VK_I);
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
		gv.showInfoPanel(status);
		gv.updateUI();
		gv.getDisplay().updateUI();
	}

}
