/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.upgma;

import net.phyloviz.upgmanjcore.AbstractClusteringMethod;
import net.phyloviz.upgma.tree.UPGMALeafNode;

/**
 *
 * @author Marta Nascimento
 */
public interface HierarchicalClusteringMethod extends AbstractClusteringMethod<UPGMALeafNode> {

    public float getLinkageCriteria(float d1, float d2);

}
