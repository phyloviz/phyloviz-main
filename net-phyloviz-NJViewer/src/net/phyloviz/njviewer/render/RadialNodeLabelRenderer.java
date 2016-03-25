package net.phyloviz.njviewer.render;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import net.phyloviz.njviewer.action.control.JRadialViewControlPanel;
import net.phyloviz.njviewer.ui.GraphView;
import net.phyloviz.upgmanjcore.visualization.GView;
import static prefuse.render.Renderer.DEFAULT_GRAPHICS;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.StringLib;
import prefuse.visual.VisualItem;

public class RadialNodeLabelRenderer extends NodeLabelRenderer {

    public static int DEFAULT_FONT_SIZE = 5;

    AffineTransform m_transform = new AffineTransform();
//    private int fontSize = DEFAULT_FONT_SIZE;
    private final GView gv;

    public RadialNodeLabelRenderer(GView gv, String textField) {
        super(textField);
        this.gv = gv;
        this.setTextField(textField);
    }

    // Rendering
    private String computeTextDimensions(VisualItem item, String text,
            double size) {
        // put item font in temp member variable
        m_font = item.getFont();
        // scale the font as needed
        if (size != 1) {
            m_font = FontLib.getFont(m_font.getName(), m_font.getStyle(),
                    size * m_font.getSize());
        }

        FontMetrics fm = DEFAULT_GRAPHICS.getFontMetrics(m_font);
        StringBuffer str = null;

        // compute the number of lines and the maximum width
        int nlines = 1, w = 0, start = 0, end = text.indexOf(m_delim);
        m_textDim.width = 0;
        String line;
        for (; end >= 0; ++nlines) {
            w = fm.stringWidth(line = text.substring(start, end));
            // abbreviate line as needed
            if (m_maxTextWidth > -1 && w > m_maxTextWidth) {
                if (str == null) {
                    str = new StringBuffer(text.substring(0, start));
                }
                str.append(StringLib.abbreviate(line, fm, m_maxTextWidth));
                str.append(m_delim);
                w = m_maxTextWidth;
            } else if (str != null) {
                str.append(line).append(m_delim);
            }
            // update maximum width and substring indices
            m_textDim.width = Math.max(m_textDim.width, w);
            start = end + 1;
            end = text.indexOf(m_delim, start);
        }
        w = fm.stringWidth(line = text.substring(start));
        // abbreviate line as needed
        if (m_maxTextWidth > -1 && w > m_maxTextWidth) {
            if (str == null) {
                str = new StringBuffer(text.substring(0, start));
            }
            str.append(StringLib.abbreviate(line, fm, m_maxTextWidth));
            w = m_maxTextWidth;
        } else if (str != null) {
            str.append(line);
        }
        // update maximum width
        m_textDim.width = Math.max(m_textDim.width, w);

        // compute the text height
        m_textDim.height = fm.getHeight() * nlines;

        return str == null ? text : str.toString();
    }

    @Override
    protected Shape getRawShape(VisualItem item) {
        m_text = getText(item);
        double size = item.getSize();

        if (m_text != null) {
            m_text = computeTextDimensions(item, m_text, size);
        } else {
            return new Rectangle( (int)item.getX(), (int)item.getY(), 0, 0);
        }
        double px = item.getDouble("xp");
        double py = item.getDouble("yp");
        double x = item.getDouble("x");
        double y = item.getDouble("y");

        double deltaY = y - py;
        double deltaX = x - px;
        double theta = Math.atan2(deltaY, deltaX);

        double w = m_textDim.getWidth();
        double h = m_textDim.getHeight();

        double distance = (w / 2) + 4;

        x = x + (distance * Math.cos(theta));
        y = y + (distance * Math.sin(theta));

        if ((theta > Math.PI / 2 && theta < 3 * Math.PI / 2)
                || (theta < -Math.PI / 2 && theta > -3 * Math.PI / 2)) {

            theta = theta + Math.PI;

        }

        AffineTransform rotator = new AffineTransform();
        Rectangle2D.Double shape = new Rectangle2D.Double(x - (w / 2), y - (h / 2), w, h);
        rotator.rotate(theta, shape.getCenterX(), shape.getCenterY());
        return rotator.createTransformedShape(shape);

    }

    @Override
    public void render(Graphics2D g, VisualItem item) {
        Shape shape = (Shape) getShape(item);
        if (shape == null) {
            return;
        }

        String text = m_text;
        m_font = new Font("Tahoma", Font.PLAIN, ((GraphView) gv).getRadialControlViewInfo(JRadialViewControlPanel.FONT));

        // render text
        int textColor = item.getTextColor();
        if (text != null && ColorLib.alpha(textColor) > 0) {
            g.setPaint(ColorLib.getColor(textColor));
            g.setFont(m_font);

            double px = item.getDouble("xp");
            double py = item.getDouble("yp");
            double x = item.getDouble("x");
            double y = item.getDouble("y");

            double deltaY = y - py;
            double deltaX = x - px;
            double theta = Math.atan2(deltaY, deltaX);

            double w = m_textDim.getWidth();
            double h = m_textDim.getHeight();

            double distance = (w / 2) + 4;

            x = x + (distance * Math.cos(theta));
            y = y + (distance * Math.sin(theta));

            if ((theta > Math.PI / 2 && theta < 3 * Math.PI / 2)
                    || (theta < -Math.PI / 2 && theta > -3 * Math.PI / 2)) {

                theta = theta + Math.PI;

            }

            g.setPaint(Color.BLACK);

            g.rotate(theta, x, y);
            g.drawString(text, (float) (x - (w / 2)), (float) (y + (h / 4)));
            g.rotate(-theta, x, y);

        }
    }

//    private int getFontSize() {
//        return fontSize;
//    }
//    public void setFontSize(int value) {
//        fontSize = value;
//    }
} // end of class LabelRenderer
