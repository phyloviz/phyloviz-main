/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.upgma.json;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.phyloviz.algo.AbstractDistance;
import net.phyloviz.algo.DistanceProvider;
import net.phyloviz.core.data.AbstractProfile;
import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.project.ProjectItem;
import net.phyloviz.project.ProjectItemFactory;
import net.phyloviz.upgma.UPGMAItem;
import net.phyloviz.upgma.algorithm.HierarchicalClusteringDistance;
import net.phyloviz.upgma.tree.NodeType;
import net.phyloviz.upgma.tree.UPGMALeafNode;
import net.phyloviz.upgma.tree.UPGMARoot;
import net.phyloviz.upgma.tree.UPGMAUnionNode;
import net.phyloviz.upgma.ui.OutputPanel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Marta Nascimento
 */
@ServiceProvider(service = ProjectItemFactory.class)
public class UPGMAItemFactory implements ProjectItemFactory {

    @Override
    public ProjectItem loadData(String datasetName, TypingData<? extends AbstractProfile> td, String directory, String filename) {

        JsonValidator jv = new JsonValidator();
        try{
            if(!jv.validate(directory, filename)) 
            return null;
        } catch(IOException e){
            Exceptions.printStackTrace(e);
        }
        UPGMAItem upgma = null;

        try (FileReader reader = new FileReader(new File(directory, filename))) {

            JSONParser parser = new JSONParser();
            JSONObject json;
            json = (JSONObject) parser.parse(reader);

            JSONArray leafsArr = (JSONArray) json.get("leaf");
            JSONArray unionsArr = (JSONArray) json.get("union");
            JSONObject rootObj = (JSONObject) json.get("root");

            Map<Integer, UPGMALeafNode> leafs = createLeafs(td, leafsArr);

            Map<Integer, UPGMAUnionNode> unions = new HashMap();
            if (unionsArr != null) {
                unions = createUnions(unionsArr, leafs);
            }

            UPGMARoot root = createRoot(rootObj, leafs, unions);

            HierarchicalClusteringDistance ad = getDistanceProvider(filename, td);
            OutputPanel op = new OutputPanel(datasetName + ": Hierarchical Clustering (" + ad.toString() + ") Output");
            upgma = new UPGMAItem(root, ad, op);

        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }

        return upgma;
    }

    private Map<Integer, UPGMALeafNode> createLeafs(TypingData<? extends AbstractProfile> td, JSONArray leafsArr) {

        Map<Integer, UPGMALeafNode> leafs = new HashMap(leafsArr.size());
        Map<Integer, Profile> profiles = new HashMap(td.size());

        for (Iterator<? extends AbstractProfile> it = td.iterator(); it.hasNext();) {
            Profile p = it.next();
            profiles.put(Integer.parseInt(p.getID()), p);
        }
        for (Iterator<JSONObject> it = leafsArr.iterator(); it.hasNext();) {
            JSONObject l = it.next();
            Integer p = (int) (long) l.get("profile");
            Integer uid = (int) (long) l.get("uid");
            leafs.put(uid, new UPGMALeafNode(uid, profiles.get(p), td.size(), uid, null));
        }

        return leafs;
    }

    private Map<Integer, UPGMAUnionNode> createUnions(JSONArray union, Map<Integer, UPGMALeafNode> leafs) {
        int size = leafs.size();
        HashMap<Integer, UPGMAUnionNode> unions = new HashMap(size);

        for (Iterator<JSONObject> it = union.iterator(); it.hasNext();) {
            JSONObject l = it.next();
            float distance = (float) (double) l.get("distance");
            Integer uid = (int) (long) l.get("uid");
            Integer n1 = (int) (long) l.get("leftID");
            Integer n2 = (int) (long) l.get("rightID");

            NodeType node1 = n1 >= size ? unions.get(n1) : leafs.get(n1);
            NodeType node2 = n2 >= size ? unions.get(n2) : leafs.get(n2);
            unions.put(uid, new UPGMAUnionNode(uid, node1, node2, distance, leafs.size(), node1.getNodeIdx() - 1, null));
        }

        return unions;
    }

    private UPGMARoot createRoot(JSONObject root, Map<Integer, UPGMALeafNode> leafs, Map<Integer, UPGMAUnionNode> unions) {
        float distance = (float) (double) root.get("distance");
        Integer n1 = (int) (long) root.get("left");
        Integer n2 = (int) (long) root.get("right");

        int size = leafs.size();
        NodeType node1 = n1 >= size ? unions.get(n1) : leafs.get(n1);
        NodeType node2 = n2 >= size ? unions.get(n2) : leafs.get(n2);

        UPGMARoot r = new UPGMARoot(node1, node2, distance);
        return r;
    }

    @Override
    public String getName() {
        return "upgma";
    }

    private HierarchicalClusteringDistance getDistanceProvider(String filename, TypingData<? extends Profile> td) throws Exception {
        String[] distanceNames = filename.split("\\.");
        String distanceName = distanceNames[distanceNames.length-2];
        int lenght = distanceName.length();
        distanceName = new StringBuilder(distanceName).deleteCharAt(lenght - 1).toString();

        Collection<? extends DistanceProvider> dp = Lookup.getDefault().lookupAll(DistanceProvider.class);
        Iterator<? extends DistanceProvider> ir = dp.iterator();
        while (ir.hasNext()) {
            AbstractDistance ad = ir.next().getDistance(td);
            if (ad != null){
                String adName = ad.toString().split(" ")[0].toLowerCase();
                if(ad instanceof HierarchicalClusteringDistance && distanceName.equals(adName))
                    return (HierarchicalClusteringDistance)ad;
            }
        }
        throw new Exception(distanceName + " Distance Provider Not Found!");
    }

}
