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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ColorsPanel extends JPanel {

    private JScrollPane sp;
    private JPanel panel;
    private ColorComponent[] labels;
    private PieChartPanel mychart;
    private HashMap<Color, TooltipInfo> tooltipMap;
    private EdgeStats edge;
    public ColorsPanel(EdgeStats edge, PieChartPanel _mychart, HashMap<Color, TooltipInfo> tooltipMap) {
        super();
        this.tooltipMap = tooltipMap;
        this.setBackground(Color.WHITE);
        BoxLayout b = new BoxLayout(this, BoxLayout.Y_AXIS);
        this.setLayout(b);
        this.mychart = _mychart;
        this.edge = edge;
        labels = new ColorComponent[edge.interval];
        panel = new JPanel();
        BoxLayout bb = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setBackground(Color.WHITE);
        panel.setLayout(bb);
        MouseListener mouse = new MyMouseAdapter(mychart);
        for (int i = 0; i < edge.interval; i++) {
            labels[i] = new ColorComponent(String.valueOf(i), tooltipMap.get(edge.colors[i]).getName(), edge.colors[i]);
            labels[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            labels[i].addMouseListener(mouse);
            panel.add(labels[i]);
        }
        sp = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp.setBackground(Color.WHITE);
        sp.getViewport().setPreferredSize(new Dimension(200, 400));
        this.setBorder(BorderFactory.createTitledBorder("Edge Color Legend"));
        this.add(sp);
        
    }

    public JScrollPane getJScrollPane() {
        return sp;
    }

    private class MyMouseAdapter extends MouseAdapter {

		private PieChartPanel mychart;

		public MyMouseAdapter(PieChartPanel mychart) {
			this.mychart = mychart;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			ColorComponent cc = (ColorComponent) e.getSource();
			ColorChooser cch = new ColorChooser(edge, mychart, cc, tooltipMap);
		}
	}

}
