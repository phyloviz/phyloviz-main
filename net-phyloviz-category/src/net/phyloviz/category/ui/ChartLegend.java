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
