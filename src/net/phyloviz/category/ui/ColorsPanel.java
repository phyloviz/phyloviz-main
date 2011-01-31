package net.phyloviz.category.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import net.phyloviz.category.CategoryProvider;

public class ColorsPanel extends JPanel {

	private JScrollPane sp;
	private JPanel panel;
	private JPanel buttons;
	private JButton load;
	private JButton save;
	private ColorComponent[] labels;
	private CategoryProvider cp;
	private PieChartPanel mychart;
	private HashMap<Color, TooltipInfo> tooltipMap;

	public ColorsPanel(PieChartPanel _mychart, CategoryProvider _cp, HashMap<Color, TooltipInfo> tooltipMap) {
		super();
		this.tooltipMap = tooltipMap;
		this.setBackground(Color.WHITE);
		BoxLayout b = new BoxLayout(this, BoxLayout.Y_AXIS);
		this.setLayout(b);
		this.mychart = _mychart;
		this.cp = _cp;
		int size = cp.getCategories().size();
		labels = new ColorComponent[size];
		panel = new JPanel();
		BoxLayout bb = new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setBackground(Color.WHITE);
		panel.setLayout(bb);
		MouseListener mouse = new MyMouseAdapter(mychart, cp);

		Iterator<Entry<String, Integer>> it = cp.getCategories().iterator();
		int k = 0;
		while (it.hasNext()) {
			Entry<String, Integer> entry = it.next();
			String label = entry.getKey();
			Color c = cp.getCategoryColor(label);
			float perc = tooltipMap.get(c).getPercentage();
			labels[k] = new ColorComponent(label, " (" + perc + "% )", c);
			labels[k].addMouseListener(mouse);
			labels[k].setAlignmentX(Component.LEFT_ALIGNMENT);
			panel.add(labels[k]);
			k++;
		}
		sp = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.setBackground(Color.WHITE);
		sp.getViewport().setPreferredSize(new Dimension(200, 400));
		this.setBorder(BorderFactory.createTitledBorder("Legend"));
		this.add(sp);
		buttons = new JPanel();
		buttons.setBackground(Color.WHITE);
		load = new JButton("Load");
		save = new JButton("Save");
		buttons.add(load);
		buttons.add(save);
		load.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser() {

					private static final long serialVersionUID = 1L;

					@Override
					protected JDialog createDialog(Component parent) throws HeadlessException {
						JDialog dialog = super.createDialog(parent);
						dialog.setTitle("Choose file");
						return dialog;
					}
				};
				int ww = fileChooser.showDialog(getParent(), "Load");
				if (ww == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					try {
						BufferedReader br = new BufferedReader(new FileReader(file));
						String line;
						int i = 0;
						while ((line = br.readLine()) != null && i < cp.getCategories().size()) {
							String[] newColors = line.split(",");
							if (newColors.length == 3) {
								Color c = new Color(Integer.parseInt(newColors[0]), Integer.parseInt(newColors[1]), Integer.parseInt(newColors[2]));
								labels[i].obtainColor().setBackground(c);
								cp.putCategoryColor(labels[i].getKey(), c);
								i++;
							}
						}
						mychart.repaint();
					} catch (Exception f) {
						System.out.println(f.getMessage());
						JOptionPane.showMessageDialog(getParent(), "Unable to load color parameters!", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		save.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser() {

					private static final long serialVersionUID = 1L;

					@Override
					protected JDialog createDialog(Component parent) throws HeadlessException {
						JDialog dialog = super.createDialog(parent);
						dialog.setTitle("Choose file");
						return dialog;
					}
				};
				int ret = fileChooser.showDialog(getParent(), "Save");
				if (ret == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					try {
						PrintWriter pw = new PrintWriter(file);
						Iterator<Entry<String, Integer>> si = cp.getCategories().iterator();
						while (si.hasNext()) {
							Color c = cp.getCategoryColor(si.next().getKey());
							pw.println(c.getRed() + "," + c.getGreen() + "," + c.getBlue());
						}
						pw.close();
					} catch (Exception f) {
						JOptionPane.showMessageDialog(getParent(), "Unable to save color parameters!", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		this.add(buttons);
	}

	public JScrollPane getJScrollPane() {
		return sp;
	}

	public CategoryProvider getCategoryProvider() {
		return cp;
	}

	private class MyMouseAdapter extends MouseAdapter {

		private PieChartPanel mychart;

		public MyMouseAdapter(PieChartPanel mychart, CategoryProvider cp) {
			this.mychart = mychart;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			ColorComponent cc = (ColorComponent) e.getSource();
			ColorChooser cch = new ColorChooser(mychart, cc, cp, tooltipMap);
		}
	}


}
