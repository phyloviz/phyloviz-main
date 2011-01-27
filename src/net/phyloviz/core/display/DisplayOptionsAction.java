package net.phyloviz.core.display;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.util.Lookup;
import org.openide.util.actions.Presenter;

public final class DisplayOptionsAction extends AbstractAction implements Presenter.Popup, Presenter.Menu {

	private final JMenu result;

	public DisplayOptionsAction() {
		result = Lookup.getDefault().lookup(DisplayMenuProvider.class);
	}

	@Override
	public JMenuItem getPopupPresenter() {
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
