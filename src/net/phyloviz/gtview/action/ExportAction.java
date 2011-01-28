package net.phyloviz.gtview.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JMenuItem;
import net.phyloviz.gtview.ui.GTExportDialog;
import net.phyloviz.gtview.ui.GraphView;

public class ExportAction implements ActionListener {

	private GraphView gv;

	public ExportAction(GraphView gv) {
		this.gv = gv;
	}

	public JMenuItem getMenuItem() {
		JMenuItem mi = new JMenuItem("Export");
		mi.setToolTipText("Export display as image");
		mi.setMnemonic(KeyEvent.VK_E);
		mi.addActionListener(this);
		return mi;
	}


	@Override
	public void actionPerformed(ActionEvent e) {

		GTExportDialog export = new GTExportDialog(gv);
		export.showExportDialog(gv, "Export view as ...", gv.getDisplay(), "export");
		
	}

}
