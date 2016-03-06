package net.phyloviz.njviewer.action;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import net.phyloviz.njviewer.action.control.JRadialViewControlPanel;
import net.phyloviz.upgmanjcore.visualization.GView;
import net.phyloviz.njviewer.ui.GraphView;
import org.openide.util.ImageUtilities;
import org.openide.windows.TopComponent;

public class RadialViewControlAction implements ActionListener {

	private GraphView gv;
	private JDialog control;
        private JRadialViewControlPanel radialPanel;

	public RadialViewControlAction(GView gv, JRadialViewControlPanel panel) {
		this.gv =(GraphView) gv;
                this.radialPanel = panel;
		gv.addAncestorListener(new AncestorListener() {

			@Override
			public void ancestorAdded(AncestorEvent event) {
			}

			@Override
			public void ancestorRemoved(AncestorEvent event) {
				if (control != null) {
					control.setVisible(false);
					control.dispose();
				}
			}

			@Override
			public void ancestorMoved(AncestorEvent event) {
			}
		});
	}

	public JMenuItem getMenuItem() {
		JMenuItem mi = new JMenuItem("Radial View Control");
		mi.setToolTipText("Rendering engine control");
		mi.setMnemonic(KeyEvent.VK_C);
		mi.addActionListener(this);
		return mi;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (control != null && control.isShowing())
			control.requestFocus();
		else
			enableViewControl();
	}

	public void enableViewControl() {

		control = new JDialog();
		control.setName("View Control");
		control.setTitle("View Control");
		control.setIconImage(ImageUtilities.loadImage("net/phyloviz/core/DataSetIcon.png"));
		control.add(radialPanel, BorderLayout.CENTER);
		control.setLocationRelativeTo(TopComponent.getRegistry().getActivated());
		control.pack();
		control.setVisible(true);
		control.setResizable(false);
	}
        
}
