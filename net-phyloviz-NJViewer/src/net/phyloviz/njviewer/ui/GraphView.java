package net.phyloviz.njviewer.ui;

import net.phyloviz.upgmanjcore.visualization.Point;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.phyloviz.algo.AbstractDistance;
import net.phyloviz.algo.Edge;
import net.phyloviz.category.CategoryProvider;
import net.phyloviz.category.filter.Category;
import net.phyloviz.core.data.Profile;
import net.phyloviz.nj.tree.NJLeafNode;
import net.phyloviz.nj.tree.NJRoot;
import net.phyloviz.nj.tree.NeighborJoiningItem;
import net.phyloviz.nj.tree.NJRoot.EdgeDistanceWrapper;
import net.phyloviz.nj.tree.NJUnionNode;
import net.phyloviz.nj.tree.NodeType;
import net.phyloviz.njviewer.action.ForceDistanceLayoutAction;
import net.phyloviz.njviewer.action.ForceDirectedLayoutAction;
import net.phyloviz.njviewer.action.control.JRadialViewControlPanel;
import net.phyloviz.njviewer.action.RadialViewControlAction;
import net.phyloviz.upgmanjcore.visualization.actions.HighQualityAction;
import net.phyloviz.njviewer.action.RoundDistanceAction;
import net.phyloviz.njviewer.action.ShowDistancesLayoutChartAction;
import net.phyloviz.njviewer.action.ForceViewControlAction;
import net.phyloviz.njviewer.render.BarChartRenderer;
import net.phyloviz.njviewer.render.ChartRenderer;
import net.phyloviz.njviewer.render.LabeledEdgeRenderer;
import net.phyloviz.njviewer.render.LabeledRadialEdgeRenderer;
import net.phyloviz.njviewer.render.NodeLabelRenderer;
import net.phyloviz.njviewer.render.RadialEdgeRenderer;
import net.phyloviz.njviewer.render.RadialNodeLabelRenderer;
import net.phyloviz.njviewer.ui.color.ChartLegendPanel;
import net.phyloviz.njviewer.ui.color.ChartsPanel;
import net.phyloviz.njviewer.ui.color.EdgeStats;
import net.phyloviz.upgmanjcore.visualization.ForcePair;

import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.controls.Control;
import prefuse.controls.ControlAdapter;
import prefuse.controls.DragControl;
import prefuse.controls.FocusControl;
import prefuse.controls.PanControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
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
import net.phyloviz.upgmanjcore.visualization.GView;
import net.phyloviz.upgmanjcore.visualization.InfoPanel;
import net.phyloviz.upgmanjcore.visualization.actions.EdgeLevelLabelAction;
import net.phyloviz.upgmanjcore.visualization.actions.ExportAction;
import net.phyloviz.upgmanjcore.visualization.actions.InfoControlAction;
import net.phyloviz.upgmanjcore.visualization.actions.LinearSizeControlAction;
import net.phyloviz.upgmanjcore.visualization.actions.RescaleEdgesControlAction;
import prefuse.action.assignment.FontAction;
import prefuse.action.layout.Layout;
import prefuse.util.PrefuseLib;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;

public class GraphView extends GView {

    private static final long serialVersionUID = 1L;
    private static final String SRC = Graph.DEFAULT_SOURCE_KEY, TRG = Graph.DEFAULT_TARGET_KEY;
    private static final float cutIncDev = 0.001f;

    private float distance;
    private final JSpinner sp;
    private final Box bottomBox;
    private final Visualization view;
    private final JProgressBar pbar;

    private boolean hasDistanceLabel = false, roundDistanceValues = false;
    private String name;
    private final Table nodeTable, edgeTable;
    private Graph graph;
    private VisualGraph vg;
    private final Display display;
    private JSearchPanel searchPanel;
    private boolean searchMatch = false;
//    private JList groupList;
    private InfoPanel infoPanel;
    //private ChartLegendPanel chartInfoPos, chartInfoNeg;
    private JPopupMenu popupMenu;
    private LabelRenderer lr;
    private DefaultRendererFactory rf;
    private Layout fdl;
    private boolean running, linear, label = true;
    // Data analysis info...
    private CategoryProvider cp;
    double maxDistance = 0, minDistance = 0;
    final Map<String, Point> nodesPositions;
    private boolean rescaleDistance = false, showChart = false;

    private ChartsPanel edgeInfoPanel;
    private EdgeColorAction edge;
    private EdgeStats positiveEdges;
    private EdgeStats negativeEdges;
    private boolean forceDistanceLayout;
    private final NJRoot root;
    private int m_size;
    private boolean isRadial = true;
//    private HashMap<String, Integer> props;
    private ActionList filter;
    private int fontSize = 5;
    private final JMenuItem showDistancesChartAction;
    private final JMenuItem forceViewControlAction;
    private final JMenuItem radialViewControlMenuItem;
    private final JMenuItem forceDistanceLayoutAction;
    private final JMenuItem rescaleEdgesAction;
    private final JMenuItem roundDistancesAction;
    private final JButton playButton;
    private final JButton pauseButton;
    private final JValueSlider animCtl;
    private JRadialViewControlPanel radialViewControlPanel;

    public GraphView(String name, NeighborJoiningItem _er, boolean linear, Map<String, Point> nodesPositions) {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        this.setOpaque(true);
        this.name = name;
        this.nodesPositions = nodesPositions;
        this.root = _er.getRoot();
//        this.linear = linear;
        // Create an empty visualization.
        view = new Visualization();
        // Setup renderers.
        rf = new DefaultRendererFactory();
        lr = new RadialNodeLabelRenderer(this, "st_id");
        rf.setDefaultRenderer(lr);
        view.setRendererFactory(rf);
        // Setup actions to process the visual data.
        ColorAction fill = new NodeColorAction("graph.nodes");
        ColorAction text = new ColorAction("graph.nodes", VisualItem.TEXTCOLOR, ColorLib.gray(0));
        edge = new EdgeColorAction("graph.edges");
        FontAction nfont
                = new FontAction("graph.nodes", FontLib.getFont("Tahoma", Font.PLAIN, fontSize)) {
                    @Override
                    public Font getFont(VisualItem item) {
                        Profile st = (Profile) item.getSourceTuple().get("st_ref");
                        return FontLib.getFont("Tahoma", Font.PLAIN, fontSize);//11 + (GraphView.this.linear ? 11 * st.getFreq() : (7 * Math.log(1 + st.getFreq()))));
                    }
                };

        ActionList draw = new ActionList();
        draw.add(fill);
        draw.add(text);
        draw.add(nfont);
        draw.add(edge);

//        fdl = new ForceDirectedLayout("graph", false, true) {
//            @Override
//            protected float getSpringLength(EdgeItem e) {
//                double dist;
//                if (e.getFloat("distance") < 0.01) {
//                    dist = 10 + 300 * 0.01f;
//                } else {
//                    dist = 10 + 300 * e.getFloat("distance");
//                }
//                dist = rescaleDistance ? Math.log(dist) : dist;
//                return (float) dist;
//            }
//
//            //@Override
//            //protected float getMassValue(VisualItem n) { 
//            //	return 3.0f;	
//            //}
//        };
//
//        fdl.setIterations(500);
//        fdl = new ForceDirectedLayout("graph") {
//            @Override
//            protected float getSpringLength(EdgeItem e) {
//                double distance = e.getDouble("distance");
//                double dist = rescaleDistance ? Math.log(1 + distance) : distance;
//                return (float) dist;
//            }
//        };
        //fdl = new RadialLayout("graph", root.distance, 8194);
        filter = new ActionList();
        filter.add(fill);
        filter.add(text);
        filter.add(edge);
        filter.add(new RepaintAction());

        ActionList layout = new ActionList();
        layout.add(filter);

        ActionList staticLayout = new ActionList();
        staticLayout.add(fill);
        staticLayout.add(new RepaintAction());
        // Register actions with visualization.
        view.putAction("draw", draw);
        view.putAction("layout", layout);
        view.putAction("static", staticLayout);
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
        nodeSchema.addColumn("profile", Profile.class);
        nodeSchema.addColumn("st_ref", NodeType.class);
        nodeSchema.addColumn("w", int.class);
        nodeSchema.addColumn("g", int.class);
        nodeSchema.addColumn("distance", float.class);
        nodeSchema.addColumn("wedgesize", float.class);
        nodeSchema.addColumn("rightborder", float.class);
        nodeSchema.addColumn("distance0", float.class);
        nodeSchema.addColumn("distance1", float.class);
        nodeSchema.addColumn("n0", NodeType.class);
        nodeSchema.addColumn("n1", NodeType.class);
        nodeSchema.addColumn("x", double.class);
        nodeSchema.addColumn("y", double.class);
        nodeSchema.addColumn("xp", double.class);
        nodeSchema.addColumn("yp", double.class);

        Schema edgeSchema = new Schema();
        edgeSchema.addColumn(SRC, int.class);
        edgeSchema.addColumn(TRG, int.class);
        edgeSchema.addColumn("edge_ref", Edge.class);
        edgeSchema.addColumn("w", int.class);
        edgeSchema.addColumn("g", int.class);
        edgeSchema.addColumn("distance", float.class);
        // Create tables.
        nodeSchema.lockSchema();
        edgeSchema.lockSchema();
        nodeTable = nodeSchema.instantiate();
        edgeTable = edgeSchema.instantiate();
        // Group panel.
//        groupList = new JList();
//        groupList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//        groupList.addListSelectionListener(new ListSelectionListener() {
//            @Override
//            public void valueChanged(ListSelectionEvent e) {
//                if (e.getValueIsAdjusting()) {
//                    return;
//                }
//                int[] selectedIndices = groupList.getSelectedIndices();
//                view.setVisible("graph", null, false);
//                for (int i = 0; i < selectedIndices.length; i++) {
//                    Group g = (Group) groupList.getModel().getElementAt(selectedIndices[i]);
//                    view.setVisible("graph",
//                            (Predicate) ExpressionParser.parse("g=" + g.getID() + "and w <= " + distance), true);
//                }
//                view.run("draw");
//
//                appendTextToInfoPanel("\n");
//                SwingWorker job = new SwingWorker() {
//                    @Override
//                    protected Object doInBackground() throws Exception {
//                        //Thread.sleep(100);
//                        Rectangle2D bounds = view.getBounds(Visualization.ALL_ITEMS);
//                        GraphicsLib.expand(bounds, 50 + (int) (1 / display.getScale()));
//                        DisplayLib.fitViewToBounds(display, bounds, 1000);
//                        return null;
//                    }
//                };
//                job.execute();
//            }
//        });
//        groupList.setCellRenderer(new GroupCellRenderer());

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);

        JPanel bs = new JPanel(new BorderLayout());
        bs.setBackground(Color.WHITE);

        bs.add(Box.createVerticalStrut(3), BorderLayout.CENTER);

        top.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 3, 2, 3));
        top.add(bs, BorderLayout.SOUTH);

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
        animCtl = new JValueSlider("Anim. speed>>", 1, 100, 50);
        animCtl.setBackground(Color.WHITE);
        animCtl.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!isRadial) {
                    ((ForceDirectedLayout) fdl).setMaxTimeStep(animCtl.getValue().longValue());
                }
            }
        });

        playButton = new JButton(">");
        playButton.setMargin(new Insets(1, 1, 1, 1));
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartAnimation();
            }
        });

        pauseButton = new JButton("||");
        pauseButton.setMargin(new Insets(1, 1, 1, 1));
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopAnimation();
            }
        });
        rescaleEdgesAction = new RescaleEdgesControlAction(this, rescaleDistance).getMenuItem();
        rescaleEdgesAction.setEnabled(!isRadial);
        roundDistancesAction = new RoundDistanceAction(this).getMenuItem();
        forceDistanceLayoutAction = new ForceDistanceLayoutAction(this).getMenuItem();
        showDistancesChartAction = new ShowDistancesLayoutChartAction(this).getMenuItem();
        forceViewControlAction = new ForceViewControlAction(this).getMenuItem();
        radialViewControlMenuItem = new RadialViewControlAction(this, getRadialViewControlPanel()).getMenuItem();

        popupMenu = new JPopupMenu();
        popupMenu.add(new InfoControlAction(this).getMenuItem());
        popupMenu.add(new HighQualityAction(this).getMenuItem());
        popupMenu.add(radialViewControlMenuItem);
        popupMenu.add(new ForceDirectedLayoutAction(this).getMenuItem());
        popupMenu.add(forceDistanceLayoutAction);
        popupMenu.add(rescaleEdgesAction);
        popupMenu.add(new LinearSizeControlAction(this, linear).getMenuItem());
        popupMenu.add(forceViewControlAction);
        popupMenu.add(new EdgeLevelLabelAction(this).getMenuItem());
        popupMenu.add(roundDistancesAction);
        popupMenu.add(showDistancesChartAction);


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

        JLabel ll = new JLabel("Distances: ");
        ll.setBackground(Color.WHITE);
        sp = new JSpinner();
        sp.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        sp.setBackground(Color.WHITE);

        /*Right Box*/
        Box verticalBox = new Box(BoxLayout.Y_AXIS);
        JLabel cutLabel1 = new JLabel("cut off");
        JLabel cutLabel2 = new JLabel("threshold:");

        verticalBox.add(Box.createVerticalStrut(5));
        verticalBox.add(cutLabel1);
        verticalBox.add(cutLabel2);
        verticalBox.add(sp);

        verticalBox.setBackground(Color.WHITE);

        JPanel verticalPanel = new JPanel(new GridLayout(3, 1));
        verticalPanel.add(emptyJPanel());
        verticalPanel.add(emptyJPanel());
        verticalPanel.add(verticalBox);
        verticalPanel.setBackground(Color.WHITE);

        /*Bottom Box*/
        bottomBox = new Box(BoxLayout.X_AXIS);
        bottomBox.add(Box.createHorizontalStrut(3));
        bottomBox.add(optionsButton);
        bottomBox.add(Box.createHorizontalStrut(5));
        bottomBox.add(exportButton);
        bottomBox.add(Box.createHorizontalStrut(8));
        bottomBox.add(playButton);
        bottomBox.add(Box.createHorizontalStrut(1));
        bottomBox.add(pauseButton);
        bottomBox.add(Box.createHorizontalStrut(5));
        bottomBox.add(animCtl);
        bottomBox.add(Box.createHorizontalGlue());
        bottomBox.add(pbar);
        bottomBox.add(Box.createHorizontalStrut(3));
        bottomBox.setOpaque(true);
        bottomBox.setBackground(Color.WHITE);
        bottomBox.updateUI();
        add(bottomBox, BorderLayout.SOUTH);
        add(verticalPanel, BorderLayout.EAST);

    }

    private JPanel emptyJPanel() {
        JPanel j = new JPanel();
        j.setBackground(Color.WHITE);
        return j;
    }
    final HashMap<Integer, Integer> uid2rowid = new HashMap<>();

    public void loadGraph(final NJRoot root, final AbstractDistance ad, final double distanceFilter, final boolean loaded) {

        // Create and register the graph.
        graph = new Graph(nodeTable, edgeTable, false);
        vg = view.addGraph("graph", graph);

        running = !loaded;
        view.setVisible("graph", null, false);
        view.runAfter("draw", "layout");
        view.run("draw");

        SwingWorker job = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                // Create initial group.
//                Group g = new Group();
//                g.setID(1);

                int nedges = root.size();

                NodeType st = null;
                Iterator<EdgeDistanceWrapper> eIter = root.getList().iterator();
                HashMap<Integer, LinkedList<EdgeDistanceWrapper>> adjList = new HashMap<>();
                while (eIter.hasNext()) {
                    EdgeDistanceWrapper edw = eIter.next();
                    if (st == null) {
                        st = edw.edge.getU();
                    }
                    int u = edw.edge.getU().getUID();
                    int v = edw.edge.getV().getUID();

                    if (adjList.get(u) == null) {
                        adjList.put(u, new LinkedList<EdgeDistanceWrapper>());
                    }
                    if (adjList.get(v) == null) {
                        adjList.put(v, new LinkedList<EdgeDistanceWrapper>());
                    }
                    adjList.get(u).add(edw);
                    adjList.get(v).add(edw);
                }
//                SwingWorker job = new SwingWorker() {
//                    @Override
//                    protected Object doInBackground() throws Exception {
//                        //Thread.sleep(500);
//                        Rectangle2D bounds = view.getBounds(Visualization.ALL_ITEMS);
//                        GraphicsLib.expand(bounds, 50 + (int) (1 / display.getScale()));
//                        DisplayLib.fitViewToBounds(display, bounds, 2000);
//                        return null;
//                    }
//                };
//                job.execute();
                // Fill tables. 
                // Fill tables. 
                LinkedList<Integer> q = new LinkedList<>();
                q.add(st.getUID());
                synchronized (view) {
                    int uRowNb = nodeTable.addRow();
                    uid2rowid.put(st.getUID(), uRowNb);
                    if (st instanceof NJLeafNode) {
                        nodeTable.setString(uRowNb, "st_id", st.getID());
                        nodeTable.set(uRowNb, "profile", st.p);
                        m_size++;
                    } else {
                        nodeTable.setString(uRowNb, "st_id", st.getUID() + "");
                        nodeTable.setFloat(uRowNb, "distance0", ((NJUnionNode) st).distance1);
                        nodeTable.setFloat(uRowNb, "distance1", ((NJUnionNode) st).distance2);
                        nodeTable.set(uRowNb, "n0", ((NJUnionNode) st).n1);
                        nodeTable.set(uRowNb, "n1", ((NJUnionNode) st).n2);
                    }
                    nodeTable.set(uRowNb, "st_ref", st);
                    nodeTable.setInt(uRowNb, "w", 0);
                    nodeTable.setInt(uRowNb, "g", 1);

                    VisualItem vu = (VisualItem) vg.getNode(uRowNb);
                    vu.setVisible(false);
                }
                int ec = 0;
                while (!q.isEmpty()) {
                    int u = q.pop();
                    int uRowNb = uid2rowid.get(u);
                    VisualItem vu = (VisualItem) vg.getNode(uRowNb);
                    vu.setFixed(true);

                    if (nodesPositions != null) {

                        String id = vu.getString("st_id");
                        double x = nodesPositions.get(id).x;
                        double y = nodesPositions.get(id).y;
                        vu.setStartX(x);
                        vu.setStartY(y);
                        vu.setX(x);
                        vu.setY(y);
                        vu.setEndX(x);
                        vu.setEndY(y);

                    }

                    eIter = adjList.get(u).iterator();
                    while (eIter.hasNext()) {
                        EdgeDistanceWrapper edw = eIter.next();
                        st = edw.edge.getV();
                        if (st.getUID() == u) {
                            st = edw.edge.getU();
                        }
                        // Let us check if this edge and vertex were already added...
                        if (uid2rowid.containsKey(st.getUID())) {
                            continue;
                        }
                        if (edw.distance > maxDistance) {
                            maxDistance = edw.distance;
                        }
                        if (edw.distance < minDistance) {
                            minDistance = edw.distance;
                        }
                        q.add(st.getUID());
                        synchronized (view) {
                            int vRowNb = nodeTable.addRow();
                            uid2rowid.put(st.getUID(), vRowNb);
                            if (st instanceof NJLeafNode) {
                                nodeTable.setString(vRowNb, "st_id", st.getID());
                                nodeTable.set(uRowNb, "profile", st.p);
                                m_size++;
                            } else if (st instanceof NJUnionNode) {
                                nodeTable.setString(vRowNb, "st_id", st.getUID() + "");
                                nodeTable.setFloat(vRowNb, "distance0", ((NJUnionNode) st).distance1);
                                nodeTable.setFloat(vRowNb, "distance1", ((NJUnionNode) st).distance2);
                                nodeTable.set(vRowNb, "n0", ((NJUnionNode) st).n1);
                                nodeTable.set(vRowNb, "n1", ((NJUnionNode) st).n2);
                            }
                            nodeTable.set(vRowNb, "st_ref", st);
                            nodeTable.setInt(vRowNb, "w", 0);
                            nodeTable.setInt(vRowNb, "g", 1);

                            int rowNb = edgeTable.addRow();
                            edgeTable.setInt(rowNb, SRC, uRowNb);
                            edgeTable.setInt(rowNb, TRG, vRowNb);
                            edgeTable.set(rowNb, "edge_ref", edw.edge);
                            edgeTable.set(rowNb, "w", 0);
                            edgeTable.set(rowNb, "g", 1);
                            edgeTable.set(rowNb, "distance", edw.distance);

                            VisualItem vv = (VisualItem) vg.getNode(vRowNb);
                            prefuse.data.Edge ve = vg.getEdge(rowNb);

                            vv.setVisible(false);
                            ((VisualItem) ve).setVisible(false);

                            //view.wait(20);
                        }
                        ec++;
                        int perc = ec * 100 / nedges;
                        setProgress(perc);
                    }
                    vu.setFixed(false);
                    if (loaded) {
                        stopAnimation();
                    }
                }

//                groupList.setListData(new Group[]{g});
                // Search stuff.
                TupleSet search = new PrefixSearchTupleSet();
                search.addTupleSetListener(new TupleSetListener() {
                    @Override
                    public void tupleSetChanged(TupleSet t, Tuple[] add, Tuple[] rem) {
                        view.run("static");
                        searchMatch = true;

//                        if (t.getTupleCount() >= 1) {
//                            String ss = searchPanel.getQuery();
//                            CascadedTable tableView = new CascadedTable(nodeTable, (Predicate) ExpressionParser.parse("st_id=\"" + ss + "\""));
//                            if (tableView.getRowCount() > 0) {
//                                int gid = tableView.getInt(0, "g");
//                                itemFound = gid;
//                            }
//                        }
//                        groupList.repaint();
                    }
                });
                view.addFocusGroup(Visualization.SEARCH_ITEMS, search);
                searchPanel = new NodeSearchPanel(view);

                searchPanel.setShowResultCount(true);
                bottomBox.remove(11);
                bottomBox.add(searchPanel, 11);
                bottomBox.validate();

//                groupList.setSelectedIndex(0);
//                groupList.repaint();
                return null;
            }
        };
        job.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent pce) {
                if (pce.getPropertyName().equals("progress")) {
                    int progress = (Integer) pce.getNewValue();
                    pbar.setValue(progress);
                    if (progress == 100) {

                        //fdl = new RadialLayout("graph", root.distance, 8194);
                        setForceDirectedLayout(false);

                        distance = (float) (distanceFilter == -1 ? maxDistance : distanceFilter);
                        distance = Math.round(distance*1000.0f) / 1000.0f;
                        maxDistance += cutIncDev;

                        sp.setValue(maxDistance);

                        final SpinnerNumberModel model = new SpinnerNumberModel(distance, minDistance, maxDistance, cutIncDev);
                        model.addChangeListener(new ChangeListener() {
                            @Override
                            public void stateChanged(ChangeEvent e) {
                                updateDistanceFilter(model.getNumber().floatValue());
                            }
                        });
                        sp.setModel(model);
                        sp.setValue(distance);

                        if (loaded) {
                            stopAnimation();
                        }

                        //Center view
                        Rectangle2D bounds = view.getBounds(Visualization.ALL_ITEMS);
                        GraphicsLib.expand(bounds, 50 + (int) (1 / display.getScale()));
                        DisplayLib.fitViewToBounds(display, bounds, 2000);

                    }

                }
            }
        });
        job.execute();
    }

    private void updateDistanceFilter(float value) {
        view.setVisible("graph", null, false);
        distance = value;
//        int[] selectedIndices = groupList.getSelectedIndices();
//        for (int i = 0; i < selectedIndices.length; i++) {
//            Group g = (Group) groupList.getModel().getElementAt(selectedIndices[i]);
        view.setVisible("graph", (Predicate) ExpressionParser.parse("distance <= " + distance), true);
//        }
        view.run("draw");
        //view.run("layout");
        updateUI();
        //groupList.repaint();
    }

    public void startAnimation() {
        view.run("draw");
        view.run("layout");
        updateUI();
//        groupList.setSelectedIndex(0);
//        groupList.repaint();
        running = true;
    }

    public void restartAnimation() {
        if (running) {
            return;
        }
        view.run("layout");
        view.repaint();
        running = true;
        updateUI();
    }

    public void stopAnimation() {
        view.cancel("layout");
        view.repaint();
        running = false;
        updateUI();
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

    public InfoPanel getInfoPanel() {
        return infoPanel;
    }

    @Override
    public void closeInfoPanel() {
        if (infoPanel != null) {
            infoPanel.close();
        }
    }

    public void closeEdgeInfoPanel() {
        if (edgeInfoPanel != null) {
            edgeInfoPanel.close();
        }
    }

    public void closePanels() {
        closeInfoPanel();
        closeEdgeInfoPanel();

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
            updateUI();
        }
    }

    @Override
    public void setLevelLabel(boolean status) {
        hasDistanceLabel = status;
        roundDistancesAction.setEnabled(status);
        setEdgeRenderer(hasDistanceLabel);
    }

    public void setEdgePercentageLabel(boolean status) {
        if (status) {
            rf.setDefaultEdgeRenderer(new LabeledEdgeRenderer(this, "edgep", false));
        } else {
            rf.setDefaultEdgeRenderer(new RadialEdgeRenderer());
        }
    }

    public void setRoundDistance(boolean status) {
        roundDistanceValues = status;
        setEdgeRenderer(hasDistanceLabel);
    }

    @Override
    public void setHighQuality(boolean status) {
        display.setHighQuality(status);
        view.run("draw");
        updateUI();
    }

    public void setCategoryProvider(CategoryProvider cp) {
        this.cp = cp;
    }

    public void appendTextToInfoPanel(String text) {
        if (infoPanel != null) {
            infoPanel.append(text);
            infoPanel.flush();
            infoPanel.requestFocus();
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
        return ((ForceDirectedLayout) fdl).getMaxTimeStep();
    }

    public void setSpeed(long step) {
        ((ForceDirectedLayout) fdl).setMaxTimeStep(step);
    }

    public void resetDefaultRenderer() {
        view.cancel("layout");
        view.cancel("static");
        rf.setDefaultRenderer(lr);
        //if (running) {
        view.run("layout");
        view.run("draw");
        view.repaint();
        updateUI();
        //} else {
        //    view.run("static");
        //}
    }

    public void setDefaultRenderer(AbstractShapeRenderer r) {
        view.cancel("layout");
        view.cancel("static");
        rf.setDefaultRenderer(r);
        // if (running) {
        view.run("layout");
        view.run("draw");
        view.repaint();
        updateUI();
        //} else {
        //    view.run("static");
        //}
    }

    public ForceDirectedLayout getForceLayout() {
        return ((ForceDirectedLayout) fdl);
    }

    public JForcePanel getForcePanel() {
        return new JForcePanel(((ForceDirectedLayout) fdl).getForceSimulator());
    }

    public ArrayList<ForcePair> getForces() {
        ForceSimulator fs = ((ForceDirectedLayout) fdl).getForceSimulator();
        Force[] farray = fs.getForces();
        ArrayList<ForcePair> array = new ArrayList<>();
        for (Force farray1 : farray) {
            int ni = farray1.getParameterCount();
            for (int j = 0; j < ni; j++) {
                array.add(new ForcePair(farray1.getParameterName(j), farray1.getParameter(j)));
            }
        }
        return array;
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
    public void showGroupPanel(boolean status) {

    }

    @Override
    public float getDistanceFilterValue() {
        return distance;
    }

    @Override
    public void setDistanceFilterValue(float value) {
        updateDistanceFilter(value);
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

    @Override
    public void setRescaleEdges(boolean status) {

        rescaleDistance = status;

        if (showChart) {
            setDistanceChart(false);
        }
        setForceDirectedLayout(true);
    }

    @Override
    public boolean getRescaleEdges() {
        return rescaleDistance;
    }

    public void updateEdgeColor() {
        view.run("draw");
        view.repaint();
        updateUI();
    }

    private void openEdgeInfoChart() {
        stopAnimation();
        view.run("draw");
        view.repaint();
        updateUI();
        edge.updatePositions();
        edgeInfoPanel = new ChartsPanel(1);
        ChartLegendPanel chartInfoPos = new ChartLegendPanel(positiveEdges, new Dimension(128, 128));
        edgeInfoPanel.add(chartInfoPos);
        if (negativeEdges.minD != Double.POSITIVE_INFINITY) {
            edgeInfoPanel = new ChartsPanel(2);
            ChartLegendPanel chartInfoNeg = new ChartLegendPanel(negativeEdges, new Dimension(128, 128));
            edgeInfoPanel.add(chartInfoPos);
            edgeInfoPanel.add(chartInfoNeg);
        }
        edgeInfoPanel.setName(name + " (Selection view)");
        edgeInfoPanel.open();
        edgeInfoPanel.requestActive();
    }

    public void setDistanceChart(boolean status) {
        showChart = status;
        if (status) {
            openEdgeInfoChart();
        } else {
            showDistancesChartAction.setSelected(showChart);
            restartAnimation();
            closeEdgeInfoPanel();
        }
    }

    public void setForceDistanceLayout(boolean status) {
        forceDistanceLayout = status;
        setForceDirectedLayout(true);
    }

    public void setForceDirectedLayout(boolean status) {
        if (status) {
            ForceDirectedLayout new_fdl;
            if (isRadial) {
                isRadial = !status;
                new_fdl = new ForceDirectedLayout("graph");
            } else {
                new_fdl = new ForceDirectedLayout("graph") {
                    @Override
                    protected float getSpringLength(EdgeItem e) {
                        if (forceDistanceLayout) {
                            double dist;
                            if (e.getFloat("distance") < 0.01) {
                                dist = 10 + 300 * 0.01f;
                            } else {
                                dist = 10 + 300 * e.getFloat("distance");
                            }
                            dist = rescaleDistance ? Math.log(dist) : dist;
                            return (float) dist;
                        } else {
                            double distance = e.getDouble("distance");
                            double dist = rescaleDistance ? Math.log(1 + distance) : distance;
                            return (float) dist;
                        }
                    }
                };
            }
            ActionList newLayout = new ActionList(ActionList.INFINITY);
            newLayout.add(new_fdl);
            newLayout.add(filter);
            view.removeAction("layout");
            view.putAction("layout", newLayout);

            fdl = new_fdl;
            fdl.setVisualization(view);
            if (cp != null) {
                setDefaultRenderer(new ChartRenderer(cp, this));
            } else {
                lr = new NodeLabelRenderer("st_id");
                ((NodeLabelRenderer) lr).setRoundedCorner(10, 10);
                setDefaultRenderer(lr);
            }
            setEdgeRenderer(hasDistanceLabel);//rf.setDefaultEdgeRenderer(new EdgeRenderer());
            enableForceDirectedActions(true);
            view.run("layout");
            view.run("draw");
            view.repaint();
            updateUI();
        } else {
            isRadial = true;
            RadialLayout new_fdl = new RadialLayout(this, "graph", root.distance, m_size);
            ActionList newLayout = new ActionList();
            newLayout.add(new_fdl);
            newLayout.add(filter);
            view.removeAction("layout");
            view.putAction("layout", newLayout);

            fdl = new_fdl;
            fdl.setVisualization(view);
            if (cp != null) {
                setDefaultRenderer(new BarChartRenderer(cp, this));
            } else {
                lr = new RadialNodeLabelRenderer(this, "st_id");
                setDefaultRenderer(lr);
                rf.setDefaultRenderer(lr);
            }
            setEdgeRenderer(hasDistanceLabel); //rf.setDefaultEdgeRenderer(new RadialEdgeRenderer());
            enableForceDirectedActions(false);
            view.run("layout");
            view.run("draw");
            view.repaint();
            updateUI();
        }

    }

    public void updateLayout() {
        view.run("layout");
        view.run("draw");
        view.repaint();
        updateUI();
    }

    public boolean isRadial() {
        return isRadial;
    }

    public JRadialViewControlPanel getRadialViewControlPanel() {
        radialViewControlPanel = new JRadialViewControlPanel(this);
        return radialViewControlPanel;
    }

//    public void setHeightViewControlValue(int value) {
//        if (cp == null) {
//            return;
//        }
//
//        BarChartRenderer r = (BarChartRenderer) rf.getDefaultRenderer();
//        r.setHeight(value);
//
//        view.run("layout");
//        view.run("draw");
//        view.repaint();
//        updateUI();
//    }
//
//    public void setWidthViewControlValue(int value) {
//        if (cp == null) {
//            return;
//        }
//
//        BarChartRenderer r = (BarChartRenderer) rf.getDefaultRenderer();
//        r.setWidth(value);
//
//        view.run("layout");
//        view.run("draw");
//        view.repaint();
//        updateUI();
//    }
    public int getRadialControlViewInfo(String prop) {
        return radialViewControlPanel.getProp(prop);
    }
//    public int getFontSize() {
//        return radialViewControlPanel.getProp(JRadialViewControlPanel.FONT);
//    }

//    public void setFontViewControlValue(int value) {
//        fontSize = value;
////        if (cp != null) {
////            BarChartRenderer r = (BarChartRenderer) rf.getDefaultRenderer();
////            r.setFontSize(value);
////        } else {
////            RadialNodeLabelRenderer r = (RadialNodeLabelRenderer) rf.getDefaultRenderer();
////            r.setFontSize(value);
////        }
//
//        view.run("layout");
//        view.run("draw");
//        view.repaint();
//        updateUI();
//    }
//    public void setZeroDistanceViewControlValue(int value){
//        
//    }
    public void enableViewControl(boolean status) {
        radialViewControlMenuItem.setEnabled(status);
        forceViewControlAction.setEnabled(!status);
    }

    private void enableForceDirectedActions(boolean status) {
        animCtl.setEnabled(status);
        playButton.setEnabled(status);
        pauseButton.setEnabled(status);
        rescaleEdgesAction.setEnabled(status);
        forceDistanceLayoutAction.setEnabled(status);
        showDistancesChartAction.setEnabled(status);
        forceViewControlAction.setEnabled(status);
        radialViewControlMenuItem.setEnabled(!status);
    }

    // Private classes.
    private class NodeColorAction extends ColorAction {

        public NodeColorAction(String group) {
            super(group, VisualItem.FILLCOLOR);
        }

        @Override
        public int getColor(VisualItem item) {
            Tuple itemTuple = item.getSourceTuple();
            if (itemTuple.get("st_ref") instanceof NJLeafNode) {
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
                } else if (((Node) item).getDegree() < 3) {
                    return ColorLib.rgb(200, 200, 255);
                } else {
                    return ColorLib.rgb(200, 200, 55);
                }
            }
            return ColorLib.color(Color.WHITE);
        }
    }

    private class EdgeColorAction extends ColorAction {

        public EdgeColorAction(String group) {
            super(group, VisualItem.STROKECOLOR);

            positiveEdges = new EdgeStats(GraphView.this, 10, 'g');
            negativeEdges = new EdgeStats(GraphView.this, 2, 'r');
        }

        public void updatePositions() {

            Iterator<EdgeItem> nodes = vg.edges();
//            while (nodes.hasNext()) {
//                EdgeItem i = nodes.next();
//                double vizDist = (PrefuseLib.distance(i.getSourceItem(), i.getTargetItem()) - 10) / 300;
//                double realDist = i.getDouble("distance");
//
//                if (realDist > 0 && realDist < 0.01) {
//                    positiveEdges.updateScale(vizDist);
//                }
//            }
//            nodes = vg.edges();
            while (nodes.hasNext()) {
                EdgeItem i = nodes.next();
                double vizDist = (PrefuseLib.distance(i.getSourceItem(), i.getTargetItem()) - 10) / 300;
                double realDist = i.getDouble("distance");

                if (realDist < 0) {
                    negativeEdges.update(realDist, vizDist);
                } else if (realDist >= 0.01) {
                    positiveEdges.update(realDist, vizDist);
                }
            }
        }

        @Override
        public int getColor(VisualItem item) {

            EdgeItem edge = (EdgeItem) item;
            float distance = item.getSourceTuple().getFloat("distance");
            if (showChart) {
                double viDistance = (PrefuseLib.distance(edge.getSourceItem(), edge.getTargetItem()) - 10) / 300;
                if (distance < 0) {
                    double diff = negativeEdges.diff(distance, viDistance);
                    return ColorLib.color(negativeEdges.getColor(diff));
                } else {
                    if (distance < 0.01) {
                        return ColorLib.rgb(0, 0, 255);
                    }
                    double diff = positiveEdges.diff(distance, viDistance);
                    return ColorLib.color(positiveEdges.getColor(diff));
                }
            } else {
                if (distance < 0) {
                    return ColorLib.color(Color.RED);
                }
                return ColorLib.color(Color.BLACK);
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

    private class ItemInfoControl extends ControlAdapter {

        @Override
        public void itemClicked(VisualItem item, MouseEvent e) {
            showInfoPanel();
            if (item instanceof EdgeItem) {
                Edge edge = (Edge) ((EdgeItem) item).getSourceTuple().get("edge_ref");
                appendTextToInfoPanel(
                        "From " + edge.getU().getID() + " --> " + edge.getV().getID() + " (distance=" + item.getSourceTuple().getString("distance") + ")\n\n");
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

    private void setEdgeRenderer(boolean label) {
        if (isRadial) {
            if (label) {
                rf.setDefaultEdgeRenderer(new LabeledRadialEdgeRenderer(this, "distance", roundDistanceValues));
            } else {
                rf.setDefaultEdgeRenderer(new RadialEdgeRenderer());
            }
        } else if (label) {
            rf.setDefaultEdgeRenderer(new LabeledEdgeRenderer(this, "distance", roundDistanceValues));
        } else {
            rf.setDefaultEdgeRenderer(new EdgeRenderer());
        }
        view.run("draw");
        updateUI();
    }

//    private class Group {
//
//        private int id;
//        private final LinkedList<Integer> list;
//
//        Group() {
//            this.id = 0;
//            list = new LinkedList<>();
//        }
//
//        public void setID(int id) {
//            this.id = id;
//        }
//
//        public int getID() {
//            return id;
//        }
//
//        public List<Integer> getItems() {
//            return list;
//        }
//
//        public void add(int u) {
//            list.add(u);
//        }
//
//        public int size() {
//            return list.size();
//        }
//
//        @Override
//        public String toString() {
//            return Integer.toString(id);
//        }
//    }
//    private class GroupCellRenderer extends JLabel implements ListCellRenderer {
//
//        private static final long serialVersionUID = 1L;
//
//        @Override
//        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
//            String s = value.toString();
//            setText(s);
//            if (isSelected) {
//                setBackground(list.getSelectionBackground());
//                setForeground(list.getSelectionForeground());
//            } else {
//                setBackground(list.getBackground());
//                setForeground(list.getForeground());
//            }
//            setEnabled(list.isEnabled());
//            setFont(list.getFont());
//
//            Group g = (Group) groupList.getModel().getElementAt(index);
//            if (g.size() == 1) {
//                setFont(list.getFont().deriveFont(Font.ITALIC));
//                setForeground(Color.GRAY);
//            }
//            if (itemFound == g.getID()) {
//                setFont(list.getFont().deriveFont(Font.BOLD));
//                setForeground(Color.RED);
//            }
//            setOpaque(true);
//            return this;
//        }
//    }
}
