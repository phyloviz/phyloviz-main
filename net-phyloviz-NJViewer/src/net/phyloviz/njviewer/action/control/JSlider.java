/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.njviewer.action.control;

import prefuse.util.ui.JValueSlider;

/**
 *
 * @author martanascimento
 */
public class JSlider extends JValueSlider{

    private String name;
    
    public JSlider(String title, Number min, Number max, Number value) {
        super(title, min, max, value);
        this.name = title;
    }
    
    public String getName(){
        return name;
    }
}
