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

package net.phyloviz.njviewer.render;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import net.phyloviz.njviewer.action.control.JRadialViewControlPanel;
import net.phyloviz.njviewer.ui.GraphView;
import net.phyloviz.upgmanjcore.visualization.GView;
import prefuse.render.EdgeRenderer;
import prefuse.util.FontLib;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

public class LabeledRadialEdgeRenderer extends RadialEdgeRenderer {

    private final String m_key;
    private boolean round = false;
    private final GView gv;

    public LabeledRadialEdgeRenderer(GView gv, String key, boolean round) {
        super();
        this.gv = gv;
        m_key = key;
        this.round = round;
    }

    @Override
    public void render(Graphics2D g, VisualItem item) {
        super.render(g, item);
        NodeItem u = ((EdgeItem) item).getSourceItem();
        NodeItem v = ((EdgeItem) item).getTargetItem();

        double ux = u.getDouble("x");
        double uy = u.getDouble("y");
        double vx = v.getDouble("x");
        double vy = v.getDouble("y");

        // Draw label...
        Shape shape = getShape(item);
        if (shape == null) {
            return;
        }

        double x = (ux + vx) / 2;
        double y = (uy + vy) / 2;
        Font df = FontLib.getFont("Tahoma", Font.PLAIN, ((GraphView) gv).getRadialControlViewInfo(JRadialViewControlPanel.FONT));
        Color dc = g.getColor();

        g.setFont(df);
        g.setColor(dc);
        float distance = item.getFloat(m_key);
        String distanceString = null;
        if (round) {
            distanceString = String.format("%.3f", distance);
        } else {
            distanceString = distance + "";
        }
        g.drawString(distanceString, (float) x, (float) y);
    }

    public void setRoundDistance(boolean setRoundTo) {
        round = setRoundTo;
    }
}
