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
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.phyloviz.goeburst.cluster.Edge;
import net.phyloviz.goeburst.cluster.GOeBurstClusterWithStats;
import net.phyloviz.core.data.AbstractProfile;
import net.phyloviz.goeburst.GOeBurstResult;
import net.phyloviz.goeburst.algorithm.HammingDistance;
import net.phyloviz.gtview.action.EdgeViewControlAction;
import net.phyloviz.gtview.action.ExportAction;
import net.phyloviz.gtview.action.GroupControlAction;
import net.phyloviz.gtview.action.InfoControlAction;
import net.phyloviz.gtview.action.LinearSizeControlAction;
import net.phyloviz.gtview.action.ViewControlAction;
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
import prefuse.visual.VisualItem;

public class GraphView extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final String SRC = Graph.DEFAULT_SOURCE_KEY;
	private static final String TRG = Graph.DEFAULT_TARGET_KEY;

	private Table nodeTable;
	private Table edgeTable;

	private TreeMap<AbstractProfile, Integer> st2rowid;
	private TreeMap<Edge, Integer> edge2rowid;
	Graph graph;
	
	private TreeMap<Integer, Color> colors;

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
	
	private JScrollPane infoPanel;
	private boolean infoPanelStatus;
	private JTextArea textArea;
	private JPopupMenu popupMenu;

	private LabelRenderer lr;
	private DefaultRendererFactory rf;
	private ForceDirectedLayout fdl;

	private boolean running;
	private boolean linear;

	// Data analysis info...
	//private TreeMap<String, List<Group>> stMap;

	public GraphView(GOeBurstResult er) {
		super(new BorderLayout());
		this.setBackground(Color.WHITE);
		this.setOpaque(true);

		colors = new TreeMap<Integer, Color>();

		// Create an empty visualization.
		view = new Visualization();

		// Setup renderers.
		rf = new DefaultRendererFactory();
		lr = new LabelRenderer("st_id");
		lr.setRoundedCorner(10,10);
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
				    AbstractProfile st = (AbstractProfile) item.getSourceTuple().get("st_ref");
				    return FontLib.getFont("Tahoma", Font.PLAIN, 11 + (linear ? st.getFreq() : (5 * Math.log(st.getFreq() + 1))));
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
		//display.setHighQuality(true);
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
		nodeSchema.addColumn("st_ref", AbstractProfile.class);

		Schema edgeSchema = new Schema();
		edgeSchema.addColumn(SRC, int.class);
		edgeSchema.addColumn("viz", int.class);
		edgeSchema.addColumn("group", int.class);
		edgeSchema.addColumn(TRG, int.class);
		edgeSchema.addColumn("edge_ref", Edge.class);

		// Create and fill tables.
		nodeSchema.lockSchema();
		edgeSchema.lockSchema();
		nodeTable = nodeSchema.instantiate();
		edgeTable = edgeSchema.instantiate();

		TreeMap<String,Integer> nodeMap = new TreeMap<String,Integer>();
		st2rowid = new TreeMap<AbstractProfile,Integer>();
		edge2rowid = new TreeMap<Edge,Integer>();

		Iterator<GOeBurstClusterWithStats> gIter = groups.iterator();
		while (gIter.hasNext()) {
			GOeBurstClusterWithStats g = gIter.next();
			
			Iterator<AbstractProfile> stIter = g.getSTs().iterator();
			while (stIter.hasNext()) {
				AbstractProfile st = stIter.next();
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

			Iterator<Edge> edgeIter = g.getEdges().iterator();
			while (edgeIter.hasNext()) {
				Edge e = edgeIter.next();
		
				if (! e.visible())
					continue;
				
				int rowNb = edgeTable.addRow();
				edge2rowid.put(e, rowNb);
				edgeTable.setInt(rowNb, SRC, nodeMap.get(e.getU().getID()));
				
				if (e.visible()) {
					nodeTable.setInt(nodeMap.get(e.getU().getID()), "dg",
							nodeTable.getInt(nodeMap.get(e.getU().getID()), "dg") + 1);
					nodeTable.setInt(nodeMap.get(e.getV().getID()), "dg",
							nodeTable.getInt(nodeMap.get(e.getV().getID()), "dg") + 1);
					edgeTable.setInt(rowNb, "viz", HammingDistance.compute(e.getU(), e.getV()));
				}

				edgeTable.setInt(rowNb, "group", g.getID());
				edgeTable.setInt(rowNb, TRG, nodeMap.get(e.getV().getID()));
				edgeTable.set(rowNb, "edge_ref", e);
			}
		}

		// Create the graph.
		graph = new Graph(nodeTable, edgeTable, false);
		view.add("graph", graph);
		view.setVisible("graph", null, false);

		running = true;
		linear = false;
		
		/* Setup groupPanel. */
		groupList = new JList();
		groupList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		groupList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return;
				
				view.setVisible("graph", null, false);
				int[] selectedIndices = groupList.getSelectedIndices();
				ArrayList<GOeBurstClusterWithStats> gList = new ArrayList<GOeBurstClusterWithStats>(selectedIndices.length);
				for (int i = 0; i < selectedIndices.length; i++) {
					view.setVisible("graph",
					    (Predicate) ExpressionParser.parse("group=" + selectedIndices[i] + "and " + viz), true);

					gList.add((GOeBurstClusterWithStats) groupList.getModel().getElementAt(selectedIndices[i]));
				}

				view.run("draw");
				textArea.append(GOeBurstClusterWithStats.combinedInfo(gList) + "\n");
				textArea.setCaretPosition(textArea.getDocument().getLength());
				
				double x = display.getDisplayX();
				double y = display.getDisplayY();
				double dx = display.getWidth();
				double dy = display.getHeight();
				display.pan(x+dx/2,y+dy/2);
			}
		});
		groupList.setCellRenderer(new GroupCellRenderer());
		groupList.setListData(new Vector<GOeBurstClusterWithStats>(groups));
		
		groupPanel = new JScrollPane(groupList,
		    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		groupPanel.getViewport().setBackground(Color.WHITE);
		groupPanel.setBackground(Color.WHITE);
		tb = new TitledBorder("Groups at " +
				(level == 1 ? "S" : level == 2 ? "D" : "T") + "LV:");
		tb.setBorder(new LineBorder(Color.BLACK));
		groupPanel.setBorder(tb);
		groupPanel.setPreferredSize(new Dimension(90,600));
		add(groupPanel, BorderLayout.WEST);
		groupPanelStatus = true;
		
		// Info panel.
		textArea = new JTextArea();
		textArea.setEditable(false);
		//textArea.setText("Click an edge!");
		
		infoPanel = new JScrollPane(textArea,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		infoPanel.getViewport().setBackground(Color.WHITE);
		infoPanel.setBackground(Color.WHITE);
		TitledBorder itb = new TitledBorder("Info:");
		itb.setBorder(new LineBorder(Color.BLACK));
		infoPanel.setBorder(itb);
		infoPanel.setPreferredSize(new Dimension(210,600));
		//add(infoPanel, BorderLayout.EAST);
		infoPanelStatus = false;
		
		// Search stuff.
		TupleSet search = new PrefixSearchTupleSet();
		search.addTupleSetListener(new TupleSetListener() {
			@Override
			public void tupleSetChanged(TupleSet t, Tuple[] add, Tuple[] rem) {
				view.run("static");
				searchMatch = true;

				itemFound = -1;
				if (t.getTupleCount() >= 1) {
					String ss=searchPanel.getQuery();
					CascadedTable tableView = new CascadedTable(nodeTable,(Predicate) ExpressionParser.parse("st_id=\"" + ss + "\""));
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
		playButton.setMargin( new Insets(1, 1, 1, 1));
		playButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				restartAnimation();
			}
		});

		JButton pauseButton = new JButton("||");
		pauseButton.setMargin( new Insets(1, 1, 1, 1));
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
		popupMenu.add(new LinearSizeControlAction(this).getMenuItem());
		popupMenu.add(new ViewControlAction(this).getMenuItem());
		popupMenu.add(new ExportAction(this).getMenuItem());

		JButton optionsButton = new JButton("Options");
		optionsButton.setMargin( new Insets(1, 1, 1, 1));
		optionsButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
                		popupMenu.show(e.getComponent(), e.getX(), e.getY());
            		}
		});

		// Bottom box.
		Box box = new Box(BoxLayout.X_AXIS);
		box.add(Box.createHorizontalStrut(3));
		box.add(optionsButton);
		box.add(Box.createHorizontalStrut(8));
		box.add(playButton);
		box.add(Box.createHorizontalStrut(1));
		box.add(pauseButton);
		box.add(Box.createHorizontalStrut(5));
		box.add(animCtl);
		box.add(Box.createHorizontalGlue());
		box.add(searchPanel);
		box.add(Box.createHorizontalStrut(3));
		box.setOpaque(true);
		box.setBackground(Color.WHITE);
		box.updateUI();
		add(box, BorderLayout.SOUTH);
	}
	
	public void startAnimation() {
		view.run("draw");
		view.run("layout");
		updateUI();
		groupList.setSelectedIndex(0);
		groupList.repaint();
	}
	
	public JScrollPane getInfoPanel(){
		return infoPanel;
	}
	
	public JScrollPane getGroupPanel(){
		return groupPanel;
	}
	
	public void restartAnimation() {

		if (running)
			return;

		//view.cancel("static");
		view.run("layout");

		view.repaint();

		running = true;

		updateUI();
	}

	public void stopAnimation() {

		if (! running)
			return;

		view.cancel("layout");
		//view.run("static");

		view.repaint();

		running = false;

		updateUI();
	}

	public void showGroupPanel(boolean status) {
		if (status == groupPanelStatus)
			return;
		
		if (status) 
			add(groupPanel, BorderLayout.WEST);
		else
			remove(groupPanel);
		groupPanelStatus = status;
	}
	
	public void showInfoPanel(boolean status) {
		if (status == infoPanelStatus)
			return;
		
		if (status) 
			add(infoPanel, BorderLayout.EAST);
		else
			remove(infoPanel);
		infoPanelStatus = status;
	}

	public boolean getLinearSize() {
		return linear;
	}

	public void setLinearSize(boolean status) {
		if (linear != status) {
			linear = status;
			view.run("draw");
		}
	}

	public void setAllEdges(boolean status) {
		if (status)
			viz = "viz > 0";
		else
			viz = "viz = 1";
		
		view.setVisible("graph", null, false);
		int[] selectedIndices = groupList.getSelectedIndices();
		for (int i = 0; i < selectedIndices.length; i++) {
			view.setVisible("graph",
			    (Predicate) ExpressionParser.parse("group=" + selectedIndices[i] + "and " + viz), true);
		}
		view.run("draw");
	}
	
	//public void setStMap(TreeMap<String, List<Group>> map) {
	//	stMap = map;
	//}
	
	public void appendTextToInfoPanel(String text) {
		textArea.append(text);
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}

	public JComponent getDisplay() {
		return display;
	}

	public Visualization getVisualization() {
		return view;
	}
	
	public long getSpeed() {
		return fdl.getMaxTimeStep();
	}
	
	public void setSpeed(long step) {
		fdl.setMaxTimeStep(step);
	}
	
	public void addSelection(File file, Color color) {
		try {
			Scanner in = new Scanner(file);
			while (in.hasNextInt())
				colors.put(in.nextInt(), color);
		} catch (Exception e) {
			System.out.println("Error:" + e.getStackTrace());
		}
	}

	public void resetSelection() {
		colors.clear();
	}
	
	public void resetDefaultRenderer() {
		view.cancel("layout");
		view.cancel("static");
		rf.setDefaultRenderer(lr);
		if (running)
			view.run("layout");
		else
			view.run("static");
	}

	public void setDefaultRenderer(AbstractShapeRenderer r) {
		view.cancel("layout");
		view.cancel("static");
		rf.setDefaultRenderer(r);
		if (running)
			view.run("layout");
		else
			view.run("static");
	}

        public ForceDirectedLayout getForceLayout(){
            return fdl;
        }

	// We make sure that we return always the same panel.
	public JForcePanel getForcePanel() {
		return new JForcePanel(fdl.getForceSimulator());
	}

        public static class Pair{
            public float value;
            public String name;
            public Pair(String name,float i){
                value=i;
                this.name=name;
            }

		@Override
            public String toString(){
                return name+ ":"+value;
            }

            public static Pair valueOf(String s){
                int k=s.indexOf(':');
                String i=s.substring(0,k);
                float j=Float.parseFloat(s.substring(k+1));
                Pair p=new Pair(i,j);
                return p;
            }
        }

        public ArrayList<Pair> getForces() {
             ForceSimulator fs =fdl.getForceSimulator();
              Force[] farray=fs.getForces();
              ArrayList<Pair> array=new ArrayList<Pair>();
              for(int i=0;i<farray.length;i++){

                 int ni= farray[i].getParameterCount();
                  for(int j=0;j<ni;j++){
                      array.add(new Pair(farray[i].getParameterName(j),farray[i].getParameter(j)));
                  }
              }
              return array;
		//return fdl.getForceSimulator();
	}

       
              
      
	// Private classes.
	private class NodeColorAction extends ColorAction{

		public NodeColorAction(String group) {
        		super(group, VisualItem.FILLCOLOR);
                      
        	}
        
		@Override
		public int getColor(VisualItem item) {
			Tuple itemTuple = item.getSourceTuple();
			

			if ( m_vis.isInGroup(item, Visualization.SEARCH_ITEMS) ) {

				if (itemTuple.getString("st_id").equals(searchPanel.getQuery())) {
					if (searchMatch)
						display.panToAbs(new Point2D.Double(item.getX(), item.getY()));
					searchMatch = false;
					return ColorLib.rgb(255,0,0);
				}

				return ColorLib.rgb(200,100,100);

			} else if (item.isFixed())
				return ColorLib.rgb(255,100,100);
			else if (item.isHighlighted())
				return ColorLib.rgb(255,200,125);
			else if (itemTuple.getInt("hdlv") == 1)
				return ColorLib.rgb(255,255,125);
			else {
				Color color = colors.get(itemTuple.getString("st_id"));
				if (color != null)
					return color.getRGB();
				
				if (itemTuple.getInt("founder") == 1)
					return ColorLib.rgb(200, 255, 55);
				if (itemTuple.getInt("founder") == 2)
					return ColorLib.rgb(55, 255, 200);
				
				if (item.getSourceTuple().getInt("dg") < 3)
					return ColorLib.rgb(200,200,255);
				else
					return ColorLib.rgb(200,200,55);
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

			if (item.getSourceTuple().getInt("viz") > 1)
				return ColorLib.gray(170 + (item.getSourceTuple().getInt("viz") - 1)*35);
			
			// old color: ColorLib.gray(200);
			GOeBurstClusterWithStats g = (GOeBurstClusterWithStats) groupList.getModel().getElementAt(item.getSourceTuple().getInt("group"));
			switch (g.getEdgeMaxTieLevel(edge)) {
				case 0: return  ColorLib.rgb(  0,   0,   0);
				case 1: return  ColorLib.rgb(  0,   0, 255);
				case 2: return  ColorLib.rgb(  0, 255,   0);
				case 3: return  ColorLib.rgb(255,   0,   0);
				default: return ColorLib.rgb(255, 255,   0); 
			}
		}
	}
		
	private class NodeSearchPanel extends JSearchPanel {

		private static final long serialVersionUID = 1L;

		NodeSearchPanel(Visualization view) {
			super(view, "graph.nodes", Visualization.SEARCH_ITEMS, "st_id", true, true);
			setShowResultCount(false);
			setBorder(BorderFactory.createEmptyBorder(5,5,4,0));
			setFont(FontLib.getFont("Tahoma", Font.PLAIN, 11));
			setBackground(Color.WHITE);
			setForeground(Color.BLACK);
			requestFocus();
		}
	}
	
	private class BurstNeighborHighlightControl extends NeighborHighlightControl {
		@Override
		protected void setNeighborHighlight(NodeItem n, boolean state) {
			AbstractProfile st = (AbstractProfile) n.getSourceTuple().get("st_ref");
			GOeBurstClusterWithStats g = (GOeBurstClusterWithStats) groupList.getModel().getElementAt(n.getSourceTuple().getInt("group"));
			
			Iterator<AbstractProfile> iter = g.getSLVs(st).iterator();
		        while (iter.hasNext() ) {
		        	NodeItem nitem = 
	        			(NodeItem) view.getVisualItem("graph.nodes", n.getTable().getTuple(st2rowid.get(iter.next())));
		        	nitem.setHighlighted(state);
		        }

			iter = g.getDLVs(st).iterator();
		        while (iter.hasNext() )
		        	n.getTable().setInt(st2rowid.get(iter.next()), "hdlv", state ? 1 : 0);
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
				Edge edge = (Edge) ((EdgeItem) item).getSourceTuple().get("edge_ref");
				textArea.append(edge + 
						((GOeBurstClusterWithStats) groupList.getModel().getElementAt(item.getInt("group"))).getInfo(edge)
						+ "\n");
			}
			if (item instanceof NodeItem) {
				AbstractProfile st = (AbstractProfile) ((NodeItem) item).getSourceTuple().get("st_ref");
				textArea.append(st +
						((GOeBurstClusterWithStats) groupList.getModel().getElementAt(item.getInt("group"))).getInfo(st));
				
				textArea.append("Chart details:\n");
//				if (stMap != null) {
//					int total = 0;
//					DecimalFormat df = new DecimalFormat("#.##");
//					Iterator<Group> groups = stMap.getValue(st.getID()).iterator();
//					while (groups.hasNext()) {
//						Group group = groups.next();
//						double percent = (((double) group.size() * 100) / st.getFreq());
//						textArea.append(" +" + group.getName() + " " + df.format(percent) + "%\n");
//						total += group.size();
//					}
//					double percent = (((double) (st.getFreq() - total) * 100) / st.getFreq());
//					if (percent > 0)
//						textArea.append(" + 'others' " + df.format(percent) + "%\n");
//				}
				
				textArea.append("\n");

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
			textArea.setCaretPosition(textArea.getDocument().getLength());
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
				if (g.getFakeRoot() != null)
					nodeTable.setInt(st2rowid.get(g.getFakeRoot()), "founder", g.isFounder(g.getFakeRoot()) ? 1 : 0);
				g.updateVisibleEdges((AbstractProfile) item.get("st_ref"));
				nodeTable.setInt(st2rowid.get(g.getFakeRoot()), "founder", 2);
			}
			
			view.setVisible("graph", null, false);
			view.cancel("layout");
			
			Iterator<Edge> edgeIter = g.getEdges().iterator();
			while (edgeIter.hasNext()) {
				Edge e = edgeIter.next();
				
				if (! e.visible() && edge2rowid.containsKey(e)) {
					graph.removeEdge(edge2rowid.get(e));
					edge2rowid.remove(e);
					
					int stRow = st2rowid.get(e.getU());
					nodeTable.setInt(stRow, "dg", nodeTable.getInt(stRow, "dg") - 1);
					stRow = st2rowid.get(e.getV());
					nodeTable.setInt(stRow, "dg", nodeTable.getInt(stRow, "dg") - 1);
				}
				
				if (e.visible() && ! edge2rowid.containsKey(e)) {
					int rowID = graph.addEdge(st2rowid.get(e.getU()), st2rowid.get(e.getV()));
					edge2rowid.put(e, rowID);
					
					edgeTable.setInt(rowID, "group", g.getID());
					edgeTable.set(rowID, "edge_ref", e);
					edgeTable.setInt(rowID, "viz", HammingDistance.compute(e.getU(), e.getV()));
					
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
