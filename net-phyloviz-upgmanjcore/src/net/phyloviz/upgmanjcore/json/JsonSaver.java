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

package net.phyloviz.upgmanjcore.json;

import java.util.HashMap;

/**
 *
 * @author Adriano
 */
public class JsonSaver{

    private final String[] titles;
    public final IJsonWriter writer;
    /**
     * 
     * @param titles to save has keys
     * @param writer entry point with values to be saved has values
     */
    public JsonSaver(String[] titles, IJsonWriter writer) {
        this.titles = titles;
        this.writer = writer;
    }
    /**
     * 
     * @return data to be saved in format JSON
     */
    public String getJSON() {
        HashMap<String, String> map = writer.getBuildersMap();
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        for (int i = 0; i < titles.length; i++) {
            setObjectArray(map, sb, i);
        }
        sb.append(writer.getRoot());
        sb.append("}");
        
        return sb.toString();
    }
    
    private void setObjectArray(HashMap<String, String> map, StringBuilder sb, int idx){
        if(map.get(titles[idx]).length() > 0){
            String toAppend = "\t\"" + titles[idx] + "\": [\n";
            sb.append(toAppend);
            String result = map.get(titles[idx]);
            result = result.substring(0, result.length() - 2); //to remove last ','
            result += "\n\t],\n";
            sb.append(result);
        }
    }
}
