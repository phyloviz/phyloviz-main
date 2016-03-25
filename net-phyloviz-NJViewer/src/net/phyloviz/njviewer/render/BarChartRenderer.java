package net.phyloviz.njviewer.render;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;
import net.phyloviz.category.CategoryProvider;
import net.phyloviz.category.filter.Category;
import net.phyloviz.core.data.Profile;
import net.phyloviz.nj.tree.NJLeafNode;
import net.phyloviz.nj.tree.NJUnionNode;
import net.phyloviz.njviewer.action.control.JRadialViewControlPanel;
import net.phyloviz.njviewer.ui.GraphView;
import net.phyloviz.upgmanjcore.visualization.GView;

import prefuse.render.AbstractShapeRenderer;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;

public class BarChartRenderer extends AbstractShapeRenderer {

    public static int DEFAULT_WIDTH = 50;
    public static int DEFAULT_HEIGHT = 5;
    public static int DEFAULT_FONT_SIZE = 5;

    private GView gv;
    private CategoryProvider cp;
    private int widthBar = DEFAULT_WIDTH;
    private int heightBar = DEFAULT_HEIGHT;
//    private int fontSize = DEFAULT_FONT_SIZE;

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

        Rectangle2D pos = getBar(item, 0, 0);

        rotator = new AffineTransform();
        rotator.rotate(theta, pos.getCenterX(), pos.getCenterY());

        g.setColor(fillColor);
        int currAngle = Math.round((float) pos.getBounds2D().getMinX());
        Color groupColor = fillColor;
        if (glst != null) {
            giter = glst.iterator();
            while (giter.hasNext()) {
                // Compute the start and stop angles
                Category group = giter.next();
                float groupPercentage = group.weight() * 100.0f / freq;

                int x_pos = Math.round(groupPercentage * ((float) pos.getBounds2D().getWidth()) / 100);
                int newWidth = currAngle + x_pos;
                if (newWidth > pos.getBounds2D().getMaxX()) {
                    x_pos -= (newWidth - Math.round((float) pos.getBounds2D().getMaxX()));
                }
                groupColor = cp.getCategoryColor(group.getName());
                g.setColor(groupColor);

                if (currAngle + x_pos >= pos.getBounds2D().getMaxX()) {
                    x_pos = (Math.round((float) pos.getBounds2D().getMaxX())) - currAngle;
                }
                Rectangle2D.Double shap = new Rectangle2D.Double(currAngle, pos.getBounds2D().getY(), x_pos, pos.getBounds2D().getHeight());
                GeneralPath rect2 = new GeneralPath(rotator.createTransformedShape(shap));
                g.setColor(groupColor);
                g.fill(rect2);

                currAngle += x_pos;
            }
        }
        if (currAngle < pos.getBounds2D().getMaxX()) {
            g.setColor(groupColor);
            Rectangle2D.Double shap = new Rectangle2D.Double(currAngle, pos.getBounds2D().getY(), Math.round((float) pos.getBounds2D().getMaxX()) - currAngle, pos.getBounds2D().getHeight());
            GeneralPath rect2 = new GeneralPath(rotator.createTransformedShape(shap));
            g.fill(rect2);
        }
        String text = stId;
        Font m_font = new Font("Tahoma", Font.PLAIN, ((GraphView) gv).getRadialControlViewInfo(JRadialViewControlPanel.FONT));
        g.setFont(m_font);
        FontRenderContext frc = g.getFontRenderContext();
        double width = m_font.getStringBounds(text, frc).getWidth();
        double height = m_font.getStringBounds(text, frc).getHeight();
        double barWidth = pos.getWidth();
        double distance = (barWidth / 2) + (width / 2) + 4;

        double x = pos.getCenterX() + (distance * Math.cos(theta));
        double y = pos.getCenterY() + (distance * Math.sin(theta));

        if ((theta > Math.PI / 2 && theta < 3 * Math.PI / 2)
                || (theta < -Math.PI / 2 && theta > -3 * Math.PI / 2)) {

            theta = theta + Math.PI;

        }

        g.setPaint(Color.BLACK);

        g.rotate(theta, x, y);
        g.drawString(text, (float) (x - (width / 2)), (float) (y + (height / 4)));
        g.rotate(-theta, x, y);
    }

    @Override
    protected Shape getRawShape(VisualItem item) {
        double px = item.getDouble("xp");
        double py = item.getDouble("yp");
        double x = item.getDouble("x");
        double y = item.getDouble("y");

        Profile st = (Profile) item.getSourceTuple().get("st_ref");
        if (st == null || st instanceof NJUnionNode) {
            return new Rectangle((int) x, (int) y, 0, 0);
        }
        double deltaY = y - py;
        double deltaX = x - px;
        double theta = Math.atan2(deltaY, deltaX);

        double w = getWidthBarControlValue();

        AffineTransform rotator = new AffineTransform();
        Rectangle2D.Double shape = getBar(item, w, 0);
        rotator.rotate(theta, shape.getCenterX(), shape.getCenterY());
        return rotator.createTransformedShape(shape);
    }

    public Rectangle2D.Double getBar(VisualItem item, double width, double height) {
        double px = item.getDouble("xp");
        double py = item.getDouble("yp");
        double x = item.getDouble("x");
        double y = item.getDouble("y");
        
        Profile st = (Profile) item.getSourceTuple().get("st_ref");
        int offsetX = (int) (gv.getLinearSize() ? 12 * st.getFreq() : (12 * Math.log(1 + st.getFreq())));
        
        double w = getWidthBarControlValue() + offsetX + width;
        double h = getHeightBarControlValue() + height;

        double deltaY = y - py;
        double deltaX = x - px;
        double theta = Math.atan2(deltaY, deltaX);

        double distance = (w / 2) + 4;
        x = x + (distance * Math.cos(theta));
        y = y + (distance * Math.sin(theta));

        Rectangle2D.Double shape = new Rectangle2D.Double(x - (w / 2), y - (h / 2), w, h);
        return shape;
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
//                g.draw(shape);
                drawBar(g, shape, item, stId, freq, fillColor);
            }
        }
    }

    private int getWidthBarControlValue() {
        return ((GraphView) gv).getRadialControlViewInfo(JRadialViewControlPanel.WIDTH);
    }

    private int getHeightBarControlValue() {
        return ((GraphView) gv).getRadialControlViewInfo(JRadialViewControlPanel.HEIGHT);
    }

//    public void setWidth(int value) {
//        widthBar = value;
//    }
//
//    public void setHeight(int value) {
//        heightBar = value;
//    }
//    private int getFontSize() {
//        return fontSize;
//    }
//    public void setFontSize(int value) {
//        fontSize = value;
//    }
}
