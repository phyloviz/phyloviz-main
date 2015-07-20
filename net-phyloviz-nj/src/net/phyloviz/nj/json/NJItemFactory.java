package net.phyloviz.nj.json;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.phyloviz.upgmanjcore.AbstractClusteringMethod;
import net.phyloviz.algo.AbstractDistance;
import net.phyloviz.upgmanjcore.ClusteringMethodProvider;
import net.phyloviz.algo.DistanceProvider;
import net.phyloviz.core.data.AbstractProfile;
import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.nj.AgglomerativeClusteringMethod;
import net.phyloviz.upgmanjcore.distance.ClusteringDistance;
import net.phyloviz.nj.tree.NJLeafNode;
import net.phyloviz.nj.tree.NJRoot;
import net.phyloviz.nj.tree.NJUnionNode;
import net.phyloviz.nj.tree.NeighborJoiningItem;
import net.phyloviz.nj.tree.NodeType;
import net.phyloviz.nj.ui.OutputPanel;
import net.phyloviz.project.ProjectItem;
import net.phyloviz.project.ProjectItemFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = ProjectItemFactory.class)
public class NJItemFactory implements ProjectItemFactory {

    @Override
    public ProjectItem loadData(String datasetName, TypingData<? extends AbstractProfile> td, String directory, String filename, String distance) {

        JsonValidator validator = new JsonValidator();
        try {
            if (!validator.validate(directory, filename)) {
                return null;
            }
        } catch (IOException e) {
            Exceptions.printStackTrace(e);
        }

        NeighborJoiningItem njItem = null;

        try (FileReader reader = new FileReader(new File(directory, filename))) {

            JSONParser parser = new JSONParser();
            JSONObject json;
            json = (JSONObject) parser.parse(reader);

            JSONArray leafsArr = (JSONArray) json.get("leaf");
            JSONArray unionsArr = (JSONArray) json.get("union");
            JSONObject rootObj = (JSONObject) json.get("root");

            Map<Integer, NJLeafNode> leafs = createLeafs(td, leafsArr);

            Map<Integer, NJUnionNode> unions = new HashMap();
            if (unionsArr != null) {
                unions = createUnions(unionsArr, leafs);
            }
            NJRoot root = createRoot(rootObj, leafs, unions);
            
            AgglomerativeClusteringMethod cm = getMethodProvider(filename, td);
            AbstractDistance ad = (AbstractDistance) getDistanceProvider(distance, td);

            OutputPanel op = new OutputPanel(datasetName + ": Neighbor-Joining");
            njItem = new NeighborJoiningItem(root, ad, cm, op);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        } 
        return njItem;
    }

    private Map<Integer, NJLeafNode> createLeafs(TypingData<? extends AbstractProfile> td, JSONArray leafsArr) {

        Map<Integer, NJLeafNode> leafs = new HashMap(leafsArr.size());
        Map<Integer, Profile> profiles = new HashMap(td.size());

        for (Iterator<? extends AbstractProfile> it = td.iterator(); it.hasNext();) {
            Profile p = it.next();
            profiles.put(p.getUID(), p);
        }
        for (Iterator<JSONObject> it = leafsArr.iterator(); it.hasNext();) {
            JSONObject l = it.next();
            Integer p = (int) (long) l.get("profile");
            Integer uid = (int) (long) l.get("id");
            double x = (double) l.get("x");
            double y = (double) l.get("y");
            leafs.put(uid, new NJLeafNode(profiles.get(p), td.size(), uid, null, x, y));
        }
        return leafs;
    }

    private Map<Integer, NJUnionNode> createUnions(JSONArray union, Map<Integer, NJLeafNode> leafs) {
        int size = leafs.size();
        HashMap<Integer, NJUnionNode> unions = new HashMap(size);
        for (Iterator<JSONObject> it = union.iterator(); it.hasNext();) {
            JSONObject l = it.next();
            Integer id = (int) (long) l.get("id");
            Integer nl = (int) (long) l.get("left");
            Integer nr = (int) (long) l.get("right");
            float dl = (float) (double) l.get("distanceLeft");
            float dr = (float) (double) l.get("distanceRight");
            double x = (double) l.get("x");
            double y = (double) l.get("y");

            NodeType node1 = nl >= size ? unions.get(nl) : leafs.get(nl);
            NodeType node2 = nr >= size ? unions.get(nr) : leafs.get(nr);
            unions.put(id, new NJUnionNode(node1, dl, node2, dr, size, 0, null, id, x, y));
        }
        return unions;
    }

    private NJRoot createRoot(JSONObject root, Map<Integer, NJLeafNode> leafs, Map<Integer, NJUnionNode> unions) {
        float distance = (float) (double) root.get("distance");
        Integer n1 = (int) (long) root.get("left");
        Integer n2 = (int) (long) root.get("right");

        int size = leafs.size();
        NodeType node1 = n1 >= size ? unions.get(n1) : leafs.get(n1);
        NodeType node2 = n2 >= size ? unions.get(n2) : leafs.get(n2);

        NJRoot r = new NJRoot(node1, node2, distance);
        return r;
    }
    
    private AgglomerativeClusteringMethod getMethodProvider(String filename, TypingData<? extends Profile> td) throws Exception {
        
        String[] methodNames = filename.split("\\.");
        String methodName = methodNames[methodNames.length - 2];
        int lenght = methodName.length();
        methodName = new StringBuilder(methodName).deleteCharAt(lenght - 1).toString();

        Collection<? extends ClusteringMethodProvider> dp = Lookup.getDefault().lookupAll(ClusteringMethodProvider.class);
        Iterator<? extends ClusteringMethodProvider> ir = dp.iterator();
        while (ir.hasNext()) {
            AbstractClusteringMethod cm = ir.next().getMethod(td);
            if (cm != null) {
                String cmName = cm.toString().split(" ")[0].toLowerCase();
                if (cm instanceof AgglomerativeClusteringMethod && methodName.equals(cmName)) {
                    return (AgglomerativeClusteringMethod) cm;
                }
            }
        }
        throw new Exception(methodName + " Method Provider Not Found!");
    }

    private AbstractDistance getDistanceProvider(String distance, TypingData<? extends Profile> td) throws Exception {
        String[] distanceNames = distance.split("\\.");
        String distanceName = distanceNames[distanceNames.length - 1];

        Collection<? extends DistanceProvider> dp = Lookup.getDefault().lookupAll(DistanceProvider.class);
        Iterator<? extends DistanceProvider> ir = dp.iterator();
        while (ir.hasNext()) {
            AbstractDistance ad = ir.next().getDistance(td);
            if (ad != null) {
                String adName = ad.toString().split(" ")[0].toLowerCase();
                if (ad instanceof ClusteringDistance && distanceName.equals(adName)) {
                    return (ClusteringDistance) ad;
                }
            }
        }
        throw new Exception(distanceName + " Distance Provider Not Found!");
    }
    
    @Override
    public String getName() {
        return "Neighbor-Joining";
    }
}
