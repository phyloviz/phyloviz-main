/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.nj.tree;

import net.phyloviz.nj.json.NeighborJoiningToJson;
import net.phyloviz.upgmanjcore.json.JsonSaver;
import net.phyloviz.upgmanjcore.tree.IndexListNode.NodeIterator;

/**
 *
 * @author Adriano
 */
public class NJUnionNode extends NodeType{
    
    public final NodeType n1;
    public final NodeType n2;
    public final float distance1;
    public final float distance2;
    
    public NJUnionNode(NodeType n1, float distance1, NodeType n2, float distance2, int size, int nodeIdx, NodeIterator in, int nodeId, double x, double y) {
        super(size, nodeIdx, "Union " + nodeId, in, null, nodeId, x, y);
        this.n1 = n1;
        this.n2 = n2;
        this.distance1 = distance1;
        this.distance2 = distance2;
    }
    public NJUnionNode(NodeType n1, float distance1, NodeType n2, float distance2, int size, int nodeIdx, NodeIterator in, int nodeId){
        this(n1, distance1, n2, distance2, size, nodeIdx, in, nodeId, 0, 0);
    }
    
    @Override
    public String toString(){
        return ".";
    }
    @Override public int profileLength(){
        return 0;
    }
    @Override public String getValue(int idx){
        return "";
    }
    @Override public int getUID(){
        return this.id;
    }
    @Override public String getID(){
        return name;
    }
    @Override public int getFreq(){
        return 1;
    }
    @Override public String get(int idx){
        return "";
    }
    @Override public void setFreq(int freq){}
    @Override public int length(){
        return this.id;
    }
    @Override public int weight(){
        return this.id;
    }
    @Override
    public void saveData(JsonSaver js) {
        n1.saveData(js);
        n2.saveData(js);
        ((NeighborJoiningToJson)js.njtj).putUnion(id, n1.id, distance1, n2.id, distance2, x, y);
    }

}
