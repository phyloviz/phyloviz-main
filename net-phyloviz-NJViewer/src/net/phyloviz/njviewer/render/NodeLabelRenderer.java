package net.phyloviz.njviewer.render;

import java.awt.Color;
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

public class NodeLabelRenderer extends LabelRenderer {

    AffineTransform m_transform = new AffineTransform();

    public NodeLabelRenderer(String textField) {
        this.setTextField(textField);
    }

    @Override
    protected String getText(VisualItem item) {
        String s = null;
        if (item.canGetString(m_labelName)) {
            if (item.get("st_ref") instanceof NJLeafNode) {
                return "ST-" + item.getString("st_id");
            } else {
                return s;
            }
        }
        return s;
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

        // fill the shape, if requested
        int type = getRenderType(item);
        if (type == RENDER_TYPE_FILL || type == RENDER_TYPE_DRAW_AND_FILL) {
            // GraphicsLib.paint(g, item, shape, getStroke(item), RENDER_TYPE_FILL);
        }

        // now render the image and text
        String text = m_text;
        Image img = getImage(item);

        if (text == null && img == null) {
            return;
        }

        double size = item.getSize();
        boolean useInt = 1.5 > Math.max(g.getTransform().getScaleX(),
                g.getTransform().getScaleY());
        double x = shape.getBounds2D().getCenterX() + size * m_horizBorder;
        double y = shape.getBounds2D().getMinY() + size * m_vertBorder;

        // render image
        if (img != null) {
            double w = size * img.getWidth(null);
            double h = size * img.getHeight(null);
            double ix = x, iy = y;

            // determine one co-ordinate based on the image position
            switch (m_imagePos) {
                case Constants.LEFT:
                    x += w + size * m_imageMargin;
                    break;
                case Constants.RIGHT:
                    ix = shape.getBounds2D().getMaxX() - size * m_horizBorder - w;
                    break;
                case Constants.TOP:
                    y += h + size * m_imageMargin;
                    break;
                case Constants.BOTTOM:
                    iy = shape.getBounds2D().getMaxY() - size * m_vertBorder - h;
                    break;
                default:
                    throw new IllegalStateException(
                            "Unrecognized image alignment setting.");
            }

            // determine the other coordinate based on image alignment
            switch (m_imagePos) {
                case Constants.LEFT:
                case Constants.RIGHT:
                    // need to set image y-coordinate
                    switch (m_vImageAlign) {
                        case Constants.TOP:
                            break;
                        case Constants.BOTTOM:
                            iy = shape.getBounds2D().getMaxY() - size * m_vertBorder - h;
                            break;
                        case Constants.CENTER:
                            iy = shape.getBounds2D().getCenterY() - h / 2;
                            break;
                    }
                    break;
                case Constants.TOP:
                case Constants.BOTTOM:
                    // need to set image x-coordinate
                    switch (m_hImageAlign) {
                        case Constants.LEFT:
                            break;
                        case Constants.RIGHT:
                            ix = shape.getBounds2D().getMaxX() - size * m_horizBorder - w;
                            break;
                        case Constants.CENTER:
                            ix = shape.getBounds2D().getCenterX() - w / 2;
                            break;
                    }
                    break;
            }

            if (useInt && size == 1.0) {
                // if possible, use integer precision
                // results in faster, flicker-free image rendering
                //  g.drawImage(img, (int) ix, (int) iy, null);
            } else {
                m_transform.setTransform(size, 0, 0, size, ix, iy);
                // g.drawImage(img, m_transform, null);
            }
        }

        // render text
        int textColor = item.getTextColor();
        if (text != null && ColorLib.alpha(textColor) > 0) {
            g.setPaint(ColorLib.getColor(textColor));
            g.setFont(m_font);
            FontMetrics fm = DEFAULT_GRAPHICS.getFontMetrics(m_font);

            // compute available height
            double th;
            switch (m_imagePos) {
                case Constants.LEFT:
                case Constants.RIGHT:
                    th = shape.getBounds2D().getHeight() - 2 * size * m_vertBorder;
                    break;
                default:
                    th = m_textDim.height;
            }

            // compute starting y-coordinate
            y += fm.getAscent();
            y += th - m_textDim.height;

            FontRenderContext frc = g.getFontRenderContext();
            Rectangle2D strBounds = m_font.getStringBounds(text, frc);
            float width = (float) strBounds.getWidth();
            LineMetrics lm = m_font.getLineMetrics(text, frc);
            float height = lm.getAscent() + lm.getDescent();

            // Scale text into rect.
            float xScale = (float) shape.getBounds2D().getWidth() / width;
            float yScale = (float) shape.getBounds2D().getHeight() / height;
            float scale = (xScale > yScale) ? yScale : xScale;

            double px = item.getDouble("xp");
            double py = item.getDouble("yp");
            double tx = item.getDouble("x");
            double ty = item.getDouble("y");
            double deltaY = ty - py;
            double deltaX = tx - px;
            double theta = Math.atan2(deltaY, deltaX);

            double distance = 3;
            if ((theta > Math.PI / 2 && theta < 3 * Math.PI / 2)
                    || (theta < -Math.PI / 2 && theta > -3 * Math.PI / 2)) {
                theta = theta + Math.PI;
                double b = m_textDim.getWidth();
                distance = -b - 3;

            }

            x = x + (distance * Math.cos(theta));
            y = y + (distance * Math.sin(theta));

            AffineTransform at = AffineTransform.getTranslateInstance(x, y);
            at.scale(scale, scale);
            AffineTransform rotator = new AffineTransform();


            rotator.rotate(theta, x, y);
            rotator.concatenate(at);
            g.setFont(m_font.deriveFont(rotator));
            g.setPaint(Color.BLACK);

            g.drawString(text, 0, 0);

        }
    }

} // end of class LabelRenderer
