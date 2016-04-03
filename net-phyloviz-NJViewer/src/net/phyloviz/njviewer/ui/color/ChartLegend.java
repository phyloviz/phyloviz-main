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
