package net.phyloviz.nj.algorithm.saitou_nei;

import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.nj.AgglomerativeClusteringMethod;
import net.phyloviz.nj.algorithm.NJ;
import net.phyloviz.nj.tree.NJLeafNode;
import net.phyloviz.nj.ui.OutputPanel;
import net.phyloviz.upgmanjcore.distance.ClusteringDistance;

public class NJMethodSaitouNei implements AgglomerativeClusteringMethod{
    
    @Override
    public String toString() {
        return "Saitou-Nei Criterion";
    }

    @Override
    public NJ getClusteringMethod(TypingData<? extends Profile> inTd, ClusteringDistance<NJLeafNode> inAd, OutputPanel op) {
        return new NJSaitouNei(inTd, inAd, op);
    }
}
