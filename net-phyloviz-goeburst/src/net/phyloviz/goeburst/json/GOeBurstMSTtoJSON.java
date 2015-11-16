/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.goeburst.json;

import java.util.Collection;
import net.phyloviz.algo.Edge;
import net.phyloviz.goeburst.tree.GOeBurstNode;
import net.phyloviz.upgmanjcore.json.JsonSaver;

/**
 *
 * @author martanascimento
 */

public class GOeBurstMSTtoJSON {
    
    private final Collection<Edge<GOeBurstNode>> edges;
    private final int level;

    public GOeBurstMSTtoJSON(Collection<Edge<GOeBurstNode>> edges, int level) {
        this.edges = edges;
        this.level = level;
    }
    public String saveToJSON(){
        JsonSaver js = new JsonSaver(new String[] {"nodes", "edges"}, new GOeBurstMSTJsonWriter(edges, level));
        String output = js.getJSON();
        return output;
    }
    
}