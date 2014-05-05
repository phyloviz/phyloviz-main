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
package net.phyloviz.gtview.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.phyloviz.algo.AbstractDistance;
import net.phyloviz.algo.Edge;
import net.phyloviz.algo.util.DisjointSet;
import net.phyloviz.category.CategoryProvider;
import net.phyloviz.category.filter.Category;
import net.phyloviz.core.data.DataItem;
import net.phyloviz.core.data.DataModel;
import net.phyloviz.core.data.Population;
import net.phyloviz.core.data.Profile;
import net.phyloviz.goeburst.tree.GOeBurstMSTResult;
import net.phyloviz.goeburst.tree.GOeBurstNode;
import net.phyloviz.gtview.action.*;
import net.phyloviz.gtview.render.LabeledEdgeRenderer;

import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.FontAction;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.Control;
import prefuse.controls.ControlAdapter;
import prefuse.controls.DragControl;
import prefuse.controls.FocusControl;
import prefuse.controls.PanControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.CascadedTable;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Schema;
import prefuse.data.Table;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.expression.Predicate;
import prefuse.data.expression.parser.ExpressionParser;
import prefuse.data.search.PrefixSearchTupleSet;
import prefuse.data.tuple.TupleSet;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.GraphicsLib;
import prefuse.util.display.DisplayLib;
import prefuse.util.force.Force;
import prefuse.util.force.ForceSimulator;
import prefuse.util.ui.JForcePanel;
import prefuse.util.ui.JSearchPanel;
import prefuse.util.ui.JValueSlider;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;

public class GraphView2 extends GView {

	private static final long serialVersionUID = 1L;
	private static final String SRC = Graph.DEFAULT_SOURCE_KEY;
	private static final String TRG = Graph.DEFAULT_TARGET_KEY;
	private String name;
	private Table nodeTable;
	private Table edgeTable;
	private Graph graph;
	private VisualGraph vg;
	private GOeBurstMSTResult er;
	private final Visualization view;
	private Display display;
	private JSearchPanel searchPanel;
	private JProgressBar pbar;
	private boolean searchMatch = false;
	private JPanel groupPanel;
	private boolean groupPanelStatus;
	private JList groupList;
	private int itemFound = -1;
	private InfoPanel infoPanel;
	private JPopupMenu popupMenu;
	private LabelRenderer lr;
	private DefaultRendererFactory rf;
	private ForceDirectedLayout fdl;
	private boolean running;
	private boolean linear;
	private boolean label = true;
	private int level;
	private JSpinner sp;
	private JMenuItem groupMenuItem;
	private Box box;
	// Data analysis info...
	private CategoryProvider cp;

	public GraphView2(String name, GOeBurstMSTResult _er) {
		this.setLayout(new BorderLayout());
		this.setBackground(Color.WHITE);
		this.setOpaque(true);

		this.name = name;

		this.er = _er;

		// Create an empty visualization.
		view = new Visualization();

		// Setup renderers.
		rf = new DefaultRendererFactory();
		lr = new LabelRenderer("st_id");
		lr.setRoundedCorner(10, 10);
		rf.setDefaultRenderer(lr);
		view.setRendererFactory(rf);

		// Setup actions to process the visual data.
		ColorAction fill = new NodeColorAction("graph.nodes");
		ColorAction text = new ColorAction("graph.nodes", VisualItem.TEXTCOLOR, ColorLib.gray(0));
		ColorAction edge = new EdgeColorAction("graph.edges");
		FontAction nfont =
			new FontAction("graph.nodes", FontLib.getFont("Tahoma", Font.PLAIN, 11)) {
			@Override
			public Font getFont(VisualItem item) {
				Profile st = (Profile) item.getSourceTuple().get("st_ref");
				return FontLib.getFont("Tahoma", Font.PLAIN, 11 + (linear ? 11 * st.getFreq() : (7 * Math.log(1 + st.getFreq()))));
			}
		};

		ActionList draw = new ActionList();
		draw.add(fill);
		draw.add(text);
		draw.add(nfont);
		draw.add(edge);

		fdl = new ForceDirectedLayout("graph");

		ActionList layout = new ActionList(Activity.INFINITY);
		layout.add(fdl);
		layout.add(fill);
		layout.add(text);
		layout.add(nfont);
		layout.add(edge);
		layout.add(new RepaintAction());

		ActionList staticLayout = new ActionList();
		staticLayout.add(fill);
		staticLayout.add(new RepaintAction());

		// Register actions with visualization.
		view.putAction("draw", draw);
		view.putAction("layout", layout);
		view.putAction("static", staticLayout);

		//view.runAfter("draw", "layout");
		//view.runAfter("draw", "static");

		// Setup the display.
		display = new Display(view);
		display.setForeground(Color.GRAY);
		display.setBackground(Color.WHITE);
		display.setHighQuality(false);
		add(display, BorderLayout.CENTER);

		// Display controls.
		display.addControlListener(new DragControl());
		display.addControlListener(new FocusControl(1));
		display.addControlListener(new PanControl());
		display.addControlListener(new WheelZoomControl());
		display.addControlListener(new ZoomControl());
		display.addControlListener(new ZoomToFitControl());
		display.addControlListener((Control) new ItemInfoControl());

		display.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				view.run("static");
			}
		});

		// Build schemas.
		Schema nodeSchema = new Schema();
		nodeSchema.addColumn("st_id", String.class);
		nodeSchema.addColumn("st_ref", Profile.class);
		nodeSchema.addColumn("w", int.class);
		nodeSchema.addColumn("g", int.class);

		Schema edgeSchema = new Schema();
		edgeSchema.addColumn(SRC, int.class);
		edgeSchema.addColumn(TRG, int.class);
		edgeSchema.addColumn("edge_ref", Edge.class);
		edgeSchema.addColumn("w", int.class);
		edgeSchema.addColumn("g", int.class);

		// Create tables.
		nodeSchema.lockSchema();
		edgeSchema.lockSchema();
		nodeTable = nodeSchema.instantiate();
		edgeTable = edgeSchema.instantiate();

		// Group panel.
		groupList = new JList();
		groupList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		groupList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return;
				}

				int[] selectedIndices = groupList.getSelectedIndices();
				ArrayList<Group> gList = new ArrayList<Group>(selectedIndices.length);
				view.setVisible("graph", null, false);
				for (int i = 0; i < selectedIndices.length; i++) {
					Group g = (Group) groupList.getModel().getElementAt(selectedIndices[i]);
					view.setVisible("graph",
						(Predicate) ExpressionParser.parse("g=" + g.getID() + "and w <= " + level), true);

					gList.add(g);
				}

				view.run("draw");
				Iterator<Group> ig = gList.iterator();
				while (ig.hasNext()) {
					Group g = ig.next();
					appendTextToInfoPanel("Group " + g.id + " (" + g.size() + ")\n" + g.levelStats());
				}
				appendTextToInfoPanel("\n");

				SwingWorker job = new SwingWorker() {
					@Override
					protected Object doInBackground() throws Exception {

						Thread.sleep(100);

						Rectangle2D bounds = view.getBounds(Visualization.ALL_ITEMS);
						GraphicsLib.expand(bounds, 50 + (int) (1 / display.getScale()));
						DisplayLib.fitViewToBounds(display, bounds, 1000);

						return null;
					}
				};

				job.execute();

			}
		});
		groupList.setCellRenderer(new GroupCellRenderer());

		groupPanel = new JPanel();
		groupPanel.setLayout(new BorderLayout());

		JScrollPane groupListPanel = new JScrollPane(groupList,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		groupListPanel.getViewport().setBackground(Color.WHITE);
		groupListPanel.setBackground(Color.WHITE);
		TitledBorder tb = new TitledBorder("Groups");
		tb.setBorder(new LineBorder(Color.BLACK));
		groupListPanel.setBorder(tb);


		JPanel top = new JPanel(new BorderLayout());
		top.setBackground(Color.WHITE);
		//top.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));

		JLabel ll = new JLabel("Level: ");
		//ll.setMargin( new Insets(1, 1, 1, 1));
		ll.setBackground(Color.WHITE);

		sp = new JSpinner();
		sp.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
		sp.setBackground(Color.WHITE);


		top.add(ll, BorderLayout.WEST);
		top.add(sp, BorderLayout.CENTER);

		JPanel bs = new JPanel(new BorderLayout());
		bs.setBackground(Color.WHITE);

		JButton ub = new JButton("Get Groups");
		ub.setMargin(new Insets(1, 1, 1, 1));
		ub.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				DisjointSet ds = new DisjointSet(graph.getNodeCount());
				for (int i = 0; i < graph.getEdgeCount(); i++) {
					if (edgeTable.getInt(i, "w") <= level) {
						ds.unionSet(edgeTable.getInt(i, SRC), edgeTable.getInt(i, TRG));
					}
				}

				HashMap<Integer, Group> gmap = new HashMap<Integer, Group>();
				for (int i = 0; i < graph.getNodeCount(); i++) {
					if (!gmap.containsKey(ds.findSet(i))) {
						gmap.put(ds.findSet(i), new Group());
					}

					Group g = gmap.get(ds.findSet(i));
					g.add(i);
				}

				Group[] ga = gmap.values().toArray(new Group[0]);
				Arrays.sort(ga, new Comparator<Group>() {
					@Override
					public int compare(Group o1, Group o2) {
						return o2.size() - o1.size();
					}
				});
				int gc = 0;
				for (int i = 0; i < ga.length; i++) {
					ga[i].setID(i + 1);

					if (ga[i].size() == 1) {
						nodeTable.setInt(ga[i].getItems().get(0), "g", ga[i].getID());
					}
				}

				for (int i = 0; i < graph.getEdgeCount(); i++) {

					if (edgeTable.getInt(i, "w") > level) {
						edgeTable.setInt(i, "g", -1);
						continue;
					}

					int u = edgeTable.getInt(i, SRC);
					int v = edgeTable.getInt(i, TRG);

					Group g = gmap.get(ds.findSet(u));
					g.edgeAtLevel(edgeTable.getInt(i, "w"));
					nodeTable.setInt(u, "g", g.getID());
					nodeTable.setInt(v, "g", g.getID());
					edgeTable.setInt(i, "g", g.getID());
				}

				groupList.setListData(ga);
				startAnimation();
			}
		});
		bs.add(ub, BorderLayout.NORTH);

		JButton sab = new JButton("Save Groups");
		sab.setEnabled(er.getDataSet().getLookup().lookup(Population.class) != null);
		sab.setMargin(new Insets(1, 1, 1, 1));
		sab.addActionListener(new ActionListener() {
			Population pop = er.getDataSet().getLookup().lookup(Population.class);

			@Override
			public void actionPerformed(ActionEvent e) {

				// Integrate with isolate data, if it exists.
				if (pop != null) {

					Object[] options = {"Yes", "No"};
					int opt = JOptionPane.showOptionDialog(groupPanel,
						"Do you want to save groups for all possible levels at once?\n"
						+ "Note that it may take some time. Choose 'No' if you just want\n"
						+ "to save current groups.",
						"Save all groups",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null, // No icon
						options,
						options[1]);

					if (opt == 0) {

						// Sort edges....
						Integer[] eidx = new Integer[graph.getEdgeCount()];
						for (int i = 0; i < graph.getEdgeCount(); i++) {
							eidx[i] = i;
						}
						Arrays.sort(eidx, new Comparator<Integer>() {
							@Override
							public int compare(Integer o1, Integer o2) {
								return edgeTable.getInt(o1, "w") - edgeTable.getInt(o2, "w");
							}
						});

						DisjointSet ds = new DisjointSet(graph.getNodeCount());

						// Initialize level
						int lv = edgeTable.getInt(eidx[0], "w");

						// Process edges
						int i = 0;
						while (i < graph.getEdgeCount()) {

							int j = i;
							while (i < graph.getEdgeCount() && edgeTable.getInt(eidx[i], "w") == lv) {
								ds.unionSet(edgeTable.getInt(eidx[i], SRC), edgeTable.getInt(eidx[i], TRG));
								i++;
							}

							lv++;
							if (j == i) {
								continue;
							}

							HashMap<Integer, Group> gmap = new HashMap<Integer, Group>();
							for (int k = 0; k < graph.getNodeCount(); k++) {
								if (!gmap.containsKey(ds.findSet(k))) {
									gmap.put(ds.findSet(k), new Group());
								}

								Group g = gmap.get(ds.findSet(k));
								g.add(k);
							}

							Group[] ga = gmap.values().toArray(new Group[0]);
							Arrays.sort(ga, new Comparator<Group>() {
								@Override
								public int compare(Group o1, Group o2) {
									return o2.size() - o1.size();
								}
							});
							for (int k = 0; k < ga.length; k++) {
								ga[k].setID(k + 1);
							}

							final HashMap<String, String> st2cl = new HashMap<String, String>();
							for (int k = 0; k < nodeTable.getRowCount(); k++) {
								st2cl.put(nodeTable.getString(k, "st_id"),
									Integer.toString(gmap.get(ds.findSet(k)).getID()));
							}

							pop.addColumn("goeBURST MST[" + (lv - 1) + "]", new DataModel.ColumnFiller() {
								@Override
								public String getValue(DataItem i) {
									return st2cl.get(i.get(er.getDataSet().getPopKey()));
								}
							});

							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									pop.tableModel().fireTableStructureChanged();
								}
							});

						}

					} else {

						final HashMap<String, String> st2cl = new HashMap<String, String>();
						for (int i = 0; i < nodeTable.getRowCount(); i++) {
							st2cl.put(nodeTable.getString(i, "st_id"),
								String.valueOf(nodeTable.getInt(i, "g")));
						}

						pop.addColumn("goeBURST MST[" + level + "]", new DataModel.ColumnFiller() {
							@Override
							public String getValue(DataItem i) {
								return st2cl.get(i.get(er.getDataSet().getPopKey()));
							}
						});

						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								pop.tableModel().fireTableStructureChanged();
							}
						});
					}

				}
			}
		});
		bs.add(sab, BorderLayout.SOUTH);

		bs.add(Box.createVerticalStrut(3), BorderLayout.CENTER);

		top.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 3, 2, 3));
		top.add(bs, BorderLayout.SOUTH);

		groupPanel.add(top, BorderLayout.SOUTH);
		groupPanel.add(groupListPanel, BorderLayout.CENTER);
		groupPanel.setPreferredSize(new Dimension(90, 600));
		//add(groupPanel, BorderLayout.WEST);
		groupPanelStatus = false;

		infoPanel = new InfoPanel(name + " Info");
		infoPanel.open();
		infoPanel.requestActive();

		pbar = new JProgressBar();
		pbar.setMinimum(0);
		pbar.setMaximum(100);
		pbar.setStringPainted(true);
		pbar.setString(null);
		pbar.setValue(0);

		// Animation speed control.
		final JValueSlider animCtl = new JValueSlider("animation speed >>", 1, 100, 50);
		animCtl.setBackground(Color.WHITE);
		animCtl.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				fdl.setMaxTimeStep(animCtl.getValue().longValue());
			}
		});

		JButton playButton = new JButton(">");
		playButton.setMargin(new Insets(1, 1, 1, 1));
		playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				restartAnimation();
			}
		});

		JButton pauseButton = new JButton("||");
		pauseButton.setMargin(new Insets(1, 1, 1, 1));
		pauseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopAnimation();
			}
		});

		popupMenu = new JPopupMenu();
		groupMenuItem = new GroupControlAction(this).getMenuItem();
		groupMenuItem.setEnabled(false);
		popupMenu.add(groupMenuItem);
		popupMenu.add(new InfoControlAction(this).getMenuItem());
		//popupMenu.add(new EdgeViewControlAction(this).getMenuItem());
		//popupMenu.add(new EdgeFullViewControlAction(this).getMenuItem());
		popupMenu.add(new ShowLabelControlAction(this).getMenuItem());
		popupMenu.add(new EdgeLevelLabelAction(this).getMenuItem());
        //popupMenu.add(new EdgePercentageLabelAction(this).getMenuItem());
		popupMenu.add(new LinearSizeControlAction(this).getMenuItem());
		popupMenu.add(new HighQualityAction(this).getMenuItem());
		popupMenu.add(new ViewControlAction(this).getMenuItem());
		//popupMenu.add(new ExportAction(this).getMenuItem());

		JButton optionsButton = new JButton("Options");
		optionsButton.setMargin(new Insets(1, 1, 1, 1));
		optionsButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		});

		JButton exportButton = new JButton();
		exportButton.setIcon(new ImageIcon(GraphView2.class.getResource("export.png")));
		exportButton.setMargin(new Insets(0, 0, 0, 0));
		exportButton.addActionListener(new ExportAction(this));

		// Bottom box.
		box = new Box(BoxLayout.X_AXIS);
		box.add(Box.createHorizontalStrut(3));
		box.add(optionsButton);
		box.add(Box.createHorizontalStrut(5));
		box.add(exportButton);
		box.add(Box.createHorizontalStrut(8));
		box.add(playButton);
		box.add(Box.createHorizontalStrut(1));
		box.add(pauseButton);
		box.add(Box.createHorizontalStrut(5));
		box.add(animCtl);
		box.add(Box.createHorizontalGlue());
		//box.add(searchPanel);
		box.add(pbar);
		box.add(Box.createHorizontalStrut(3));
		box.setOpaque(true);
		box.setBackground(Color.WHITE);
		box.updateUI();
		add(box, BorderLayout.SOUTH);
	}

	public void loadGraph(final Collection<Edge<GOeBurstNode>> edges, final AbstractDistance ad) {
		final HashMap<Integer, Integer> uid2rowid = new HashMap<Integer, Integer>();

		// Create and register the graph.
		graph = new Graph(nodeTable, edgeTable, false);
		vg = view.addGraph("graph", graph);

		running = true;
		linear = false;
		view.setVisible("graph", null, false);
		view.runAfter("draw", "layout");
		view.run("draw");

		SwingWorker job = new SwingWorker() {
			@Override
			protected Object doInBackground() throws Exception {

				// Create initial group.
				Group g = new Group();
				g.setID(1);

				int maxlv = 0;
				int nedges = edges.size();

				Profile st = null;
				Iterator<Edge<GOeBurstNode>> eIter = edges.iterator();
				HashMap<Integer, LinkedList<Edge<GOeBurstNode>>> adjList = new HashMap<Integer, LinkedList<Edge<GOeBurstNode>>>();
				while (eIter.hasNext()) {
					Edge<GOeBurstNode> e = eIter.next();

					if (st == null) {
						st = e.getU();
					}

					int u = e.getU().getUID();
					int v = e.getV().getUID();


					if (adjList.get(u) == null) {
						adjList.put(u, new LinkedList<Edge<GOeBurstNode>>());
					}
					if (adjList.get(v) == null) {
						adjList.put(v, new LinkedList<Edge<GOeBurstNode>>());
					}
					adjList.get(u).add(e);
					adjList.get(v).add(e);
				}

				SwingWorker job = new SwingWorker() {
					@Override
					protected Object doInBackground() throws Exception {

						Thread.sleep(500);

						Rectangle2D bounds = view.getBounds(Visualization.ALL_ITEMS);
						GraphicsLib.expand(bounds, 50 + (int) (1 / display.getScale()));
						DisplayLib.fitViewToBounds(display, bounds, 2000);

						return null;
					}
				};

				job.execute();


				// Fill tables.
				LinkedList<Integer> q = new LinkedList<Integer>();
				q.add(st.getUID());
				int uRowNb = -1;
				synchronized (view) {
					uRowNb = nodeTable.addRow();
					uid2rowid.put(st.getUID(), uRowNb);
					nodeTable.setString(uRowNb, "st_id", st.getID());
					nodeTable.set(uRowNb, "st_ref", st);
					nodeTable.setInt(uRowNb, "w", 0);
					nodeTable.setInt(uRowNb, "g", 1);

					VisualItem vu = (VisualItem) vg.getNode(uRowNb);
					vu.setVisible(true);
				}

				int ec = 0;
				while (!q.isEmpty()) {

					int u = q.pop();
					uRowNb = uid2rowid.get(u);
					VisualItem vu = (VisualItem) vg.getNode(uRowNb);
					vu.setFixed(true);

					eIter = adjList.get(u).iterator();
					while (eIter.hasNext()) {

						Edge<GOeBurstNode> e = eIter.next();

						st = e.getV();
						if (st.getUID() == u) {
							st = e.getU();
						}

						// Let us check if this edge and vertex were already added...
						if (uid2rowid.containsKey(st.getUID())) {
							continue;
						}

						q.add(st.getUID());

						int lv = ad.level(e.getU(), e.getV());
						if (lv > maxlv) {
							maxlv = lv;
						}
						g.edgeAtLevel(lv);

						synchronized (view) {

							int vRowNb = nodeTable.addRow();
							uid2rowid.put(st.getUID(), vRowNb);
							nodeTable.setString(vRowNb, "st_id", st.getID());
							nodeTable.set(vRowNb, "st_ref", st);
							nodeTable.setInt(vRowNb, "w", 0);
							nodeTable.setInt(vRowNb, "g", 1);

							int rowNb = edgeTable.addRow();
							edgeTable.setInt(rowNb, SRC, uRowNb);
							edgeTable.setInt(rowNb, TRG, vRowNb);
							edgeTable.set(rowNb, "edge_ref", e);
							edgeTable.set(rowNb, "w", lv);
							edgeTable.set(rowNb, "g", 1);

							VisualItem vv = (VisualItem) vg.getNode(vRowNb);
							prefuse.data.Edge ve = vg.getEdge(rowNb);

							vv.setStartX(vu.getEndX());
							vv.setStartX(vu.getEndX());
							vv.setX(vu.getEndX());
							vv.setY(vu.getEndY());
							vv.setEndX(vu.getEndX());
							vv.setEndY(vu.getEndY());

							vv.setVisible(true);
							((VisualItem) ve).setVisible(true);


							view.wait(20);
						}

						//Thread.sleep(5);

						ec++;
						int perc = ec * 100 / nedges;
						setProgress(perc);
					}

					vu.setFixed(false);
				}
				level = maxlv;

				groupList.setListData(new Group[]{g});
				final SpinnerNumberModel model = new SpinnerNumberModel(maxlv, 1, maxlv, 1);
				model.addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent e) {
						view.setVisible("graph", null, false);
						level = model.getNumber().intValue();

						int[] selectedIndices = groupList.getSelectedIndices();
						for (int i = 0; i < selectedIndices.length; i++) {
							Group g = (Group) groupList.getModel().getElementAt(selectedIndices[i]);
							view.setVisible("graph",
								(Predicate) ExpressionParser.parse("g=" + g.getID() + "and w <= " + level), true);
						}

						view.run("draw");

					}
				});
				sp.setModel(model);
				sp.setValue(maxlv);

				showGroupPanel(true);
				groupMenuItem.setEnabled(true);

				// Search stuff.
				TupleSet search = new PrefixSearchTupleSet();
				search.addTupleSetListener(new TupleSetListener() {
					@Override
					public void tupleSetChanged(TupleSet t, Tuple[] add, Tuple[] rem) {
						view.run("static");
						searchMatch = true;

						itemFound = -1;
						if (t.getTupleCount() >= 1) {
							String ss = searchPanel.getQuery();
							CascadedTable tableView = new CascadedTable(nodeTable, (Predicate) ExpressionParser.parse("st_id=\"" + ss + "\""));
							if (tableView.getRowCount() > 0) {
								int gid = tableView.getInt(0, "g");
								itemFound = gid;
							}
						}
						groupList.repaint();
					}
				});
				view.addFocusGroup(Visualization.SEARCH_ITEMS, search);
				searchPanel = new NodeSearchPanel(view);
				searchPanel.setShowResultCount(true);
				box.remove(11);
				box.add(searchPanel, 11);
				box.validate();

				groupList.setSelectedIndex(0);
				groupList.repaint();

				return null;
			}
		};

		job.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent pce) {
				if (pce.getPropertyName().equals("progress")) {
					int progress = (Integer) pce.getNewValue();
					pbar.setValue(progress);
				}
			}
		});

		job.execute();
	}

	public void startAnimation() {
		view.run("draw");
		view.run("layout");
		updateUI();
		groupList.setSelectedIndex(0);
		groupList.repaint();

		running = true;
	}

	@Override
	public InfoPanel getInfoPanel() {
		return infoPanel;
	}

	public void restartAnimation() {

		if (running) {
			return;
		}

		//view.cancel("static");
		view.run("layout");

		view.repaint();

		running = true;

		updateUI();
	}

	public void stopAnimation() {

		if (!running) {
			return;
		}

		view.cancel("layout");
		//view.run("static");

		view.repaint();

		running = false;

		updateUI();
	}

	@Override
	public void showGroupPanel(boolean status) {
		if (status == groupPanelStatus) {
			return;
		}

		if (status) {
			add(groupPanel, BorderLayout.WEST);
		} else {
			remove(groupPanel);
		}
		groupPanelStatus = status;
	}

	@Override
	public void showInfoPanel() {
		if (infoPanel == null) {
			infoPanel = new InfoPanel(name + " Info");
		}

		if (!infoPanel.isOpened()) {
			infoPanel.open();
		}

		infoPanel.requestActive();
	}

	@Override
	public void closeInfoPanel() {
		infoPanel.close();
	}

	@Override
	public boolean getLinearSize() {
		return linear;
	}

	@Override
	public void setLinearSize(boolean status) {
		if (linear != status) {
			linear = status;
			view.run("draw");
		}
	}

	@Override
	public void setLevelLabel(boolean status) {
		if (status) {
			rf.setDefaultEdgeRenderer(new LabeledEdgeRenderer("w"));
		} else {
			rf.setDefaultEdgeRenderer(new EdgeRenderer());
		}
	}
   
    //VERIFICAR
    @Override
	public void setEdgePercentageLabel(boolean status) {
		if (status)
			rf.setDefaultEdgeRenderer(new LabeledEdgeRenderer("edgep"));
		else
			rf.setDefaultEdgeRenderer(new EdgeRenderer());
	}

	@Override
	public void setHighQuality(boolean status) {
		display.setHighQuality(status);
	}

	public void setCategoryProvider(CategoryProvider cp) {
		this.cp = cp;
	}

	public void appendTextToInfoPanel(String text) {
		if (infoPanel != null) {
			infoPanel.append(text);
			infoPanel.flush();
		}
	}

	@Override
	public JComponent getDisplay() {
		return display;
	}

	@Override
	public Visualization getVisualization() {
		return view;
	}

	public long getSpeed() {
		return fdl.getMaxTimeStep();
	}

	public void setSpeed(long step) {
		fdl.setMaxTimeStep(step);
	}

	public void resetDefaultRenderer() {
		view.cancel("layout");
		view.cancel("static");
		rf.setDefaultRenderer(lr);
		if (running) {
			view.run("layout");
		} else {
			view.run("static");
		}
	}

	public void setDefaultRenderer(AbstractShapeRenderer r) {
		view.cancel("layout");
		view.cancel("static");
		rf.setDefaultRenderer(r);
		if (running) {
			view.run("layout");
		} else {
			view.run("static");
		}
	}

	@Override
	public ForceDirectedLayout getForceLayout() {
		return fdl;
	}

	// We make sure that we return always the same panel.
	@Override
	public JForcePanel getForcePanel() {
		return new JForcePanel(fdl.getForceSimulator());
	}

	public static class Pair {

		public float value;
		public String name;

		public Pair(String name, float i) {
			value = i;
			this.name = name;
		}

		@Override
		public String toString() {
			return name + ":" + value;
		}

		public static Pair valueOf(String s) {
			int k = s.indexOf(':');
			String i = s.substring(0, k);
			float j = Float.parseFloat(s.substring(k + 1));
			Pair p = new Pair(i, j);
			return p;
		}
	}

	@Override
	public ArrayList<ForcePair> getForces() {
		ForceSimulator fs = fdl.getForceSimulator();
		Force[] farray = fs.getForces();
		ArrayList<ForcePair> array = new ArrayList<ForcePair>();
		for (int i = 0; i < farray.length; i++) {

			int ni = farray[i].getParameterCount();
			for (int j = 0; j < ni; j++) {
				array.add(new ForcePair(farray[i].getParameterName(j), farray[i].getParameter(j)));
			}
		}
		return array;
		//return fdl.getForceSimulator();
	}

	@Override
	public boolean showLabel() {
		return label;
	}

	@Override
	public void setShowLabel(boolean status) {
		if (label != status) {
			label = status;
			view.run("draw");
		}
	}

	// Private classes.
	private class NodeColorAction extends ColorAction {

		public NodeColorAction(String group) {
			super(group, VisualItem.FILLCOLOR);

		}

		@Override
		public int getColor(VisualItem item) {
			Tuple itemTuple = item.getSourceTuple();


			if (m_vis.isInGroup(item, Visualization.SEARCH_ITEMS)) {

				if (itemTuple.getString("st_id").equals(searchPanel.getQuery())) {
					if (searchMatch) {
						display.panToAbs(new Point2D.Double(item.getX(), item.getY()));
					}
					searchMatch = false;
					return ColorLib.rgb(255, 0, 0);
				}

				return ColorLib.rgb(200, 100, 100);

			} else if (item.isFixed()) {
				return ColorLib.rgb(255, 100, 100);
			} else if (item.isHighlighted()) {
				return ColorLib.rgb(255, 200, 125);
			} else {
				//Color color = colors.get(itemTuple.getString("st_id"));
				//if (color != null)
				//	return color.getRGB();

				if (((Node) item).getDegree() < 3) {
					return ColorLib.rgb(200, 200, 255);
				} else {
					return ColorLib.rgb(200, 200, 55);
				}
			}
		}
	}

	private class EdgeColorAction extends ColorAction {

		public EdgeColorAction(String group) {
			super(group, VisualItem.STROKECOLOR);
		}

		@Override
		public int getColor(VisualItem item) {
			int w = item.getSourceTuple().getInt("w");

			return ColorLib.gray(200 - 200 / w); // .rgb(255, 255,   0);
		}
	}

	private class NodeSearchPanel extends JSearchPanel {

		private static final long serialVersionUID = 1L;

		NodeSearchPanel(Visualization view) {
			super(view, "graph.nodes", Visualization.SEARCH_ITEMS, "st_id", true, true);
			setShowResultCount(false);
			setBorder(BorderFactory.createEmptyBorder(5, 5, 4, 0));
			setFont(FontLib.getFont("Tahoma", Font.PLAIN, 11));
			setBackground(Color.WHITE);
			setForeground(Color.BLACK);
			requestFocus();
		}
	}

	private class ItemInfoControl extends ControlAdapter {

		@Override
		public void itemClicked(VisualItem item, MouseEvent e) {



			if (item instanceof EdgeItem) {
				Edge edge = (Edge) ((EdgeItem) item).getSourceTuple().get("edge_ref");
				appendTextToInfoPanel(edge.getU().getID() + " -- "
					+ edge.getV().getID() + " (lv="
					+ item.getSourceTuple().getString("w"));

				String einfo = er.getDistance().info(edge);
				if (einfo != null) {
					appendTextToInfoPanel("; " + einfo + ")\n\n");
				} else {
					appendTextToInfoPanel(")\n\n");
				}

			}
			if (item instanceof NodeItem) {
				Profile st = (Profile) ((NodeItem) item).getSourceTuple().get("st_ref");
				appendTextToInfoPanel(st.toString() + "\n");
				appendTextToInfoPanel("# isolates = " + st.getFreq() + "\n");

				if (cp != null) {
					appendTextToInfoPanel("Chart details:\n");
					int total = 0;
					DecimalFormat df = new DecimalFormat("#.##");
					Collection<Category> groupsList = cp.getCategories(st.getID());
					if (groupsList != null) {
						Iterator<Category> groups = groupsList.iterator();
						while (groups.hasNext()) {
							Category group = groups.next();
							double percent = (((double) group.weight() * 100) / st.getFreq());
							appendTextToInfoPanel(" +" + group.getName() + " " + df.format(percent) + "%\n");
							total += group.weight();
						}
					}
					double percent = (((double) (st.getFreq() - total) * 100) / st.getFreq());
					if (percent > 0) {
						appendTextToInfoPanel(" + 'others' " + df.format(percent) + "%\n");
					}
				}

				appendTextToInfoPanel("\n");
			}
		}
	}

	private class Group {

		private int id;
		private LinkedList<Integer> list;
		private TreeMap<Integer, Integer> levelStat;

		Group() {
			this.id = 0;
			list = new LinkedList<Integer>();
			levelStat = new TreeMap<Integer, Integer>();
		}

		public void setID(int id) {
			this.id = id;
		}

		public int getID() {
			return id;
		}

		public List<Integer> getItems() {
			return list;
		}

		public void add(int u) {
			list.add(u);
		}

		public int size() {
			return list.size();
		}

		public void edgeAtLevel(int level) {
			Integer n = levelStat.get(level);

			levelStat.put(level, (n == null) ? 1 : n + 1);
		}

		@Override
		public String toString() {
			return Integer.toString(id);
		}

		public String levelStats() {
			String s = "";

			Iterator<Entry<Integer, Integer>> i = levelStat.entrySet().iterator();
			while (i.hasNext()) {
				Entry<Integer, Integer> e = i.next();
				s += " + " + e.getValue() + " links at level " + e.getKey() + "\n";
			}

			return s;
		}
	}

	private class GroupCellRenderer extends JLabel implements ListCellRenderer {

		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			String s = value.toString();
			setText(s);
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			setEnabled(list.isEnabled());
			setFont(list.getFont());

			Group g = (Group) groupList.getModel().getElementAt(index);
			if (g.size() == 1) {
				setFont(list.getFont().deriveFont(Font.ITALIC));
				setForeground(Color.GRAY);
			}

			if (itemFound == g.getID()) {
				setFont(list.getFont().deriveFont(Font.BOLD));
				setForeground(Color.RED);
			}

			setOpaque(true);
			return this;
		}
	}
}
