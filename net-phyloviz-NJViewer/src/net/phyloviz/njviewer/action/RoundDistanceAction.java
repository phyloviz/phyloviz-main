package net.phyloviz.njviewer.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import net.phyloviz.njviewer.ui.GraphView;
import net.phyloviz.upgmanjcore.visualization.GView;

public class RoundDistanceAction extends AbstractAction {

	private final GraphView gv;

	public RoundDistanceAction(GView gv) {
		this.gv =(GraphView) gv;
	}

	public JMenuItem getMenuItem() {
		JCheckBoxMenuItem mi = new JCheckBoxMenuItem("Round Distance Labels");
		mi.setToolTipText("Round the label edges distance value");
		mi.setMnemonic(KeyEvent.VK_V);
		mi.setSelected(false);
                mi.setEnabled(false);
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
		gv.setRoundDistance(status);
	}

}
