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
import net.phyloviz.algo.AbstractDistance;
import net.phyloviz.algo.Edge;
import net.phyloviz.core.data.Profile;

/**
 *
 * @author Adriano
 * @param <T>
 */
public abstract class ClusteringDistance<T extends Profile> implements AbstractDistance<T> {

    @Override
    public abstract int level(T px, T py);

    @Override
    public abstract int level(Edge<T> e);

    @Override
    public abstract int compare(Edge<T> ex, Edge<T> ey);

    @Override
    public abstract int compare(T px, T py);

    @Override
    public abstract String info(Profile px, Profile py);

    @Override
    public abstract String info(Edge<T> e);

    @Override
    public abstract Comparator<T> getProfileComparator();

    @Override
    public abstract Comparator<Edge<T>> getEdgeComparator();

    @Override
    public boolean configurable() {
        return false;
    }

    @Override
    public void configure() {
        //do nothing
    }

    @Override
    public int maxLevel() {
        return -1;
    }
}
