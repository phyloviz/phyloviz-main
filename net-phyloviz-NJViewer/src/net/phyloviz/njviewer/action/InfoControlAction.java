package net.phyloviz.njviewer.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;

import javax.swing.JMenuItem;
import net.phyloviz.njviewer.ui.GView;

public class InfoControlAction extends AbstractAction {

	private GView gv;

	public InfoControlAction(GView gv) {
		this.gv = gv;
	}

	public JMenuItem getMenuItem() {
		final JMenuItem mi = new JMenuItem("Info");
		mi.setToolTipText("Info Panel");
		mi.setMnemonic(KeyEvent.VK_I);
		mi.addActionListener(this);

		return mi;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		gv.showInfoPanel();
	}
}
