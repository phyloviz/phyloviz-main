package net.phyloviz.njviewer.render;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import net.phyloviz.nj.tree.NJLeafNode;

import prefuse.Constants;
import static prefuse.render.AbstractShapeRenderer.RENDER_TYPE_DRAW_AND_FILL;
import static prefuse.render.AbstractShapeRenderer.RENDER_TYPE_FILL;
import prefuse.render.LabelRenderer;
import static prefuse.render.Renderer.DEFAULT_GRAPHICS;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.StringLib;
import prefuse.visual.VisualItem;

public class RadialNodeLabelRenderer extends NodeLabelRenderer {

    AffineTransform m_transform = new AffineTransform();

    public RadialNodeLabelRenderer(String textField) {
        super(textField);
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
    public void render(Graphics2D g, VisualItem item) {
        Shape shape = (Shape) getShape(item);
        if (shape == null) {
            return;
        }

        String text = m_text;
        m_font = new Font("Tahoma", Font.PLAIN, 5);

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
            
            double distance = (w/2) + 4;
            
            x = x + (distance * Math.cos(theta));
            y = y + (distance * Math.sin(theta));

            if ((theta > Math.PI / 2 && theta < 3 * Math.PI / 2)
                    || (theta < -Math.PI / 2 && theta > -3 * Math.PI / 2)) {

                theta = theta + Math.PI;

            }

            g.setPaint(Color.BLACK);
            
            AffineTransform orig = g.getTransform();
            g.rotate(theta, x, y);

            g.drawString(text, (float) (x - (w/2)), (float) (y+(h/4)));
            g.setTransform(orig);
            

        }
    }

} // end of class LabelRenderer
