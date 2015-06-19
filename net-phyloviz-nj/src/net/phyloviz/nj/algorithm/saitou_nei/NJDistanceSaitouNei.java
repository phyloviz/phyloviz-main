package net.phyloviz.nj.algorithm.saitou_nei;

import net.phyloviz.nj.tree.NJLeafNode;
import net.phyloviz.algo.AbstractDistance;
import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.nj.algorithm.NJ;
import net.phyloviz.nj.algorithm.NJAbstractDistance;
import net.phyloviz.nj.ui.OutputPanel;

public class NJDistanceSaitouNei extends NJAbstractDistance {
    
    @Override
    public String toString() {
        return "Neighbor-Joining distance Saitou N. and Nei M. Criterion";
    }

    @Override
    public NJ getAlgorithm(TypingData<? extends Profile> inTd, AbstractDistance<NJLeafNode> inAd, OutputPanel op) {
        return new NJSaitouNei(inTd, inAd, op);
    }
}
