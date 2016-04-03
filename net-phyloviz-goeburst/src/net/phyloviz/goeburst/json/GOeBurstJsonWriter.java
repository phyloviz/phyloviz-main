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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.phyloviz.goeburst.cluster.Edge;
import net.phyloviz.goeburst.cluster.GOeBurstClusterWithStats;
import net.phyloviz.goeburst.cluster.GOeBurstNodeExtended;
import net.phyloviz.upgmanjcore.json.IJsonWriter;

/**
 *
 * @author martanascimento
 */
public class GOeBurstJsonWriter implements IJsonWriter{
    
    private final Collection<GOeBurstClusterWithStats> clustering;
    private final StringBuilder edges = new StringBuilder();
    private final StringBuilder groups = new StringBuilder();
    private final StringBuilder nodes = new StringBuilder();
    private final StringBuilder edgeStats = new StringBuilder();
    
    public GOeBurstJsonWriter(Collection<GOeBurstClusterWithStats> clustering){
        this.clustering = clustering;
    }
   
    private void updateClusterInfo(){
        int nodeUID = 0;
        int edgeUID = 0;
        int groupUID = 0;
        Map<String, Integer> nodesList;//<Profile, nodeUID>
        Map<String, Integer> tmpNodesList; //<Profile, nodeUID>
        for (GOeBurstClusterWithStats cluster : clustering) {
            
            nodesList = new HashMap<>();
            tmpNodesList = new HashMap<>();
            
            for(GOeBurstNodeExtended node : cluster.getSTs()){
                String profile = node.getProfile().getID();
                if(nodesList.get(profile) != null)
                    continue;
                
                nodesList.put(profile, nodeUID);
                
                int graph_slv = cluster.getSTxLV(node, 0);
                int graph_dlv = cluster.getSTxLV(node, 1);
                int graph_tlv = cluster.getSTxLV(node, 2);
                int graph_sat = cluster.getSTxLV(node, 3);
                
                int group_slv = node.getLV(0);
                int group_dlv = node.getLV(1);
                int group_tlv = node.getLV(2);
                int group_sat = node.getLV(3);
                
                Collection<GOeBurstNodeExtended> slvs = node.getSLVs();
                Collection<GOeBurstNodeExtended> dlvs = node.getDLVs();
                
                String n = "\t\t{"
                    + "\"id\": " + nodeUID++ + ", "    
                    + "\"profile\": \"" + profile + "\", "
                    + "\"graph-lvs\": [ " + graph_slv + ", " + graph_dlv + ", " + graph_tlv + ", " + graph_sat + "], "
                    + "\"group-lvs\": [ " + group_slv + ", " + group_dlv + ", " + group_tlv + ", " + group_sat + "], "
                    + "\"slvs\": " + getProfileIdsArray(slvs) + ", "   
                    + "\"dlvs\": " + getProfileIdsArray(dlvs) + "},\n ";
                nodes.append(n);
            }
            tmpNodesList.putAll(nodesList);
            
            String group = "\t\t{"
                        + "\"id\": " + groupUID + ", "
                        + "\"edges\": [ ";
                  
            for(Edge<GOeBurstNodeExtended> e : cluster.getEdges()){
                String edge = "\t\t{"
                        + "\"id\": " + edgeUID + ", ";
                        
                Integer uid = nodesList.get(e.getU().getID());
                Integer vid = nodesList.get(e.getV().getID());
                
                edge +=   "\"u\": " + uid + ", "
                        + "\"v\": " + vid + ", "
                        + "\"visible\": " + e.visible() + ", "
                        + "\"maxTie\": " + cluster.getEdgeMaxTieLevel(e) + "},\n";
                edges.append(edge);
                group += edgeUID++ + ",";
                
                tmpNodesList.remove(e.getU().getID());
                tmpNodesList.remove(e.getV().getID());
            }
            groups.append(group);
            groups.replace(groups.length()-1, groups.length(), "], ");
            
            //add singletons
            group = "\"nodes\": [ ";
            for (Integer nodeId : tmpNodesList.values()) {
                group += String.valueOf(nodeId) + ",";
            }
            groups.append(group);
            groups.replace(groups.length()-1, groups.length(), "], ");
            
            group = "\"maxLVs\": [ ";
            for(int i=0; i<=GOeBurstClusterWithStats.MAXLV; i++){
                group += String.valueOf(cluster.getMaxXLV(i)) + ",";
            }
            groups.append(group);
            groups.replace(groups.length()-1, groups.length(), "], ");
            
            group = "\"maxStId\": " + cluster.getMaxStId() +"},\n"; 
            groups.append(group);
            
            
            edgeStats.append("\t\t{\"group\": ").append(groupUID).append(", ");
            edgeStats.append("\"ne\": ").append(Arrays.toString(cluster.getEdgeTieStatsNE())).append(", ");   
            edgeStats.append("\"fne\": ").append(Arrays.toString(cluster.getEdgeTieStatsFNE())).append(", ");   
            edgeStats.append("\"xLV\": ").append(Arrays.toString(cluster.getEdgeTieStatsXlv())).append(", ");   
            edgeStats.append("\"withoutTies\": ").append(cluster.getEdgeTieStatsWithoutTies()).append("},\n");   
            
            groupUID++;
        }
    }

    @Override
    public HashMap<String, String> getBuildersMap(){
        HashMap<String, String> map = new HashMap<>();
        updateClusterInfo();
        map.put("nodes", nodes.toString());
        map.put("edges", edges.toString());
        map.put("groups", groups.toString());
        map.put("edgeTieStats", edgeStats.toString());
        return map;
    }
    @Override
    public String getRoot() { return ""; }

    private String getProfileIdsArray(Collection<GOeBurstNodeExtended> lvsList) {
        StringBuilder lvs = new StringBuilder("[ ");
        for (GOeBurstNodeExtended node : lvsList) {
            lvs.append("\"").append(node.getID()).append("\",");
        }
        lvs.replace(lvs.length()-1, lvs.length(), "]");
        return lvs.toString();
    }
    
}
