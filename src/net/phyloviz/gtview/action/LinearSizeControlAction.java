package net.phyloviz.gtview.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import net.phyloviz.gtview.ui.GraphView;

public class LinearSizeControlAction extends AbstractAction {

	private GraphView gv;

	public LinearSizeControlAction(GraphView gv) {
		this.gv = gv;
	}

	public JMenuItem getMenuItem() {

		JCheckBoxMenuItem mi = new JCheckBoxMenuItem("Linear Nodes");
		mi.setToolTipText("Linear size for nodes");
		mi.setMnemonic(KeyEvent.VK_L);
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
		gv.setLinearSize(status);
	}

}
