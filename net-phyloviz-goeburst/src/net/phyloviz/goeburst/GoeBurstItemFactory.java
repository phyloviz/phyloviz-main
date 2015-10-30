/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.goeburst;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import net.phyloviz.algo.AbstractDistance;
import net.phyloviz.core.data.AbstractProfile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.goeburst.algorithm.GOeBurstWithStats;
import net.phyloviz.goeburst.cluster.Edge;
import net.phyloviz.goeburst.cluster.GOeBurstClusterWithStats;
import net.phyloviz.goeburst.cluster.GOeBurstNodeExtended;
import net.phyloviz.goeburst.json.GOeBurstSchemaValidator;
import net.phyloviz.goeburst.ui.OutputPanel;
import net.phyloviz.project.ProjectItem;
import net.phyloviz.project.ProjectItemFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author martanascimento
 */
@ServiceProvider(service = ProjectItemFactory.class)
public class GoeBurstItemFactory implements ProjectItemFactory {

    private final int SLV = 0, DLV = 1, TLV = 2, SAT = 3;
    private GOeBurstWithStats goeburstStats;

    @Override
    public ProjectItem loadData(String datasetName, TypingData<? extends AbstractProfile> td, String directory, String filename, AbstractDistance ad, int level) {
        GOeBurstSchemaValidator validator = new GOeBurstSchemaValidator();
        try {
            if (!validator.validate(directory, filename)) {
                return null;
            }
        } catch (IOException e) {
            Exceptions.printStackTrace(e);
        }

        GOeBurstResult goeburstItem = null;

        try {
            FileReader reader = new FileReader(new File(directory, filename));

            JSONParser parser = new JSONParser();
            JSONObject json;
            json = (JSONObject) parser.parse(reader);

            JSONArray nodesArray = (JSONArray) json.get("nodes");
            JSONArray edgesArray = (JSONArray) json.get("edges");
            JSONArray edgeTieStatsArray = (JSONArray) json.get("edgeTieStats");
            JSONArray groupsArray = (JSONArray) json.get("groups");

            goeburstStats = new GOeBurstWithStats(level);

            Map<String, GOeBurstNodeExtended> profiles = new HashMap<>();
            for (Iterator<? extends AbstractProfile> tdIt = td.iterator(); tdIt.hasNext();) {
                GOeBurstNodeExtended node = new GOeBurstNodeExtended(tdIt.next());
                profiles.put(node.getID(), node);
                goeburstStats.addSTlvs(node);
            }

            Map<Integer, GOeBurstNodeExtended> nodes = getNodes(profiles, nodesArray);

            Map<Integer, EdgeInfo> edges = createEdges(edgesArray, nodes);

            Map<Integer, GOeBurstClusterWithStats> groups = createGroups(groupsArray, edges, nodes, ad);

            updateEdgeStats(edgeTieStatsArray, groups);
            
            ArrayList<GOeBurstClusterWithStats> gList = new ArrayList<>(groups.values());
            Collections.sort(gList);
            
            OutputPanel op = new OutputPanel(datasetName + ": goeBurst");
            goeburstItem = new GOeBurstResult(gList, ad, level, op);

        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        return goeburstItem;
    }

    private Map<Integer, GOeBurstNodeExtended> getNodes(Map<String, GOeBurstNodeExtended> td, JSONArray onodes) {

        Map<Integer, GOeBurstNodeExtended> nodes = new HashMap<>();
        for (Iterator<JSONObject> nIt = onodes.iterator(); nIt.hasNext();) {
            JSONObject node = nIt.next();
            Integer uid = (int) (long) node.get("id");
            String profile = (String) node.get("profile");
            JSONArray group_lvs = (JSONArray) node.get("group-lvs");
            JSONArray graph_lvs = (JSONArray) node.get("graph-lvs");
            JSONArray slvs = (JSONArray) node.get("slvs");
            JSONArray dlvs = (JSONArray) node.get("dlvs");
            
            GOeBurstNodeExtended n = td.get(profile);
            n.setLV(SLV, (int) (long) group_lvs.get(SLV));
            n.setLV(DLV, (int) (long) group_lvs.get(DLV));
            n.setLV(TLV, (int) (long) group_lvs.get(TLV));
            n.setLV(SAT, (int) (long) group_lvs.get(SAT));
            goeburstStats.setSTlvs(n,
                    (int) (long) graph_lvs.get(SLV),
                    (int) (long) graph_lvs.get(DLV),
                    (int) (long) graph_lvs.get(TLV),
                    (int) (long) graph_lvs.get(SAT));
            for (Iterator profileIt = slvs.iterator(); profileIt.hasNext();) {
                String p = (String) profileIt.next();
                GOeBurstNodeExtended pNode = td.get(p);
                n.addSLV(pNode);
            }
            for (Iterator profileIt = dlvs.iterator(); profileIt.hasNext();) {
                String p = (String) profileIt.next();
                GOeBurstNodeExtended pNode = td.get(p);
                n.addDLV(pNode);
            }
            nodes.put(uid, n);

        }
        return nodes;
    }

    private Map<Integer, EdgeInfo> createEdges(JSONArray edgesArray, Map<Integer, GOeBurstNodeExtended> nodes) {

        HashMap<Integer, EdgeInfo> edges = new HashMap();

        for (Iterator<JSONObject> eIt = edgesArray.iterator(); eIt.hasNext();) {
            JSONObject edge = eIt.next();
            Integer id = (int) (long) edge.get("id");
            Integer u = (int) (long) edge.get("u");
            Integer v = (int) (long) edge.get("v");
            boolean visible = (Boolean) edge.get("visible");
            Integer maxTie = (int) (long) edge.get("maxTie");
            Edge<GOeBurstNodeExtended> e = new Edge<>(nodes.get(u), nodes.get(v));
            e.setVisible(visible);
            EdgeInfo ei = new EdgeInfo(e, maxTie);
            edges.put(id, ei);
        }
        return edges;
    }

    private void updateEdgeStats(JSONArray edgesStatsArray, Map<Integer, GOeBurstClusterWithStats> groups) {
        
        for (Iterator<JSONObject> esIt = edgesStatsArray.iterator(); esIt.hasNext();) {
            JSONObject edgeStat = esIt.next();
            Integer groupId = (int) (long) edgeStat.get("group");
            JSONArray ne = (JSONArray) edgeStat.get("ne");
            JSONArray fne = (JSONArray) edgeStat.get("fne");
            JSONArray xLV = (JSONArray) edgeStat.get("xLV");
            Integer withoutTies = (int) (long) edgeStat.get("withoutTies");
            
            GOeBurstClusterWithStats group = groups.get(groupId);
            group.setEdgeTieStatsWithoutTies(withoutTies);
            int i = 0;
            for(; i < ne.size(); i++){
                
                group.setEdgeTieStatsNE(i, (int)(long)ne.get(i));
                group.setEdgeTieStatsFNE(i, (int)(long)fne.get(i));
                group.setEdgeTieStatsNE(i, (int)(long)xLV.get(i));
            
            }
            for(; i < xLV.size(); i++)
                group.setEdgeTieStatsXlv(i, (int)(long)xLV.get(i));
            
        }
            
    }

    private Map<Integer, GOeBurstClusterWithStats> createGroups(JSONArray groupsArray, Map<Integer, EdgeInfo> edges, Map<Integer, GOeBurstNodeExtended> nodes, AbstractDistance ad) {

        TreeMap<Integer, GOeBurstClusterWithStats> groups = new TreeMap<>();

        for (Iterator<JSONObject> gIt = groupsArray.iterator(); gIt.hasNext();) {
            JSONObject group = gIt.next();
            Integer id = (int) (long) group.get("id");
            JSONArray g_edges = (JSONArray) group.get("edges");
            JSONArray g_nodes = (JSONArray) group.get("nodes");
            JSONArray g_maxLVs = (JSONArray) group.get("maxLVs");
            Integer maxStId = (int) (long) group.get("maxStId");

            GOeBurstClusterWithStats g_cluster = new GOeBurstClusterWithStats(goeburstStats, ad);
            g_cluster.setID(id);
            for (Iterator<Long> geIt = g_edges.iterator(); geIt.hasNext();) {
                EdgeInfo ei = edges.get((int) (long) geIt.next());
                Edge<GOeBurstNodeExtended> edge = ei.edge;
                g_cluster.setEdgeMaxTieLevel(edge, ei.maxTie);
                g_cluster.updateVisibleEdges(edge);
                g_cluster.addEdge(edge);
            }
            for (Iterator<Long> gnIt = g_nodes.iterator(); gnIt.hasNext();) {
                Integer nodeId = (int) (long) gnIt.next();
                g_cluster.addNode(nodes.get(nodeId));
            }
            int lv = 0;
            for (Iterator<Long> gLVsIt = g_maxLVs.iterator(); gLVsIt.hasNext();) {
                Integer lv_val = (int) (long) gLVsIt.next();
                g_cluster.setMaxXLV(lv++, lv_val);
            }
            //g_cluster.computeStatistics();
            g_cluster.setMaxStId(maxStId);
            groups.put(id, g_cluster);
        }

        return groups;
    }

    @Override
    public String getName() {
        return "goeBurst";
    }

    private class EdgeInfo {

        public Edge<GOeBurstNodeExtended> edge;
        public int maxTie;

        private EdgeInfo(Edge<GOeBurstNodeExtended> e, Integer maxTie) {
            this.edge = e;
            this.maxTie = maxTie;
        }

    }

}
