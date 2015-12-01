/*-
 * Copyright (c) 2011, PHYLOViZ Team <phyloviz@gmail.com>
 * All rights reserved.
 * 
 * This file is part of PHYLOViZ <http://www.phyloviz.net>.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole combination.
 * 
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent modules,
 * and to copy and distribute the resulting executable under terms of your
 * choice, provided that you also meet, for each linked independent module,
 * the terms and conditions of the license of that module.  An independent
 * module is a module which is not derived from or based on this library.
 * If you modify this library, you may extend this exception to your version
 * of the library, but you are not obligated to do so.  If you do not wish
 * to do so, delete this exception statement from your version.
 */

package net.phyloviz.upgma.treeviewer;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import net.phyloviz.category.CategoryChangeListener;
import net.phyloviz.category.CategoryProvider;
import net.phyloviz.core.data.DataModel;
import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.tview.TViewPanel;
import net.phyloviz.upgma.UPGMAItem;
import net.phyloviz.upgma.treeviewer.render.ChartRenderer;
import org.openide.util.Lookup.Result;
import net.phyloviz.upgma.ui.OutputPanel;
import net.phyloviz.upgmanjcore.visualization.GView;
import net.phyloviz.upgmanjcore.visualization.IGTPanel;
import net.phyloviz.upgmanjcore.visualization.PersistentVisualization;
import net.phyloviz.upgmanjcore.visualization.Visualization;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;

public final class GTPanel extends TopComponent implements IGTPanel{

	private ArrayList<JMenuItem> al;
	private Result<CategoryProvider> r;
	private CategoryChangeListener gvCatListen;
        private UPGMAViewer uv;
        private final JComponent tvc;
        private CategoryProvider catProvider;
        private TypingData<? extends Profile> ds;

	/** Creates new form GTPanel
     * @param name
     * @param gr
     * @param ds */
	public GTPanel(String name, UPGMAItem gr, TypingData<? extends Profile> ds) {
		super(Lookups.singleton(gr));
		initComponents();
		this.setName(name);
                
                this.ds =  ds;
                String distanceProvider = gr.getDistanceProvider();
                int split = distanceProvider.lastIndexOf(".") + 1;
                distanceProvider = distanceProvider.substring(split);
                uv = new UPGMAViewer(name, gr.getRoot(), distanceProvider);
                
                Visualization viz = gr.getVisualization();
                if(viz != null && viz.pv != null){
                    loadVisualization(viz);
                } 
                
                tvc = uv.generateTreeViewComponent();
               
                
                this.add(tvc);
		gvCatListen = new CategoryChangeListener() {

			@Override
			public void categoryChange(CategoryProvider cp) {

				if (cp.isOn()) {
                                        catProvider = cp;
					uv.setDefaultRenderer( new ChartRenderer(cp, uv));
					uv.setCategoryProvider(cp);
				} else {
                                        catProvider = null;
					uv.resetDefaultRenderer();
					uv.setCategoryProvider(null);
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
        public GView getGView() {
            return uv;
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
                uv.closeInfoPanel();
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

    @Override
    public Visualization getVisualization() {

        Visualization v = new Visualization();
        PersistentVisualization pv = new PersistentVisualization();

        if (catProvider != null && catProvider.isOn()) {

            for (TopComponent tc : TopComponent.getRegistry().getOpened()) {
                if (tc instanceof TViewPanel) {
                    TViewPanel tvp = (TViewPanel) tc;
                    TypingData td = tvp.ds.getLookup().lookup(TypingData.class);
                    if (ds == td) {
                        DataModel dm = catProvider.getDataModel();
                        if(dm == tvp.cp.getDataModel()){
                            v.filter = tvp.getFilter();
                            v.category = catProvider;
                            break;
                        }
                    }
                }
            }
        }
        pv.distanceFilterValue = uv.getDistanceFilterValue();
        pv.linearSize = uv.getLinearSize();
        v.pv = pv;

        return v;
    }

    @Override
    public void loadVisualization(Visualization viz) {

       if (viz.category != null) {
            catProvider = viz.category;

            uv.setDefaultRenderer(new ChartRenderer(catProvider, uv));
            uv.setCategoryProvider(catProvider);
        }

        if (viz.pv.distanceFilterValue != -1) {
            uv.setDistanceFilterValue(viz.pv.distanceFilterValue);
        }
        if(viz.pv.linearSize){
            uv.setLinearSize(viz.pv.linearSize);
        }
        uv.repaint();

    }
}

class LocalNodeListener implements NodeListener {

		private OutputPanel op;

		LocalNodeListener(OutputPanel op) {
			this.op = op;
		}

		@Override
		public void childrenAdded(NodeMemberEvent nme) {
		}

		@Override
		public void childrenRemoved(NodeMemberEvent nme) {
		}

		@Override
		public void childrenReordered(NodeReorderEvent nre) {
		}

		@Override
		public void nodeDestroyed(NodeEvent ne) {

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					op.close();
				}
			});
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
		}
	}