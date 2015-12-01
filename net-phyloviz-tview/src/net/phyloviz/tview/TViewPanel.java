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

package net.phyloviz.tview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.TreeSet;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import net.phyloviz.category.CategoryProvider;
import net.phyloviz.category.ui.ChartLegendPanel;
import net.phyloviz.core.data.DataModel;
import net.phyloviz.core.data.DataSaver;
import net.phyloviz.core.data.DataSet;
import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;

public class TViewPanel extends TopComponent {

	public final DataSet ds;
	public final CategoryProvider cp;
	private final ChartLegendPanel clp;
	private TablePanel table;
	private TreePanel tree;
	private JScrollPane sp;
	private final JTextField filterText;
	private JButton selectButton;
	private JButton resetButton;
	private JButton viewButton;
	private JRadioButton treeButton;
	private JRadioButton tableButton;
	private boolean tableortree;
	private boolean firstTime;
	private ButtonGroup group;

	/** Creates new form TViewPanel */
	public TViewPanel(String name, DataModel dm, DataSet _ds) {
		InstanceContent ic = new InstanceContent();
		associateLookup(new AbstractLookup(ic));

		ic.add(dm);
		
		DataSaver saver = dm.getSaver();
		if (saver != null)
			ic.add(saver);

		initComponents();
		this.setName(name);
		this.ds = _ds;

		cp = new CategoryProvider(dm);
		if (dm instanceof TypingData) {
			((TypingData<? extends Profile>) dm).add(cp);
		} else {
			// TODO: fix this... it assumes that the first typing data as key reference...  
			TypingData<? extends Profile> ltd = (TypingData<? extends Profile>) ds.getLookup().lookup(TypingData.class);
			ltd.add(cp);
		}

		clp = new ChartLegendPanel(new Dimension(128, 128), cp, dm.weight());
		clp.setName(name + " (Selection view)");

		//the finders
		table = new TablePanel(dm);
		tree = new TreePanel(dm);
		firstTime = true;
		tableortree = true;
		sp = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.setBackground(Color.BLACK);
		filterText = new JTextField("");
		filterText.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (tableortree) {
					table.releasedKey(filterText.getText());
				} else {
					tree.releasedKey(filterText.getText());
				}
			}
		});

		resetButton = new JButton("Reset");
		resetButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO: Phyloviz.getWindow().removeTab("Chart");
				filterText.setText("");
				table.reseting();
				tree.reseting();
				cp.setSelection(null);
				clp.repaint();
			}
		});

		viewButton = new JButton("View");
		viewButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				treeButton.setEnabled(false);
				tableButton.setEnabled(false);

				TreeSet<String>[] filter1;
				TreeSet<String>[] filter2;
				TreeSet<String>[] filter;
				filter1 = table.viewing();
				filter2 = tree.viewing();
				if (tableortree) {
					filter = filter1;
				} else {
					filter = filter2;
				}

				cp.setSelection(filter);
				clp.repaint();
				clp.open();
				clp.requestActive();

				treeButton.setEnabled(true);
				tableButton.setEnabled(true);
			}
		});

		selectButton = new JButton("Select");
		selectButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				treeButton.setEnabled(false);
				tableButton.setEnabled(false);
				if (tableortree) {
					table.selecting(null, false);
				} else {
					tree.selecting(null, false);

				}
				treeButton.setEnabled(true);
				tableButton.setEnabled(true);
			}
		});

		treeButton = new JRadioButton("tree");
		tableButton = new JRadioButton("table");

		ItemListener radio1 = new RadioListener1();
		ItemListener radio2 = new RadioListener2();
		treeButton.addItemListener(radio1);
		tableButton.addItemListener(radio2);

		Box box = new Box(BoxLayout.X_AXIS);
		group = new ButtonGroup();
		box.add(new JLabel("View: "));
		box.add(tableButton);
		box.add(treeButton);
		group.add(tableButton);
		group.add(treeButton);
		tableButton.setSelected(true);
		box.add(Box.createHorizontalGlue());
		box.add(new JLabel(" Regex filter: "));
		box.add(Box.createHorizontalGlue());
		box.add(filterText);
		box.add(Box.createHorizontalGlue());
		box.add(selectButton);
		box.add(viewButton);
		box.add(resetButton);
		add(sp, BorderLayout.CENTER);
		add(box, BorderLayout.NORTH);
	}

	private class RadioListener1 implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			filterText.setText("");
			if (e.getStateChange() == ItemEvent.SELECTED) {
				tableortree = false;
				sp.setViewportView(tree);
				tree.selecting(table.getFilterSet(), true);
			}
		}
	}

	private class RadioListener2 implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				filterText.setText("");
				tableortree = true;
				sp.setViewportView(table);
				if (firstTime) {
					firstTime = false;
				} else {
					table.selecting(tree.getFilterSet(), true);
				}
			}
		}
	}

	@Override
	protected void componentOpened() {
		super.componentOpened();
		clp.open();
	}



	@Override
	protected void componentClosed() {
		super.componentClosed();
		ds.remove(cp);
		clp.close();
	}

	@Override
	public int getPersistenceType() {
		return PERSISTENCE_NEVER;
	}

	@Override
	protected String preferredID() {
		return "TViewer";
	}
        
        public TreeSet<String>[] getFilter(){
            if (tableortree) {
                return table.getFilterSet();
            } 
            return tree.getFilterSet();
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
