/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.njviewer.Force;

import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.visual.VisualItem;

/**
 *
 * @author Adriano
 */
public class NJForceDirectLayout extends ForceDirectedLayout{
    
    
    public NJForceDirectLayout(String g){
        super(g);
    }
    @Override
    protected float getMassValue(VisualItem e){
        return e.getFloat("distance");
    }
}
