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

package net.phyloviz.category.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

class ColorComponent extends JPanel {

	private String key;
	private JLabel name;
	private JLabel color;
	private final Dimension preferredSize;

	public JLabel obtainName() {
		return name;
	}

	public JLabel obtainColor() {
		return color;
	}

	public String getKey() {
		return key;
	}

	public ColorComponent(String n, String extra, Color c) {
		super();
		this.key = n;
		BoxLayout b = new BoxLayout(this, BoxLayout.X_AXIS);
		this.setLayout(b);
		this.name = new JLabel(n + extra);
		this.color = new JLabel("    ");
		this.name.setOpaque(true);
		this.name.setBackground(Color.WHITE);
		this.color.setOpaque(true);
		this.color.setBackground(c);
		preferredSize = new Dimension(50, 15);
		color.setPreferredSize(preferredSize);
		color.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.add(color);
		this.add(name);
	}
}
