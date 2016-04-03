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

package net.phyloviz.goeburst.json;

import java.util.Collection;
import net.phyloviz.goeburst.cluster.GOeBurstClusterWithStats;
import net.phyloviz.upgmanjcore.json.JsonSaver;

/**
 *
 * @author martanascimento
 */

public class GOeBurstToJSON {
    
    private final Collection<GOeBurstClusterWithStats> clustering;

    public GOeBurstToJSON(Collection<GOeBurstClusterWithStats> clustering) {
        this.clustering = clustering;
    }
    public String saveToJSON(){
        JsonSaver js = new JsonSaver(new String[] {"nodes", "edges", "groups", "edgeTieStats"}, new GOeBurstJsonWriter(clustering));
        String output = js.getJSON();
        return output;
    }
    
}
