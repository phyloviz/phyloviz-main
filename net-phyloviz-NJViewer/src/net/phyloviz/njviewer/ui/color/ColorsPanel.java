package net.phyloviz.njviewer.ui.color;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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

    public ColorsPanel(PieChartPanel _mychart, int size, Color[] c, HashMap<Color, TooltipInfo> tooltipMap) {
        super();
        this.tooltipMap = tooltipMap;
        this.setBackground(Color.WHITE);
        BoxLayout b = new BoxLayout(this, BoxLayout.Y_AXIS);
        this.setLayout(b);
        this.mychart = _mychart;
        labels = new ColorComponent[size];
        panel = new JPanel();
        BoxLayout bb = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setBackground(Color.WHITE);
        panel.setLayout(bb);

        for (int i = 0; i < size; i++) {
            labels[i] = new ColorComponent("", tooltipMap.get(c[i]).getName(), c[i]);
            labels[i].setAlignmentX(Component.LEFT_ALIGNMENT);
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

}
