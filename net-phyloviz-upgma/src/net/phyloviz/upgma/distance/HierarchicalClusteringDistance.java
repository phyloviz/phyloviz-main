/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.upgma.distance;

import java.util.Comparator;
import net.phyloviz.algo.AbstractDistance;
import net.phyloviz.algo.Edge;
import net.phyloviz.upgma.tree.UPGMALeafNode;

/**
 *
 * @author Marta Nascimento
 */
public abstract class HierarchicalClusteringDistance implements  AbstractDistance<UPGMALeafNode>{

    
    @Override
    public abstract int level(UPGMALeafNode px, UPGMALeafNode py);

    @Override
    public abstract int level(Edge<UPGMALeafNode> e);
    
    @Override
    public int compare(Edge<UPGMALeafNode> ex, Edge<UPGMALeafNode> ey) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int compare(UPGMALeafNode px, UPGMALeafNode py) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String info(UPGMALeafNode px, UPGMALeafNode py) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String info(Edge<UPGMALeafNode> e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int maxLevel() {
        return -1;
    }

    @Override
    public Comparator<UPGMALeafNode> getProfileComparator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Comparator<Edge<UPGMALeafNode>> getEdgeComparator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean configurable() {
        return false;
    }

    @Override
    public void configure() {
       //do nothing
    }

}
