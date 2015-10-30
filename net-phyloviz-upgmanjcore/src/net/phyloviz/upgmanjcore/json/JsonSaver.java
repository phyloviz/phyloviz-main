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
    public final IJsonWriter writer;
    /**
     * 
     * @param titles to save has keys
     * @param writer entry point with values to be saved has values
     */
    public JsonSaver(String[] titles, IJsonWriter writer) {
        this.titles = titles;
        this.writer = writer;
    }
    /**
     * 
     * @return data to be saved in format JSON
     */
    public String getJSON() {
        HashMap<String, String> map = writer.getBuildersMap();
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        for (int i = 0; i < titles.length; i++) {
            setObjectArray(map, sb, i);
        }
        sb.append(writer.getRoot());
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
