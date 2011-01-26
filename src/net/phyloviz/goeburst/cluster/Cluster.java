package net.phyloviz.goeburst.cluster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import net.phyloviz.core.data.AbstractProfile;

public abstract class Cluster implements Comparable<Cluster> {
	protected int id;
	protected TreeSet<AbstractProfile> nodes;
	protected ArrayList<Edge> edges;
	protected int visibleEdges;
	protected int isolates;
	
	public Cluster () {
		id = 0;
		nodes = new TreeSet<AbstractProfile>();
		edges = new ArrayList<Edge>();
		isolates = 0;
		visibleEdges = 0;
	}
	
	public Collection<AbstractProfile> getSTs() {
		return nodes;
	}
	
	public ArrayList<Edge> getEdges() {
		return edges;
	}

	public int size() {
		return nodes.size();
	}
	
	public int getIsolates() {
		return isolates;
	}
	
	public int getVisibleEdges() {
		return visibleEdges;
	}
	
	public boolean add(AbstractProfile st) {
		// Note that a 'TreeSet' only stores distinct elements. 
		if (!nodes.add(st))
			return false;
		
		isolates += st.getFreq();
		
		return true;
	}
	
	public void add(Edge e) {
		add(e.getU());
		add(e.getV());
		edges.add(e);
	}
	
	public void setID(int id) {
		this.id = id;
	}
	
	public int getID() {
		return id;
	}

	// TODO: rewrite this stuff...
	@Override
	public int compareTo(Cluster g) {
		return g.size() - size();
	}

	public boolean equals(Cluster g) {
		return compareTo(g) == 0;
	}
}
