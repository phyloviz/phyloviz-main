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

import net.phyloviz.upgmanjcore.visualization.ForcePair;
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
//import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
//import java.util.Scanner;
import java.util.TreeMap;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.phyloviz.category.CategoryProvider;
import net.phyloviz.category.filter.Category;
import net.phyloviz.goeburst.cluster.Edge;
import net.phyloviz.goeburst.cluster.GOeBurstClusterWithStats;
import net.phyloviz.core.data.Profile;
import net.phyloviz.goeburst.GOeBurstResult;
import net.phyloviz.goeburst.cluster.GOeBurstCluster;
//import net.phyloviz.gtview.action.EdgeFullViewControlAction;
import net.phyloviz.goeburst.cluster.GOeBurstNodeExtended;
import net.phyloviz.gtview.action.*;
import net.phyloviz.gtview.render.LabeledEdgeRenderer;
import net.phyloviz.upgmanjcore.visualization.InfoPanel;
import net.phyloviz.upgmanjcore.visualization.Point;
import net.phyloviz.upgmanjcore.visualization.actions.EdgeLevelLabelAction;
import net.phyloviz.upgmanjcore.visualization.actions.ExportAction;
import net.phyloviz.upgmanjcore.visualization.actions.HighQualityAction;
import net.phyloviz.upgmanjcore.visualization.actions.InfoControlAction;
import net.phyloviz.upgmanjcore.visualization.actions.LinearSizeControlAction;
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
import prefuse.controls.NeighborHighlightControl;
import prefuse.controls.PanControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.CascadedTable;
import prefuse.data.Graph;
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
import prefuse.util.force.Force;
import prefuse.util.force.ForceSimulator;
import prefuse.util.ui.JForcePanel;
import prefuse.util.ui.JSearchPanel;
import prefuse.util.ui.JValueSlider;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;

public class GraphView extends net.phyloviz.upgmanjcore.visualization.GView {

    private static final long serialVersionUID = 1L;

    private static final String SRC = Graph.DEFAULT_SOURCE_KEY;
    private static final String TRG = Graph.DEFAULT_TARGET_KEY;

    private String name;

    private Table nodeTable;
    private Table edgeTable;
    private VisualGraph vg;
    private TreeMap<Profile, Integer> st2rowid;
    private TreeMap<Edge<GOeBurstNodeExtended>, Integer> edge2rowid;
    Graph graph;

    private GOeBurstResult er;

    private Visualization view;
    private Display display;

    private String viz = "viz = 1";

    private JSearchPanel searchPanel;
    private boolean searchMatch = false;

    TitledBorder tb;
    private JScrollPane groupPanel;
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

    // Data analysis info...
    private CategoryProvider cp;
    final Map<String, Point> nodesPositions;
    private final Box box;

    public GraphView(String name, GOeBurstResult er, boolean isLinear, Map<String, Point> nodesPositions) {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        this.setOpaque(true);

        this.name = name;
        this.nodesPositions = nodesPositions;
        this.er = er;

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
        FontAction nfont
                = new FontAction("graph.nodes", FontLib.getFont("Tahoma", Font.PLAIN, 11)) {
                    @Override
                    public Font getFont(VisualItem item) {
                        GOeBurstNodeExtended st = (GOeBurstNodeExtended) item.getSourceTuple().get("st_ref");
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
        layout.add(new RepaintAction());

        ActionList staticLayout = new ActionList();
        staticLayout.add(fill);
        staticLayout.add(new RepaintAction());

        // Register actions with visualization.
        view.putAction("draw", draw);
        view.putAction("layout", layout);
        view.putAction("static", staticLayout);

        view.runAfter("draw", "layout");
        view.runAfter("draw", "static");

        // Setup the display.
        display = new Display(view);
        display.setForeground(Color.GRAY);
        display.setBackground(Color.WHITE);
        display.setHighQuality(false);
        add(display, BorderLayout.CENTER);

        // Display controls.
        display.addControlListener(new DragControl());
        display.addControlListener(new FocusControl(1));
        display.addControlListener(new BurstNeighborHighlightControl());
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

        Collection<GOeBurstClusterWithStats> groups = er.getClustering();
        int level = er.getLevel();

        // Build schemas.
        Schema nodeSchema = new Schema();
        nodeSchema.addColumn("st_id", String.class);
        nodeSchema.addColumn("viz", int.class);
        nodeSchema.addColumn("group", int.class);
        nodeSchema.addColumn("founder", int.class);
        nodeSchema.addColumn("hdlv", int.class);
        nodeSchema.addColumn("dg", int.class);
        nodeSchema.addColumn("st_ref", GOeBurstNodeExtended.class);

        Schema edgeSchema = new Schema();
        edgeSchema.addColumn(SRC, int.class);
        edgeSchema.addColumn("viz", int.class);
        edgeSchema.addColumn("edgep", double.class);
        edgeSchema.addColumn("group", int.class);
        edgeSchema.addColumn(TRG, int.class);
        edgeSchema.addColumn("edge_ref", Edge.class);

        // Create and fill tables.
        nodeSchema.lockSchema();
        edgeSchema.lockSchema();
        nodeTable = nodeSchema.instantiate();
        edgeTable = edgeSchema.instantiate();

        TreeMap<String, Integer> nodeMap = new TreeMap<String, Integer>();
        st2rowid = new TreeMap<Profile, Integer>();
        edge2rowid = new TreeMap<Edge<GOeBurstNodeExtended>, Integer>();

        Iterator<GOeBurstClusterWithStats> gIter = groups.iterator();
        while (gIter.hasNext()) {
            GOeBurstClusterWithStats g = gIter.next();

            Iterator<GOeBurstNodeExtended> stIter = g.getSTs().iterator();
            while (stIter.hasNext()) {
                GOeBurstNodeExtended st = stIter.next();
                int rowNb = nodeTable.addRow();
                st2rowid.put(st, rowNb);
                nodeMap.put(st.getID(), rowNb);
                nodeTable.setString(rowNb, "st_id", st.getID());
                nodeTable.setInt(rowNb, "viz", 1);
                nodeTable.setInt(rowNb, "group", g.getID());
                nodeTable.setInt(rowNb, "founder", g.isFounder(st) ? 1 : 0);
                nodeTable.setInt(rowNb, "hdlv", 0);
                nodeTable.setInt(rowNb, "dg", 0);
                nodeTable.set(rowNb, "st_ref", st);
            }

            Iterator<Edge<GOeBurstNodeExtended>> edgeIter = g.getEdges().iterator();
            while (edgeIter.hasNext()) {
                Edge<GOeBurstNodeExtended> e = edgeIter.next();

                if (!e.visible()) {
                    continue;
                }

                int rowNb = edgeTable.addRow();
                edge2rowid.put(e, rowNb);
                edgeTable.setInt(rowNb, SRC, nodeMap.get(e.getU().getID()));

                if (e.visible()) {
                    nodeTable.setInt(nodeMap.get(e.getU().getID()), "dg",
                            nodeTable.getInt(nodeMap.get(e.getU().getID()), "dg") + 1);
                    nodeTable.setInt(nodeMap.get(e.getV().getID()), "dg",
                            nodeTable.getInt(nodeMap.get(e.getV().getID()), "dg") + 1);
                    edgeTable.setInt(rowNb, "viz", er.getDistance().level(e));
                    if (er.getEdgestats().size() != 0) {
                        String edgestats = e.getU().getID() + e.getV().getID();
                        double freq = Double.valueOf(er.getEdgestats().get(edgestats));
                        edgeTable.setDouble(rowNb, "edgep", (double) Math.round((Math.pow(10, freq) * 100.0) * 10) / 10);
                    }
                    //} else {
                    //	edgeTable.setInt(rowNb, "viz", -1);
                }

                edgeTable.setInt(rowNb, "group", g.getID());
                edgeTable.setInt(rowNb, TRG, nodeMap.get(e.getV().getID()));
                edgeTable.set(rowNb, "edge_ref", e);
            }
        }

        /* Setup groupPanel. */
        groupList = new JList();
        groupList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        groupList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }

                view.setVisible("graph", null, false);
                int[] selectedIndices = groupList.getSelectedIndices();
                ArrayList<GOeBurstClusterWithStats> gList = new ArrayList<GOeBurstClusterWithStats>(selectedIndices.length);
                for (int i = 0; i < selectedIndices.length; i++) {
                    view.setVisible("graph",
                            (Predicate) ExpressionParser.parse("group=" + selectedIndices[i] + "and " + viz), true);

                    gList.add((GOeBurstClusterWithStats) groupList.getModel().getElementAt(selectedIndices[i]));
                }

                view.run("draw");
                appendTextToInfoPanel(GOeBurstClusterWithStats.combinedInfo(gList) + "\n");

                double x = display.getDisplayX();
                double y = display.getDisplayY();
                double dx = display.getWidth();
                double dy = display.getHeight();
                display.pan(x + dx / 2, y + dy / 2);
            }
        });
        groupList.setCellRenderer(new GroupCellRenderer());
        groupList.setListData(new Vector<GOeBurstClusterWithStats>(groups));

        groupPanel = new JScrollPane(groupList,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        groupPanel.getViewport().setBackground(Color.WHITE);
        groupPanel.setBackground(Color.WHITE);
        tb = new TitledBorder("Groups at "
                + (level == 1 ? "S" : level == 2 ? "D" : "T") + "LV:");
        tb.setBorder(new LineBorder(Color.BLACK));
        groupPanel.setBorder(tb);
        groupPanel.setPreferredSize(new Dimension(90, 600));
        add(groupPanel, BorderLayout.WEST);
        groupPanelStatus = true;

        infoPanel = new InfoPanel(name + " Info");
        infoPanel.open();
        infoPanel.requestActive();

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
        popupMenu.add(new GroupControlAction(this).getMenuItem());
        popupMenu.add(new InfoControlAction(this).getMenuItem());
        popupMenu.add(new EdgeViewControlAction(this).getMenuItem());
        //popupMenu.add(new EdgeFullViewControlAction(this).getMenuItem());
        popupMenu.add(new ShowLabelControlAction(this).getMenuItem());
        popupMenu.add(new EdgeLevelLabelAction(this).getMenuItem());
        if (er.getEdgestats().size() != 0) {
            popupMenu.add(new EdgePercentageLabelAction(this).getMenuItem());
        }
        popupMenu.add(new LinearSizeControlAction(this, linear).getMenuItem());
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
        exportButton.setIcon(new ImageIcon(GraphView.class.getResource("export.png")));
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

    }

    public void loadGraph(GOeBurstResult gr, boolean loaded) {

        // Create the graph.
        graph = new Graph(nodeTable, edgeTable, false);
        vg = view.addGraph("graph", graph);
        running = true;
        view.setVisible("graph", null, false);
        view.runAfter("draw", "layout");
        view.run("draw");

        if (nodesPositions != null) {

            Iterator<NodeItem> nodes = vg.nodes();
            while (nodes.hasNext()) {

                NodeItem n = nodes.next();
                VisualItem vu = (VisualItem) n;

                String profile = vu.getSourceTuple().getString("st_id");
                double x = nodesPositions.get(profile).x;
                double y = nodesPositions.get(profile).y;
                vu.setStartX(x);
                vu.setStartY(y);
                vu.setX(x);
                vu.setY(y);
                vu.setEndX(x);
                vu.setEndY(y);

            }
        }
        if (loaded) {
            stopAnimation();
        }
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
                        int gid = tableView.getInt(0, "group");
                        itemFound = gid;
                    }
                }
                groupList.repaint();
            }
        });
        view.addFocusGroup(Visualization.SEARCH_ITEMS, search);
        searchPanel = new NodeSearchPanel(view);
        searchPanel.setShowResultCount(true);
        box.add(searchPanel);
        box.add(Box.createHorizontalStrut(3));
        box.setOpaque(true);
        box.setBackground(Color.WHITE);
        box.updateUI();
        add(box, BorderLayout.SOUTH);

        groupList.setSelectedIndex(0);
        groupList.repaint();
        if (loaded) {
            view.cancel("layout");
            view.repaint();
            running = false;
            updateUI();
        }

    }

    public Map<String, Point> getNodesPositions() {
        Map<String, Point> positions = new HashMap<String, Point>();
        Iterator<NodeItem> nodes = vg.nodes();
        while (nodes.hasNext()) {
            NodeItem i = nodes.next();

            if (!i.canGetString("st_id")) {
                continue;
            }

            String id = i.getString("st_id");
            Point p = new Point(i.getX(), i.getY());
            positions.put(id, p);
        }
        return positions;
    }

    public void startAnimation() {
        view.run("draw");
        view.run("layout");
        updateUI();
        groupList.setSelectedIndex(0);
        groupList.repaint();
    }

    @Override
    public InfoPanel getInfoPanel() {
        return infoPanel;
    }

    public JScrollPane getGroupPanel() {
        return groupPanel;
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
            rf.setDefaultEdgeRenderer(new LabeledEdgeRenderer());
        } else {
            rf.setDefaultEdgeRenderer(new EdgeRenderer());
        }
    }

    //VERIFICAR
    @Override
    public void setEdgePercentageLabel(boolean status) {
        if (status) {
            rf.setDefaultEdgeRenderer(new LabeledEdgeRenderer("edgep"));
        } else {
            rf.setDefaultEdgeRenderer(new EdgeRenderer());
        }
    }

    @Override
    public void setHighQuality(boolean status) {
        display.setHighQuality(status);
    }

    public void setAllEdges(boolean status) {
        if (status) {
            viz = "viz > 0";
        } else {
            viz = "viz = 1";
        }

        view.setVisible("graph", null, false);
        int[] selectedIndices = groupList.getSelectedIndices();
        for (int i = 0; i < selectedIndices.length; i++) {
            view.setVisible("graph",
                    (Predicate) ExpressionParser.parse("group=" + selectedIndices[i] + "and " + viz), true);
        }
        view.run("draw");
    }

    public void forceSetAllEdges(boolean status) {
        if (status) {
            viz = "viz > -2";
        } else {
            viz = "viz = 1";
        }

        view.setVisible("graph", null, false);
        int[] selectedIndices = groupList.getSelectedIndices();
        for (int i = 0; i < selectedIndices.length; i++) {
            view.setVisible("graph",
                    (Predicate) ExpressionParser.parse("group=" + selectedIndices[i] + "and " + viz), true);
        }
        view.run("draw");
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

//	public void addSelection(File file, Color color) {
//		try {
//			Scanner in = new Scanner(file);
//			while (in.hasNextInt())
//				colors.put(in.nextInt(), color);
//		} catch (Exception e) {
//			System.out.println("Error:" + e.getStackTrace());
//		}
//	}
//	public void resetSelection() {
//		colors.clear();
//	}
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

    @Override
    public void setRescaleEdges(boolean status) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getRescaleEdges() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public float getDistanceFilterValue() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setDistanceFilterValue(float value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
            } else if (itemTuple.getInt("hdlv") == 1) {
                return ColorLib.rgb(255, 255, 125);
            } else {
                //Color color = colors.get(itemTuple.getString("st_id"));
                //if (color != null)
                //	return color.getRGB();

                if (itemTuple.getInt("founder") == 1) {
                    return ColorLib.rgb(200, 255, 55);
                }
                if (itemTuple.getInt("founder") == 2) {
                    return ColorLib.rgb(55, 255, 200);
                }

                if (item.getSourceTuple().getInt("dg") < 3) {
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

            // Black -> Red (tiebreak)
            Edge edge = (Edge) item.getSourceTuple().get("edge_ref");

            if (item.getSourceTuple().getInt("viz") > 1) {
                return ColorLib.gray(170 + (item.getSourceTuple().getInt("viz") - 1) * (85 / GOeBurstCluster.MAXLV));
            }

            // old color: ColorLib.gray(200);
            GOeBurstClusterWithStats g = (GOeBurstClusterWithStats) groupList.getModel().getElementAt(item.getSourceTuple().getInt("group"));
            switch (g.getEdgeMaxTieLevel(edge)) {
                case 0:
                    return ColorLib.rgb(0, 0, 0);
                case 1:
                    return ColorLib.rgb(0, 0, 255);
                case 2:
                    return ColorLib.rgb(0, 255, 0);
                case 3:
                    return ColorLib.rgb(255, 0, 0);
                default:
                    return ColorLib.rgb(255, 255, 0);
            }
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

    private class BurstNeighborHighlightControl extends NeighborHighlightControl {

        @Override
        protected void setNeighborHighlight(NodeItem n, boolean state) {
            GOeBurstNodeExtended st = (GOeBurstNodeExtended) n.getSourceTuple().get("st_ref");
            GOeBurstClusterWithStats g = (GOeBurstClusterWithStats) groupList.getModel().getElementAt(n.getSourceTuple().getInt("group"));

            Iterator<GOeBurstNodeExtended> iter = st.getSLVs().iterator();
            while (iter.hasNext()) {
                NodeItem nitem
                        = (NodeItem) view.getVisualItem("graph.nodes", n.getTable().getTuple(st2rowid.get(iter.next())));
                nitem.setHighlighted(state);
            }

            iter = st.getDLVs().iterator();
            while (iter.hasNext()) {
                n.getTable().setInt(st2rowid.get(iter.next()), "hdlv", state ? 1 : 0);
            }
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

            if (((GOeBurstClusterWithStats) groupList.getModel().getElementAt(index)).size() == 1) {
                setFont(list.getFont().deriveFont(Font.ITALIC));
                setForeground(Color.GRAY);
            }

            if (itemFound == index) {
                setFont(list.getFont().deriveFont(Font.BOLD));
                setForeground(Color.RED);
            }

            setOpaque(true);
            return this;
        }
    }

    private class ItemInfoControl extends ControlAdapter {

        @Override
        public void itemClicked(VisualItem item, MouseEvent e) {

            if (item instanceof EdgeItem) {
                Edge<GOeBurstNodeExtended> edge = (Edge<GOeBurstNodeExtended>) ((EdgeItem) item).getSourceTuple().get("edge_ref");

                char lv = '?';
                switch (er.getDistance().level(edge.getU(), edge.getV())) {
                    case 1:
                        lv = 's';
                        break;
                    case 2:
                        lv = 'd';
                        break;
                    case 3:
                        lv = 't';
                }

                appendTextToInfoPanel(edge + " ( " + lv + "lv level");

                String einfo = er.getDistance().info(edge);
                if (einfo != null) {
                    appendTextToInfoPanel("; " + einfo + ")\n");
                } else {
                    appendTextToInfoPanel(")\n");
                }

                appendTextToInfoPanel(((GOeBurstClusterWithStats) groupList.getModel().getElementAt(item.getInt("group"))).getInfo(edge)
                        + "\n");
            }
            if (item instanceof NodeItem) {
                GOeBurstNodeExtended st = (GOeBurstNodeExtended) ((NodeItem) item).getSourceTuple().get("st_ref");
                appendTextToInfoPanel(st + "\n"
                        + ((GOeBurstClusterWithStats) groupList.getModel().getElementAt(item.getInt("group"))).getInfo(st));

                appendTextToInfoPanel("Chart details:\n");
                if (cp != null) {
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

                if (e.isShiftDown()) {
                    JPopupMenu menu = new JPopupMenu();
                    JCheckBoxMenuItem founderItem = new JCheckBoxMenuItem("Founder");
                    founderItem.setToolTipText("Set as founder");

                    GOeBurstClusterWithStats g = (GOeBurstClusterWithStats) groupList.getModel().getElementAt(item.getInt("group"));
                    founderItem.setSelected(g.getFakeRoot() != null && g.getFakeRoot().getID().equals(item.getString("st_id")));
                    founderItem.addActionListener(new FounderActionListener(item));

                    menu.add(founderItem);
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        }
    }

    private class FounderActionListener implements ActionListener {

        VisualItem item;

        FounderActionListener(VisualItem item) {
            super();
            this.item = item;
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            GOeBurstClusterWithStats g = (GOeBurstClusterWithStats) groupList.getModel().getElementAt(item.getInt("group"));

            if (g.getFakeRoot() != null && g.getFakeRoot().getID().equals(item.getString("st_id"))) {
                ((JCheckBoxMenuItem) event.getSource()).setSelected(false);
                nodeTable.setInt(st2rowid.get(g.getFakeRoot()), "founder", g.isFounder(g.getFakeRoot()) ? 1 : 0);
                g.updateVisibleEdges();
            } else {
                ((JCheckBoxMenuItem) event.getSource()).setSelected(true);
                if (g.getFakeRoot() != null) {
                    nodeTable.setInt(st2rowid.get(g.getFakeRoot()), "founder", g.isFounder(g.getFakeRoot()) ? 1 : 0);
                }
                g.updateVisibleEdges((GOeBurstNodeExtended) item.get("st_ref"));
                nodeTable.setInt(st2rowid.get(g.getFakeRoot()), "founder", 2);
            }

            view.setVisible("graph", null, false);
            view.cancel("layout");

            Iterator<Edge<GOeBurstNodeExtended>> edgeIter = g.getEdges().iterator();
            while (edgeIter.hasNext()) {
                Edge<GOeBurstNodeExtended> e = edgeIter.next();

                if (!e.visible() && edge2rowid.containsKey(e)) {
                    graph.removeEdge(edge2rowid.get(e));
                    edge2rowid.remove(e);

                    int stRow = st2rowid.get(e.getU());
                    nodeTable.setInt(stRow, "dg", nodeTable.getInt(stRow, "dg") - 1);
                    stRow = st2rowid.get(e.getV());
                    nodeTable.setInt(stRow, "dg", nodeTable.getInt(stRow, "dg") - 1);
                }

                if (e.visible() && !edge2rowid.containsKey(e)) {
                    int rowID = graph.addEdge(st2rowid.get(e.getU()), st2rowid.get(e.getV()));
                    edge2rowid.put(e, rowID);

                    edgeTable.setInt(rowID, "group", g.getID());
                    edgeTable.set(rowID, "edge_ref", e);
                    edgeTable.setInt(rowID, "viz", er.getDistance().level(e));
                    if (er.getEdgestats().size() != 0) {
                        String edgestats = e.getU().getID() + e.getV().getID();
                        double freq = Double.valueOf(er.getEdgestats().get(edgestats));
                        freq = (double) Math.round(freq * 10) / 10;
                        edgeTable.setDouble(rowID, "edgep", freq);
                    }

                    int stRow = st2rowid.get(e.getU());
                    nodeTable.setInt(stRow, "dg", nodeTable.getInt(stRow, "dg") + 1);
                    stRow = st2rowid.get(e.getV());
                    nodeTable.setInt(stRow, "dg", nodeTable.getInt(stRow, "dg") + 1);
                }
            }

            int[] selectedIndices = groupList.getSelectedIndices();

            for (int i = 0; i < selectedIndices.length; i++) {

                view.setVisible("graph",
                        (Predicate) ExpressionParser.parse("group=" + selectedIndices[i] + "and " + viz), true);
            }
            view.run("draw");
            view.run("layout");
        }
    }
}
