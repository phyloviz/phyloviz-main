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
public class GOeBurstMSTSchemaValidator extends JsonSchemaValidator{

    private static final String[] nodes = new String[]{"id", "profile", "group-lvs"};
    private static final String[] edges = new String[]{"u", "v"};

    public GOeBurstMSTSchemaValidator() {
        Map<String, String[]> dataIds = new HashMap<>();
        dataIds.put("nodes", nodes);
        dataIds.put("edges", edges);
        setDataIds(dataIds);
    }

    public boolean validate(String directory, String filename) throws IOException {
        String dfn = org.openide.util.NbBundle.getMessage(this.getClass(), "schema-fullmst");
        URL url = this.getClass().getResource(dfn);
        InputStream stream = url.openStream();
        return validate(stream, directory, filename);
    }
}
