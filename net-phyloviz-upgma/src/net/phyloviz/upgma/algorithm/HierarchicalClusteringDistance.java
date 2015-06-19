/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.upgma.algorithm;

import net.phyloviz.algo.AbstractDistance;
import net.phyloviz.upgma.tree.UPGMALeafNode;

/**
 *
 * @author Marta Nascimento
 */
public interface HierarchicalClusteringDistance extends AbstractDistance<UPGMALeafNode>{
 
    float distance(float d1, float d2);
}
