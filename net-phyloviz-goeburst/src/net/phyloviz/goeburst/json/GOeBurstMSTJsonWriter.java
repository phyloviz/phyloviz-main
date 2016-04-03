/*-
 * Copyright (c) 2016, PHYLOViZ Team <phyloviz@gmail.com>
 * All rights reserved.
 * 
 * This file is part of PHYLOViZ <http://www.phyloviz.net/>.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
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
