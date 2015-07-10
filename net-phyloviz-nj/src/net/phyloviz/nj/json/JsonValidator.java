/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.nj.json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import net.phyloviz.upgmanjcore.json.JsonSchemaValidator;

/**
 *
 * @author Adriano
 */
public class JsonValidator {

    private static final String[] leafIds = new String[]{"id", "profile", "x", "y"};
    private static final String[] unionIds = new String[]{"id", "left", "distanceLeft", "right", "distanceRight", "x", "y"};
    private static final String[] rootIds = new String[]{"distance", "left", "right"};
    
    
    private final JsonSchemaValidator sV = new JsonSchemaValidator();
    
    public JsonValidator(){
        Map<String, String[]> dataIds = new HashMap<>();
        dataIds.put("leaf", leafIds);
        dataIds.put("union", unionIds);
        dataIds.put("root", rootIds);
        sV.setDataIds(dataIds);
    }

    public boolean validate(String directory, String filename) throws IOException {
        String dfn = org.openide.util.NbBundle.getMessage(this.getClass(), "schema");
        URL url = this.getClass().getResource(dfn);
        InputStream stream = url.openStream();
        return sV.validate(stream, directory, filename);
    }
}
