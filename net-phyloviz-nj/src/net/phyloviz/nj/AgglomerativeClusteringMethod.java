/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.nj;

import net.phyloviz.algo.AbstractClusteringMethod;
import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.nj.algorithm.NJ;
import net.phyloviz.nj.tree.NJLeafNode;
import net.phyloviz.nj.ui.OutputPanel;
import net.phyloviz.upgmanjcore.distance.ClusteringDistance;

/**
 *
 * @author Adriano Sousa
 */
public interface AgglomerativeClusteringMethod extends AbstractClusteringMethod<NJLeafNode> {

    NJ getClusteringMethod(TypingData<? extends Profile> inTd, ClusteringDistance<NJLeafNode> inAd, OutputPanel op);
    
}
