/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.goeburst.tree;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.phyloviz.algo.AbstractDistance;
import net.phyloviz.algo.Edge;
import net.phyloviz.core.data.AbstractProfile;
import net.phyloviz.core.data.DataSet;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.goeburst.cluster.GOeBurstClusterWithStats;
import net.phyloviz.goeburst.cluster.GOeBurstNodeExtended;
import net.phyloviz.goeburst.json.GOeBurstMSTSchemaValidator;
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
public class GOeBurstMSTItemFactory implements ProjectItemFactory {

    @Override
    public ProjectItem loadData(DataSet dataset, TypingData<? extends AbstractProfile> td, String directory, String filename, AbstractDistance ad, int level) {
        GOeBurstMSTSchemaValidator validator = new GOeBurstMSTSchemaValidator();
        try {
            if (!validator.validate(directory, filename)) {
                return null;
            }
        } catch (IOException e) {
            Exceptions.printStackTrace(e);
        }

        GOeBurstMSTResult goeburstItem = null;

        try {
            FileReader reader = new FileReader(new File(directory, filename));

            JSONParser parser = new JSONParser();
            JSONObject json;
            json = (JSONObject) parser.parse(reader);

            JSONArray nodesArray = (JSONArray) json.get("nodes");
            JSONArray edgesArray = (JSONArray) json.get("edges");

            Map<String, GOeBurstNode> profiles = new HashMap<>();
            for (Iterator<? extends AbstractProfile> tdIt = td.iterator(); tdIt.hasNext();) {
                GOeBurstNode node = new GOeBurstNode(tdIt.next());
                profiles.put(node.getID(), node);
            }

            Map<Integer, GOeBurstNode> nodes = getNodes(profiles, nodesArray);

            Collection<Edge<GOeBurstNode>> tree = createEdges(edgesArray, nodes);
            
            OutputPanel op = new OutputPanel(dataset.toString() + ": goeBURST Full MST");
            goeburstItem = new GOeBurstMSTResult(dataset, tree, ad, op);

        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        return goeburstItem;
    }

    private Map<Integer, GOeBurstNode> getNodes(Map<String, GOeBurstNode> td, JSONArray onodes) {

        Map<Integer, GOeBurstNode> nodes = new HashMap<>();
        for (Iterator<JSONObject> nIt = onodes.iterator(); nIt.hasNext();) {
            JSONObject node = nIt.next();
            Integer uid = (int) (long) node.get("id");
            String profile = (String) node.get("profile");
            JSONArray group_lvs = (JSONArray) node.get("group-lvs");
            
            GOeBurstNode n = td.get(profile);
            n.lv = new int[group_lvs.size()];
            for(int i = 0; i < group_lvs.size(); i++ )
                n.lv[i] = (int) (long) group_lvs.get(i);
            
            nodes.put(uid, n);

        }
        return nodes;
    }

    private Collection<Edge<GOeBurstNode>> createEdges(JSONArray edgesArray, Map<Integer, GOeBurstNode> nodes) {

        List<Edge<GOeBurstNode>> edges = new ArrayList<>();

        for (Iterator<JSONObject> eIt = edgesArray.iterator(); eIt.hasNext();) {
            JSONObject edge = eIt.next();
            Integer u = (int) (long) edge.get("u");
            Integer v = (int) (long) edge.get("v");
            edges.add(new Edge(nodes.get(u), nodes.get(v)));
        }
        return edges;
    }

    @Override
    public String getName() {
        return "goeBurst-fullmst";
    }

}
