/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.upgma.json;

import java.util.HashMap;
import net.phyloviz.upgmanjcore.json.IJsonSaverTag;

/**
 *
 * @author Adriano
 */
public class UPGMAJsonWriter implements IJsonSaverTag{
    
    private final StringBuilder sbLeafs = new StringBuilder();
    private final StringBuilder sbUnions = new StringBuilder();
    private String root; 
    
    public void putLeaf(int id, String profile){
        String value = "\t\t{\"uid\": " + id + ", \"profile\": " + profile + "},\n";
        sbLeafs.append(value);
    }
    public void putUnion(int id, float distance, int left, int right){
        String value = "\t\t{\""
                + "uid\": " + id + ", \""
                + "distance\": " + distance + ", \""
                + "leftID\": " + left + ", \""
                + "rightID\": " + right + "},\n";
        sbUnions.append(value);
    }
    public void putRoot(double d, int l, int r){
        root = "\t\t{\"distance\": "+ d + ", \"left\": " + l + ", \"right\": " + r + "}\n";
    }
    @Override
    public HashMap<String, String> getBuildersMap(){
        HashMap<String, String> map = new HashMap<>();
        map.put("leaf", sbLeafs.toString());
        map.put("union", sbUnions.toString());
        map.put("root", root);
        return map;
    }
    @Override
    public String getRoot() {
        return root;
    }
    
}
