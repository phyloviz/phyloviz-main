/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.upgma.treeviewer.render;

import net.phyloviz.upgma.treeviewer.TreeView;
import prefuse.Constants;
import prefuse.action.layout.graph.NodeLinkTreeLayout;
import prefuse.data.Graph;
import prefuse.visual.NodeItem;

/**
 * <p>
 * TreeLayout that computes a tidy layout of a node-link tree diagram. This
 * algorithm lays out a rooted tree such that each depth level of the tree is on
 * a shared line. The orientation of the tree can be set such that the tree goes
 * left-to-right (default), right-to-left, top-to-bottom, or bottom-to-top.</p>
 *
 * <p>
 * The algorithm used is that of Christoph Buchheim, Michael Jünger, and
 * Sebastian Leipert from their research paper
 * <a href="http://citeseer.ist.psu.edu/buchheim02improving.html">
 * Improving Walker's Algorithm to Run in Linear Time</a>, Graph Drawing 2002.
 * This algorithm corrects performance issues in Walker's algorithm, which
 * generalizes Reingold and Tilford's method for tidy drawings of trees to
 * support trees with an arbitrary number of children at any given node.</p>
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class NodeLinkLayout extends NodeLinkTreeLayout {

    private NodeItem root;
    private int SCALE_X = 250;
    private int SCALE_Y = 40;
    private double maxDepth;
    private int orientation;
    private double MAX_Y;
    private TreeView tv;

    public NodeLinkLayout(TreeView tv, String group, int orientation, double dspace, double bspace, double tspace) {
        super(group, orientation, dspace, bspace, tspace);
        this.orientation = orientation;
        this.tv = tv;
    }

    public void setScaleX(int x) {
        SCALE_X = x;
    }

    public void setScaleY(int y) {
        SCALE_Y = y;
    }

    public int getScaleX() {
        return SCALE_X;
    }

    public int getScaleY() {
        return SCALE_Y;
    }

    @Override
    public void run(double frac) {
        Graph g = (Graph) m_vis.getGroup(m_group);
        initSchema(g.getNodes());

        root = getLayoutRoot();
        maxDepth = root.getDouble("distance");
//        if(tv.getRescaleEdges()){
//            maxDepth = Math.log(1 + maxDepth) * SCALE_X;
//        }
//        else
            maxDepth = maxDepth * SCALE_X;

        NodeItem n = (NodeItem) root.getFirstChild();
        n = (NodeItem) n.getNextSibling();
        NodeItem r = (NodeItem) n.getNextSibling();
        NodeItem r2 = (NodeItem) r.getFirstChild();
        setDepth(r, root, root.getBounds().getMinX());
        setDepth(r2, root, maxDepth);

        MAX_Y = Double.MIN_VALUE;

        secondWalk(root, null, false);

        setBreadth(r, root, MAX_Y + (SCALE_Y * 2));
        setBreadth(r2, root, MAX_Y + (SCALE_Y * 2));

    }

    private double secondWalk(NodeItem n, NodeItem p, boolean up) {

        NodeItem left = (NodeItem) n.getFirstChild();
        NodeItem right = (NodeItem) left.getNextSibling();

        if (left.getChildCount() == 0) { // is Leaf
            setDepth(left, n, maxDepth);
            int id = left.getInt("idx");
            if (MAX_Y < id * SCALE_Y) {
                MAX_Y = id * SCALE_Y;
            }
            setBreadth(left, p, id * SCALE_Y);
        } else {
            secondWalk(left, n, true);
        }

        if (right.getChildCount() == 0) { //is Leaf
            setDepth(right, n, maxDepth);
            int id = right.getInt("idx");
            if (MAX_Y < id * SCALE_Y) {
                MAX_Y = id * SCALE_Y;
            }
            setBreadth(right, p, id * SCALE_Y);
        } else {
            secondWalk(right, n, false);
        }

        double distance;
        double myDistance = n.getDouble("distance");
        if(tv.getRescaleEdges())
            myDistance = Math.log(1 + myDistance) * SCALE_X;
        else
            myDistance = myDistance * SCALE_X;

        distance = maxDepth - myDistance;

        setDepth(n, p, distance);
        double b = (left.getBounds().getMaxY() + right.getBounds().getMinY()) / 2; //(breadthL + breadthR) / 2;
        if (MAX_Y < b) {
            MAX_Y = b;
        }
        setBreadth(n, p, b);
        return b;
    }

    private void setBreadth(NodeItem n, NodeItem p, double b) {
        switch (orientation) {
            case Constants.ORIENT_LEFT_RIGHT:
            case Constants.ORIENT_RIGHT_LEFT:
                setY(n, p, b);
                break;
            case Constants.ORIENT_TOP_BOTTOM:
            case Constants.ORIENT_BOTTOM_TOP:
                setX(n, p, b);
                break;
            default:
                throw new IllegalStateException();
        }
    }

    private void setDepth(NodeItem n, NodeItem p, double d) {

        switch (orientation) {
            case Constants.ORIENT_LEFT_RIGHT:
                setX(n, p, d);
                break;
            case Constants.ORIENT_RIGHT_LEFT:
                setX(n, p, -d);
                break;
            case Constants.ORIENT_TOP_BOTTOM:
                setY(n, p, d);
                break;
            case Constants.ORIENT_BOTTOM_TOP:
                setY(n, p, -d);
                break;
            default:
                throw new IllegalStateException();
        }
    }
} // end of class NodeLinkLayout
