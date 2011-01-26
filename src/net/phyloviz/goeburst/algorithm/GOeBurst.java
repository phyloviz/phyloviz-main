package net.phyloviz.goeburst.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeMap;

import net.phyloviz.core.data.AbstractProfile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.goeburst.cluster.Edge;
import net.phyloviz.goeburst.cluster.GOeBurstCluster;

public class GOeBurst implements ClusteringMethod<GOeBurstCluster> {
	
	private int level;
	private int maxStId;
	
	public GOeBurst(int level) {
		this.level = level;
		this.maxStId = 0;
	}
	
	public GOeBurst() {
		this(1);
	}

	@Override
	public Collection<GOeBurstCluster> getClustering(TypingData<? extends AbstractProfile> td) {
		
		ArrayList<Edge> edges = getEdges(td);
		Collection<GOeBurstCluster> clustering = getGroups(td, edges);
		
		// Update LVs for STs in each group and set group id.
		Iterator<GOeBurstCluster> gIter = clustering.iterator();
		int gid = 0;
		while (gIter.hasNext()) {
			GOeBurstCluster g = gIter.next();
			g.setID(gid++);
			g.updateVisibleEdges();
		}
		
		return clustering;
	}

	private ArrayList<Edge> getEdges(TypingData<? extends AbstractProfile> td) {
		ArrayList<Edge> edges = new ArrayList<Edge>();
		maxStId = 0;
		
		Iterator<? extends AbstractProfile> uIter = td.iterator();
		while (uIter.hasNext()) {
			AbstractProfile u = uIter.next();
			
			maxStId = Math.max(maxStId, u.getUID());
			
			Iterator<? extends AbstractProfile> vIter = td.iterator();
			while (vIter.hasNext()) {
				AbstractProfile v = vIter.next();

				if (u.getUID() < v.getUID() && HammingDistance.compute(u, v) <= level)
					edges.add(new Edge(u,v));
			}
		}
		
		return edges;
	}
	
	private Collection<GOeBurstCluster> getGroups(TypingData<? extends AbstractProfile> td, Collection<Edge> edges) {
		DisjointSet s = new DisjointSet(maxStId);
		
		Iterator<Edge> eIter = edges.iterator();
		while (eIter.hasNext()) {
			Edge e = eIter.next();
			s.unionSet(e.getU().getUID(), e.getV().getUID());
		}
		
		TreeMap<Integer,GOeBurstCluster> groups = new TreeMap<Integer,GOeBurstCluster>();
		eIter = edges.iterator();
		while (eIter.hasNext()) {
			Edge e = eIter.next();
			
			int pi = s.findSet(e.getU().getUID());
			GOeBurstCluster g = groups.get(pi);
			if ( g == null) {
				g = new GOeBurstCluster();
				groups.put(pi, g);
			}
			
			g.add(e);
		}
		
		// Add singletons.
		Iterator<? extends AbstractProfile> stIter = td.iterator();
		while (stIter.hasNext()) {
			AbstractProfile st = stIter.next();
			
			int pi = s.findSet(st.getUID());
			if (groups.get(pi) == null) {
				GOeBurstCluster g = new GOeBurstCluster();
				g.add(st);
				groups.put(pi, g);
			}
		}
		
		ArrayList<GOeBurstCluster> gList = new ArrayList<GOeBurstCluster>(groups.values());
		Collections.sort(gList);
		
		return gList;
	}
}
