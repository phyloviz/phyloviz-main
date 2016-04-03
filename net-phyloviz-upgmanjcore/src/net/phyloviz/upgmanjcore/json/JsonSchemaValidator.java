/*-
 * Copyright (c) 2016, PHYLOViZ Team <phyloviz@gmail.com>
 * All rights reserved.
 * 
 * This file is part of PHYLOViZ <http://www.phyloviz.net/>.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.phyloviz.upgmanjcore.json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JOptionPane;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openide.util.Exceptions;
import org.openide.windows.WindowManager;

/**
 *
 * @author Adriano
 */
public abstract class JsonSchemaValidator {

    private String[] orderList;
    private final Map<String, JsonProp> validatorMap = new HashMap<>();
    private Map<String, String[]> dataIds;

    /**
     * schemaPath - schema source folder schemaFileName - schema file name
     *
     * @param dataIds
     */
    public void setDataIds(Map<String, String[]> dataIds) {
        this.dataIds = dataIds;
    }

    /**
     * Validates choosen file with shema
     *
     * @param schema schema with validation
     * @param directory file path to be load
     * @param filename file name to be load
     * @return
     */
    public boolean validate(InputStream schema, String directory, String filename) {
        //parse validator

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(schema));

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(br);

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
                    JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(), "Invalid Schema!!!!");
                    return false;
                }
            }
        } catch (IOException | ParseException e) {
            Exceptions.printStackTrace(e);
        }
        //validate file
        try {
            FileReader reader = new FileReader(new File(directory, filename));

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(reader);

            for (String key : orderList) {
                JsonProp jp = validatorMap.get(key);
                if (jp.type.equalsIgnoreCase("array")) {
                    JSONArray ja = (JSONArray) json.get(key);
                    if (ja != null) {
                        if (!validateJsonArray(ja, validatorMap.get(key), filename)) {
                            return false;
                        }
                    }
                } else if (jp.type.equalsIgnoreCase("object")) {
                    JSONObject jo = (JSONObject) json.get(key);
                    if (jo != null) {
                        if (!validateJsonObject(jo, validatorMap.get(key), filename)) {
                            return false;
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(), "Invalid file " + filename);
                    return false;
                }
            }
        } catch (IOException | ParseException e) {
            Exceptions.printStackTrace(e);
        }
        return true;
    }

    private JsonProp createProps(JSONObject propertiesObj, String key, String[] propsName) {
        JSONObject propObj = (JSONObject) propertiesObj.get(key);
        JSONObject propItem = (JSONObject) propObj.get("items");
        JSONObject propProps = (JSONObject) propItem.get("properties");
        ItemType it = new ItemType();
        for (int i = 0; i < propProps.size(); i++) {
            JSONObject obj = (JSONObject) propProps.get(propsName[i]);
            it.pType.put(propsName[i], (String) obj.get("type"));
        }
        return new JsonProp((String) propObj.get("type"), it);
    }

    private void createOrder(JSONArray orderObj) {
        orderList = new String[orderObj.size()];
        for (int i = 0; i < orderObj.size(); i++) {
            orderList[i] = (String) orderObj.get(i);
        }
    }

    private boolean validateJsonArray(JSONArray ja, JsonProp get, String file) {
        ItemType it = get.item;
        for (Object ja1 : ja) {
            JSONObject jo = (JSONObject) ja1;
            Iterator<String> itKeys = it.pType.keySet().iterator();
            while (itKeys.hasNext()) {
                String key = itKeys.next();
                Object o = (Object) jo.get(key);
                if (!checkType(o, it.pType.get(key), file)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     *
     * @param s objecto value to be checked
     * @param get objecto key to be checked
     * @param file with that is being verified
     * @return
     */
    private boolean checkType(Object s, String get, String file) {
        try {
            switch (get) {
                case ("integer"):
                    int value1 = (int) (long) s;
                    return true;
                case ("number"):
                    float value2 = (float) (double) s;
                    return true;
                case ("object"):
                    double value3 = (double) s;
                    return true;
                case ("string"):
                    String value4 = (String) s;
                    return true;
                case ("boolean"):
                    boolean value = (boolean) s;
                    return true;
                case ("array"):
                    return (s.getClass().getName().equals(JSONArray.class.getName()));
                default:
                    JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(), "Invalid type in " + file);
                    return false;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(WindowManager.getDefault().getMainWindow(), "Invalid type in " + file);
            return false;
        }
    }

    private boolean validateJsonObject(JSONObject jo, JsonProp get, String file) {
        Iterator<String> itKeys = get.item.pType.keySet().iterator();
        while (itKeys.hasNext()) {
            String key = itKeys.next();
            Object o = (Object) jo.get(key);
            if (!checkType(o, get.item.pType.get(key), file)) {
                return false;
            }
        }
        return true;
    }

    /**
     * properties of a object in json
     */
    private class ItemType {

        public Map<String, String> pType = new HashMap<>();
    }

    /**
     * Objecto with key - value data
     */
    private class JsonProp {

        public String type;
        public ItemType item;

        private JsonProp(String type, ItemType item) {
            this.type = type;
            this.item = item;
        }
    }

}
