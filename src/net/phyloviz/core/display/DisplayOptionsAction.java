package net.phyloviz.core.display;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.util.actions.Presenter;
import org.openide.windows.TopComponent;

public final class DisplayOptionsAction extends AbstractAction implements Presenter.Popup, Presenter.Menu {

	@Override
	public JMenuItem getPopupPresenter() {

		JMenu result = new JMenu("Display Options");
		result.setMnemonic(KeyEvent.VK_D);
		TopComponent tc = TopComponent.getRegistry().getActivated();

		if (tc instanceof DisplayActionsProvider) {
			Iterator<Action> ia = ((DisplayActionsProvider) tc).getDisplayActions().iterator();
			while (ia.hasNext()) {
				JMenuItem mi = new JMenuItem();
				Action a = ia.next();
				mi.setText(a.toString());
				mi.addActionListener(a);
				result.add(mi);
			}
		}

		if (result.getItemCount() == 0)
			result.setEnabled(false);

		return result;
	}

	@Override
	public JMenuItem getMenuPresenter() {
		return getPopupPresenter();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// No action.
	}
}
