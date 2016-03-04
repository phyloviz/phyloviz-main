/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
