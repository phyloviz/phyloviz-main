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

package net.phyloviz.goeburst.algorithm;

import net.phyloviz.goeburst.AbstractDistance;
import net.phyloviz.algo.util.DisjointSet;
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
	public Collection<GOeBurstCluster> getClustering(TypingData<? extends AbstractProfile> td, AbstractDistance ad) {
		
		ArrayList<Edge> edges = getEdges(td, ad);
		Collection<GOeBurstCluster> clustering = getGroups(td, edges, ad);
		
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

	private ArrayList<Edge> getEdges(TypingData<? extends AbstractProfile> td, AbstractDistance ad) {
		ArrayList<Edge> edges = new ArrayList<Edge>();
		maxStId = 0;
		
		Iterator<? extends AbstractProfile> uIter = td.iterator();
		while (uIter.hasNext()) {
			AbstractProfile u = uIter.next();
			
			maxStId = Math.max(maxStId, u.getUID());
			
			Iterator<? extends AbstractProfile> vIter = td.iterator();
			while (vIter.hasNext()) {
				AbstractProfile v = vIter.next();

				if (u.getUID() < v.getUID() && ad.compute(u, v) <= level)
					edges.add(new Edge(u, v, ad));
			}
		}
		
		return edges;
	}
	
	private Collection<GOeBurstCluster> getGroups(TypingData<? extends AbstractProfile> td, Collection<Edge> edges, AbstractDistance ad) {
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
				g = new GOeBurstCluster(ad);
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
				GOeBurstCluster g = new GOeBurstCluster(ad);
				g.add(st);
				groups.put(pi, g);
			}
		}
		
		ArrayList<GOeBurstCluster> gList = new ArrayList<GOeBurstCluster>(groups.values());
		Collections.sort(gList);
		
		return gList;
	}
}
