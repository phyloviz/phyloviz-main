package net.phyloviz.nj.run;

import net.phyloviz.nj.tree.NJLeafNode;
import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.nj.AgglomerativeClusteringMethod;
import net.phyloviz.nj.tree.NeighborJoiningItem;
import net.phyloviz.nj.tree.NJRoot;
import net.phyloviz.nj.algorithm.NJ;
import net.phyloviz.upgmanjcore.distance.ClusteringDistance;
import net.phyloviz.nj.ui.OutputPanel;

/**
 *
 * @author Adriano
 */
public class NeighborJoiningRunner implements Runnable{
    
    private final OutputPanel op;
    private final ClusteringDistance<NJLeafNode> ad;
    private final AgglomerativeClusteringMethod cm;
    private final TypingData<? extends Profile> td;

    public NeighborJoiningRunner(OutputPanel op, ClusteringDistance<NJLeafNode> ad, AgglomerativeClusteringMethod cm, TypingData<? extends Profile> td) {
        this.op = op;
        this.ad = ad;
        this.td = td;
        this.cm = cm;
    }

    @Override
    public void run() {
        op.appendWithDate("Neighbor-Joining has started\nNJ algorithm: computing nodes...\n");
        op.flush();

        op.appendWithDate("\nNJ algorithm: computing diferences...\n");
        op.flush();
        
        NJ matrix = cm.getClusteringMethod(td, ad, op);
        NJRoot root = matrix.generateTree();
        op.flush();

        op.appendWithDate("\nNJ algorithm: finished!\n");
        op.flush();

        td.add(new NeighborJoiningItem(root, ad, cm, op));
    }
}
