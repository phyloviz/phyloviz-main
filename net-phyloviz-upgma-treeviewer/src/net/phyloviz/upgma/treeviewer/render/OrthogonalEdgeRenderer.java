/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.upgma.treeviewer.render;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Path2D;
import net.phyloviz.upgma.treeviewer.TreeView;
import prefuse.Constants;
import prefuse.render.EdgeRenderer;
import prefuse.util.FontLib;
import prefuse.visual.EdgeItem;
import prefuse.visual.VisualItem;

/**
 *
 * @author Marta Nascimento
 */
public class OrthogonalEdgeRenderer extends EdgeRenderer {

    private TreeView tv;

    /**
     *
     * Creates a new edge renderer with a given arrowType. The edgeType is
     * ignored -- the edges are drawn orthogonally, as per the responsibility of
     * this class. The arrowType is one of the Constants.EDGE_ARROW_* values.
     *
     * @param edgeType Ignored.
     * @param arrowType One of Constants.EDGE_ARROW_*.
     * @see prefuse.Constants
     */
    public OrthogonalEdgeRenderer(int edgeType, int arrowType) {
        super(edgeType, arrowType);
    }

    /**
     * Creates a new instance with no arrow head.
     */
    public OrthogonalEdgeRenderer(TreeView tv) {
        this(Constants.EDGE_TYPE_LINE, Constants.EDGE_ARROW_NONE);
        this.tv = tv;
    }

//    public OrthogonalEdgeRenderer(double value) {
//        this(Constants.EDGE_TYPE_LINE, Constants.EDGE_ARROW_NONE);
//    }
    /**
     * Creates an orthogonal shape (an edge) to draw between two nodes.
     *
     * @param vi The visual item with start and end points.
     * @return The shape to draw between the nodes for this edge.
     */
    @Override
    protected Shape getRawShape(VisualItem vi) {
        
        Path2D.Double result = new Path2D.Double();
        
        if (vi instanceof EdgeItem) {
            EdgeItem edge = (EdgeItem) vi;
            VisualItem source = edge.getSourceItem();
            VisualItem target = edge.getTargetItem();

            double sx = source.getBounds().getMaxX();
            double sy = source.getBounds().getCenterY();
            double tx = target.getBounds().getMinX();
            double ty = target.getBounds().getCenterY();

            if (source.getBoolean("isRuler") && target.getBoolean("isRuler")) {
                
                result.moveTo(sx, ty);
                result.lineTo(tx, ty);

            }
            if (!source.getBoolean("isRuler") && target.getBoolean("isRuler")) {
                return result;

            } else {
                
                result.moveTo(sx, sy);
                result.lineTo(sx, ty);
                result.lineTo(tx, ty);
                result.lineTo(sx, ty);
                result.lineTo(sx, sy);
                
                
                
                edge.setDouble("distance", tx - sx);
            }
        }

        return result;
    }

    @Override
    public void render(Graphics2D g, VisualItem item) {
        super.render(g, item);
        if (item instanceof EdgeItem) {
            EdgeItem edge = (EdgeItem) item;
            VisualItem source = edge.getSourceItem();
            VisualItem target = edge.getTargetItem();

            double sx = source.getBounds().getMaxX();
            double sy = source.getBounds().getCenterY();
            double tx = target.getBounds().getMinX();
            double ty = target.getBounds().getY();

            if (source.getBoolean("isRuler") && target.getBoolean("isRuler")) {

                double scaleX = tv.getScaleX();
                double x = sx;
                double y = ty + 20;
                double distanceX = tx;
                String distance = String.format("%.2f", distanceX / scaleX);

                Font df = FontLib.getFont("Tahoma", Font.PLAIN, 11);
                Color dc = g.getColor();
                Font mf = df.deriveFont(Font.BOLD);
                g.setFont(mf);
                g.setColor(dc);

                int tickSpace = 4;
                double oneTick = (tx - sx) / ((double) tickSpace);
                for (int i = 0; i <= tickSpace; i++) {
                    Path2D.Double result = new Path2D.Double();
                    result.moveTo(x, sy);
                    result.lineTo(x, sy + 10);
                    result.lineTo(x, sy - 10);
                    g.draw(result);

                    g.drawString(distance, (float) (x), (float) y);
                    x += oneTick;
                    distanceX -= oneTick;
                    distance = String.format("%.2f", distanceX / scaleX);

                }
            }
        }
    }
}
