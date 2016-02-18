package net.phyloviz.njviewer.render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.util.Iterator;
import java.util.List;
import net.phyloviz.category.CategoryProvider;
import net.phyloviz.category.filter.Category;
import net.phyloviz.core.data.Profile;
import net.phyloviz.nj.tree.NJLeafNode;
import net.phyloviz.nj.tree.NJUnionNode;
import net.phyloviz.upgmanjcore.visualization.GView;

import prefuse.render.AbstractShapeRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.visual.VisualItem;

public class BarChartRenderer extends AbstractShapeRenderer {

    private GView gv;
    private CategoryProvider cp;

    public BarChartRenderer(CategoryProvider cp, GView gv) {
        this.cp = cp;
        this.gv = gv;
    }

    public void drawBar(Graphics2D g, Shape area, VisualItem item, String id, int freq, Color fillColor) {

        String stId = String.valueOf(id);

        List<Category> glst = cp.getCategories(stId);
        Iterator<Category> giter;

        AffineTransform rotator = new AffineTransform();

        double px = item.getDouble("xp");
        double py = item.getDouble("yp");
        double tx = item.getDouble("x");
        double ty = item.getDouble("y");
        double deltaY = ty - py;
        double deltaX = tx - px;
        double theta = Math.atan2(deltaY, deltaX);

        Rectangle2D pos = area.getBounds().getBounds2D();
        rotator.rotate(-theta, pos.getCenterX(), pos.getCenterY());
        area = rotator.createTransformedShape(area);
        pos = area.getBounds().getBounds2D();

        rotator = new AffineTransform();
        rotator.rotate(theta, pos.getCenterX(), pos.getCenterY());

        g.setColor(fillColor);
        int currAngle = Math.round((float) area.getBounds2D().getMinX());
        Color groupColor = fillColor;
        if (glst != null) {
            giter = glst.iterator();
            while (giter.hasNext()) {
                // Compute the start and stop angles
                Category group = giter.next();
                float groupPercentage = group.weight() * 100.0f / freq;

                int x_pos = Math.round(groupPercentage * ((float) area.getBounds2D().getWidth()) / 100);
                int newWidth = currAngle + x_pos;
                if (newWidth > area.getBounds2D().getMaxX()) {
                    x_pos -= (newWidth - Math.round((float) area.getBounds2D().getMaxX()));
                }
                groupColor = cp.getCategoryColor(group.getName());
                g.setColor(groupColor);

                if (currAngle + x_pos >= area.getBounds2D().getMaxX()) {
                    x_pos = (Math.round((float) area.getBounds2D().getMaxX())) - currAngle;
                }
                Rectangle2D.Double shap = new Rectangle2D.Double(currAngle, area.getBounds2D().getY(), x_pos, area.getBounds2D().getHeight());
                GeneralPath rect2 = new GeneralPath(rotator.createTransformedShape(shap));
                g.setColor(groupColor);
                //g.draw(rect2);
                g.fill(rect2);

                currAngle += x_pos;
            }
        }
        if (currAngle < area.getBounds2D().getMaxX()) {
            g.setColor(groupColor);
            Rectangle2D.Double shap = new Rectangle2D.Double(currAngle, area.getBounds2D().getY(), Math.round((float) area.getBounds2D().getMaxX()) - currAngle, area.getBounds2D().getHeight());
            GeneralPath rect2 = new GeneralPath(rotator.createTransformedShape(shap));
            g.fill(rect2);
        }
        String text = "ST-" + stId;
        Font m_font = FontLib.getFont("Tahoma", Font.PLAIN, 12);
        FontRenderContext frc = g.getFontRenderContext();
        float width = (float) m_font.getStringBounds(text, frc).getWidth();

        double distance = 3;
        if ((theta > Math.PI / 2 && theta < 3 * Math.PI / 2)
                || (theta < -Math.PI / 2 && theta > -3 * Math.PI / 2)) {
            theta = theta + Math.PI;
            distance = -pos.getBounds2D().getWidth()-width-3;
        }
        double xpos = pos.getX() + pos.getWidth() + distance;
        AffineTransform at = AffineTransform.getTranslateInstance(xpos, pos.getMaxY());
        rotator = new AffineTransform();
        rotator.rotate(theta, pos.getBounds2D().getCenterX(), pos.getBounds2D().getCenterY());
        rotator.concatenate(at);
        g.setFont(m_font.deriveFont(rotator));
        g.setPaint(Color.BLACK);
        g.drawString(text, 0, 0);

    }

    @Override
    protected Shape getRawShape(VisualItem item) {
        int x = (int) item.getX();
        int y = (int) item.getY();

        Profile st = (Profile) item.getSourceTuple().get("st_ref");
        if (st == null || st instanceof NJUnionNode) {
            return new Rectangle(x, y, 0, 0);
        }

        int offsetX = (int) (gv.getLinearSize() ? 12 * st.getFreq() : (12 * Math.log(1 + st.getFreq())));
        int offsetY = (int) (20);

        int w = 50 + offsetX;
        int h = offsetY - 15;

        double px = item.getDouble("xp");
        double py = item.getDouble("yp");
        double tx = item.getDouble("x");
        double ty = item.getDouble("y");
        double deltaY = ty - py;
        double deltaX = tx - px;
        double theta = Math.atan2(deltaY, deltaX);

        double distance = (w / 2) + 4;
        double xpos = tx + (distance * Math.cos(theta));
        double ypos = ty + (distance * Math.sin(theta));

        AffineTransform rotator = new AffineTransform();
        Rectangle shape = new Rectangle((int) xpos - (w / 2), (int) ypos - (h / 2), w, h);
        rotator.rotate(theta, shape.getCenterX(), shape.getCenterY());
        return rotator.createTransformedShape(shape);

    }

    @Override
    protected void drawShape(Graphics2D g, VisualItem item, Shape shape
    ) {

        Color fillColor = ColorLib.getColor(item.getFillColor());

        if (shape instanceof Path2D) {
            Profile profile = (Profile) item.get("st_ref");
            if (profile instanceof NJLeafNode) {
                String stId = item.getString("st_id");
                int freq = profile.getFreq();
                drawBar(g, shape, item, stId, freq, fillColor);
            }
        }
    }
}
