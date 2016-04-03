/*-
 * Copyright (c) 2016, PHYLOViZ Team <phyloviz@gmail.com>
 * All rights reserved.
 * 
 * This file is part of PHYLOViZ <http://www.phyloviz.net/>.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.phyloviz.category.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.swing.JPanel;
import net.phyloviz.category.CategoryProvider;

public class PieChartPanel extends JPanel {

	private CategoryProvider cp;
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

	public PieChartPanel(Dimension d, CategoryProvider cp, HashMap<Color, TooltipInfo> tooltipMap, int total) {
		this.cp = cp;
		this.total = total;
		this.tooltipMap = tooltipMap;
		int w = 3 * (int) d.getWidth() / 2;
		int h = (int) d.getHeight();
		image = new DrawingImage(w, h);
		renderHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		renderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		chart = new ChartLegendRenderer(cp, total);
		this.addMouseMotionListener(new MyMouseListener(this));
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

	private class MyMouseListener extends MouseMotionAdapter {

		private PieChartPanel pie;

		public MyMouseListener(PieChartPanel pie) {
			this.pie = pie;
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			Point p = e.getPoint();
			if (p.x < image.image.getWidth() && p.y < image.image.getHeight() & p.x > 0 && p.y > 0) {
				Color c = new Color(image.image.getRGB(p.x, p.y));
				if (chart != null) {
					TooltipInfo pp = tooltipMap.get(c);
					if (pp != null) {
						pie.setToolTipText(pp.getName() + "  " + pp.getPercentage() + "%");
						//pie.getToolTipLocation(e);
						//pie.update(pie.getGraphics());
					} else {
						pie.setToolTipText("");
					}
				}
			}
		}
	}

	@Override
	public Point getToolTipLocation(MouseEvent e) {
		return new Point(e.getX() + 10, e.getY() + 10);
	}

	public CategoryProvider getCategoryProvider() {
		return cp;
	}

	protected ChartLegendRenderer getChartLegendRenderer() {
		return chart;
	}

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
		chart.drawPie(g2d, rec, Color.yellow);
		g.drawImage(image.image, 0, 0, this);
	}

	protected class ChartLegendRenderer extends Component {

		private int total;
		private CategoryProvider cp;

		public ChartLegendRenderer(CategoryProvider cp, int total) {
			this.total = total;
			this.cp = cp;
		}

		public void drawPie(Graphics2D g, Rectangle area, Color fillColor) {
			// Get total value of all slices
			int sum = 0;
			Iterator<Entry<String, Integer>> giter = cp.getCategories().iterator();
			// Draw background pie...
			g.setColor(fillColor);
			g.fillArc(area.x, area.y, area.width, area.height, 0, 360);
			double currAngle = 0;
			double arcAngle = 0;
			Arc2D arc = null;
			while (giter.hasNext()) {
				// Draw each pie slice
				Entry<String, Integer> entry = giter.next();
				String groupName = entry.getKey();
				if (g != null) {
					sum += entry.getValue();
					arcAngle = ((((double) entry.getValue()) / total) * 360);
					arc = new Arc2D.Double(area.x + 2, area.y + 2, area.width - 4, area.height - 4, 90 - currAngle, -arcAngle, Arc2D.PIE);
					Color c = cp.getCategoryColor(groupName);
					g.setColor(c);
					//   g.fillArc(area.x + 2, area.y + 2, area.width - 4, area.height - 4, 90 - currAngle, -arcAngle);
					g.fill(arc);
					currAngle += arcAngle;
				}
			}
			arcAngle = Math.round((((float) (total - sum)) / total) * 360);
			if (arcAngle > 0) {
				// Ensure that rounding errors do not leave a gap between the first and last slice
				arcAngle = 360 - currAngle;
				// Set color and draw 'others' slice
				g.setColor(Color.LIGHT_GRAY);
				//g.fillArc(area.x + 2, area.y + 2, area.width - 4, area.height - 4, 90 - currAngle, - arcAngle);
				//TODO: tem que se instanciar os others que s??o os isolados que n??o t??m profile....
				arc = new Arc2D.Double(area.x + 2, area.y + 2, area.width - 4, area.height - 4, 90 - currAngle, -arcAngle, Arc2D.PIE);
				g.fill(arc);
			}
			/*  int others = total - sum;
			arcAngle = Math.round((((float) (others)) / total * 360));
			System.out.println("angle" + arcAngle + "others" + others);
			g.setColor(Color.LIGHT_GRAY);
			colorPercentageMap.put(Color.LIGHT_GRAY, new TooltipInfo(((float) (others)) / total, "others"));
			g.fillArc(area.x + 2, area.y + 2, area.width - 4, area.height - 4, 90 - currAngle, -arcAngle);
			 */
			//int x = (int) area.getCenterX();
			//int y = (int) area.getCenterY();
			g.setPaint(new Color(255, 255, 255));
			//Font font = FontLib.getFont("Tahoma", Font.PLAIN, 11 /*+ 5*Math.log(freq)*/);
			//g.setFont(font);
		}
	}

	private class DrawingImage {

		protected BufferedImage image;
		protected Graphics2D imgGraphics;

		public DrawingImage(int w, int h) {
			if (h <= 0) h = 1;
			if (w <= 0) w = 1;
			image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			imgGraphics = image.createGraphics();
		}
	}
}
