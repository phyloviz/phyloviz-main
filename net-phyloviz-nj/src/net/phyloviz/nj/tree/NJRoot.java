/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
        return size(n1) + size(n2) - 1;
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
