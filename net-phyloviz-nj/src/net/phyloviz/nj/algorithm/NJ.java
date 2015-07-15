package net.phyloviz.nj.algorithm;

import java.util.Iterator;
import net.phyloviz.core.data.AbstractProfile;
import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.nj.tree.IndexList;
import net.phyloviz.nj.tree.IndexList.IndexNode;
import net.phyloviz.nj.tree.NJLeafNode;
import net.phyloviz.nj.tree.NJRoot;
import net.phyloviz.nj.tree.NJUnionNode;
import net.phyloviz.nj.tree.NodeType;
import net.phyloviz.nj.ui.OutputPanel;
import net.phyloviz.upgmanjcore.distance.ClusteringDistance;

/**
 *
 * @author Adriano
 */
public abstract class NJ {
    
    private final ClusteringDistance<NJLeafNode> ad;
    private final TypingData<? extends Profile> td;
    
    private IndexList nodeList;
    
    private NodeType[] nodeArray; 
    private NJRoot root;
    private final OutputPanel op;
    private int nodeIdx;

    public NJ(TypingData<? extends Profile> inTd, ClusteringDistance<NJLeafNode> inAd, OutputPanel op) {
        nodeList = new IndexList();
        nodeArray = new NodeType[inTd.size()];
        td = inTd;
        ad = inAd;
        this.op = op;
        nodeIdx = inTd.size() + 1;
    }
    
    public NJRoot generateTree(){
        createDistanceMatrix();
        
        while(nodeList.getSize() > 2){ 
            //calculates Q
            CalculateMin(nodeArray, nodeList);
            //geting minQ
            Wrapper w = getMinPair(nodeList, nodeArray);
            //creating union
            NJUnionNode union = createUnion(nodeArray, w, nodeList, op, nodeIdx++);
            //third
            NodeType oldNtLeft = nodeArray[w.c];
            NodeType oldNtRight = nodeArray[w.l];
            float dst = nodeArray[w.c].getPositionDistanceLine(w.l);
            op.append("Created " + oldNtLeft.getID() + " -> " + oldNtRight.getID() + "\n");
            
            calculateNewDistances(oldNtLeft, oldNtRight, union, dst, w.c, w.l);
            
            alterIndexesElements(w.minLNode, oldNtLeft, oldNtRight, union, w.c, w.l);
        }
        //get last edge and help garbage collector
        Iterator<IndexNode> iter = nodeList.iterator();
        int minC = iter.next().index, minL = iter.next().index;
        NodeType nt1 = nodeArray[minC];     NodeType nt2 = nodeArray[minL];
        nodeArray = null;
        nodeList = null;
        root = new NJRoot(nt1, nt2, nt1.getPositionDistanceLine(minL));
        clean(nt1, nt2);
        return root;
    }
    private void clean(NodeType nt1, NodeType nt2){             //helps gasbage collector
        nt1.empty();
        nt2.empty();
    }
    private void createDistanceMatrix() {        
        int idx = 0;
        //creating leafs
        for (Iterator<? extends AbstractProfile> it = td.iterator(); it.hasNext(); idx++) {
            NJLeafNode leaf = new NJLeafNode(it.next(), td.size(), idx, nodeList.Add());
            nodeArray[idx] = leaf;
        }
        //generating matrix distances
        Iterator<IndexNode> outerIter = nodeList.iterator();
        while(outerIter.hasNext()){                                 //foreach column
            idx = outerIter.next().index;
            NJLeafNode leaf = (NJLeafNode) nodeArray[idx];
            Iterator<IndexNode> iter = leaf.in.iterator();
            while (iter.hasNext()) {                                //and foreach line, calculates distance
                IndexNode in = iter.next();
                float distance = ad.level((NJLeafNode)nodeArray[in.index], leaf); 
                leaf.setDistance(in, distance);
                nodeArray[in.index].addToDistance(distance);
            }
        }
    }

    private void alterIndexesElements(IndexNode minLNode, NodeType oldNtLeft, NodeType oldNtRight, NJUnionNode union, int minC, int minL) {
        clean(oldNtLeft, oldNtRight);
        nodeArray[minC] = union;
        nodeArray[minL] = null;                             //to help garbage collector release memory
        minLNode.remove();
    }

    private void calculateNewDistances(NodeType oldNtLeft, NodeType oldNtRight, NJUnionNode union, float dst, int minC, int minL) {
        Iterator<IndexNode> iter = nodeList.iterator();
        while(iter.hasNext()){
            IndexNode idx = iter.next();
            if(idx.index == minC || idx.index == minL) continue;
            NodeType nt = nodeArray[idx.index];
            float distanceX_U1 = nt.getNodeIdx() < oldNtLeft.getNodeIdx() ? nt.getPositionDistanceLine(minC) : oldNtLeft.getPositionDistanceLine(idx.index);
            float distanceY_U1 = nt.getNodeIdx() < oldNtRight.getNodeIdx() ? nt.getPositionDistanceLine(minL) : oldNtRight.getPositionDistanceLine(idx.index);
            float newDistance = (distanceX_U1 + distanceY_U1 - dst) / 2;

            if(nt.getNodeIdx() < union.getNodeIdx()){
                nt.setandRemoveDistance(minC, minL, newDistance);    //adds Union values but removes two old values
                union.addToDistance(newDistance);
            } else {
                union.setDistance(idx, newDistance);
                //also need to change in the element idx
                nt.addToDistance(newDistance - distanceX_U1 - distanceY_U1);
            }
        }
    }
    protected Wrapper getMinPair(IndexList nodeList, NodeType[] nodeArray) {
        Iterator<IndexNode> iter;
        int minC = 0, minL = 0;
        IndexList.IndexNode minLNode = null;
        float smallestDst = Float.POSITIVE_INFINITY;
        iter = nodeList.iterator();
        while(iter.hasNext()){
            int c = iter.next().index;
            NodeType nt = nodeArray[c];
            if(nt.getMinQ() < smallestDst){
                minC = c;
                minLNode = nt.getMinL();
                minL = minLNode.index;
                smallestDst = nt.getMinQ();
            }
        }   
        return new Wrapper(minC, minL, minLNode);
    }
    
    protected abstract void CalculateMin(NodeType[] nodeArray, IndexList nodeList);
    protected abstract NJUnionNode createUnion(NodeType[] nodeArray, Wrapper w, IndexList nodeList, OutputPanel op, int nodeIdx);
    public abstract float calculateMin(NodeType[] nodeArray, int nodeIdx, int l, int total);
    
    protected class Wrapper{
        public final int c, l;
        public final IndexNode minLNode;
        protected Wrapper(int c, int l, IndexNode in){
            this.c = c;
            this.l = l;
            this.minLNode = in;
        }
    }
}
