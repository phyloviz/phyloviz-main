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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import net.phyloviz.upgmanjcore.json.JsonSchemaValidator;
import org.openide.util.Exceptions;

/**
 *
 * @author Adriano
 */
public class JsonValidator extends JsonSchemaValidator {
    
    private static final String[] leafIds = new String[]{"id", "profile", "x", "y"};
    private static final String[] unionIds = new String[]{"id", "left", "distanceLeft", "right", "distanceRight", "x", "y"};
    private static final String[] rootIds = new String[]{"distance", "left", "right"};
    private static final String schemaFileName = "schema.json";
    private static final URL path = JsonValidator.class.getResource(schemaFileName);
    private static final File file = new File(path.getFile());
    
    public JsonValidator() throws IOException{
        super(file);        
        BufferedReader reader = new BufferedReader(new FileReader(file));
        Map<String, String[]> dataIds = new HashMap<>();
        dataIds.put("leaf", leafIds);
        dataIds.put("union", unionIds);
        dataIds.put("root", rootIds);
        setDataIds(dataIds);
    }
    
}
