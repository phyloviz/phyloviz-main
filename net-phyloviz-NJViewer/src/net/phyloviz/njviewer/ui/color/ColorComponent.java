package net.phyloviz.njviewer.ui.color;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ColorComponent extends JPanel {

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
		this.name = new JLabel(extra);
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
