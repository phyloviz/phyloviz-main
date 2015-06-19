/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.njviewer.Force;

import java.util.Iterator;
import prefuse.action.layout.graph.ForceDirectedLayout;
import static prefuse.action.layout.graph.ForceDirectedLayout.FORCEITEM;
import static prefuse.action.layout.graph.ForceDirectedLayout.FORCEITEM_SCHEMA;
import prefuse.data.tuple.TupleSet;
import prefuse.util.force.ForceItem;
import prefuse.util.force.ForceSimulator;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

/**
 *
 * @author Adriano
 */
public class NJForceDirectLayout extends ForceDirectedLayout{
    
    private float scale = 1;
    
    public NJForceDirectLayout(String g){
        super(g);
    }
    
    @Override
    protected void initSimulator(ForceSimulator fsim) {     
        // make sure we have force items to work with
        TupleSet ts = m_vis.getGroup(m_nodeGroup);
        if ( ts == null ) return;
        try {
            ts.addColumns(FORCEITEM_SCHEMA);
        } catch ( IllegalArgumentException iae ) { /* ignored */ }
        
        float startX = (referrer == null ? 0f : (float)referrer.getX());
        float startY = (referrer == null ? 0f : (float)referrer.getY());
        startX = Float.isNaN(startX) ? 0f : startX;
        startY = Float.isNaN(startY) ? 0f : startY;
       
        Iterator iter = m_vis.visibleItems(m_nodeGroup);
        while ( iter.hasNext() ) {
            VisualItem item = (VisualItem)iter.next();
            ForceItem fitem = (ForceItem)item.get(FORCEITEM);
            fitem.mass = getMassValue(item);
            double x = item.getEndX();
            double y = item.getEndY();
            fitem.location[0] = (Double.isNaN(x) ? startX : (float)x);
            fitem.location[1] = (Double.isNaN(y) ? startY : (float)y);
            fsim.addItem(fitem);
        }
        if ( m_edgeGroup != null ) {
            iter = m_vis.visibleItems(m_edgeGroup);
            while ( iter.hasNext() ) {
                EdgeItem  e  = (EdgeItem)iter.next();
                NodeItem  n1 = e.getSourceItem();
                ForceItem f1 = (ForceItem)n1.get(FORCEITEM);
                NodeItem  n2 = e.getTargetItem();
                ForceItem f2 = (ForceItem)n2.get(FORCEITEM);
                float dist = e.getFloat("distance");
                fsim.addSpring(f1, f2, dist*scale);
            }
        }
    }

    public void changeScale(int value) {
        scale = value;
    }
    
}
