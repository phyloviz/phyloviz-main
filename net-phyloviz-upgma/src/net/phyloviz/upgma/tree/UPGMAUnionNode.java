/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.upgma.tree;

import net.phyloviz.upgmanjcore.json.JsonSaver;
import net.phyloviz.upgma.json.UPGMAJsonWriter;
import net.phyloviz.upgmanjcore.tree.IndexListNode.NodeIterator;

/**
 *
 * @author Adriano
 */
public class UPGMAUnionNode extends NodeType{
    
    //public static int unionId;
    
    private final float distance;		// The full-distance of the node
    private final NodeType n1, n2;	// Left and right children
    
    public UPGMAUnionNode(int id, NodeType n1, NodeType n2, float distance, int size, int nodeIdx, NodeIterator in){
        super(id, size, nodeIdx, "Union", in);
        this.n1 = n1;
        this.n2 = n2;
        this.distance = distance;
    }
    
    @Override
    public String toString(){
        String s = getName() + "\n";
        s += "[ " + n1 + " -> " + n2 + "]\n";
        return s;
    }
    @Override
    public void saveData(JsonSaver js) {
        n1.saveData(js);
        n2.saveData(js);
        ((UPGMAJsonWriter)js.njtj).putUnion(id, distance, n1.id, n2.id);
    }
    @Override
    public String getDisplayName() {
        return "U";// + id;
    }
    public NodeType getNodeLeft(){
        return n1;
    }
    public NodeType getNodeRight(){
        return n2;
    }
    public double getDistance(){
        return distance;
    }
}
