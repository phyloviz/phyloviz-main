package net.phyloviz.njviewer.ui;

import net.phyloviz.njviewer.Force.NJForceDirectLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.phyloviz.algo.AbstractDistance;
import net.phyloviz.algo.Edge;
import net.phyloviz.category.CategoryProvider;
import net.phyloviz.category.filter.Category;
import net.phyloviz.core.data.Profile;
import net.phyloviz.nj.tree.NJLeafNode;
import net.phyloviz.nj.tree.NJRoot;
import net.phyloviz.nj.tree.NeighborJoiningItem;
import net.phyloviz.nj.tree.NJRoot.EdgeDistanceWrapper;
import net.phyloviz.nj.tree.NodeType;
import net.phyloviz.njviewer.action.HighQualityAction;
import net.phyloviz.njviewer.action.RoundDistanceAction;
import net.phyloviz.njviewer.action.ViewControlAction;
import net.phyloviz.njviewer.render.LabeledEdgeRenderer;

import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
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
import net.phyloviz.upgmanjcore.visualization.GView;
import net.phyloviz.upgmanjcore.visualization.InfoPanel;
import net.phyloviz.upgmanjcore.visualization.actions.EdgeLevelLabelAction;
import net.phyloviz.upgmanjcore.visualization.actions.ExportAction;
import net.phyloviz.upgmanjcore.visualization.actions.InfoControlAction;
import net.phyloviz.upgmanjcore.visualization.actions.LinearSizeControlAction;
import prefuse.action.assignment.FontAction;

public class GraphView extends GView {

    private static final long serialVersionUID = 1L;
    private static final String SRC = Graph.DEFAULT_SOURCE_KEY, TRG = Graph.DEFAULT_TARGET_KEY;

    private float distance;
    private final JSpinner sp;
    private final Box bottomBox;
    private final Visualization view;
    private final JProgressBar pbar;

    private boolean hasDistanceLabel = false, isRound = false;
    private String name;
    private Table nodeTable, edgeTable;
    private Graph graph;
    private VisualGraph vg;
    private Display display;
    private JSearchPanel searchPanel;
    private boolean searchMatch = false;
    private JList groupList;
    private int itemFound = -1;
    private InfoPanel infoPanel;
    private JPopupMenu popupMenu;
    private LabelRenderer lr;
    private DefaultRendererFactory rf;
    private NJForceDirectLayout fdl;
    private boolean running, linear, label = true;
    // Data analysis info...
    private CategoryProvider cp;
    double maxDistance = 0, minDistance = 0;

    public GraphView(String name, NeighborJoiningItem _er, boolean linear) {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        this.setOpaque(true);
        this.name = name;
//        this.linear = linear;
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
                    return FontLib.getFont("Tahoma", Font.PLAIN, 11 + (GraphView.this.linear ? 11 * st.getFreq() : (7 * Math.log(1 + st.getFreq()))));
            }
        };

        ActionList draw = new ActionList();
        draw.add(fill);
        draw.add(text);
        draw.add(nfont);
        draw.add(edge);

        fdl = new NJForceDirectLayout("graph");

        ActionList layout = new ActionList(Activity.INFINITY);
        layout.add(fdl);
        layout.add(fill);
        layout.add(text);
        layout.add(edge);
        layout.add(new RepaintAction());

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
        nodeSchema.addColumn("st_ref", NodeType.class);
        nodeSchema.addColumn("w", int.class);
        nodeSchema.addColumn("g", int.class);
        nodeSchema.addColumn("distance", float.class);

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
        groupList = new JList();
        groupList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        groupList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                int[] selectedIndices = groupList.getSelectedIndices();
                view.setVisible("graph", null, false);
                for (int i = 0; i < selectedIndices.length; i++) {
                    Group g = (Group) groupList.getModel().getElementAt(selectedIndices[i]);
                    view.setVisible("graph",
                            (Predicate) ExpressionParser.parse("g=" + g.getID() + "and w <= " + distance), true);
                }
                view.run("draw");

                appendTextToInfoPanel("\n");
                SwingWorker job = new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        //Thread.sleep(100);
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
        final JValueSlider animCtl = new JValueSlider("Anim. speed>>", 1, 100, 50);
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
        popupMenu.add(new InfoControlAction(this).getMenuItem());
        popupMenu.add(new EdgeLevelLabelAction(this).getMenuItem());
        popupMenu.add(new RoundDistanceAction(this).getMenuItem());
        popupMenu.add(new LinearSizeControlAction(this, linear).getMenuItem());
        popupMenu.add(new HighQualityAction(this).getMenuItem());
        popupMenu.add(new ViewControlAction(this).getMenuItem());

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

        Box verticalPanel = new Box(BoxLayout.Y_AXIS);

        verticalPanel.add(Box.createVerticalGlue());
        verticalPanel.add(sp);

        //verticalPanel.add(sp, BorderLayout.PAGE_END);
        verticalPanel.setBackground(Color.WHITE);
        // Bottom box.
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

    public void loadGraph(final NJRoot root, final AbstractDistance ad, final double distanceFilter, final boolean loaded) {
        final HashMap<Integer, Integer> uid2rowid = new HashMap<>();

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
                Group g = new Group();
                g.setID(1);

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
                SwingWorker job = new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        //Thread.sleep(500);
                        Rectangle2D bounds = view.getBounds(Visualization.ALL_ITEMS);
                        GraphicsLib.expand(bounds, 50 + (int) (1 / display.getScale()));
                        DisplayLib.fitViewToBounds(display, bounds, 2000);
                        return null;
                    }
                };
                job.execute();
                // Fill tables. 
                // Fill tables. 
                LinkedList<Integer> q = new LinkedList<>();
                q.add(st.getUID());
                synchronized (view) {
                    int uRowNb = nodeTable.addRow();
                    uid2rowid.put(st.getUID(), uRowNb);
                    if (st instanceof NJLeafNode) {
                        nodeTable.setString(uRowNb, "st_id", st.getID());
                    }
                    nodeTable.set(uRowNb, "st_ref", st);
                    nodeTable.setInt(uRowNb, "w", 0);
                    nodeTable.setInt(uRowNb, "g", 1);

                    VisualItem vu = (VisualItem) vg.getNode(uRowNb);
                    vu.setVisible(true);
                }
                int ec = 0;
                while (!q.isEmpty()) {
                    int u = q.pop();
                    int uRowNb = uid2rowid.get(u);
                    VisualItem vu = (VisualItem) vg.getNode(uRowNb);
                    vu.setFixed(true);

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
                            vv.setStartX(st.x);
                            vv.setStartY(st.y);
                            vv.setX(st.x);
                            vv.setY(st.y);
                            vv.setEndX(st.x);
                            vv.setEndY(st.y);

                            vv.setVisible(true);
                            ((VisualItem) ve).setVisible(true);

                            view.wait(20);
                        }
                        ec++;
                        int perc = ec * 100 / nedges;
                        setProgress(perc);
                    }
                    vu.setFixed(false);
                }

                groupList.setListData(new Group[]{g});
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
                bottomBox.remove(11);
                bottomBox.add(searchPanel, 11);
                bottomBox.validate();

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
                    if (progress == 100) {
                        distance = (float) (distanceFilter == -1 ? maxDistance : distanceFilter);

                        sp.setValue(maxDistance);

                        final SpinnerNumberModel model = new SpinnerNumberModel(distance, minDistance, maxDistance, 0.001);
                        model.addChangeListener(new ChangeListener() {
                            @Override
                            public void stateChanged(ChangeEvent e) {
                                updateDistanceFilter(model.getNumber().floatValue());
                            }
                        });
                        sp.setModel(model);
                        sp.setValue(distance);
                        
                        if(loaded)
                            stopAnimation();
                    }

                }
            }
        });
        job.execute();
    }

    private void updateDistanceFilter(float value) {
        view.setVisible("graph", null, false);
        distance = value;
        int[] selectedIndices = groupList.getSelectedIndices();
        for (int i = 0; i < selectedIndices.length; i++) {
            Group g = (Group) groupList.getModel().getElementAt(selectedIndices[i]);
            view.setVisible("graph", (Predicate) ExpressionParser.parse("g=" + g.getID() + "and distance <= " + distance), true);
        }
        view.run("draw");
    }

    public void startAnimation() {
        view.run("draw");
        view.run("layout");
        updateUI();
        groupList.setSelectedIndex(0);
        groupList.repaint();
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
        hasDistanceLabel = status;
        setRenderer();
    }

    public void setEdgePercentageLabel(boolean status) {
        if (status) {
            rf.setDefaultEdgeRenderer(new LabeledEdgeRenderer("edgep"));
        } else {
            rf.setDefaultEdgeRenderer(new EdgeRenderer());
        }
    }

    public void setRoundDistance(boolean status) {
        isRound = status;
        setRenderer();
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

    public ForceDirectedLayout getForceLayout() {
        return fdl;
    }

    public JForcePanel getForcePanel() {
        return new JForcePanel(fdl.getForceSimulator());
    }

    public ArrayList<ForcePair> getForces() {
        ForceSimulator fs = fdl.getForceSimulator();
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

    // Private classes.
    private class NodeColorAction extends ColorAction {

        public NodeColorAction(String group) {
            super(group, VisualItem.FILLCOLOR);
        }

        @Override
        public int getColor(VisualItem item) {
            Tuple itemTuple = item.getSourceTuple();
            refreshNodePosition(item);
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

        private void refreshNodePosition(VisualItem item) {
            Tuple itemTuple = item.getSourceTuple();
            NodeType nt = (NodeType) itemTuple.get("st_ref");
            nt.x = item.getX();
            nt.y = item.getY();
        }
    }

    private class EdgeColorAction extends ColorAction {

        public EdgeColorAction(String group) {
            super(group, VisualItem.STROKECOLOR);
        }

        @Override
        public int getColor(VisualItem item) {
            float c = item.getSourceTuple().getFloat("distance");
            return c >= 0f ? ColorLib.rgb(0, 0, 0) : ColorLib.rgb(255, 0, 0);
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

    private void setRenderer() {
        if (isRound && hasDistanceLabel) {
            rf.setDefaultEdgeRenderer(new LabeledEdgeRenderer("distance", true));
        } else if (hasDistanceLabel) {
            rf.setDefaultEdgeRenderer(new LabeledEdgeRenderer("distance"));
        } else {
            rf.setDefaultEdgeRenderer(new EdgeRenderer());
        }
    }

    private class Group {

        private int id;
        private final LinkedList<Integer> list;

        Group() {
            this.id = 0;
            list = new LinkedList<>();
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

        @Override
        public String toString() {
            return Integer.toString(id);
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
