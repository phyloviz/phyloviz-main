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
    private EdgeStats edge;

    public ChartLegend(EdgeStats edge, Dimension d) {
        colorPercentageMap = new HashMap<Color, TooltipInfo>();
        // Draw background pie...
        double value = edge.maxD;
        if (edge.minD < 0) {
            value += (edge.minD * -1);
        } else {
            value -= edge.minD;
        }
        double step = value / edge.interval;
        double sum = edge.minD;
        for (int i = 0; i < edge.interval; i++) {
            TooltipInfo tti;
            if(edge.maxD >= edge.interval)
                tti = new TooltipInfo(Float.valueOf(i), String.format((i != 0 ? "]" : "[") + "%dx, %dx]", (int)sum, (int)((sum + step))));
            else
                tti = new TooltipInfo(Float.valueOf(i), String.format((i != 0 ? "]" : "[") + "%.3fx, %.3fx]", sum, ((sum + step))));
            colorPercentageMap.put(edge.colors[i], tti);
            sum += step;
        }

        chartPie = new PieChartPanel(d, colorPercentageMap, edge.interval);
        colors = new ColorsPanel(edge, chartPie, colorPercentageMap);
    }

    protected PieChartPanel getChartPie() {
        return this.chartPie;
    }

    //get legends panel...
    protected ColorsPanel getLegends() {
        return colors;
    }

}
