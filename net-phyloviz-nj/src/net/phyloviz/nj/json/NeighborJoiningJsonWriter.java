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

import java.util.HashMap;
import net.phyloviz.upgmanjcore.json.IJsonWriter;

/**
 *
 * @author Adriano
 */
public class NeighborJoiningJsonWriter implements IJsonWriter{
    
    private final StringBuilder sbLeafs = new StringBuilder();
    private final StringBuilder sbUnions = new StringBuilder();
    private String root; 
    
    public void putLeaf(int id, String profile){
        String value = "\t\t{\""
                + "id\": " + id + ", \""
                + "profile\": \"" + profile + "\"},\n";
        sbLeafs.append(value);
    }
    public void putUnion(int id, int left, float distL, int right, float distR){
        String value = "\t\t{\""
                + "id\": " + id + ", \""
                + "left\": " + left + ", \"" 
                + "distanceLeft\": " + distL + ", \""
                + "right\": " + right + ", \""
                + "distanceRight\": " + distR + "},\n";
        sbUnions.append(value);
    }
    public void putRoot(double d, int l, int r){
        root = "\t\t{\""
                + "distance\": "+ d + ", \""
                + "left\": " + l + ", \""
                + "right\": " + r + "},\n";
    }
    @Override
    public HashMap<String, String> getBuildersMap(){
        HashMap<String, String> map = new HashMap<>();
        map.put("leaf", sbLeafs.toString());
        map.put("union", sbUnions.toString());
        map.put("root", root);
        return map;
    }
    @Override
    public String getRoot() {
        return "\t\t\"root\":" + root;
    }
    
}
