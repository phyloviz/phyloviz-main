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

import com.google.code.aephyr.TreeModelTransformer;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.CheckboxTree;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingModel.CheckingMode;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.swing.MenuSelectionManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import net.phyloviz.core.data.DataModel;

public class TreePanel extends CheckboxTree {

	private AbstractTableModel tm;
	private TreeModelTransformer transformModel;
	private TreeSet<String>[] filterSet;
	private DefaultMutableTreeNode root;
        private DataModel model;

	@SuppressWarnings("unchecked")
	public TreePanel(DataModel model) {
		super();
                this.model=model;
		tm = model.tableModel();
		int ncol = tm.getColumnCount();
		filterSet = new TreeSet[ncol];
		for (int i = 0; i < filterSet.length; i++) {
			filterSet[i] = new TreeSet<String>();
		}

		root = new DefaultMutableTreeNode("Columns");
		for (int i = 0; i < ncol; ++i) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(tm.getColumnName(i));
			Iterator<String> iter = model.getDomain(i).iterator();
			while (iter.hasNext()) {
				String next = iter.next();
				DefaultMutableTreeNode aux = new DefaultMutableTreeNode(next);
				node.add(aux);
			}
			root.add(node);
		}
		DefaultTreeModel lTreeModel = new DefaultTreeModel(root);
		this.setModel(lTreeModel);
		this.setRootVisible(false);
		this.getCheckingModel().setCheckingMode(CheckingMode.PROPAGATE);

		TreeModel lmodel = this.getModel();
		this.setLargeModel(true);
		this.setShowsRootHandles(true);
		transformModel = new TreeModelTransformer(this, lmodel);
		this.setModel(transformModel);
               
                tm.addTableModelListener(new TableModelListener(){
 
            @Override
                  public void tableChanged(TableModelEvent e) {
                       filterSet = new TreeSet[tm.getColumnCount()];
                        for (int i = 0; i < filterSet.length; i++) {
                            filterSet[i] = new TreeSet<String>();
                        }
                       DefaultMutableTreeNode node = new DefaultMutableTreeNode(tm.getColumnName(tm.getColumnCount()-1));
			Iterator<String> iter = getDomain(tm.getColumnCount()-1);
			while (iter.hasNext()) {
				String next = iter.next();
				DefaultMutableTreeNode aux = new DefaultMutableTreeNode(next);
				node.add(aux);
			}
			root.add(node);
                        updateUI();
                  }
                });
    }

        private Iterator<String> getDomain(int index){
            return model.getDomain(index).iterator();
        }

	


         
           
	public TreeSet<String>[] getFilterSet() {
		this.clearFilterSet();
		TreePath[] selectedPaths = this.getCheckingModel().getCheckingPaths();
		for (int i = 0; i < selectedPaths.length && selectedPaths[i] != null && selectedPaths[i].getParentPath() != null; i++) {
			int cidx = tm.findColumn(selectedPaths[i].getParentPath().getLastPathComponent().toString());

			String value = selectedPaths[i].getLastPathComponent().toString();

			// Headers return -1 ...
			if (cidx != -1) {
				filterSet[cidx].add(value);
			}
		}
		return filterSet;
	}

	private void clearFilterSet() {
		for (int i = 0; i < filterSet.length; i++) {
			if (filterSet[i] != null) {
				filterSet[i].clear();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void selecting(TreeSet<String>[] otherView, boolean toReplace) {

		MenuSelectionManager.defaultManager().clearSelectedPath();


		//this.getCheckingModel().clearChecking();
		this.getCheckingModel().setCheckingMode(CheckingMode.PROPAGATE);
		if (!toReplace) {
			//CBTree.getCheckingModel().clearChecking();
			TreePath[] paths = new TreePath[0];
			paths = (TreePath[]) transformModel.toSelect.toArray(paths);
			if (paths != null && paths.length != 0) {
				this.getCheckingModel().addCheckingPaths(paths);
			}

		} else {
			this.getCheckingModel().clearChecking();

			filterSet = otherView;

			ArrayList<TreePath> pathsToBeSelected = new ArrayList<TreePath>();


			Iterator<String> it;

			for (int i = 0; i < filterSet.length; i++) {
				it = filterSet[i].iterator();

				while (it.hasNext()) {
					String s = it.next();
					DefaultMutableTreeNode novo = find(i,s);
					TreeNode[] novoPath = novo.getPath();
					if (novo != null && novoPath.length > 0) {
						pathsToBeSelected.add(new TreePath(novo.getPath()));
					}
				}
			}

			this.getCheckingModel().setCheckingPaths(pathsToBeSelected.toArray(new TreePath[0]));
		}

	}

	public void add(DefaultMutableTreeNode novo, ArrayList<TreePath> st) {
		Enumeration e = novo.postorderEnumeration();
		while (e.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
			TreePath p = new TreePath(node.getPath());
			if (!st.contains(p)) {
				st.add(p);
			}
		}
	}

        public DefaultMutableTreeNode find(int i_child,String name){

            DefaultMutableTreeNode tn=(DefaultMutableTreeNode)root.getChildAt(i_child);
            return (find(tn,name));
        }

	public DefaultMutableTreeNode find(DefaultMutableTreeNode root,String name) {

		// TODO: fix this... we must search by column!

		if (root == null) {
			return null;
		}
		Enumeration e = root.postorderEnumeration();
		while (e.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
			if (((String) node.getUserObject()).equals(name)) {
				return node;
			}
		}
		return null;
	}

	public TreeSet<String>[] viewing() {
		TreePath[] selectedPaths = this.getCheckingModel().getCheckingPaths();

		for (int i = 0; i < selectedPaths.length && selectedPaths[i] != null && selectedPaths[i].getParentPath() != null; i++) {
			int cidx = tm.findColumn(selectedPaths[i].getParentPath().getLastPathComponent().toString());

			String value = selectedPaths[i].getLastPathComponent().toString();

			// Headers return -1 ...
			if (cidx != -1) {
				filterSet[cidx].add(value);
			}
		}

		return filterSet;
	}

	public void reseting() {
		this.getCheckingModel().setCheckingMode(CheckingMode.PROPAGATE);
		this.getCheckingModel().clearChecking();
		MenuSelectionManager.defaultManager().clearSelectedPath();
		this.clearStructures();
		this.clearSelection();
		transformModel.setFilter(null, null);
	}

	public void clearStructures() {

		for (int i = 0; i < filterSet.length; i++) {
			if (filterSet[i] != null) {
				filterSet[i].clear();
			}
		}
	}

	public void releasedKey(String regex) {
		MenuSelectionManager.defaultManager().clearSelectedPath();
		this.getCheckingModel().setCheckingMode(CheckingMode.SIMPLE);
		TreeModelTransformer.Filter filter = regex.isEmpty() ? null
			: new TreeModelTransformer.RegexFilter(Pattern.compile(regex, Pattern.CASE_INSENSITIVE), false);
		TreePath startingPath = filter == null ? null : this.getSelectionPath();
		transformModel.setFilter(filter, startingPath);
		MenuSelectionManager.defaultManager().clearSelectedPath();
		this.getCheckingModel().setCheckingMode(CheckingMode.PROPAGATE);
	}
}
