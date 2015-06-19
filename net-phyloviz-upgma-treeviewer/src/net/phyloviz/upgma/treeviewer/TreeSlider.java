///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
package net.phyloviz.upgma.treeviewer;
//
//import java.awt.Color;

import java.util.Dictionary;
import java.util.Hashtable;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

//import java.awt.Dimension;
//import java.awt.Font;
//import java.awt.FontMetrics;
//import java.awt.Graphics;
//import java.awt.Graphics2D;
//import java.awt.Insets;
//import java.awt.Rectangle;
//import java.awt.event.ActionEvent;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.awt.geom.AffineTransform;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import javax.swing.AbstractAction;
//import javax.swing.BoxLayout;
//import javax.swing.Icon;
//import javax.swing.JComponent;
//import javax.swing.JLabel;
//import javax.swing.JSlider;
//import javax.swing.JTextField;
//import javax.swing.event.ChangeEvent;
//import javax.swing.event.ChangeListener;
//import javax.swing.plaf.basic.BasicLabelUI;
//import prefuse.util.StringLib;
//import prefuse.util.ui.JValueSlider;
//
///**
// *
// * @author Marta Nascimento
// */
public class TreeSlider {

    private final JSlider slider;
    private final int MIN;// = 0;
    private final int MAX;// = 100;
    private final JLabel label;
    public TreeSlider(int orientation, int min, int max, int init){
        
        slider = new JSlider(orientation, min, max, init);
        label = new JLabel(init + "");
        
        MIN = min;
        MAX = max;
        
    }
    
    public JSlider getSlider(){
        return slider;
    }
    
    public void setValuesLabel(){
        Dictionary<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(0,      new JLabel("" + MIN));
        labelTable.put(MAX/10, new JLabel("" + MAX / 10));
        labelTable.put(MAX,    new JLabel("" + MAX));
        
        slider.setLabelTable(labelTable);
        slider.setPaintLabels(true);
        
    }   
    
    public void setTickSpacing(int tickSpacing){
        slider.setMajorTickSpacing(tickSpacing);
        slider.setPaintTicks(true);
    }
        
        //Create the label table.
        

    public void setCurrentValueText(String val) {
        label.setText(val);
    }

    public JLabel getLabel(){
        return label;
    }
    
}
