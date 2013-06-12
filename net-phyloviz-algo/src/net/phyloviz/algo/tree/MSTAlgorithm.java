/*-
 * Copyright (c) 2011, PHYLOViZ Team <phyloviz@gmail.com>
 * All rights reserved.
 * 
 * This file is part of PHYLOViZ <http://www.phyloviz.net>.
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
 * 
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole combination.
 * 
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent modules,
 * and to copy and distribute the resulting executable under terms of your
 * choice, provided that you also meet, for each linked independent module,
 * the terms and conditions of the license of that module.  An independent
 * module is a module which is not derived from or based on this library.
 * If you modify this library, you may extend this exception to your version
 * of the library, but you are not obligated to do so.  If you do not wish
 * to do so, delete this exception statement from your version.
 */

package net.phyloviz.algo.tree;

import net.phyloviz.algo.Edge;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.phyloviz.algo.util.FastRankRelaxedHeap;
import net.phyloviz.algo.util.Heap;
import net.phyloviz.core.data.Profile;

public class MSTAlgorithm<T extends Profile> {

	private List<T> nodes;
	private Comparator<Edge<T>> cmp;

	private Heap q;
	private int[] s;
	private int[] pi;
	private Edge[] d;
	
	public MSTAlgorithm(List<T> nlst, Comparator<Edge<T>> c) {

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
	
	public List<Edge<T>> getTree() {

		ArrayList<Edge<T>> edges = new ArrayList<Edge<T>>(nodes.size() - 1);

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

				Edge<T> e = new Edge<T>(nodes.get(u), nodes.get(v));

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
