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

package net.phyloviz.upgma.tree;

import java.util.Iterator;
import net.phyloviz.upgmanjcore.tree.IndexList.IndexNode;
import net.phyloviz.upgmanjcore.tree.IndexList.NodeIterator;
import net.phyloviz.upgmanjcore.json.INodeJsonSaver;

public abstract class NodeType implements INodeJsonSaver{
    
    //private static int idx = 0;
    
    private float[] distances;
    private final String name;
    public final int id;
    
    private final int nodeOffset;
    private final int nodeIdx;
    public NodeIterator in;
    private IndexNode minL;
    private float minQ = Float.POSITIVE_INFINITY;
    private final String type;
    
    public NodeType(int id, int size, int nodeIdx, String name, NodeIterator in){
        this.distances = new float[size - nodeIdx - 1];         //removes 1 because size counts itself
        this.id = id;
        this.type = name;
        this.name = name + " " + id;
        this.nodeOffset = nodeIdx + 1;                      //+1 becouse theres no A->A, only A->B, A->C...
        this.nodeIdx = nodeIdx;
        this.in = in;
    }
       public NodeType(int id, int size, int nodeIdx, String name){
        this.id = id;
        this.type = name;
        this.name = name + " " + id;
        this.nodeOffset = nodeIdx + 1;                      //+1 becouse theres no A->A, only A->B, A->C...
        this.nodeIdx = nodeIdx;
    }
    /**
     * @param idx position in distance matrix
     * @return distance from index idx
     */
    public float getDistanceTo(int idx){
        return distances[idx - nodeOffset];
    }
    public int getNodeIdx(){
        return nodeOffset;
    }
    public String getType(){
        return type;
    }
    public String getName(){
        return name;
    }
    public abstract int getSize();
    /**
     * Sets new distance and check if new minimum
     * @param in IndexNode from new distance
     * @param value new distance do set
     */
    public void setDistance(IndexNode in, float value) {
        distances[in.index - nodeOffset] = value;
        if(value < minQ){
            minL = in;
            minQ = value;
        }
    }
    @Override
    public abstract String toString();
    public abstract String getDisplayName();
     
    /**
     * sets distances to null, helpful for large data
     */
    public void empty() { 
        distances = null;
        in = null;
        minL = null;
    }
    //Calculates minimum distance and minimum index
    private void calculateMin(NodeType[] nodeArray, int ominL) {
        minQ = Float.POSITIVE_INFINITY;
        minL = null;
        Iterator<IndexNode> iter = in.iterator();
        while(iter.hasNext()){
            IndexNode cin = iter.next();
            int l = cin.index;
            float res = nodeArray[nodeIdx].getDistanceTo(l);
            if(res < minQ && cin.index != ominL && l != nodeIdx){
                minL = cin;
                minQ = res;
            }
        }
    }
    /**
     * @return minimum distance value
     */
    public float getMinDistance(){
        return minQ;
    }
    /**
     * @return minimum line index
     */
    public IndexNode getMinDistanceIndex(){
        return minL;
    }
    /**
     * Check for possible alterations with minL remove
     * @param minL current node with smallest index to be removed
     * @param numOfElems total number of elements
     * @param nodeArray array with all the nodes
     */
    public void removeDistance(int minL, int numOfElems, NodeType[] nodeArray) {
        if(this.minL == null || this.minL.index == minL || nodeArray[this.minL.index] == null){   //if minL hasnt been calculated yet
            calculateMin(nodeArray, minL);
        }
    }

    public int compareTo(NodeType o) {
        if(this.id > o.id){
            return 1;
        } else if(this.id == o.id){
            return 0;
        } else {
            return -1;
        }
    }
}
