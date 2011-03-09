package net.phyloviz.gtview.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.phyloviz.algo.tree.Edge;
import net.phyloviz.category.CategoryProvider;
import net.phyloviz.category.filter.Category;
import net.phyloviz.core.data.AbstractProfile;
import net.phyloviz.core.data.Profile;
import net.phyloviz.goeburst.tree.GOeBurstMSTResult;
import net.phyloviz.gtview.action.ExportAction;
import net.phyloviz.gtview.action.HighQualityAction;
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

public class GraphView2 extends GView {
	private static final long serialVersionUID = 1L;

	private static final String SRC = Graph.DEFAULT_SOURCE_KEY;
	private static final String TRG = Graph.DEFAULT_TARGET_KEY;

	private Table nodeTable;
	private Table edgeTable;

	private TreeMap<Integer, Integer> uid2rowid;
	Graph graph;
	
	private GOeBurstMSTResult er;

	private Visualization view;
	private Display display;

	private JSearchPanel searchPanel;
	private boolean searchMatch = false;
	
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
	private CategoryProvider cp;

	public GraphView2(GOeBurstMSTResult er) {
		this.setLayout(new BorderLayout());
		this.setBackground(Color.WHITE);
		this.setOpaque(true);

		this.er = er;

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
				    return FontLib.getFont("Tahoma", Font.PLAIN, 11 + (linear ? st.getFreq() : (7 * Math.log(1 + st.getFreq()))));
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

		Schema edgeSchema = new Schema();
		edgeSchema.addColumn(SRC, int.class);
		edgeSchema.addColumn(TRG, int.class);
		edgeSchema.addColumn("edge_ref", Edge.class);
		edgeSchema.addColumn("w", int.class);

		// Create and fill tables.
		nodeSchema.lockSchema();
		edgeSchema.lockSchema();
		nodeTable = nodeSchema.instantiate();
		edgeTable = edgeSchema.instantiate();

		uid2rowid = new TreeMap<Integer,Integer>();

		Iterator<Edge> eIter = er.getEdges().iterator();
		while (eIter.hasNext()) {
			Edge e = eIter.next();

			int uRowNb = -1;
			Profile st = e.getU().getProfile();
			if (! uid2rowid.containsKey(st.getUID())) {

				uRowNb = nodeTable.addRow();
				uid2rowid.put(st.getUID(), uRowNb);
				nodeTable.setString(uRowNb, "st_id", st.getID());
				nodeTable.set(uRowNb, "st_ref", st);
			} else
				uRowNb = uid2rowid.get(st.getUID());

			int vRowNb = -1;
			st = e.getV().getProfile();
			if (! uid2rowid.containsKey(st.getUID())) {

				vRowNb = nodeTable.addRow();
				uid2rowid.put(st.getUID(), vRowNb);
				nodeTable.setString(vRowNb, "st_id", st.getID());
				nodeTable.set(vRowNb, "st_ref", st);
			} else
				vRowNb = uid2rowid.get(st.getUID());

			int rowNb = edgeTable.addRow();
			edgeTable.setInt(rowNb, SRC, uRowNb);
			edgeTable.setInt(rowNb, TRG, vRowNb);
			edgeTable.set(rowNb, "edge_ref", e);
			edgeTable.set(rowNb, "w", er.getDistance().compute(e.getU().getProfile(), e.getV().getProfile()));
		}

		// Create the graph.
		graph = new Graph(nodeTable, edgeTable, false);

		view.add("graph", graph);
		view.setVisible("graph", null, true);

		running = true;
		linear = false;
		
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
		//popupMenu.add(new GroupControlAction(this).getMenuItem());
		popupMenu.add(new InfoControlAction(this).getMenuItem());
		//popupMenu.add(new EdgeViewControlAction(this).getMenuItem());
		//popupMenu.add(new EdgeFullViewControlAction(this).getMenuItem());
		popupMenu.add(new LinearSizeControlAction(this).getMenuItem());
		popupMenu.add(new HighQualityAction(this).getMenuItem());
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
	}
	
	public JScrollPane getInfoPanel(){
		return infoPanel;
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

	@Override
	public void showInfoPanel(boolean status) {
		if (status == infoPanelStatus)
			return;
		
		if (status) 
			add(infoPanel, BorderLayout.EAST);
		else
			remove(infoPanel);
		infoPanelStatus = status;
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
	public void setHighQuality(boolean status) {
		display.setHighQuality(status);
	}

	public void setCategoryProvider(CategoryProvider cp) {
		this.cp = cp;
	}
	
	public void appendTextToInfoPanel(String text) {
		textArea.append(text);
		textArea.setCaretPosition(textArea.getDocument().getLength());
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

	@Override
        public ForceDirectedLayout getForceLayout(){
            return fdl;
        }

	// We make sure that we return always the same panel.
	@Override
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

	@Override
        public ArrayList<ForcePair> getForces() {
             ForceSimulator fs =fdl.getForceSimulator();
              Force[] farray=fs.getForces();
              ArrayList<ForcePair> array=new ArrayList<ForcePair>();
              for(int i=0;i<farray.length;i++){

                 int ni= farray[i].getParameterCount();
                  for(int j=0;j<ni;j++){
                      array.add(new ForcePair(farray[i].getParameterName(j),farray[i].getParameter(j)));
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
			else {
				//Color color = colors.get(itemTuple.getString("st_id"));
				//if (color != null)
				//	return color.getRGB();
				
				if (((Node) item).getDegree() < 3)
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
			int w = item.getSourceTuple().getInt("w");

			return ColorLib.gray(255 - 255/w); // .rgb(255, 255,   0);
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
	
	private class ItemInfoControl extends ControlAdapter {
		@Override
		public void itemClicked(VisualItem item, MouseEvent e) {

                     

			if (item instanceof EdgeItem) {
				Edge edge = (Edge) ((EdgeItem) item).getSourceTuple().get("edge_ref");
				textArea.append(edge.getU().getProfile().getID() + " -- "
					      + edge.getV().getProfile().getID() + " (lv="
					      + item.getSourceTuple().getString("w") + ")\n\n");
			}
			if (item instanceof NodeItem) {
				AbstractProfile st = (AbstractProfile) ((NodeItem) item).getSourceTuple().get("st_ref");
				textArea.append(st.toString() + "\n");
				
				if (cp != null) {
					textArea.append("Chart details:\n");
					int total = 0;
					DecimalFormat df = new DecimalFormat("#.##");
					Collection<Category> groupsList = cp.getCategories(st.getID());
					if (groupsList != null) {
						Iterator<Category> groups = groupsList.iterator();
						while (groups.hasNext()) {
							Category group = groups.next();
							double percent = (((double) group.weight() * 100) / st.getFreq());
							textArea.append(" +" + group.getName() + " " + df.format(percent) + "%\n");
							total += group.weight();
						}
					}
					double percent = (((double) (st.getFreq() - total) * 100) / st.getFreq());
					if (percent > 0)
						textArea.append(" + 'others' " + df.format(percent) + "%\n");
				}
				
				textArea.append("\n");
			}
			textArea.setCaretPosition(textArea.getDocument().getLength());
		}
	}
}
