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

package net.phyloviz.nj.tree;

import net.phyloviz.nj.json.NeighborJoiningJsonWriter;
import net.phyloviz.upgmanjcore.json.JsonSaver;
import net.phyloviz.upgmanjcore.tree.IndexList.NodeIterator;

/**
 *
 * @author Adriano
 */
public class NJUnionNode extends NodeType{
    
    public final NodeType n1;
    public final NodeType n2;
    public final float distance1;
    public final float distance2;
    
    public NJUnionNode(NodeType n1, float distance1, NodeType n2, float distance2, int size, int nodeIdx, NodeIterator in, int nodeId) {
        super(size, nodeIdx, "Union " + nodeId, in, null, nodeId);
        this.n1 = n1;
        this.n2 = n2;
        this.distance1 = distance1;
        this.distance2 = distance2;
    }
    
    @Override
    public String toString(){
        return this.getName();
    }
    @Override public int profileLength(){
        return 0;
    }
    @Override public String getValue(int idx){
        return "";
    }
    @Override public int getUID(){
        return this.id;
    }
    @Override public String getID(){
        return name;
    }
    @Override public int getFreq(){
        return 1;
    }
    @Override public String get(int idx){
        return "";
    }
    @Override public void setFreq(int freq){}
    @Override public int length(){
        return this.id;
    }
    @Override public int weight(){
        return this.id;
    }
    @Override
    public void saveData(JsonSaver js) {
        n1.saveData(js);
        n2.saveData(js);
        ((NeighborJoiningJsonWriter)js.writer).putUnion(id, n1.id, distance1, n2.id, distance2);
    }

}
