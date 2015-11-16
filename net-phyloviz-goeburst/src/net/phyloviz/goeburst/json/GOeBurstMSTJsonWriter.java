/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.goeburst.json;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.phyloviz.algo.Edge;
import net.phyloviz.goeburst.tree.GOeBurstNode;
import net.phyloviz.upgmanjcore.json.IJsonWriter;

/**
 *
 * @author martanascimento
 */
public class GOeBurstMSTJsonWriter implements IJsonWriter{
    
    private Collection<Edge<GOeBurstNode>> edgesCollection;
    private int level;
    private String nodes;
    private String edges;
    
    public GOeBurstMSTJsonWriter(Collection<Edge<GOeBurstNode>> edges, int level){
        this.edgesCollection = edges;
        this.level = level;
    }
   
    private void getInfo(){
        StringBuilder n = new StringBuilder("");
        StringBuilder e = new StringBuilder("");
        Map<Integer, GOeBurstNode> nodesMap = new HashMap<>();
        for (Edge<GOeBurstNode> edge : edgesCollection) {
            
            GOeBurstNode u = edge.getU();
            GOeBurstNode v = edge.getV();
            if(!nodesMap.containsKey(u.getUID())){
                
                String node = "\t\t{"   
                           + "\"id\": " + u.getUID() + ", "
                           + "\"profile\": \"" + u.getID() + "\", "
                           + "\"group-lvs\": "+ getLVs(u)  + "},\n ";
                n.append(node);
                nodesMap.put(u.getUID(), u);
            }
            if(!nodesMap.containsKey(v.getUID())){
                
                String node = "\t\t{"   
                           + "\"id\": " + v.getUID() + ", "
                           + "\"profile\": \"" + v.getID() + "\", "
                           + "\"group-lvs\": "+ getLVs(v)  + "},\n ";
                n.append(node);
                nodesMap.put(v.getUID(), v);
            }
            
            e.append("\t\t{")
                .append("\"u\": ").append(u.getUID()).append(", ")
                .append("\"v\": ").append(v.getUID()).append("},\n ");
            
        }
        nodes = n.toString();
        edges = e.toString();
        
    }
    
    @Override
    public HashMap<String, String> getBuildersMap(){
        HashMap<String, String> map = new HashMap<>();

        getInfo();
        
        map.put("nodes", nodes);
        map.put("edges", edges);
        return map;
    }

    @Override
    public String getRoot() { return ""; }

    private String getLVs(GOeBurstNode u) {
        StringBuilder lvs = new StringBuilder("[");
        for(int i = 0; i < level; i++)
            lvs.append(u.getLV(i)).append(",");
        lvs.replace(lvs.length()-1, lvs.length(), "]");
        return lvs.toString();
    }
    
}
