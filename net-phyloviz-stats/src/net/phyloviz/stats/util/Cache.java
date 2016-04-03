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
