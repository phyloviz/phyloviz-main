///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
package net.phyloviz.upgmanjcore.visualization;
//
//import java.awt.Color;

import java.util.Dictionary;
import java.util.Hashtable;
import javax.swing.JLabel;
import javax.swing.JSlider;


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
        label = new JLabel(String.valueOf(init));
        
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
