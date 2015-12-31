package net.phyloviz.njviewer.ui.color;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JPanel;
import org.openide.util.ImageUtilities;

class ColorChooser extends JDialog {

	private JColorChooser j;
	private JButton change;
	private JButton cancel;
	private final ColorComponent cc;
	private PieChartPanel mychart;
	private HashMap<Color, TooltipInfo> tooltipMap;
        private EdgeStats edge;

	public ColorChooser(EdgeStats edge, PieChartPanel _mychart, final ColorComponent cc, HashMap<Color, TooltipInfo> tooltipMap) {
		super();
		setName("Colors");
		setTitle("Colors");
		setIconImage(ImageUtilities.loadImage("net/phyloviz/core/DataSetIcon.png"));
		this.tooltipMap = tooltipMap;
		this.cc = cc;
                this.edge = edge;
		JPanel panel = new JPanel();
		j = new JColorChooser();
		j.setColor(cc.obtainColor().getBackground());
		this.mychart = _mychart;
		this.add(j, BorderLayout.CENTER);
		change = new JButton("Change");
		cancel = new JButton("Cancel");
		panel.add(change);
		panel.add(cancel);
		change.addActionListener(new ColorActionListener());
		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		this.add(panel, BorderLayout.SOUTH);
		this.setVisible(true);
		this.pack();
	}

	private class ColorActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Color c = j.getColor();
			TooltipInfo ti = tooltipMap.get(cc.obtainColor().getBackground());
			tooltipMap.remove(cc.obtainColor().getBackground());
			tooltipMap.put(c, ti);
                        
			cc.obtainColor().setBackground(c);
                        edge.updateColor(Integer.parseInt(cc.getKey()), c);
			mychart.repaint();
			dispose();
		}
	}
}
