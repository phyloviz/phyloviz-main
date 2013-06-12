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

import net.phyloviz.algo.AbstractDistance;
import net.phyloviz.algo.util.DisjointSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeMap;

import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.goeburst.cluster.Edge;
import net.phyloviz.goeburst.cluster.GOeBurstCluster;
import net.phyloviz.goeburst.cluster.GOeBurstNodeExtended;

public class GOeBurst implements ClusteringMethod<GOeBurstCluster, GOeBurstNodeExtended> {
	
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
	public Collection<GOeBurstCluster> getClustering(TypingData<? extends Profile> otd, AbstractDistance<GOeBurstNodeExtended> ad) {
	
		ArrayList<GOeBurstNodeExtended> td = new ArrayList<GOeBurstNodeExtended>(otd.size());
		for (Iterator<? extends Profile> iter = otd.iterator(); iter.hasNext(); )
			td.add(new GOeBurstNodeExtended(iter.next()));
		
		ArrayList<Edge<GOeBurstNodeExtended>> edges = getEdges(td, ad);
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

	private ArrayList<Edge<GOeBurstNodeExtended>> getEdges(Collection<GOeBurstNodeExtended> td, AbstractDistance<GOeBurstNodeExtended> ad) {
		ArrayList<Edge<GOeBurstNodeExtended>> edges = new ArrayList<Edge<GOeBurstNodeExtended>>();
		maxStId = 0;
		
		Iterator<GOeBurstNodeExtended> uIter = td.iterator();
		while (uIter.hasNext()) {
			GOeBurstNodeExtended u = uIter.next();
			
			maxStId = Math.max(maxStId, u.getUID());
			
			Iterator<GOeBurstNodeExtended> vIter = td.iterator();
			while (vIter.hasNext()) {
				GOeBurstNodeExtended v = vIter.next();

				if (u.getUID() < v.getUID() && ad.level(u, v) <= level)
					edges.add(new Edge<GOeBurstNodeExtended>(u, v));
			}
		}
		
		return edges;
	}
	
	private Collection<GOeBurstCluster> getGroups(Collection<GOeBurstNodeExtended> td, Collection<Edge<GOeBurstNodeExtended>> edges, AbstractDistance<GOeBurstNodeExtended> ad) {
		DisjointSet s = new DisjointSet(maxStId);
		
		Iterator<Edge<GOeBurstNodeExtended>> eIter = edges.iterator();
		while (eIter.hasNext()) {
			Edge e = eIter.next();
			s.unionSet(e.getU().getUID(), e.getV().getUID());
		}
		
		TreeMap<Integer,GOeBurstCluster> groups = new TreeMap<Integer,GOeBurstCluster>();
		eIter = edges.iterator();
		while (eIter.hasNext()) {
			Edge<GOeBurstNodeExtended> e = eIter.next();
			
			int pi = s.findSet(e.getU().getUID());
			GOeBurstCluster g = groups.get(pi);
			if ( g == null) {
				g = new GOeBurstCluster(ad);
				groups.put(pi, g);
			}
			
			g.add(e);
		}
		
		// Add singletons.
		Iterator<GOeBurstNodeExtended> stIter = td.iterator();
		while (stIter.hasNext()) {
			GOeBurstNodeExtended st = stIter.next();
			
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
