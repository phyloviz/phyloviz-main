package net.phyloviz.core.display;

import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.Iterator;
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

	public void updateMenu(Collection<JMenuItem> mc) {

		this.removeAll();

		if (mc != null) {
			Iterator<JMenuItem> mi = mc.iterator();
			while (mi.hasNext())
				this.add(mi.next());
		}

		if (this.getItemCount() == 0)
			this.setEnabled(false);
		else
			this.setEnabled(true);
	}
}
