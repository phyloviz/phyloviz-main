/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.upgma.tree;

import net.phyloviz.upgma.json.UPGMAJsonWriter;
import net.phyloviz.upgma.json.JsonSaver;
import net.phyloviz.upgma.json.UPGMAToJSON.IEdgeJsonSaver;

/**
 *
 * @author Adriano
 */
public class UPGMARoot implements IEdgeJsonSaver{
        
    private final NodeType n1, n2;
    private final float distance;

    public UPGMARoot(NodeType n1, NodeType n2, float distance){
        this.n1 = n1;
        this.n2 = n2;
        this.distance = distance;
    }
    @Override
    public void saveData(JsonSaver js, NodeType from) {
        n1.saveData(js); //start saving to the left
        n2.saveData(js); //start saving to the right
        ((UPGMAJsonWriter)js.njtj).putRoot(distance, n1.id, n2.id);
    }
    
    public NodeType getNodeLeft(){
        return n1;
    }
    public NodeType getNodeRight(){
        return n2;
    }
    public String getDisplayName(){
        return "root";
    }
    public double getDistance(){
        return distance;
    }
}
