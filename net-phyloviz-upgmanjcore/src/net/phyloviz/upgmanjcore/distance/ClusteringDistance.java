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
public abstract class ClusteringDistance<T extends Profile> implements AbstractDistance<T>{
    
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
    public boolean configurable() {
        return false;
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
        return null;
    }

    @Override
    public String info(Edge<T> e) {
        return null;
    }

    @Override
    public int maxLevel() {
        return -1;
    }

    @Override
    public Comparator<T> getProfileComparator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Comparator<Edge<T>> getEdgeComparator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void configure() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }        
}
