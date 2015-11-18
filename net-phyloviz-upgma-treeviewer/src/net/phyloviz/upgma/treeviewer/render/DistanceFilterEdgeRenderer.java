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
import net.phyloviz.upgma.treeviewer.TreeView;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.visual.EdgeItem;
import prefuse.visual.VisualItem;

public class DistanceFilterEdgeRenderer extends OrthogonalEdgeRenderer {

    private String m_key;
    private double m_distance;
    private final int m_scale;
    private final boolean m_labeled;
    private double distanceai ;

    public DistanceFilterEdgeRenderer(TreeView tv, double distance, int scale, boolean labeled, double maxDistance) {
        super(tv);
        m_key = "viz";
        m_distance = tv.getRescaleEdges() ? (maxDistance - Math.log(1 + (distance))) * scale
                : (maxDistance - distance) * scale;
        m_scale = scale;
        m_labeled = labeled;

    }

    @Override
    public void render(Graphics2D g, VisualItem item) {
        super.render(g, item);
        VisualItem u = ((EdgeItem) item).getSourceItem();
        VisualItem v = ((EdgeItem) item).getTargetItem();

        String u_id = u.canGetString("p_id") ? u.getString("p_id") : null;
        String v_id = v.canGetString("p_id") ? v.getString("p_id") : null;

        double d = -1;
        if (u_id == null && v_id != null) {
            d = u.getDouble("distance");
        }
        if (v_id == null && u_id != null) {
            d = v.getDouble("distance");
        }

        String distance = d == -1 ? "" : String.valueOf(d);

        Shape shape = getShape(item);
        if (shape == null) {
            return;
        }
        if (!u.getBoolean("isRuler") || !v.getBoolean("isRuler")) {
            double diMax = item.getBounds().getMaxX();
            double h = item.getBounds().getCenterY();
            boolean hide = m_distance > item.getBounds().getMinX();
            boolean hide2 = m_distance < item.getBounds().getMaxX();
            if (diMax <= m_distance
                    || //item.getBounds().contains(m_distance-0.001, h)){
                    (hide && hide2)) {
                item.setStrokeColor(ColorLib.rgb(214, 214, 214));
                if (u.canGetString("p_id")) {
                    u.setBoolean("hide", true);
                }
                if (v.canGetString("p_id")) {
                    v.setBoolean("hide", true);
                }
            } else {
                item.setStrokeColor(ColorLib.rgb(4, 58, 71));

                if (u.canGetString("p_id")) {
                    u.setBoolean("hide", false);
                }
                if (v.canGetString("p_id")) {
                    v.setBoolean("hide", false);
                }
            }

            if (m_labeled) {
                double x = (u.getX() + v.getX()) / 2;
                double y = v.getY() - 2;

                Font df = FontLib.getFont("Tahoma", Font.PLAIN, 11);
                Color dc = g.getColor();

                Font mf = df.deriveFont(Font.BOLD);

                g.setFont(mf);
                g.setColor(dc);
                g.drawString(distance + "", (float) (x), (float) y);
            }
        }
    }
}
