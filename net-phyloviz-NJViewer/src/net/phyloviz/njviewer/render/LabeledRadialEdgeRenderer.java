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
