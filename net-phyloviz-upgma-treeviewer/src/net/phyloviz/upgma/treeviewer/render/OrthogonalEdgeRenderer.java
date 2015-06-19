/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.upgma.treeviewer.render;

import java.awt.Shape;
import java.awt.geom.Path2D;
import prefuse.Constants;
import prefuse.render.EdgeRenderer;
import prefuse.visual.EdgeItem;
import prefuse.visual.VisualItem;

/**
 *
 * @author Marta Nascimento
 */
public class OrthogonalEdgeRenderer  extends EdgeRenderer {

        /**
         * Creates a new edge renderer with a given arrowType. The edgeType is
         * ignored -- the edges are drawn orthogonally, as per the
         * responsibility of this class. The arrowType is one of the
         * Constants.EDGE_ARROW_* values.
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
        public OrthogonalEdgeRenderer() {
            this(Constants.EDGE_TYPE_LINE, Constants.EDGE_ARROW_NONE);
        }

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
                
                result.moveTo(sx, sy);
                
                result.lineTo(sx, ty);
                result.lineTo(tx, ty);
            }

            return result;
        }
    }
