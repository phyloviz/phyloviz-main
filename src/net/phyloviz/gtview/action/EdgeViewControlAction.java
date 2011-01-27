package net.phyloviz.gtview.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import net.phyloviz.gtview.ui.GraphView;

public class EdgeViewControlAction extends AbstractAction {

	private GraphView gv;

	public EdgeViewControlAction(GraphView gv) {
		this.gv = gv;
	}

	public JMenuItem getMenuItem() {

		JCheckBoxMenuItem mi = new JCheckBoxMenuItem("High Level Edges");
		mi.setToolTipText("Show high level edges");
		mi.setMnemonic(KeyEvent.VK_H);
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
		gv.setAllEdges(status);

	}

}
