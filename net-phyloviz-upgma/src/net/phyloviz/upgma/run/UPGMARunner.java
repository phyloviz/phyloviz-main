/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.upgma.run;

import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.upgma.UPGMAItem;
import net.phyloviz.upgma.algorithm.HierarchicalClusteringDistance;
import net.phyloviz.upgma.ui.OutputPanel;
import net.phyloviz.upgma.algorithm.UPGMA;
import net.phyloviz.upgma.tree.UPGMARoot;


public class UPGMARunner implements Runnable{
    
    private OutputPanel op;
    private HierarchicalClusteringDistance ad;
    private TypingData<? extends Profile> td;

    public UPGMARunner(OutputPanel op, HierarchicalClusteringDistance ad, TypingData<? extends Profile> td) {
        this.op = op;
        this.ad = ad;
        this.td = td;
    }

    
    @Override
    public void run() {
        
        op.appendWithDate("Hierarchical Clustering started\n" + ad.toString() +" algorithm: computing distance matrix...\n\n");
        op.flush();
        //TypingData<? extends AbstractProfile> td = (TypingData<? extends AbstractProfile>) n.getLookup().lookup(TypingData.class);
        
        UPGMA algorithm = new UPGMA(td, ad, op);
        
        UPGMARoot root = algorithm.execute();
       
        op.appendWithDate( ad.toString() + " algorithm: done.\n");
        op.flush();
        
        td.add(new UPGMAItem(root, ad, op));
    }
    
}
