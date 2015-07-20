package net.phyloviz.upgma.algorithm;

import java.util.Iterator;
import net.phyloviz.core.data.AbstractProfile;
import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.upgma.HierarchicalClusteringMethod;
import net.phyloviz.upgmanjcore.tree.IndexList;
import net.phyloviz.upgmanjcore.tree.IndexList.IndexNode;
import net.phyloviz.upgma.tree.UPGMALeafNode;
import net.phyloviz.upgma.tree.UPGMARoot;
import net.phyloviz.upgma.tree.UPGMAUnionNode;
import net.phyloviz.upgma.tree.NodeType;
import net.phyloviz.upgma.ui.OutputPanel;
import net.phyloviz.upgmanjcore.distance.ClusteringDistance;

/**
 *
 * @author Adriano
 */
public class UPGMA {

    private final HierarchicalClusteringMethod cm;
    private final ClusteringDistance<UPGMALeafNode> ad;
    private final TypingData<? extends Profile> td;

    private IndexList nodeList;

    private NodeType[] nodeArray;
    private UPGMARoot root;
    private final OutputPanel op;

    private float minDistance = Float.POSITIVE_INFINITY;
    private IndexNode minC, minL;
    private int ties = 0;
    private int unionId;

    public UPGMA(TypingData<? extends Profile> inTd, ClusteringDistance<UPGMALeafNode> oad, HierarchicalClusteringMethod ocm, OutputPanel op) {
        this.nodeList = new IndexList();
        this.nodeArray = new NodeType[inTd.size()];
        this.td = inTd;
        this.ad = oad;
        this.cm = ocm;
        this.op = op;

        int idx = 0, size = td.size();
        for (Iterator<? extends AbstractProfile> it = td.iterator(); it.hasNext(); idx++) {
            nodeArray[idx] = new UPGMALeafNode(idx , it.next(), size, idx, nodeList.Add());
        }
        unionId = idx;
    }

    private void findMinimumDistance() {

        minDistance = Float.POSITIVE_INFINITY;

        Iterator<IndexNode> iter = nodeList.iterator();
        while (iter.hasNext()) {
            IndexNode inInner = iter.next();
            NodeType nt = nodeArray[inInner.index];

            if (nt.getMinDistance() == minDistance) {
                ties++;
                continue;
            }

            if (nt.getMinDistance() < minDistance) {
                this.minC = inInner;
                this.minL = nt.getMinDistanceIndex();
                this.minDistance = nt.getMinDistance();
            }
        }
    }

    private void join(NodeType n1, NodeType n2) {
        
        UPGMAUnionNode union = new UPGMAUnionNode(unionId++, n1, n2, minDistance / 2, nodeArray.length, n1.getNodeIdx() - 1, n1.in);
        
        op.append("joining: " + union.getName() + " [ " + n1.getName() + " -> " + n2.getName() + "]\n");
        //change distances in distance matrix
        Iterator<IndexNode> iter = nodeList.iterator();
        while (iter.hasNext()) {

            IndexNode in = iter.next();
            int idx = in.index;
            if (idx == minC.index || idx == minL.index) {
                continue;                //deleted nodes
            }

            NodeType current = nodeArray[idx];

            float d1, d2;
            if (current.getNodeIdx() < n1.getNodeIdx()) {
                d1 = current.getDistanceTo(minC.index);
            } else {
                d1 = n1.getDistanceTo(idx);
            }
            if (current.getNodeIdx() < n2.getNodeIdx()) {
                d2 = current.getDistanceTo(minL.index);
            } else {
                d2 = n2.getDistanceTo(idx);
            }

            float distance = cm.getLinkageCriteria(d1, d2);

            if (current.getNodeIdx() < union.getNodeIdx()) {
                current.setDistance(minC, distance);
                current.removeDistance(minL.index, nodeList.getSize(), nodeArray);
            } else {
                union.setDistance(in, distance);
                current.removeDistance(minL.index, nodeList.getSize(), nodeArray);
            }
        }

        clean(n1, n2);                      //cleans distances from nodes (helps GC)
        nodeArray[minC.index] = union;
        nodeArray[minL.index] = null;       //to help GC release memory
        minL.remove();                      //remove index from IndexNode list
    }

    public UPGMARoot execute() {

        createDistanceMatrix();

        while (nodeList.getSize() > 2) {

            NodeType oldNtLeft = nodeArray[minC.index];
            NodeType oldNtRight = nodeArray[minL.index];
            
            join(oldNtLeft, oldNtRight);
            
            findMinimumDistance();
            op.flush();
        }
        //get last edge and help garbage collector
        Iterator<IndexNode> iter = nodeList.iterator();
        minC = iter.next();
        minL = iter.next();
        NodeType nt1 = nodeArray[minC.index];
        NodeType nt2 = nodeArray[minL.index];
        root = new UPGMARoot(nt1, nt2, nt1.getDistanceTo(minL.index) / 2);

        op.append("\nRoot: [" + nt1.getName() +"->"+ nt2.getName() + "]");
        
        nodeArray = null;
        nodeList = null;
        clean(nt1, nt2);

        op.append("\nTotal Number of Distance Ties: " + ties +".\n");
        return root;
    }

    private void clean(NodeType nt1, NodeType nt2) {
        nt1.empty();
        nt2.empty();
    }

    private void createDistanceMatrix() {
        Iterator<IndexNode> outerIter = nodeList.iterator();
        while (outerIter.hasNext()) {
            IndexNode outerNode = outerIter.next();
            int idx = outerNode.index;
            UPGMALeafNode leaf = (UPGMALeafNode) nodeArray[idx];
            Iterator<IndexNode> iter = leaf.in.iterator();
            while (iter.hasNext()) {
                IndexNode in = iter.next();
                UPGMALeafNode leafToAdd = (UPGMALeafNode) nodeArray[in.index];
                float distance = ad.level(leafToAdd, leaf);
                leaf.setDistance(in, distance);
                if (distance == minDistance) {
                    ties++;
                    continue;
                }
                if (distance < minDistance) {
                    minDistance = distance;
                    minC = outerNode;
                    minL = in;
                }
            }
        }
    }
}
