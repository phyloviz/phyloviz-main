package net.phyloviz.nj.algorithm.studier_keppler;

import net.phyloviz.nj.tree.NJLeafNode;
import net.phyloviz.algo.AbstractDistance;
import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.nj.algorithm.NJ;
import net.phyloviz.nj.algorithm.NJAbstractDistance;
import net.phyloviz.nj.ui.OutputPanel;

public class NJDistanceStudierKeppler extends NJAbstractDistance {
    
    @Override
    public String toString() {
        return "Studier-Keppler Criterion";
    }
    
    @Override
    public NJ getAlgorithm(TypingData<? extends Profile> inTd, AbstractDistance<NJLeafNode> inAd, OutputPanel op) {
        return new NJStudierKeppler(inTd, inAd, op);
    }
}
