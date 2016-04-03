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
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import net.phyloviz.category.CategoryProvider;

/**
 *
 * @author Cátia vaz
 */
public class ChartLegend {

	private ColorsPanel colors;
	private PieChartPanel chartPie;
	private HashMap<Color, TooltipInfo> colorPercentageMap;

	public ChartLegend(Dimension d, CategoryProvider cp, int total) {
		colorPercentageMap = new HashMap<Color, TooltipInfo>();

		int sum = 0;
		Iterator<Entry<String, Integer>> giter = cp.getCategories().iterator();
		// Draw background pie...
		while (giter.hasNext()) {
			// Draw each pie slice

			Entry<String, Integer> entry = giter.next();
			String groupName = entry.getKey();
				sum += entry.getValue();
				Color c = cp.getCategoryColor(groupName);
				colorPercentageMap.put(c, new TooltipInfo((((float) entry.getValue()) / total), groupName));
		}
		colorPercentageMap.put(Color.LIGHT_GRAY, new TooltipInfo(((float) (total - sum)) / total, "others"));
		cp.putCategoryColor("others", Color.LIGHT_GRAY);

		chartPie = new PieChartPanel(d, cp, colorPercentageMap, total);
		colors = new ColorsPanel(chartPie, cp, colorPercentageMap);
	}

	protected PieChartPanel getChartPie() {
		return this.chartPie;
	}

	//get legends panel...
	protected ColorsPanel getLegends() {
		return colors;
	}

}
