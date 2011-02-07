package net.phyloviz.algo.tree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.phyloviz.algo.util.FastRankRelaxedHeap;
import net.phyloviz.algo.util.Heap;

public class MSTAlgorithm {

	private List<? extends Node> nodes;
	private Comparator<Edge> cmp;

	private Heap q;
	private int[] s;
	private int[] pi;
	private Edge[] d;
	
	public MSTAlgorithm(List<? extends Node> nlst, Comparator<Edge> c) {

		this.nodes = nlst;
		this.cmp = c;

		s = new int[nodes.size()];
		pi = new int[nodes.size()];
		d = new Edge[nodes.size()];
		
		for (int i = 0; i < nodes.size(); i++) {
			s[i] = 0;
			pi[i] = -1;
		}

		Comparator<Integer> icmp = new Comparator<Integer>() {

			@Override
			public int compare(Integer u, Integer v) {
				return cmp.compare(d[u],d[v]);
			}

		};
		q = new FastRankRelaxedHeap(nodes.size(), icmp);
	}
	
	public List<Edge> getTree() {

		ArrayList<Edge> edges = new ArrayList<Edge>(nodes.size() - 1);

		// We take node 0 as root or src.
		d[0] = null;
		q.push(0);
		
		while (q.size() != 0) {
			
			int u = q.pop();
			s[u] = 1;

			if (pi[u] >= 0)
				edges.add(d[u]);
			
			for (int i = 0; i < nodes.size(); i++) {
				int v = i;
				
				if (s[v] == 1)
					continue;

				Edge e = new Edge(nodes.get(u), nodes.get(v));

				// Relax.
				if (d[v] == null || cmp.compare(d[v], e) > 0) {
					d[v]  = e;
					pi[v] = u;
					
					q.push(v);
				}
			}
		}

		return edges;
	}
}
