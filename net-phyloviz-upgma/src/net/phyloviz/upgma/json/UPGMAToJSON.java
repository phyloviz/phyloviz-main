/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.upgma.json;

import net.phyloviz.upgma.tree.UPGMARoot;
import net.phyloviz.upgma.tree.NodeType;

/** 
 *
 * @author Adriano
 */
public class UPGMAToJSON {
    
    private final UPGMARoot root;

    public UPGMAToJSON(UPGMARoot root) {
        this.root = root;
    }
    public String saveToJSON(){
        JsonSaver js = new JsonSaver(new String[] {"leaf", "union"}, new UPGMAJsonWriter());
        root.saveData(js, null);
        return js.createFile();
    }
    public interface INodeJsonSaver{
        void saveData(JsonSaver js);
    }
    public interface IEdgeJsonSaver{
        void saveData(JsonSaver js, NodeType from);
    }
    
}
