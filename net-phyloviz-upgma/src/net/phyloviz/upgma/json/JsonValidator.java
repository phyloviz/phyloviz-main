/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.upgma.json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import net.phyloviz.upgmanjcore.json.JsonSchemaValidator;

/**
 *
 * @author Adriano
 */
public class JsonValidator {

    private static final String[] leafIds = new String[]{"uid", "profile"};
    private static final String[] unionIds = new String[]{"uid", "distance", "leftID", "rightID"};
    private static final String[] rootIds = new String[]{"distance", "left", "right"};

    private static String rootPath = new File("").getAbsolutePath();
    private final String packageName = this.getClass().getPackage().getName();

    private final JsonSchemaValidator sV = new JsonSchemaValidator();
    private File schemaFile;

    public JsonValidator() {

//        
//        StringBuilder sb = new StringBuilder();
//        try{
//            
//            while(br.ready()){
//                sb.append(br.readLine());
//            }
//        }catch(IOException e){
//            
//        }
//        String s = sb.toString();
        //schemaFile = new File(p);
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
