/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.goeburst.json;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import net.phyloviz.upgmanjcore.json.JsonSchemaValidator;

/**
 *
 * @author martanascimento
 */
public class GOeBurstSchemaValidator extends JsonSchemaValidator{

    private static final String[] nodes = new String[]{"id", "profile", "graph-lvs", "group-lvs", "slvs", "dlvs"};
    private static final String[] edges = new String[]{"id", "u", "v", "visible", "maxTie"};
    private static final String[] groups = new String[]{"id", "edges", "nodes", "maxLVs", "maxStId"};
    private static final String[] edgeTieStats = new String[]{"group", "ne", "fne", "xLV", "withoutTies"};

    public GOeBurstSchemaValidator() {
        Map<String, String[]> dataIds = new HashMap<>();
        dataIds.put("nodes", nodes);
        dataIds.put("edges", edges);
        dataIds.put("groups", groups);
        dataIds.put("edgeTieStats", edgeTieStats);
        setDataIds(dataIds);
    }

    public boolean validate(String directory, String filename) throws IOException {
        String dfn = org.openide.util.NbBundle.getMessage(this.getClass(), "schema");
        URL url = this.getClass().getResource(dfn);
        InputStream stream = url.openStream();
        return validate(stream, directory, filename);
    }
}
