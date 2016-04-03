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

import net.phyloviz.upgmanjcore.json.JsonSaver;
import net.phyloviz.upgma.json.UPGMAJsonWriter;
import net.phyloviz.upgmanjcore.tree.IndexList.NodeIterator;

/**
 *
 * @author Adriano
 */
public class UPGMAUnionNode extends NodeType{
    
    //public static int unionId;
    
    private final float distance;		// The full-distance of the node
    private final NodeType n1, n2;	// Left and right children
    private int size;
    
    public UPGMAUnionNode(int id, NodeType n1, NodeType n2, float distance, int size, int nodeIdx, NodeIterator in){
        super(id, size, nodeIdx, "Union", in);
        this.n1 = n1;
        this.n2 = n2;
        this.distance = distance;
        this.size = n1.getSize() + n2.getSize();
    }
    public UPGMAUnionNode(int id, NodeType n1, NodeType n2, float distance, int size, int nodeIdx){
        super(id, size, nodeIdx, "Union");
        this.n1 = n1;
        this.n2 = n2;
        this.distance = distance;
        this.size = n1.getSize() + n2.getSize();
    }
    
    @Override
    public String toString(){
        String s = getName() + "\n";
        s += "[ " + n1 + " -> " + n2 + "]\n";
        return s;
    }
    @Override
    public void saveData(JsonSaver js) {
        n1.saveData(js);
        n2.saveData(js);
        ((UPGMAJsonWriter)js.writer).putUnion(id, distance, n1.id, n2.id);
    }
    @Override
    public String getDisplayName() {
        return "U";// + id;
    }
    public NodeType getNodeLeft(){
        return n1;
    }
    public NodeType getNodeRight(){
        return n2;
    }
    public double getDistance(){
        return distance;
    }
    @Override
    public int getSize(){
        return size;
    }
}
