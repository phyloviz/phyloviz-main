package net.phyloviz.gtview.ui;

import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JMenuItem;
import net.phyloviz.category.CategoryChangeListener;
import net.phyloviz.category.CategoryProvider;
import net.phyloviz.core.data.DataSet;
import net.phyloviz.goeburst.tree.GOeBurstMSTResult;
import net.phyloviz.gtview.render.ChartRenderer;
import org.openide.util.Lookup.Result;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;

public class GTPanel2 extends TopComponent {

	private GraphView2 gv;
	private ArrayList<JMenuItem> al;
	private Result<CategoryProvider> r;
	private CategoryChangeListener gvCatListen;

	/** Creates new form GTPanel */
	public GTPanel2(String name, GOeBurstMSTResult gr, DataSet ds) {
		super(Lookups.singleton(gr));
		initComponents();
		this.setName(name);
		gv = new GraphView2(gr);
		this.add(gv);
		gv.startAnimation();

		gvCatListen = new CategoryChangeListener() {

			@Override
			public void categoryChange(CategoryProvider cp) {

				if (cp.isOn()) {
					gv.setDefaultRenderer(new ChartRenderer(cp, gv));
					gv.setCategoryProvider(cp);
				} else {
					gv.resetDefaultRenderer();
					gv.setCategoryProvider(null);
				}
			}
		};

		// Let us track category providers...
		// TODO: implement this within a renderer.
		r = ds.getLookup().lookupResult(CategoryProvider.class);
		Iterator<? extends CategoryProvider> i = r.allInstances().iterator();
		while (i.hasNext())
			i.next().addCategoryChangeListener(gvCatListen);

		r.addLookupListener(new LookupListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void resultChanged(LookupEvent le) {

				Iterator<? extends CategoryProvider> i = ((Result<CategoryProvider>) le.getSource()).allInstances().iterator();
				while (i.hasNext()) {
					CategoryProvider cp = i.next();
					cp.removeCategoryChangeListener(gvCatListen);
					cp.addCategoryChangeListener(gvCatListen);
				}
			}
		});
	}

	@Override
	public int getPersistenceType() {
		return PERSISTENCE_NEVER;
	}

	@Override
	protected String preferredID() {
		return "GTPanel";
	}

	@Override
	protected void componentClosed() {
		gv.stopAnimation();
		super.componentClosed();
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
