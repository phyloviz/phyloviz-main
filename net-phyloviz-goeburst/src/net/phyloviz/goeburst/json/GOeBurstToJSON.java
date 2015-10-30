/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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