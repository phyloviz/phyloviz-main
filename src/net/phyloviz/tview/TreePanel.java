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
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import net.phyloviz.core.util.DataModel;

public class TreePanel extends CheckboxTree {

	private AbstractTableModel tm;
	private TreeModelTransformer transformModel;
	private TreeSet<String>[] filterSet;
	private DefaultMutableTreeNode root;

	public TreePanel(DataModel model) {
		super();

		tm = model.tableModel();
		int ncol = tm.getColumnCount();

		filterSet = new TreeSet[ncol];
		for (int i = 0; i < filterSet.length; i++) {
			filterSet[i] = new TreeSet<String>();
		}

		root = new DefaultMutableTreeNode("Columns");

		TreePath raiz = new TreePath(root);
		Node caminhos[] = new Node[ncol];

		for (int i = 0; i < ncol; ++i) {
			//System.out.println(pop.getHeaders()[i]);
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(tm.getColumnName(i));
			caminhos[i] = new Node(tm.getColumnName(i), raiz, node);
			Iterator<String> iter = model.getDomain(i).iterator();
			while (iter.hasNext()) {
				String next = iter.next();
				DefaultMutableTreeNode aux = new DefaultMutableTreeNode(next);
				node.add(aux);
				Node novo = new Node(next, caminhos[i].getPath(), aux);
				caminhos[i].addChild(novo);

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
					DefaultMutableTreeNode novo = find(s);
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

	public DefaultMutableTreeNode find(String name) {

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

	private static class Node implements Comparable<Node> {

		private String name;
		private TreeSet<Node> children;
		private TreePath path;
		private TreeNode n;

		@Override
		public int compareTo(Node n) {
			return this.name.compareTo(n.name);
		}

		public Node(String name, TreeNode n) {
			this.name = name.trim();
			children = new TreeSet<Node>();
			this.n = n;
			path = new TreePath(n);
		}

		public TreeSet<Node> getChildren() {
			return children;
		}

		public TreePath getPath() {
			return path;
		}

		public Node(String name, TreePath parent, TreeNode n) {
			this.name = name;
			children = new TreeSet<Node>();
			this.n = n;
			path = parent.pathByAddingChild(n); //nao sei se coloque aqui o nÃ³ ou o nome....
		}

		public void addChild(Node child) {
			children.add(child);
		}

		public Node find(String name) {
			if (n == null) {
				return null;
			}
			if (this.name.equalsIgnoreCase(name)) {
				return this;
			}
			if (this.name.length() >= 2) {
				String aux = this.name.substring(1, this.name.length() - 1);
				if (aux.equalsIgnoreCase(name)) {
					return this;
				}
			}
			Iterator<Node> itera = children.iterator();
			while (itera.hasNext()) {
				Node nex = itera.next();
				Node resultado = nex.find(name);
				if (resultado != null) {
					return nex;
				}
			}

			return null;
		}

		public String getName() {
			return name;
		}
	}
}
