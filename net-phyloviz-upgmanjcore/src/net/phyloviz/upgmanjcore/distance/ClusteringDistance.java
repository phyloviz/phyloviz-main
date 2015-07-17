/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
