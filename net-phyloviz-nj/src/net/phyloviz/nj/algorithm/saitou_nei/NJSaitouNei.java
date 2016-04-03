/*-
 * Copyright (c) 2016, PHYLOViZ Team <phyloviz@gmail.com>
 * All rights reserved.
 * 
 * This file is part of PHYLOViZ <http://www.phyloviz.net/>.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
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
import net.phyloviz.upgmanjcore.tree.IndexList;

/**
 *
 * @author Adriano
 */
public class NJSaitouNei extends NJ{
    
    public NJSaitouNei(TypingData<? extends Profile> inTd, ClusteringDistance inAd, OutputPanel op){
        super(inTd, inAd, op);
    }
    
    @Override
    protected void CalculateMin(NodeType[] nodeArray, IndexList nodeList) {
        Iterator<IndexList.IndexNode> iter = nodeList.iterator();
        while(iter.hasNext()){                              //for each line in this column, calculates Q
            int idx = iter.next().index;
            NodeType nt = nodeArray[idx];
            nt.calculateMin(nodeArray, nodeList.getSize(), this);     
        }
    }

    @Override
    protected NJUnionNode createUnion(NodeType[] nodeArray, Wrapper w, IndexList nodeList, OutputPanel op, int nodeIdx) {
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
