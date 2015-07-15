/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.nj.algorithm.saitou_nei;

import java.util.Iterator;
import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.nj.algorithm.NJ;
import net.phyloviz.nj.tree.NJUnionNode;
import net.phyloviz.nj.tree.NodeType;
import net.phyloviz.nj.ui.OutputPanel;
import net.phyloviz.upgmanjcore.distance.ClusteringDistance;
import net.phyloviz.upgmanjcore.tree.IndexListNode;

/**
 *
 * @author Adriano
 */
public class NJSaitouNei extends NJ{
    
    public NJSaitouNei(TypingData<? extends Profile> inTd, ClusteringDistance inAd, OutputPanel op){
        super(inTd, inAd, op);
    }
    
    @Override
    protected void CalculateMin(NodeType[] nodeArray, IndexListNode nodeList) {
        Iterator<IndexListNode.IndexNode> iter = nodeList.iterator();
        while(iter.hasNext()){                              //for each line in this column, calculates Q
            int idx = iter.next().index;
            NodeType nt = nodeArray[idx];
            nt.calculateMin(nodeArray, nodeList.getSize(), this);     
        }
    }

    @Override
    protected NJUnionNode createUnion(NodeType[] nodeArray, Wrapper w, IndexListNode nodeList, OutputPanel op, int nodeIdx) {
        float dst = nodeArray[w.c].getPositionDistanceLine(w.l);
        float dlk = (0.5f*dst) + (getSum(nodeArray, w.c, nodeList.getSize()) - getSum(nodeArray, w.l, nodeList.getSize()))/2;
        float drk = dst - dlk;
        op.flush();
        NodeType n = nodeArray[w.c];
        return new NJUnionNode(n, dlk, nodeArray[w.l], drk, nodeArray.length, n.getNodeIdx() - 1, n.in, nodeIdx);    
    }

    @Override
    public float calculateMin(NodeType[] nodeArray, int nodeIdx, int l, int total) {
        return nodeArray[nodeIdx].getPositionDistanceLine(l) - getSum(nodeArray, nodeIdx, total) - getSum(nodeArray, l , total);
    }
    
    private float getSum(NodeType[] nodeArray, int pos, int total){
        return nodeArray[pos].getDistancesSum() / (total - 2);
    }
}
