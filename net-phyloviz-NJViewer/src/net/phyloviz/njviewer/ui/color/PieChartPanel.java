package net.phyloviz.njviewer.ui.color;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.JPanel;

public class PieChartPanel extends JPanel {

    private RenderingHints renderHints;
    private int total;
    private int popSize;
    private DrawingImage image;
    private ChartLegendRenderer chart;
    private HashMap<Color, TooltipInfo> tooltipMap;

    public int getTotal() {
        return total;
    }

    public int getPopulationSize() {
        return popSize;
    }

    public PieChartPanel(Dimension d, HashMap<Color, TooltipInfo> tooltipMap, int total) {
        this.total = total;
        this.tooltipMap = tooltipMap;
        int w = 3 * (int) d.getWidth() / 2;
        int h = (int) d.getHeight();
        image = new DrawingImage(w, h);
        renderHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        renderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        chart = new ChartLegendRenderer(total);
        //this.addMouseMotionListener(new MyMouseListener(this));
        this.addComponentListener(new MyComponentAdapter(this));
        revalidate();
    }

    private void setDrawingImage(DrawingImage newImage) {
        image = newImage;
    }

    protected ChartLegendRenderer getRenderer() {
        return chart;
    }

    private class MyComponentAdapter extends ComponentAdapter {

        PieChartPanel pie;

        public MyComponentAdapter(PieChartPanel pie) {
            this.pie = pie;
        }

        @Override
        public void componentResized(ComponentEvent e) {
            Dimension d = pie.getSize();
            DrawingImage i = new DrawingImage(d.width, d.height);
            pie.setDrawingImage(i);
            pie.repaint();
        }
    }

//    private class MyMouseListener extends MouseMotionAdapter {
//
//        private PieChartPanel pie;
//
//        public MyMouseListener(PieChartPanel pie) {
//            this.pie = pie;
//        }
//
//        @Override
//        public void mouseMoved(MouseEvent e) {
//            Point p = e.getPoint();
//            if (p.x < image.image.getWidth() && p.y < image.image.getHeight() & p.x > 0 && p.y > 0) {
//                Color c = new Color(image.image.getRGB(p.x, p.y));
//                if (chart != null) {
//                    TooltipInfo pp = tooltipMap.get(c);
//                    if (pp != null) {
//                        pie.setToolTipText(pp.getName() + "  " + pp.getPercentage() + "%");
//						//pie.getToolTipLocation(e);
//                        //pie.update(pie.getGraphics());
//                    } else {
//                        pie.setToolTipText("");
//                    }
//                }
//            }
//        }
//    }
//
//    @Override
//    public Point getToolTipLocation(MouseEvent e) {
//        return new Point(e.getX() + 10, e.getY() + 10);
//    }
//
//    protected ChartLegendRenderer getChartLegendRenderer() {
//        return chart;
//    }
//
    @Override
    public void paint(Graphics g) {
        int largura = this.getVisibleRect().width;
        int altura = this.getVisibleRect().height;
        Graphics gg = image.image.getGraphics();
        gg.setColor(Color.WHITE);
        gg.fillRect(0, 0, largura, altura);
        int minimo = Math.min(largura, altura);
        Rectangle rec = new Rectangle(20 + (largura - minimo) / 2, 20 + (altura - minimo) / 2, minimo - 40, minimo - 40);
        Graphics2D g2d = (Graphics2D) gg;
        g2d.setRenderingHints(renderHints);
        chart.drawPie(g2d, rec, tooltipMap.keySet().iterator());
        g.drawImage(image.image, 0, 0, this);
    }
//

    protected class ChartLegendRenderer extends Component {

        private int total;

        public ChartLegendRenderer(int total) {
            this.total = total;
        }

        public void drawPie(Graphics2D g, Rectangle area, Iterator<Color> cIt) {

            g.setColor(Color.WHITE);
            g.fillRect(area.x, area.y, area.width, area.height);
            // Draw each pie slice
            double currAngle = 0;
            double arcAngle = 0;
            Arc2D arc = null;
            for (int i = 0; i < total; i++) {
                arcAngle = (((double) 1 / total) * 360);
                arc = new Arc2D.Double(area.x + 2, area.y + 2, area.width - 4, area.height - 4, 90 - currAngle, -arcAngle, Arc2D.PIE);

                g.setColor(cIt.next());

                g.fill(arc);
                currAngle += arcAngle;
            }

        }
    }

    private class DrawingImage {

        protected BufferedImage image;
        protected Graphics2D imgGraphics;

        public DrawingImage(int w, int h) {
            if (h <= 0) {
                h = 1;
            }
            if (w <= 0) {
                w = 1;
            }
            image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            imgGraphics = image.createGraphics();
        }
    }
}
