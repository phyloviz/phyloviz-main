package net.phyloviz.gtview.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import net.phyloviz.gtview.ui.GView;

public class GroupControlAction extends AbstractAction {

	private GView gv;

	public GroupControlAction (GView gv) {
		this.gv = gv;
	}

	public JMenuItem getMenuItem() {
		JCheckBoxMenuItem mi = new JCheckBoxMenuItem("Groups");
		mi.setToolTipText("Group Panel");
		mi.setMnemonic(KeyEvent.VK_G);
		mi.setSelected(true);
		mi.addActionListener(this);
		return mi;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (gv == null) {
			((JCheckBoxMenuItem) e.getSource()).setSelected(true);
			return;
		}

		boolean status = ((JCheckBoxMenuItem) e.getSource()).isSelected();
		gv.showGroupPanel(status);
		gv.updateUI();
		gv.getDisplay().updateUI();
	}
}
