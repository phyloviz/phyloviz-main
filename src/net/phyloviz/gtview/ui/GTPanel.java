package net.phyloviz.gtview.ui;

import java.util.ArrayList;
import javax.swing.JMenuItem;
import net.phyloviz.core.display.DisplayMenuProvider;
import net.phyloviz.goeburst.GOeBurstResult;
import net.phyloviz.gtview.action.EdgeViewControlAction;
import net.phyloviz.gtview.action.GroupControlAction;
import net.phyloviz.gtview.action.InfoControlAction;
import net.phyloviz.gtview.action.LinearSizeControlAction;
import net.phyloviz.gtview.action.ViewControlAction;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;

public class GTPanel extends TopComponent {

	private GraphView gv;
	private ArrayList<JMenuItem> al;

	/** Creates new form GTPanel */
	public GTPanel(String name, GOeBurstResult gr) {
		super(Lookups.singleton(gr));
		initComponents();
		this.setName(name);
		gv = new GraphView(gr);
		this.add(gv);
	}

	@Override
	public void componentActivated() {
		super.componentActivated();

		if (al == null) {
			al = new ArrayList<JMenuItem>();
			al.add(new GroupControlAction(gv).getMenuItem());
			al.add(new InfoControlAction(gv).getMenuItem());
			al.add(new EdgeViewControlAction(gv).getMenuItem());
			al.add(new LinearSizeControlAction(gv).getMenuItem());
			al.add(new ViewControlAction(gv).getMenuItem());
		}

		DisplayMenuProvider dmp = Lookup.getDefault().lookup(DisplayMenuProvider.class);
		dmp.updateMenu(al);
	}

	@Override
	protected void componentDeactivated() {
		super.componentDeactivated();
		DisplayMenuProvider dmp = Lookup.getDefault().lookup(DisplayMenuProvider.class);
		dmp.updateMenu(null);
	}



	@Override
	public int getPersistenceType() {
		return PERSISTENCE_NEVER;
	}

	@Override
	protected String preferredID() {
		return "GTPanel";
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
        private void initComponents() {

                setLayout(new java.awt.BorderLayout());
        }// </editor-fold>//GEN-END:initComponents


        // Variables declaration - do not modify//GEN-BEGIN:variables
        // End of variables declaration//GEN-END:variables
}
