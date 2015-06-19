/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.upgma.json;

import java.io.File;
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
    private static final String schemaPath = new File("").getAbsolutePath();
    
    public JsonValidator(){
        super(schemaPath, schemaFileName);
        Map<String, String[]> dataIds = new HashMap<>();
        dataIds.put("leaf", leafIds);
        dataIds.put("union", unionIds);
        dataIds.put("root", rootIds);
        setDataIds(dataIds);
    }
    
}
