/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.nj;

import net.phyloviz.algo.AbstractDistance;
import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.nj.algorithm.NJ;
import net.phyloviz.nj.tree.NJLeafNode;
import net.phyloviz.nj.ui.OutputPanel;

/**
 *
 * @author Adriano Sousa
 * @param <T>
 */
public interface AgglomerativeClusteringMethod <T extends Profile> {

    NJ getCulsteringMethod(TypingData<? extends Profile> inTd, AbstractDistance<NJLeafNode> inAd, OutputPanel op);
    
}
