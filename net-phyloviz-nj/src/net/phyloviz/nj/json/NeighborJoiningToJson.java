/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.nj.json;

import java.util.HashMap;
import net.phyloviz.upgmanjcore.json.IJsonSaverTag;

/**
 *
 * @author Adriano
 */
public class NeighborJoiningToJson implements IJsonSaverTag{
    
    private final StringBuilder sbLeafs = new StringBuilder();
    private final StringBuilder sbUnions = new StringBuilder();
    private String root; 
    
    public void putLeaf(int id, String profile, double x, double y){
        String value = "\t\t{\""
                + "id\": " + id + ", \""
                + "profile\": " + profile + ", \""
                + "x\": " + x + ", \""
                + "y\": " + y + "},\n";
        sbLeafs.append(value);
    }
    public void putUnion(int id, int left, float distL, int right, float distR, double x, double y){
        String value = "\t\t{\""
                + "id\": " + id + ", \""
                + "left\": " + left + ", \"" 
                + "distanceLeft\": " + distL + ", \""
                + "right\": " + right + ", \""
                + "distanceRight\": " + distR + ", \""
                + "x\": " + x + ", \""
                + "y\": " + y + "},\n";
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
        return root;
    }
    
}
