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

    private TreeMap<Integer, StringBuffer> tmBodyCache;

    public Cache() {
	   tmBodyCache = new TreeMap<Integer, StringBuffer>();
    }

    public void put(int index, StringBuffer sbContent) {
	   tmBodyCache.put(index, sbContent);
    }

    public boolean hasBody(int i) {
	   return tmBodyCache.containsKey(i);
    }

    public String getBody(int i) {
	   if (hasBody(i)) {
		  return tmBodyCache.get(i).toString();
	   }
	   return "Not found!";
    }
}
