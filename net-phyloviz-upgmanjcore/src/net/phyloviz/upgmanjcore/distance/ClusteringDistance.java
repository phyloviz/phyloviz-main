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
 */
public abstract class ClusteringDistance implements AbstractDistance<Profile>{
    
    @Override
    public int level(Profile px, Profile py) {
        int diffs = 0;

        for (int i = 0; i < px.profileLength(); i++) {
            if (px.getValue(i).compareTo(py.getValue(i)) != 0) {
                diffs++;
            }
        }

        return diffs;
    }
    
    @Override
    public int level(Edge<Profile> e) {
        return level(e.getU(), e.getV());
    }
    
    @Override
    public boolean configurable() {
        return false;
    }

    @Override
    public int compare(Edge<Profile> ex, Edge<Profile> ey) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int compare(Profile px, Profile py) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String info(Profile px, Profile py) {
        return null;
    }

    @Override
    public String info(Edge<Profile> e) {
        return null;
    }

    @Override
    public int maxLevel() {
        return -1;
    }

    @Override
    public Comparator<Profile> getProfileComparator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Comparator<Edge<Profile>> getEdgeComparator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void configure() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }        
}
