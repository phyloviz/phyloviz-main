/*-
 * Copyright (c) 2016, PHYLOViZ Team <phyloviz@gmail.com>
 * All rights reserved.
 * 
 * This file is part of PHYLOViZ <http://www.phyloviz.net/>.
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
 */

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
