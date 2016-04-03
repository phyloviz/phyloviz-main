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

package net.phyloviz.upgmanjcore.visualization;
//
//import java.awt.Color;

import java.util.Dictionary;
import java.util.Hashtable;
import javax.swing.JLabel;
import javax.swing.JSlider;


//
///**
// * use in case is necessary a slider
// * @author Adriano Sousa
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
    /**
     * set slider label
     */
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
