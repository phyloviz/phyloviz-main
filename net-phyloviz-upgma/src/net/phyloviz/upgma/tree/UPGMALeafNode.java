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

package net.phyloviz.upgma.tree;

import net.phyloviz.core.data.Profile;
import net.phyloviz.upgma.json.UPGMAJsonWriter;
import net.phyloviz.upgmanjcore.json.JsonSaver;
import net.phyloviz.upgmanjcore.tree.IndexList.NodeIterator;

/**
 *
 * @author Adriano
 */
public class UPGMALeafNode extends NodeType implements Profile, Comparable<UPGMALeafNode>{
    
    public final Profile p;
    public final int size = 1;
    
    public UPGMALeafNode(int id, Profile profile, int size, int leafIdx, NodeIterator in){
        super(id, size, leafIdx, "Leaf", in);
        this.p = profile;
    }
    public UPGMALeafNode(int id, Profile profile, int size, int leafIdx){
        super(id, size, leafIdx, "Leaf");
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
        ((UPGMAJsonWriter)js.writer).putLeaf(id, p.getID());
    }

    @Override
    public String getDisplayName() {
        return p.getID();
    }
    
    @Override
    public int getSize(){
        return size;
    }
}
