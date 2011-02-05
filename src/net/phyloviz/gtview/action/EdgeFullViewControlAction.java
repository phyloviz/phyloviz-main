package net.phyloviz.gtview.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import net.phyloviz.gtview.ui.GraphView;

public class EdgeFullViewControlAction extends AbstractAction {

	private GraphView gv;

	public EdgeFullViewControlAction(GraphView gv) {
		this.gv = gv;
	}

	public JMenuItem getMenuItem() {

		JCheckBoxMenuItem mi = new JCheckBoxMenuItem("All Edges");
		mi.setToolTipText("Show all edges");
		mi.setMnemonic(KeyEvent.VK_A);
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
		gv.forceSetAllEdges(status);

	}

}
