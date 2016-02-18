package net.phyloviz.njviewer.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import net.phyloviz.njviewer.ui.GraphView;
import net.phyloviz.upgmanjcore.visualization.GView;

public class RadialLayoutAction extends AbstractAction {

	private final GraphView gv;

	public RadialLayoutAction(GView gv) {
		this.gv =(GraphView) gv;
	}

	public JMenuItem getMenuItem() {
		JCheckBoxMenuItem mi = new JCheckBoxMenuItem("Radial Layout");
		mi.setMnemonic(KeyEvent.VK_R);
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
		gv.setRadialLayout(status);
	}

}
