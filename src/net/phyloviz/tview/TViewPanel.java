package net.phyloviz.tview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.TreeMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import net.phyloviz.core.util.DataModel;
import net.phyloviz.tview.util.Palette;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;

public class TViewPanel extends TopComponent {

	private DataModel dm;
	private TablePanel table;
	private TreePanel tree;
	private JScrollPane sp;
	private final JTextField filterText;
	private JButton selectButton;
	private JButton resetButton;
	//private JButton viewButton;
	private JRadioButton treeButton;
	private JRadioButton tableButton;
	private boolean tableortree;
	private boolean firstTime;
	private ButtonGroup group;
	private Palette pal;
	private TreeMap<String, Integer> fullCats;
	private TreeMap<String, Color> colorMap;
	//private DefaultPieDataset data;
	//private TreeMap<String, List<Group>> stMap;
	private boolean atLeastOnePlot;

	/** Creates new form TViewPanel */
	public TViewPanel(String name, DataModel dm) {
		super(Lookups.singleton(dm));
		initComponents();
		this.setName(name);
		
		this.dm = dm;
	
		//the finders
		table = new TablePanel(dm);
		tree = new TreePanel(dm);
		firstTime = true;
		tableortree = true;
		this.atLeastOnePlot = false;
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

//		viewButton = new JButton("Plot selection!");
//		viewButton.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				treeButton.setEnabled(false);
//				tableButton.setEnabled(false);
//
//				GraphView view = (GraphView) Phyloviz.getWindow().get("Display");
//				TreeSet<String>[] filter1;
//				TreeSet<String>[] filter2;
//				TreeSet<String>[] filter;
//				filter1 = table.viewing();
//				filter2 = tree.viewing();
//				if (tableortree) {
//					filter = filter1;
//				} else {
//					filter = filter2;
//				}
//				if (filter == null) {
//					treeButton.setEnabled(true);
//					tableButton.setEnabled(true);
//					return;
//				}
//
//				OldPopulation pop = Phyloviz.getPop();
//				// Now that we have the values to be shown for each column...
//				IFilter f = new CompositeFilter(filter, pop.getHeaders());
//
//				// Filtering each ST...
//				stMap = new TreeMap<String, List<Group>>();
//				fullCats = new TreeMap<String, Integer>();
//				TreeMap<String, Double> tp = new TreeMap<String, Double>();
//				Iterator<SequenceType> stIter = pop.getSTs().iterator();
//				while (stIter.hasNext()) {
//					SequenceType st = stIter.next();
//					Group g = new Group("");
//					g.addAll(st.getIndividuals());
//					List<Group> l = new LinkedList<Group>();
//					l.add(g);
//					l = f.filtering(l);
//
//					Iterator<Group> gIter = l.iterator();
//					while (gIter.hasNext()) {
//						Group grp = gIter.next();
//
//						Integer x = fullCats.get(grp.getName());
//						if (x != null) {
//							x += grp.size();
//						} else {
//							x = grp.size();
//						}
//
//						fullCats.put(grp.getName(), x);
//					}
//
//					stMap.put(st.getSID(), l);
//				}
//
//
//				// Update full chart...
//				data = new DefaultPieDataset();
//				colorMap = new TreeMap<String, Color>();
//
//				pal = new Palette(fullCats.size());
//
//
//				int total = 0, i = 0;
//				Iterator<String> labels = fullCats.keySet().iterator();
//				while (labels.hasNext()) {
//					String label = labels.next();
//					int partial = fullCats.get(label);
//					data.setValue(label, ((double) partial) / pop.getSize());
//					tp.put(label, ((double) partial) / pop.getSize());
//					total += partial;
//
//					colorMap.put(label, pal.getColors()[i++]);
//				}
//
//				if (pop.getSize() - total > 0) {
//					data.setValue("'others'", ((double) pop.getSize() - total) / pop.getSize());
//					tp.put("others", ((double) pop.getSize() - total) / pop.getSize());
//				}
//
//				// aqui começa...
//
//				if (view != null) {
//					view.setDefaultRenderer(new ChartRenderer(stMap, colorMap));
//					view.setStMap(stMap);
//				}
//
//				if (Phyloviz.getDataPanel() != null) {
//					ChartLegend newChart = new ChartLegend(fullCats, pop.getSize(), colorMap);
//
//					ChartLegendPanel chl = new ChartLegendPanel(newChart);
//
//					Phyloviz.getWindow().replace("Chart", chl);
//				}
//				// Phyloviz.getWindow().replace("Chart", newChart);
//
//				/* JFreeChart chart = ChartFactory.createPieChart(null, data, true, true, false);
//				PiePlot plot = (PiePlot) chart.getPlot();
//
//				PieRenderer renderer = new PieRenderer(pal.getColors());
//				renderer.setColor(plot, data);
//				plot.setLabelGenerator(null);
//				plot.setBackgroundPaint(Color.WHITE);
//				plot.setOutlineVisible(false);
//
//				Phyloviz.getWindow().replace("Chart", new ChartPanel(chart));*/
//
//				//até aqui....
//
//				// Draw pies...
//
//				treeButton.setEnabled(true);
//				tableButton.setEnabled(true);
//				treeButton.setOpaque(true);
//				tableButton.setOpaque(true);
//				if (!atLeastOnePlot && view != null) {
//					Phyloviz.getWindow().getMyMenuBar().getcategoryColorMenu().setEnabled(true);
//				}
//			}
//		});
//
//		viewButton.setPreferredSize(new Dimension(120, 10));

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
		//box.add(viewButton);
		box.add(resetButton);
		add(sp, BorderLayout.CENTER);
		add(box, BorderLayout.NORTH);
	}

//	public TreeMap<String, List<Group>> getStMap() {
//		return stMap;
//	}

//	public DefaultPieDataset getData() {
//		return data;
//	}

	public Palette getPalette() {
		return pal;
	}

	public TreeMap<String, Integer> getFullCategories() {
		return fullCats;
	}

	public TreeMap<String, Color> getColorMap() {
		return colorMap;
	}

	private class RadioListener1 implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			filterText.setText("");
			if (e.getStateChange() == ItemEvent.SELECTED) {
				tableortree = false;
				sp.setViewportView(tree);
				tree.selecting(table.getFilterSet(), true);
			} else {
			}
		}
	}

//	public void reset(OldPopulation pop) {
//		this.pop = pop;
//		table.initModel(pop);
//
//		tree.initModel(pop);
//	}

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
	public int getPersistenceType() {
		return PERSISTENCE_NEVER;
	}

	@Override
	protected String preferredID() {
		return "TViewer";
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
