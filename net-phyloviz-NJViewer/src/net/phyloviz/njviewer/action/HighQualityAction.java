package net.phyloviz.njviewer.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import net.phyloviz.njviewer.ui.GView;

public class HighQualityAction extends AbstractAction {

	private GView gv;

	public HighQualityAction(GView gv) {
		this.gv = gv;
	}

	public JMenuItem getMenuItem() {

		JCheckBoxMenuItem mi = new JCheckBoxMenuItem("High Quality");
		mi.setToolTipText("High quality rendering");
		mi.setMnemonic(KeyEvent.VK_Q);
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
		gv.setHighQuality(status);
	}

}
