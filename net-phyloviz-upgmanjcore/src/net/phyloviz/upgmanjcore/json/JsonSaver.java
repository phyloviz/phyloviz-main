/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.upgmanjcore.json;

import java.util.HashMap;

/**
 *
 * @author Adriano
 */
public class JsonSaver{

    private final String[] titles;
    public final IJsonSaverTag njtj;
    /**
     * 
     * @param titles to save has keys
     * @param saver entry point with values to be saved has values
     */
    public JsonSaver(String[] titles, IJsonSaverTag saver) {
        this.titles = titles;
        njtj = saver;
    }
    /**
     * 
     * @return data to be saved in format JSON
     */
    public String createFile() {
        HashMap<String, String> map = njtj.getBuildersMap();
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        for (int i = 0; i < titles.length; i++) {
            setObjectArray(map, sb, i);
        }
        String root = "\t\t\"root\":" + njtj.getRoot();
        sb.append(root);
        sb.append("}");
        
        return sb.toString();
    }
    
    private void setObjectArray(HashMap<String, String> map, StringBuilder sb, int idx){
        if(map.get(titles[idx]).length() > 0){
            String toAppend = "\t\"" + titles[idx] + "\": [\n";
            sb.append(toAppend);
            String result = map.get(titles[idx]);
            result = result.substring(0, result.length() - 2); //to remove last ','
            result += "\n\t],\n";
            sb.append(result);
        }
    }
}
