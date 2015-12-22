package net.phyloviz.njviewer.ui.color;

import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;

/**
 *
 * @author Cátia vaz
 */
public class ChartLegend {

    private ColorsPanel colors;
    private PieChartPanel chartPie;
    private HashMap<Color, TooltipInfo> colorPercentageMap;

    public ChartLegend(Dimension d, int total, Color[] c, double maxEdgeDiff) {
        colorPercentageMap = new HashMap<Color, TooltipInfo>();

        // Draw background pie...
        double min = maxEdgeDiff / total;
        double sum = min;
        for (int i = 0; i < total; i++) {
            colorPercentageMap.put(c[i], new TooltipInfo((((float) 1) / total), String.format((i != 0 ? "]":"[")+"%.3f, %.3f]", sum, sum + min)));
            sum += min;
        }

        chartPie = new PieChartPanel(d, colorPercentageMap, total);
        colors = new ColorsPanel(chartPie, total, c, colorPercentageMap);
    }

    protected PieChartPanel getChartPie() {
        return this.chartPie;
    }

    //get legends panel...
    protected ColorsPanel getLegends() {
        return colors;
    }

}
