package net.phyloviz.goeburst.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeMap;
import net.phyloviz.core.data.AbstractType;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.goeburst.cluster.Edge;
import net.phyloviz.goeburst.cluster.GOeBurstCluster;
import net.phyloviz.goeburst.cluster.GOeBurstClusterWithStats;

public class GOeBurstWithStats implements ClusteringMethod<GOeBurstClusterWithStats> {
	
	private int level;
	private int maxStId;
	private TreeMap<Integer,GOeBurstCluster.STLV> stLVs;
	
	public GOeBurstWithStats(int level) {
		this.level = level;
		this.maxStId = 0;
		this.stLVs = new TreeMap<Integer,GOeBurstCluster.STLV>();
	}

	@Override
	public Collection<GOeBurstClusterWithStats> getClustering(TypingData<? extends AbstractType> td) {
		
		ArrayList<Edge> edges = getEdges(td);
		Collection<GOeBurstClusterWithStats> clustering = getGroups(td, edges);
		
		// Update LVs for STs in each group and set group id.
		Iterator<GOeBurstClusterWithStats> gIter = clustering.iterator();
		int gid = 0;
		while (gIter.hasNext()) {
			GOeBurstClusterWithStats g = gIter.next();
			g.setID(gid++);
			g.updateVisibleEdges();
		}
		
		return clustering;
	}
	
	public int getSTxLV(AbstractType st, int lv) {
		if (lv > GOeBurstClusterWithStats.MAXLV || lv < 0)
			level = GOeBurstClusterWithStats.MAXLV;
			
		return stLVs.get(st.getUID()).lv[lv];
	}

	private ArrayList<Edge> getEdges(TypingData<? extends AbstractType> td) {
		ArrayList<Edge> edges = new ArrayList<Edge>();
		maxStId = 0;
		
		Iterator<? extends AbstractType> uIter = td.iterator();
		while (uIter.hasNext()) {
			AbstractType u = uIter.next();
			GOeBurstClusterWithStats.STLV uLV = stLVs.get(u.getUID());
			
			if (uLV == null) {
				uLV = new GOeBurstClusterWithStats.STLV();
				stLVs.put(u.getUID(), uLV);
			}
			
			maxStId = Math.max(maxStId, u.getUID());
			
			Iterator<? extends AbstractType> vIter = td.iterator();
			while (vIter.hasNext()) {
				AbstractType v = vIter.next();
				
				int diff = HammingDistance.compute(u, v);
				
				if (u != v && diff <= GOeBurstClusterWithStats.MAXLV)
					uLV.lv[diff - 1] ++;
				else
					uLV.lv[GOeBurstClusterWithStats.MAXLV] ++;

				if (u.getUID() < v.getUID() && diff <= level)
					edges.add(new Edge(u,v));
			}
		}
		
		return edges;
	}
	
	private Collection<GOeBurstClusterWithStats> getGroups(TypingData<? extends AbstractType> td, Collection<Edge> edges) {
		DisjointSet s = new DisjointSet(maxStId);
		
		Iterator<Edge> eIter = edges.iterator();
		while (eIter.hasNext()) {
			Edge e = eIter.next();
			s.unionSet(e.getU().getUID(), e.getV().getUID());
		}
		
		TreeMap<Integer,GOeBurstClusterWithStats> groups = new TreeMap<Integer,GOeBurstClusterWithStats>();
		eIter = edges.iterator();
		while (eIter.hasNext()) {
			Edge e = eIter.next();
			
			int pi = s.findSet(e.getU().getUID());
			GOeBurstClusterWithStats g = groups.get(pi);
			if ( g == null) {
				g = new GOeBurstClusterWithStats(this);
				groups.put(pi, g);
			}
			
			g.add(e);
		}
		
		// Add singletons.
		Iterator<? extends AbstractType> stIter = td.iterator();
		while (stIter.hasNext()) {
			AbstractType st = stIter.next();
			
			int pi = s.findSet(st.getUID());
			if (groups.get(pi) == null) {
				GOeBurstClusterWithStats g = new GOeBurstClusterWithStats(this);
				g.add(st);
				groups.put(pi, g);
			}
		}
		
		ArrayList<GOeBurstClusterWithStats> gList = new ArrayList<GOeBurstClusterWithStats>(groups.values());
		Collections.sort(gList);
		
		return gList;
	}
}
