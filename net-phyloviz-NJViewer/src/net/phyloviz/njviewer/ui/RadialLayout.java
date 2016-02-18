package net.phyloviz.njviewer.ui;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.LinkedList;
import prefuse.action.layout.graph.RadialTreeLayout;

import prefuse.data.Graph;
import prefuse.visual.NodeItem;

public class RadialLayout extends RadialTreeLayout {

    private final int m_size;
    private final float m_root_distance;

    public RadialLayout(String group, float rootDistance, int size) {
        super(group);
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
                    w_dist = m_root_distance * 100 + 10;
                    r.setDouble("xp", (v.getX() + Math.cos(w_alfa) * w_dist));
                    r.setDouble("yp", (v.getY() + Math.sin(w_alfa) * w_dist));
                } else {
                    w_dist = v.getFloat("distance" + i) * 100 + 10;
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

} // end of class RadialTreeLayout

