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

package net.phyloviz.nj.json;

import net.phyloviz.nj.tree.NJRoot;
import net.phyloviz.nj.tree.NodeType;
import net.phyloviz.upgmanjcore.json.JsonSaver;

/** 
 *
 * @author Adriano
 */
public class NJToJSON {
    
    private final NJRoot root;

    public NJToJSON(NJRoot root) {
        this.root = root;
    }
    public String saveToJSON(){
        JsonSaver js = new JsonSaver(new String[] {"leaf", "union"}, new NeighborJoiningJsonWriter());
        root.saveData(js, null);
        return js.getJSON();
    }
    public interface IEdgeJsonSaver{
        void saveData(JsonSaver js, NodeType from);
    }
    
}
