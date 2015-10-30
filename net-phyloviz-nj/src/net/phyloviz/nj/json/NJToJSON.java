/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.nj.json;

import net.phyloviz.nj.tree.NJRoot;
import net.phyloviz.nj.tree.NodeType;
import net.phyloviz.upgmanjcore.json.JsonSaver;

/** 
 *
 * @author Adriano
 */
public class NJToJSON {
    
    private final NJRoot root;

    public NJToJSON(NJRoot root) {
        this.root = root;
    }
    public String saveToJSON(){
        JsonSaver js = new JsonSaver(new String[] {"leaf", "union"}, new NeighborJoiningJsonWriter());
        root.saveData(js, null);
        return js.getJSON();
    }
    public interface IEdgeJsonSaver{
        void saveData(JsonSaver js, NodeType from);
    }
    
}
