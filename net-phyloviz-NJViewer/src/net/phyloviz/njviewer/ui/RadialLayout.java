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

package net.phyloviz.njviewer.ui;

import java.util.LinkedList;
import net.phyloviz.njviewer.action.control.JRadialViewControlPanel;
import net.phyloviz.upgmanjcore.visualization.GView;
import prefuse.action.layout.graph.RadialTreeLayout;

import prefuse.data.Graph;
import prefuse.visual.NodeItem;

public class RadialLayout extends RadialTreeLayout {

    private final int m_size;
    private final float m_root_distance;
    public static final int DEFAULT_ZERO_DISTANCE_VALUE = 10;
    public static final int DEFAULT_DISTANCE_MULTIPLIER = 100;
    private final GView gv;
    
    public RadialLayout(GView gv, String group, float rootDistance, int size) {
        super(group);
        this.gv = gv;
        m_size = size;
        m_root_distance = rootDistance;
    }

    @Override
    public void run(double frac) {
        Graph g = (Graph) m_vis.getGroup(m_group);
        initSchema(g.getNodes());

        m_origin = getLayoutAnchor();
        NodeItem r = getLayoutRoot();

        LinkedList q = new LinkedList();
        q.push(r);
        r.setFloat("rightborder", 0);
        r.setFloat("wedgesize", (float) (2 * Math.PI));
        r.setDouble("x", 0);
        r.setDouble("y", 0);
        setX(r, null, 0);
        setY(r, null, 0);
        while (!q.isEmpty()) {
            NodeItem v = (NodeItem) q.pop();
            float v_border = v.getFloat("rightborder");
            int n = v.getChildCount();
            for (int i = 0; i < n; i++) {
                NodeItem w = (NodeItem) v.getChild(i);
                q.push(w);
                w.setFloat("rightborder", v_border);
                w.setFloat("wedgesize", (float) ((2 * Math.PI) * leafcount(w)) / m_size);
                float w_alfa = w.getFloat("rightborder") + (w.getFloat("wedgesize") / 2);
                float w_dist;
                if (v == r) {
                    w_dist = m_root_distance * getDistanceMultiplier() + getZeroDistanceValue();
                    r.setDouble("xp", (v.getX() + Math.cos(w_alfa) * w_dist));
                    r.setDouble("yp", (v.getY() + Math.sin(w_alfa) * w_dist));
                } else {
                    w_dist = v.getFloat("distance" + i) * getDistanceMultiplier() + getZeroDistanceValue();
                }
                w.setDouble("x", (v.getX() + Math.cos(w_alfa) * w_dist));
                w.setDouble("y", (v.getY() + Math.sin(w_alfa) * w_dist));
                w.setDouble("xp", v.getX());
                w.setDouble("yp", v.getY());
                setX(w, v, v.getX() + Math.cos(w_alfa) * w_dist);
                setY(w, v, v.getY() + Math.sin(w_alfa) * w_dist);

                v_border = v_border + w.getFloat("wedgesize");
            }

        }

    }

    private int leafcount(NodeItem n) {
        if (n.getChildCount() == 0) {
            return 1;
        }
        int sizeL = leafcount((NodeItem) n.getFirstChild());
        int sizeR = leafcount((NodeItem) n.getLastChild());

        return sizeL + sizeR;
    }

    private int getDistanceMultiplier() {
        return ((GraphView)gv).getRadialControlViewInfo(JRadialViewControlPanel.DISTANCE_MULTIPLIER);
    }

    private int getZeroDistanceValue() {
        return ((GraphView)gv).getRadialControlViewInfo(JRadialViewControlPanel.ZERO_DISTANCE);
    }

} // end of class RadialTreeLayout

