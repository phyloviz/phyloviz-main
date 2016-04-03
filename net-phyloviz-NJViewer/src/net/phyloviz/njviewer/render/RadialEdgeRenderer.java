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

import java.awt.Shape;
import java.awt.geom.Path2D;
import prefuse.render.EdgeRenderer;
import prefuse.visual.EdgeItem;
import prefuse.visual.VisualItem;

/**
 *
 * @author Marta Nascimento
 */
public class RadialEdgeRenderer extends EdgeRenderer {

    @Override
    protected Shape getRawShape(VisualItem vi) {

        Path2D.Double result = new Path2D.Double();
        if (vi instanceof EdgeItem) {
            EdgeItem edge = (EdgeItem) vi;
            VisualItem source = edge.getSourceItem();
            VisualItem target = edge.getTargetItem();

            double sx = source.getDouble("x");
            double sy = source.getDouble("y");
            double tx = target.getDouble("x");
            double ty = target.getDouble("y");

            result.moveTo(sx, sy);
            result.lineTo(tx, ty);
        }

        return result;
    }
}
