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

import java.util.ArrayList;
import java.util.List;
import net.phyloviz.algo.Edge;
import net.phyloviz.nj.json.NJToJSON.IEdgeJsonSaver;
import net.phyloviz.nj.json.NeighborJoiningJsonWriter;
import net.phyloviz.upgmanjcore.json.JsonSaver;

/**
 *
 * @author Adriano
 */
public class NJRoot implements IEdgeJsonSaver{
         
    public final NodeType n1, n2;
    public final float distance;

    public NJRoot(NodeType n1, NodeType n2, float distance){
        this.n1 = n1;
        this.n2 = n2;
        this.distance = distance;
    }
    @Override
    public void saveData(JsonSaver js, NodeType from) {
        n1.saveData(js); //start saving to the left
        n2.saveData(js); //start saving to the right
        ((NeighborJoiningJsonWriter)js.writer).putRoot(distance, n1.id, n2.id);
    }

    public List<EdgeDistanceWrapper> getList() {
        ArrayList<EdgeDistanceWrapper> list = new ArrayList<>();

        EdgeDistanceWrapper edw = new EdgeDistanceWrapper(new Edge<>(n1, n2), distance);
        list.add(edw);
        
        fillNodes(list, n1);
        fillNodes(list, n2);
        return list;
    }

    private void fillNodes(ArrayList<EdgeDistanceWrapper> list, NodeType nt) {
        if(nt instanceof NJLeafNode)
            return;
        NJUnionNode union = (NJUnionNode)nt;
        
        EdgeDistanceWrapper edw;  
        edw= new EdgeDistanceWrapper(new Edge<>(nt, union.n1), union.distance1);
        list.add(edw);        
        edw = new EdgeDistanceWrapper(new Edge<>(nt, union.n2), union.distance2);
        list.add(edw);
        
        fillNodes(list, union.n1);
        fillNodes(list, union.n2);
    }

    public int size() {
        int s1 = size(n1);
        int s2 = size(n2);
        return s1 + s2 - 1;
    }

    private int size(NodeType n) {
        if(n instanceof NJUnionNode) return 1 + size(((NJUnionNode)n).n1) + size(((NJUnionNode)n).n2);
        else return 1;
    }
    
    public class EdgeDistanceWrapper{
        public final Edge<NodeType> edge;
        public final float distance;
        public EdgeDistanceWrapper(Edge<NodeType> edge, float distance){
            this.edge = edge;
            this.distance = distance;
        }
    }
}
