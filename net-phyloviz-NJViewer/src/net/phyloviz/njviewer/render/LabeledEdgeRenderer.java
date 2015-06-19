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

	public LabeledEdgeRenderer() {
		super();
		m_key = "viz";
	}
	
	public LabeledEdgeRenderer(String key) {
		super();
		m_key = key;
	}
	
	@Override
	public void render(Graphics2D g, VisualItem item) {
		super.render(g, item);
		NodeItem u = ((EdgeItem) item).getSourceItem();	
		NodeItem v = ((EdgeItem) item).getTargetItem();	
		
		// Draw label...
		Shape shape = getShape(item);
		if ( shape == null ) return;

		double x = (u.getX() + v.getX())/2;
		double y = (u.getY() + v.getY())/2;
		Font df = FontLib.getFont("Tahoma", Font.PLAIN, 11);
		Color dc = g.getColor();

		Font mf = df.deriveFont(Font.BOLD);

                //insert "white shadow behind distance label"
		//g.setFont(mf);
		//g.setColor(Color.WHITE);
		//g.drawString(item.getString(m_key), (float)(x-0.5), (float)y);
		
		g.setFont(df);
		g.setColor(dc);
                float distance = item.getFloat(m_key);
		g.drawString(distance + "", (float)x, (float)y);
	}
}