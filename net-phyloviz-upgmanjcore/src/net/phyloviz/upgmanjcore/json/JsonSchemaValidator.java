/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.upgmanjcore.json;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openide.util.Exceptions;

/**
 *
 * @author Adriano
 */
public abstract class JsonSchemaValidator {    
    private String[] orderList;
    private final Map<String, JsonProp> validatorMap = new HashMap<>();
    private Map<String, String[]> dataIds;
    private final File schemaFile;
    
    /**
     * schemaPath - schema source folder
     * schemaFileName - schema file name
     * @param schemaFile
     */
    public JsonSchemaValidator(File schemaFile){
        this.schemaFile = schemaFile;
    }
    
    public void setDataIds(Map<String, String[]> dataIds){
        this.dataIds = dataIds;
    }
    
    public boolean validate(String directory, String filename) {
        //parse validator
        try(FileReader reader = new FileReader(schemaFile)) {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(reader);
            
            //get order
            JSONArray orderObj = (JSONArray) json.get("order");
            createOrder(orderObj);
            
            //get element properties
            JSONObject propertiesObj = (JSONObject) json.get("properties");
            Iterator<String> itKeys = dataIds.keySet().iterator();
            while (itKeys.hasNext()) {
                String key = itKeys.next();
                JsonProp jp = createProps(propertiesObj, key, dataIds.get(key));
                validatorMap.put(key, jp);
            }
            
            //check if has all orders
            for (String orderList1 : orderList) {
                if (!validatorMap.containsKey(orderList1)) { 
                    throw new RuntimeException("Invalid Schema!!!!");
                }
            }
            
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        //validate file
        try(FileReader reader = new FileReader(new File(directory, filename))) {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(reader);
            
            for (String key : orderList) {
                JsonProp jp = validatorMap.get(key);
                if(jp.type.equalsIgnoreCase("array")){
                    JSONArray ja = (JSONArray) json.get(key);
                    validateJsonArray(ja, validatorMap.get(key));
                }else if(jp.type.equalsIgnoreCase("object")){
                    JSONObject jo = (JSONObject) json.get(key);
                    validateJsonObject(jo, validatorMap.get(key));
                } else throw new RuntimeException("Invalid read file!!!!");
            }
            
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return true;
    }

    private JsonProp createProps(JSONObject propertiesObj, String key, String[] propsName) {
        JSONObject propObj = (JSONObject) propertiesObj.get(key);
        JSONObject propItem = (JSONObject) propObj.get("items");
        JSONObject propProps = (JSONObject) propItem.get("properties");
        ItemType it = new ItemType();
        for(int i = 0; i < propProps.size(); i++){
            JSONObject obj = (JSONObject)propProps.get(propsName[i]);
            it.pType.put(propsName[i], (String)obj.get("type"));
        }
        return new JsonProp( (String)propObj.get("type"), it);
    }
    private void createOrder(JSONArray orderObj) {
        orderList = new String[orderObj.size()];
        for(int i = 0; i < orderObj.size(); i++)
            orderList[i] = (String) orderObj.get(i);
    }

    private void validateJsonArray(JSONArray ja, JsonProp get) {
        ItemType it = get.item;
        for (Object ja1 : ja) {
            JSONObject jo = (JSONObject) ja1;
            Iterator<String> itKeys = it.pType.keySet().iterator();
            while(itKeys.hasNext()){
                String key = itKeys.next();
                Object o = (Object)jo.get(key);
                checkType( o, it.pType.get(key));
            }
        }
    }

    private void checkType(Object s, String get) {
        try{
            switch (get){
                case ("integer"):
                    int value1 = (int) (long) s;
                    break;
                case ("number"):
                    float value2 = (float) (double) s;
                    break;
                case ("object"):
                    double value3 = (double) s;
                    break;
                default:
                    throw new RuntimeException("Invalid type in input file!!!");
            }
        } catch (Exception e){
            throw new RuntimeException("Invalid type in input file!!!");
        }
    }

    private void validateJsonObject(JSONObject jo, JsonProp get) {
        Iterator<String> itKeys = get.item.pType.keySet().iterator();
        while(itKeys.hasNext()){
            String key = itKeys.next();
            Object o = (Object)jo.get(key);
            checkType( o, get.item.pType.get(key));
        }
    }
    private class ItemType{
        public Map<String, String> pType = new HashMap<>();
    }
    private class JsonProp{
        public String type;
        public ItemType item;
        private JsonProp(String type, ItemType item){
            this.type = type;
            this.item = item;
        }
    }
    
}
