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

package net.phyloviz.upgmanjcore.distance;

import java.util.Comparator;
import net.phyloviz.algo.Edge;
import net.phyloviz.core.data.Profile;

/**
 *
 * @author Marta Nascimento
 * @param <T>
 */
public class HammingDistance<T extends Profile> extends ClusteringDistance<T> {

    @Override
    public int level(T px, T py) {
        int diffs = 0;

        for (int i = 0; i < px.profileLength(); i++) {
            if (px.getValue(i).compareTo(py.getValue(i)) != 0) {
                diffs++;
            }
        }

        return diffs;
    }

    @Override
    public int level(Edge<T> e) {
        return level(e.getU(), e.getV());
    }

    @Override
    public String toString() {
        return "Hamming Distance";
    }

    @Override
    public int compare(Edge<T> ex, Edge<T> ey) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int compare(T px, T py) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String info(Profile px, Profile py) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String info(Edge<T> e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Comparator<T> getProfileComparator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Comparator<Edge<T>> getEdgeComparator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
