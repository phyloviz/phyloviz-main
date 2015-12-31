/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
