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

import net.phyloviz.upgma.json.UPGMAJsonWriter;
import net.phyloviz.upgmanjcore.json.JsonSaver;
import net.phyloviz.upgma.json.UPGMAToJSON.IEdgeJsonSaver;

/**
 *
 * @author Adriano
 */
public class UPGMARoot implements IEdgeJsonSaver{
        
    private final NodeType n1, n2;
    private final float distance;

    public UPGMARoot(NodeType n1, NodeType n2, float distance){
        this.n1 = n1;
        this.n2 = n2;
        this.distance = distance;
    }
    @Override
    public void saveData(JsonSaver js, NodeType from) {
        n1.saveData(js); //start saving to the left
        n2.saveData(js); //start saving to the right
        ((UPGMAJsonWriter)js.writer).putRoot(distance, n1.id, n2.id);
    }
    
    public NodeType getNodeLeft(){
        return n1;
    }
    public NodeType getNodeRight(){
        return n2;
    }
    public String getDisplayName(){
        return "root";
    }
    public float getDistance(){
        return distance;
    }
}
