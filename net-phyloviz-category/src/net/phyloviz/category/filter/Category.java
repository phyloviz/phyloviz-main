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
