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
import net.phyloviz.upgmanjcore.visualization.GView;

public class JRadialViewControlPanel extends JPanel {

    private ControlPanelChangeListener lstnr = new ControlPanelChangeListener();
    private GView gv;
    private Map<String, Integer> props = new HashMap<>();
    
    public String WIDTH = "width";
    public String HEIGHT = "height";
    public String FONT = "font";
    
    public JRadialViewControlPanel(GView gv) {
        this.gv = gv;
        this.props.put("width", BarChartRenderer.DEFAULT_WIDTH);
        this.props.put("height", BarChartRenderer.DEFAULT_HEIGHT);
        this.props.put("font", BarChartRenderer.DEFAULT_FONT_SIZE);
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
            JSlider field = createField(p.getKey(), p.getValue(), 1, p.getValue() * 2);
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

    /**
     * Change listener that updates parameters in response to interaction.
     */
    private class ControlPanelChangeListener implements ChangeListener {

        public void stateChanged(ChangeEvent e) {
            JSlider s = (JSlider) e.getSource();
            String name = s.getName();
            int val = s.getValue().intValue();
            if(name.equals(HEIGHT))
                ((GraphView) gv).setHeightViewControlValue(val);
            else if(name.equals(WIDTH))
                ((GraphView) gv).setWidthViewControlValue(val);
             else if(name.equals(FONT))
                ((GraphView) gv).setFontViewControlValue(val);
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
