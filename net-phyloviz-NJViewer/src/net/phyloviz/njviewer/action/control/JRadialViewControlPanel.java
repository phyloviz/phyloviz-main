/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.njviewer.action.control;

/**
 *
 * @author martanascimento
 */
import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.phyloviz.njviewer.render.BarChartRenderer;
import net.phyloviz.njviewer.ui.GraphView;
import net.phyloviz.njviewer.ui.RadialLayout;
import net.phyloviz.upgmanjcore.visualization.GView;

public class JRadialViewControlPanel extends JPanel {

    private ControlPanelChangeListener lstnr = new ControlPanelChangeListener();
    private GView gv;
    private Map<String, Integer> props = new HashMap<>();

    public static String WIDTH = "Bar width";
    public static String HEIGHT = "Bar height";
    public static String FONT = "Font size";
    public static String ZERO_DISTANCE = "Distance offset";
    public static String DISTANCE_MULTIPLIER = "Distance multiplier";

    public JRadialViewControlPanel(GView gv) {
        this.gv = gv;
        this.props.put(WIDTH, BarChartRenderer.DEFAULT_WIDTH);
        this.props.put(HEIGHT, BarChartRenderer.DEFAULT_HEIGHT);
        this.props.put(FONT, BarChartRenderer.DEFAULT_FONT_SIZE);
        this.props.put(ZERO_DISTANCE, RadialLayout.DEFAULT_ZERO_DISTANCE_VALUE);
        this.props.put(DISTANCE_MULTIPLIER, RadialLayout.DEFAULT_DISTANCE_MULTIPLIER);
        this.setBackground(Color.WHITE);
        initUI();
    }

    /**
     * Initialize the UI.
     */
    private void initUI() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        Box v = new Box(BoxLayout.Y_AXIS);
        for (Entry<String, Integer> p : props.entrySet()) {
            JSlider field = createField(p.getKey(), p.getValue(), 1, p.getValue() * 10);
            field.addChangeListener(lstnr);
            v.add(field);
        }
        v.setBorder(BorderFactory.createTitledBorder("Bar Chart"));
        this.add(v);
    }

    /**
     * Create an entry for configuring a single parameter.
     */
    private static JSlider createField(String name, int value, int min, int max) {

        JSlider s = new JSlider(name, min, max, value);
        s.setBackground(Color.WHITE);
        s.setPreferredSize(new Dimension(300, 30));
        s.setMaximumSize(new Dimension(300, 30));
        return s;
    }

    public Integer getProp(String prop) {
        return props.get(prop);
    }

    /**
     * Change listener that updates parameters in response to interaction.
     */
    private class ControlPanelChangeListener implements ChangeListener {

        public void stateChanged(ChangeEvent e) {
            JSlider s = (JSlider) e.getSource();
            String name = s.getName();
            int val = s.getValue().intValue();
            props.put(name, val);
            ((GraphView) gv).updateLayout();
//            if (name.equals(HEIGHT)) {
//                ((GraphView) gv).setHeightViewControlValue(val);
//            } else if (name.equals(WIDTH)) {
//                ((GraphView) gv).setWidthViewControlValue(val);
//            } else if (name.equals(FONT)) {
//                ((GraphView) gv).setFontViewControlValue(val);
//            } else if(name.equals(ZERO_DISTANCE))
//                ((GraphView) gv).setZeroDistanceViewControlValue(val);
//             else if(name.equals(DISTANCE_MULTIPLIER))
//                ((GraphView) gv).setDistanceMultiplierViewControlValue(val);
//            a = 0;
        }
    }

    public JFrame showControlPanel(GView gv, Map<String, Integer> props) {
        JFrame frame = new JFrame("Control Panel");
        frame.setContentPane(new JRadialViewControlPanel(gv));
        frame.pack();
        frame.setVisible(true);
        return frame;
    }

}
