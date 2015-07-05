/*-
 * Copyright (c) 2012, PHYLOViZ Team <phyloviz@gmail.com>
 * All rights reserved.
 * 
 * This file is part of PHYLOViZ <http://www.phyloviz.net>.
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
 * 
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole combination.
 * 
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent modules,
 * and to copy and distribute the resulting executable under terms of your
 * choice, provided that you also meet, for each linked independent module,
 * the terms and conditions of the license of that module.  An independent
 * module is a module which is not derived from or based on this library.
 * If you modify this library, you may extend this exception to your version
 * of the library, but you are not obligated to do so.  If you do not wish
 * to do so, delete this exception statement from your version.
 */
package net.phyloviz.upgma.treeviewer.render;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

public class DistanceFilterEdgeRenderer extends OrthogonalEdgeRenderer {

    private String m_key;
    private double m_distance;
    private final int m_scale;

    public DistanceFilterEdgeRenderer(double distance, int scale) {
        super();
        m_key = "viz";
        m_distance = distance * scale;
        m_scale = scale;

    }

    @Override
    public void render(Graphics2D g, VisualItem item) {
        super.render(g, item);
        VisualItem u = ((EdgeItem)item).getSourceItem();
        VisualItem v = ((EdgeItem)item).getTargetItem();
        
        Shape shape = getShape(item);
        if (shape == null) {
            return;
        }
        double diMax = item.getBounds().getMaxX();
        double h = item.getBounds().getCenterY();
     
        if(diMax <= m_distance || item.getBounds().contains(m_distance, h)){
            item.setStrokeColor(ColorLib.rgb(214, 214, 214));  
            if(u.canGetString("p_id"))
                u.setBoolean("hide", true);
            if(v.canGetString("p_id"))
                v.setBoolean("hide", true);
        }
        
        else{
            item.setStrokeColor(ColorLib.rgb(4, 58, 71));
            
            if(u.canGetString("p_id"))
                u.setBoolean("hide", false);
            if(v.canGetString("p_id"))
                v.setBoolean("hide", false);
        }
    }
}
