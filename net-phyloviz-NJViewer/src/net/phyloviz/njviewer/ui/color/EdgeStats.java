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
import net.phyloviz.njviewer.ui.GraphView;
import net.phyloviz.upgmanjcore.visualization.GView;

/**
 *
 * @author martanascimento
 */
public class EdgeStats {

    public double minD = Double.POSITIVE_INFINITY;
    public double maxD = Double.NEGATIVE_INFINITY;
    public int interval;
    public Color[] colors;
    private GView view;
    //public double scale = Double.NEGATIVE_INFINITY;

    public EdgeStats(GView view, int intervals, int rgb) {
        this.view = view;
        interval = intervals;
        colors = new Color[interval];
        int min = 255 / interval;
        int sum = min;
        for (int i = 0; i < colors.length; i++) {
            switch (rgb) {
                case 'r':
                    colors[i] = new Color(sum, 0, 0);
                    break;
                case 'g':
                    colors[i] = new Color(0, sum, 0);
                    break;
                case 'b':
                    colors[i] = new Color(0, 0, sum);
                    break;
            }
            sum += min;
        }
    }

    public Color getColor(double diff) {
        double min = maxD / interval;
        double sum = min;
        for (int i = 0; i < interval; i++) {
            if (diff <= sum) {
                return colors[i];
            }
            sum += min;
        }
        return colors[interval - 1];
    }

    public double diff(double realDistance, double visualDistance) {
//        if(scale != Double.NEGATIVE_INFINITY)
//            visualDistance = visualDistance - scale;
        double diff;
        if (realDistance == 0 || realDistance < 0.01) {
            diff = visualDistance;
        } else {
            diff = visualDistance / realDistance;
        } 
        return diff;
    }

    public void update(double realDistance, double visualDistance) {
        double diff = diff(realDistance, visualDistance);

        if (diff < minD) {
            minD = diff;
        }
        if (diff > maxD) {
            maxD = diff;
        }
    }

    public void updateColor(int idx, Color newColor) {
        colors[idx] = newColor;
        ((GraphView) view).updateEdgeColor();
    }
}
