package net.phyloviz.tview.filter;

import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import net.phyloviz.core.data.Isolate;

public class Group {
	
	private List<Isolate> individuals;
	private String names;
	
	public Group(String name){
		this.names=name;
		individuals=new LinkedList<Isolate>();
	}
	
	public void addAll(List<Isolate> list){
		individuals.addAll(list);
	}
	
	public void add(Isolate l){
		individuals.add(l);
	}
	
	public void setName(String name){
		this.names=name;
	}
	
	public String getName(){
		return names;
	}
	
	public Iterator<Isolate> iterator(){
		return individuals.iterator();
	}
	
	@Override
	public String toString() {
		return names + " -> " + individuals.size();
	}

	public int size() {
		return individuals.size();
	}

}
