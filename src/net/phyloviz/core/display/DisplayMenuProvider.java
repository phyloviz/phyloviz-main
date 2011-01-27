package net.phyloviz.core.display;

import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = DisplayMenuProvider.class)
public class DisplayMenuProvider extends JMenu {

	public DisplayMenuProvider() {
		super("Display Options");
		setMnemonic(KeyEvent.VK_D);
		setEnabled(false);
	}

	public void updateMenu(Collection<Action> ac) {

		this.removeAll();

		if (ac != null) {
			Iterator<Action> ia = ac.iterator();
			while (ia.hasNext()) {
				JMenuItem mi = new JMenuItem();
				Action a = ia.next();
				mi.setText(a.toString());
				mi.addActionListener(a);
				this.add(mi);
			}
		}

		if (this.getItemCount() == 0)
			this.setEnabled(false);
		else
			this.setEnabled(true);
	}
}
