package net.phyloviz.njviewer.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import net.phyloviz.njviewer.ui.GraphView;
import net.phyloviz.upgmanjcore.visualization.GView;

public class ForceDistanceLayoutAction extends AbstractAction {

	private final GraphView gv;

	public ForceDistanceLayoutAction(GView gv) {
		this.gv =(GraphView) gv;
	}

	public JMenuItem getMenuItem() {
		JCheckBoxMenuItem mi = new JCheckBoxMenuItem("Force Distance Layout");
		mi.setMnemonic(KeyEvent.VK_F);
		mi.setSelected(false);
                mi.setEnabled(!gv.isRadial());
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
		gv.setForceDistanceLayout(status);
	}

}
