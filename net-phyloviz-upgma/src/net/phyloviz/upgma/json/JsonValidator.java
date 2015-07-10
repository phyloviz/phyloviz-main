/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.upgma.json;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import net.phyloviz.upgmanjcore.json.JsonSchemaValidator;

/**
 *
 * @author Adriano
 */
public class JsonValidator extends JsonSchemaValidator {
    
    private static final String[] leafIds = new String[]{"uid", "profile"};
    private static final String[] unionIds = new String[]{"uid", "distance", "leftID", "rightID"};
    private static final String[] rootIds = new String[]{"distance", "left", "right"};
    private static final String schemaFileName = "schema.json";
    private static final URL path = JsonValidator.class.getResource(schemaFileName);
    private static final File file = new File(path.getFile());
    
    public JsonValidator(){
        super(file);
        Map<String, String[]> dataIds = new HashMap<>();
        dataIds.put("leaf", leafIds);
        dataIds.put("union", unionIds);
        dataIds.put("root", rootIds);
        setDataIds(dataIds);
    }
    
}
