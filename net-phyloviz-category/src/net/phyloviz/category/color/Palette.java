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

package net.phyloviz.category.color;

import java.awt.Color;
import java.io.Serializable;

public class Palette implements Serializable {

    private Color[] colors;

    public Palette(int n) {
        generateColors(n);
    }

    public Color[] getColors() {
        return colors;
    }

    public void setColor(int i, Color newColor) {
        if (i >= 0 && i < colors.length) {
            colors[i] = newColor;
        }
    }


    private void generateColors(int n) {
        colors = new Color[n];
        float prevColor = 0, prevBri = 0;
        if (n == 0) {
            n = 1; // para sair... desnecessário!?
        }
        int x = (int) (Math.ceil(colors.length / 3f));
        int step = 1;
        if (x != 0) // para sair... desnecess�rio!?
        {
            step = (120 / x);
        }
        float cor, sat, bri;
        float satDiff[] = {80, 100, 90, 70, 70, 90, 100, 80};
        float briDiff[] = {100, 70, 85, 65};
        for (int i = 0, j = 0; i < colors.length; i = i + 3, j++) {
            cor = (n + j * step) % 120;
            sat = satDiff[j % satDiff.length];
            bri = briDiff[j % briDiff.length];
            if (i != 0 && (prevColor - cor <= 15 && prevBri - bri <= 30)) {
                bri = bri - 20;
            }
            prevColor = cor;
            prevBri = cor;
            colors[i] = Color.getHSBColor((cor / 360f), (sat / 100f), bri / 100f);
        }
        for (int i = 2, j = 0; i < colors.length; i = i + 3, j++) {
            cor = (n / 2 + j * step + 120) % 240;
            sat = satDiff[j % satDiff.length];
            bri = briDiff[j % briDiff.length];
            if (i > 2 && (prevColor - cor <= 15 && prevBri - bri <= 30)) {
                bri = bri - 20;
            }
            colors[i] = Color.getHSBColor((cor / 360f), sat / 100f, bri / 100f);
        }
        for (int i = 1, j = 0; i < colors.length; i = i + 3, j++) {
            cor = (j * step + 240 + n / 3) % 360;
            sat = satDiff[j % satDiff.length];
            bri = briDiff[j % briDiff.length];
            if (i > 1 && (prevColor - cor <= 15 && prevBri - bri <= 30)) {
                bri = bri - 20;
            }
            colors[i] = Color.getHSBColor((cor / 360f), sat / 100f, bri / 100f);

        }

    }
}
