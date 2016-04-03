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

package net.phyloviz.nj.tree;

import java.util.Iterator;
import net.phyloviz.core.data.Profile;
import net.phyloviz.nj.algorithm.NJ;
import net.phyloviz.upgmanjcore.json.INodeJsonSaver;
import net.phyloviz.upgmanjcore.tree.IndexList.IndexNode;
import net.phyloviz.upgmanjcore.tree.IndexList.NodeIterator;

public abstract class NodeType implements INodeJsonSaver, Profile{
    
    public final String name;
    private final int nodeOffset;
    private final int nodeIdx;
    
    private float[] distances;
    private IndexNode minL;
    private float sum = 0;
    private float minQ = Float.POSITIVE_INFINITY;
    
    public NodeIterator in;
    public final Profile p;
    public final int id;
    
//    public double x = 0;
//    public double y = 0;
    
    public NodeType(int size, int nodeIdx, String name, NodeIterator in, Profile p, int id){
        distances = new float[size - nodeIdx - 1];         //removes 1 becouse size counts itself
        this.name = name;
        this.nodeOffset = nodeIdx + 1;                      //+1 becouse theres no A->A, only A->B, A->C...
        this.nodeIdx = nodeIdx;
        this.in = in;
        this.p = p;
        this.id = id;
    }
    /**
     * @param idx position in distance matrix
     * @return distance from index idx
     */
    public float getPositionDistanceLine(int idx){
        return distances[idx - nodeOffset];
    }
    public int getNodeIdx(){
        return nodeOffset;
    }
    public String getName(){
        return name;
    }
    public float getDistancesSum() {
        return sum;
    }
    /**
     * Sets new distance and check if new minimum
     * @param in IndexNode from new distance
     * @param value new distance do set
     */
    public void setDistance(IndexNode in, float value) {
        distances[in.index - nodeOffset] = value;
        sum += value;
    }
    public void setandRemoveDistance(int posL, int posR, float value){
        sum = sum - distances[posL - nodeOffset] - distances[posR - nodeOffset] + value;
        distances[posL - nodeOffset] = value;
    }
    public void addToDistance(float distance) {
        sum += distance;
    }
    /**
     * sets distances to null, helpfull for large data
     */
    public void empty() { 
        distances = null;
        in = null;
        minL = null;
    }
    
    public void calculateMin(NodeType[] nodeArray, int total, NJ nj) {
        Iterator<IndexNode> iter = in.iterator();
        float smallest = Float.POSITIVE_INFINITY;
        if(!iter.hasNext())  {
            minQ = Float.POSITIVE_INFINITY;
            return;
        }
        while(iter.hasNext()){
            IndexNode cin = iter.next();
            int l = cin.index;
            float res = nj.calculateMin(nodeArray, nodeIdx, l, total);
            if(res < smallest && l != nodeIdx){
                smallest = res;
                minL = cin;
                minQ = res;
            }
        }
    }
    /**
     * @return minimum distance value
     */
    public float getMinQ(){
        return minQ;
    }
    /**
     * @return minimum line index
     */
    public IndexNode getMinL(){
        return minL;
    }
    
}
