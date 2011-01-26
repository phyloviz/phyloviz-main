package net.phyloviz.gtview.ui;

import net.phyloviz.goeburst.GOeBurstResult;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;

public class GTPanel extends TopComponent {

	/** Creates new form GTPanel */
	public GTPanel(String name, GOeBurstResult gr) {
		super(Lookups.singleton(gr));
		initComponents();
		this.setName(name);

		this.add(new GraphView(gr));
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