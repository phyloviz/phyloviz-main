package net.phyloviz.algo.ui;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import net.phyloviz.algo.AlgorithmProvider;
import org.openide.util.Lookup;
import org.openide.util.actions.Presenter;

public class AlgoMenuAction extends AbstractAction implements Presenter.Popup {

	@Override
	public void actionPerformed(ActionEvent e) {
		// No action.
	}

	@Override
	public JMenuItem getPopupPresenter() {
		JMenu result = new JMenu("Compute");

		Collection<? extends AlgorithmProvider> lookres = Lookup.getDefault().lookupAll(AlgorithmProvider.class);
		Iterator<? extends AlgorithmProvider> ir = lookres.iterator();
		while (ir.hasNext()) {
			JMenuItem mi = new JMenuItem();
			AlgorithmProvider ap = ir.next();
			mi.setText(ap.toString());
			mi.addActionListener(ap.getAction());
			result.add(mi);
		}

		if (lookres.isEmpty())
			result.setEnabled(false);

		return result;
	}


}
