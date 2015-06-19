package net.phyloviz.upgma.tree;

import net.phyloviz.core.data.Profile;
import net.phyloviz.upgma.json.UPGMAJsonWriter;
import net.phyloviz.upgma.json.JsonSaver;
import net.phyloviz.upgma.tree.IndexListNode.NodeIterator;

/**
 *
 * @author Adriano
 */
public class UPGMALeafNode extends NodeType implements Profile, Comparable<UPGMALeafNode>{
    
    public final Profile p;
    
    public UPGMALeafNode(int id, Profile profile, int size, int leafIdx, NodeIterator in){
        super(id, size, leafIdx, "Leaf", in);
        this.p = profile;
    }

    @Override
    public int profileLength() {
        return p.profileLength();
    }

    @Override
    public String getValue(int idx) {
        return p.getValue(idx);
    }

    @Override
    public int getUID() {
        return p.getUID();
    }

    @Override
    public String getID() {
        return p.getID();
    }

    @Override
    public int getFreq() {
        return p.getFreq();
    }

    @Override
    public void setFreq(int freq) {
        p.setFreq(freq);
    }

    @Override
    public String get(int idx) {
        return p.get(idx);
    }

    @Override
    public int length() {
        return p.length();
    }

    @Override
    public int weight() {
        return p.weight();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int compareTo(UPGMALeafNode o) {
        return super.compareTo(o);
    }

    @Override
    public void saveData(JsonSaver js) {
        ((UPGMAJsonWriter)js.njtj).putLeaf(id, p.getUID() + "");
    }

    @Override
    public String getDisplayName() {
        return p.getID();
    }
}
