/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.upgma.run;

import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.upgma.HierarchicalClusteringMethod;
import net.phyloviz.upgma.UPGMAItem;
import net.phyloviz.upgma.ui.OutputPanel;
import net.phyloviz.upgma.algorithm.UPGMA;
import net.phyloviz.upgma.tree.UPGMALeafNode;
import net.phyloviz.upgma.tree.UPGMARoot;
import net.phyloviz.upgmanjcore.distance.ClusteringDistance;


public class UPGMARunner implements Runnable{
    
    private final OutputPanel op;
    private final ClusteringDistance<UPGMALeafNode> ad;
    private final TypingData<? extends Profile> td;
    private final HierarchicalClusteringMethod cm;

    public UPGMARunner(OutputPanel op, ClusteringDistance<UPGMALeafNode> ad, HierarchicalClusteringMethod cm, TypingData<? extends Profile> td) {
        this.op = op;
        this.ad = ad;
        this.td = td;
        this.cm = cm;
    }

    
    @Override
    public void run() {
        
        op.appendWithDate("Hierarchical Clustering - "+cm.toString()+" - (" +ad.toString()+  ") started\n computing distance matrix...\n\n");
        op.flush();
        
        UPGMA algorithm = new UPGMA(td, ad, cm, op);
        
        UPGMARoot root = algorithm.execute();
       
        op.appendWithDate( cm.toString() + " algorithm: done.\n");
        op.flush();
        
        td.add(new UPGMAItem(root, ad, cm, op));
    }
    
}
