package net.phyloviz.nj.run;

import net.phyloviz.nj.tree.NJLeafNode;
import net.phyloviz.algo.AbstractDistance;
import net.phyloviz.core.data.DataSet;
import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.nj.tree.NeighborJoiningItem;
import net.phyloviz.nj.tree.NJRoot;
import net.phyloviz.nj.algorithm.NJ;
import net.phyloviz.nj.algorithm.NJAbstractDistance;
import net.phyloviz.nj.ui.OutputPanel;
import org.openide.nodes.Node;

/**
 *
 * @author Adriano
 */
public class NeighborJoiningRunner implements Runnable{
    
    private final Node n;
    private final DataSet ds;
    private final OutputPanel op;
    private final AbstractDistance<NJLeafNode> ad;

    public NeighborJoiningRunner(Node n, OutputPanel op, AbstractDistance<NJLeafNode> ad) {
        this.n = n;
        this.op = op;
        this.ad = ad;
        ds = n.getParentNode().getLookup().lookup(DataSet.class);
    }

    @Override
    public void run() {
        op.appendWithDate("Neighbor-Joining has started\nNJ algorithm: computing nodes...\n");
        op.flush();

        TypingData<? extends Profile> td = (TypingData<? extends Profile>) n.getLookup().lookup(TypingData.class);

        op.appendWithDate("\nNJ algorithm: computing diferences...\n");
        op.flush();
        
        NJ matrix = ((NJAbstractDistance)ad).getAlgorithm(td, ad, op);
        NJRoot root = matrix.generateTree();
        op.flush();

        op.appendWithDate("\nNJ algorithm: finished!\n");
        op.flush();

        td.add(new NeighborJoiningItem(root, ds, ad, op));
    }
}
