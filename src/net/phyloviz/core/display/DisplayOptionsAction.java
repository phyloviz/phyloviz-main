package net.phyloviz.core.display;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.util.actions.Presenter;

public final class DisplayOptionsAction extends AbstractAction implements Presenter.Menu {

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO implement action body
	}

	@Override
	public JMenuItem getMenuPresenter() {
		JMenu result = new JMenu();

		result.add(new JMenuItem("Ok1"));
		result.add(new JMenuItem("Ok2"));

		return result;
	}



}
