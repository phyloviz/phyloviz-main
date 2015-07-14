/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.nj.distance;

import java.util.Comparator;
import net.phyloviz.algo.AbstractDistance;
import net.phyloviz.algo.Edge;
import net.phyloviz.nj.tree.NJLeafNode;

/**
 *
 * @author Adriano
 */
public abstract class ClusteringDistance implements AbstractDistance<NJLeafNode>{
    
    @Override
    public int level(NJLeafNode px, NJLeafNode py) {
        int diffs = 0;

        for (int i = 0; i < px.profileLength(); i++) {
            if (px.getValue(i).compareTo(py.getValue(i)) != 0) {
                diffs++;
            }
        }

        return diffs;
    }
    
    @Override
    public int level(Edge<NJLeafNode> e) {
        return level(e.getU(), e.getV());
    }
    
    @Override
    public boolean configurable() {
        return false;
    }

    @Override
    public int compare(Edge<NJLeafNode> ex, Edge<NJLeafNode> ey) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int compare(NJLeafNode px, NJLeafNode py) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String info(NJLeafNode px, NJLeafNode py) {
        return null;
    }

    @Override
    public String info(Edge<NJLeafNode> e) {
        return null;
    }

    @Override
    public int maxLevel() {
        return -1;
    }

    @Override
    public Comparator<NJLeafNode> getProfileComparator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Comparator<Edge<NJLeafNode>> getEdgeComparator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void configure() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }        
}
