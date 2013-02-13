/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.stats.util;

import java.util.TreeMap;

/**
 *
 * @author Pedro T. Monteiro
 */
public class Cache {

    private TreeMap<String, StringBuffer> tmBodyCache;

    public Cache() {
	   tmBodyCache = new TreeMap<String, StringBuffer>();
    }

    public void put(String index, StringBuffer sbContent) {
	   tmBodyCache.put(index, sbContent);
    }

    public boolean hasBody(String key) {
	   return tmBodyCache.containsKey(key);
    }

    public String getBody(String key) {
	   if (hasBody(key)) {
		  return tmBodyCache.get(key).toString();
	   }
	   return "Not found!";
    }
}
