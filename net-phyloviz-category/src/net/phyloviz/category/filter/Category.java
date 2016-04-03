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

package net.phyloviz.category.filter;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import net.phyloviz.core.data.DataItem;

public class Category implements Serializable{
	
	private LinkedList<DataItem> items;
	private String name;
	private int _weight;
	
	public Category(String name){
		this.items = new LinkedList<DataItem>();
		this.name=name;
		this._weight = 0;
	}
	
	public void addAll(Collection<? extends DataItem> list){
		Iterator<? extends DataItem> i = list.iterator();
		while (i.hasNext())
			add(i.next());
	}
	
	public void add(DataItem l){
		items.add(l);
		_weight += l.weight();
	}
	
	public void setName(String name){
		this.name=name;
	}
	
	public String getName(){
		return name;
	}
	
	public Iterator<DataItem> iterator(){
		return items.iterator();
	}
	
	@Override
	public String toString() {
		return name + " -> " + items.size();
	}

	public int size() {
		return items.size();
	}

	public int weight() {
		return _weight;
	}

}
