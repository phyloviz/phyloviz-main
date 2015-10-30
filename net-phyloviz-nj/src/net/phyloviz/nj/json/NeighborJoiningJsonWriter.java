/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.nj.json;

import java.util.HashMap;
import net.phyloviz.upgmanjcore.json.IJsonWriter;

/**
 *
 * @author Adriano
 */
public class NeighborJoiningJsonWriter implements IJsonWriter{
    
    private final StringBuilder sbLeafs = new StringBuilder();
    private final StringBuilder sbUnions = new StringBuilder();
    private String root; 
    
    public void putLeaf(int id, String profile){
        String value = "\t\t{\""
                + "id\": " + id + ", \""
                + "profile\": \"" + profile + "\"},\n";
        sbLeafs.append(value);
    }
    public void putUnion(int id, int left, float distL, int right, float distR){
        String value = "\t\t{\""
                + "id\": " + id + ", \""
                + "left\": " + left + ", \"" 
                + "distanceLeft\": " + distL + ", \""
                + "right\": " + right + ", \""
                + "distanceRight\": " + distR + "},\n";
        sbUnions.append(value);
    }
    public void putRoot(double d, int l, int r){
        root = "\t\t{\""
                + "distance\": "+ d + ", \""
                + "left\": " + l + ", \""
                + "right\": " + r + "},\n";
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
        return "\t\t\"root\":" + root;
    }
    
}
