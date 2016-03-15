package net.phyloviz.njviewer.render;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import prefuse.render.EdgeRenderer;
import prefuse.util.FontLib;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

public class LabeledEdgeRenderer extends EdgeRenderer {

	private final String m_key;
        
        private boolean round = false;

	public LabeledEdgeRenderer() {
		super();
		m_key = "viz";
	}
        
        public LabeledEdgeRenderer(String key) {
		super();
		m_key = key;
	}
	
	public LabeledEdgeRenderer(String key, boolean round) {
		super();
		m_key = key;
                this.round = round;
	}
	
	@Override
	public void render(Graphics2D g, VisualItem item) {
		super.render(g, item);
		NodeItem u = ((EdgeItem) item).getSourceItem();	
		NodeItem v = ((EdgeItem) item).getTargetItem();	
		
                double ux = u.getX();//u.getDouble("x");
                double uy = u.getY();//u.getDouble("y");
                double vx = v.getX();//v.getDouble("x");
                double vy = v.getY();//v.getDouble("y");
		
                // Draw label...
		Shape shape = getShape(item);
		if ( shape == null ) return;

		double x = (ux + vx)/2;
		double y = (uy + vy)/2;
		Font df = FontLib.getFont("Tahoma", Font.PLAIN, 11);
		Color dc = g.getColor();
		
		g.setFont(df);
		g.setColor(dc);
                float distance = item.getFloat(m_key);
                String distanceString = null;
                if(round)
                    distanceString = String.format("%.3f", distance);
                else distanceString = distance + "";
		g.drawString(distanceString, (float)x, (float)y);
	}

    public void setRoundDistance(boolean setRoundTo) {
        round = setRoundTo;
    }
}