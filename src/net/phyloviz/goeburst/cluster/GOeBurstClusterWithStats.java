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

package net.phyloviz.goeburst.cluster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeMap;
import net.phyloviz.algo.AbstractDistance;
import net.phyloviz.algo.util.DisjointSet;
import net.phyloviz.goeburst.algorithm.GOeBurstWithStats;

public class GOeBurstClusterWithStats extends GOeBurstCluster {

	private int[] maxLVs;
	private TreeMap<Edge, EdgeInfo> eInfoMap;
	private EdgeTieStats stat;
	private ArrayList<Edge<GOeBurstNodeExtended>> SLVedges;
	private GOeBurstWithStats mInstance;
	protected GOeBurstNodeExtended fakeRoot;

	public GOeBurstClusterWithStats(GOeBurstWithStats algInstance, AbstractDistance<GOeBurstNodeExtended> ad) {
		super(ad);
		mInstance = algInstance;
		maxLVs = new int[MAXLV + 1];
		eInfoMap = new TreeMap<Edge, EdgeInfo>();
		stat = new EdgeTieStats();
		SLVedges = new ArrayList<Edge<GOeBurstNodeExtended>>();
		fakeRoot = null;
	}

	@Override
	public void setID(int id) {
		this.id = id;
	}

	@Override
	public int getID() {
		return id;
	}

	public GOeBurstNodeExtended getFakeRoot() {
		return fakeRoot;
	}

	public int getMaxXLV(int level) {
		if (level > MAXLV || level < 0) {
			level = MAXLV;
		}

		return maxLVs[level];
	}

	@Override
	public boolean add(GOeBurstNodeExtended p) {
		boolean r = super.add(p);
		this.updateMaxLVs(p);
		return r;
	}
	
	@Override
	public void add(Edge<GOeBurstNodeExtended> e) {
		add(e.getU());
		add(e.getV());
		edges.add(e);
		if (ad.level(e) == 1) {
			SLVedges.add(e);
		}
	}

	@Override
	public int size() {
		return nodes.size();
	}

	@Override
	public Collection<GOeBurstNodeExtended> getSTs() {
		return nodes;
	}

	@Override
	public int getIsolates() {
		return isolates;
	}

	@Override
	public ArrayList<Edge<GOeBurstNodeExtended>> getEdges() {
		return edges;
	}

	public int compareTo(GOeBurstClusterWithStats g) {
		return g.size() - size();
	}

	public boolean equals(GOeBurstClusterWithStats g) {
		return compareTo(g) == 0;
	}

	@Override
	public String toString() {
		return " " + new Integer(id).toString() + " ";
	}

	@Override
	public void updateVisibleEdges() {

		// Kruskal's algorithm.
		Collections.sort(getEdges(), ad.getEdgeComparator());
		DisjointSet s = new DisjointSet(maxStId);
		Iterator<Edge<GOeBurstNodeExtended>> iter = getEdges().iterator();
		visibleEdges = 0;

		while (iter.hasNext()) {
			Edge e = iter.next();
			e.setVisible(false);

			if (!s.sameSet(e.getU().getUID(), e.getV().getUID())) {
				s.unionSet(e.getU().getUID(), e.getV().getUID());
				e.setVisible(true);
				visibleEdges++;
			}
		}

		fakeRoot = null;
	}

	@Override
	public void updateVisibleEdges(GOeBurstNodeExtended root) {
		int[] orig = new int[MAXLV + 1];

		for (int i = 0; i < MAXLV + 1; i++) {
			orig[i] = root.getLV(i);
			root.setLV(i, Integer.MAX_VALUE);
		}

		updateVisibleEdges();

		for (int i = 0; i < MAXLV + 1; i++)
			root.setLV(i, orig[i]);
	}

	@Override
	public int getVisibleEdges() {
		return visibleEdges;
	}

	public int getEdgeMaxTieLevel(Edge e) {
		EdgeInfo info = eInfoMap.get(e);
		if (info != null) {
			return info.maxTie;
		}

		return 0;
	}

	protected void updateMaxLVs(GOeBurstNodeExtended u) {
		// Update maxLVs info.
		int i, j;

		for (i = 0; i < MAXLV && u.getLV(i) == maxLVs[i]; i++);
		if (i < MAXLV && u.getLV(i) >= maxLVs[i])
			for (j = 0; j < MAXLV + 1; j++)
				maxLVs[j] = u.getLV(j);
	}

	public boolean isFounder(GOeBurstNodeExtended st) {
		int i;
		for (i = 0; i < MAXLV && st.getLV(i) == maxLVs[i]; i++);
		return i >= MAXLV;
	}

	static private class EdgeInfo {

		public int maxTie = 0;
		//public int nbTies = 0;
		public String info = "";
	}

	static private class EdgeTieStats {

		public int[] ne;
		public int[] fne;
		public int withoutTies;
		public int[] xLV;

		public EdgeTieStats() {
			withoutTies = 0;
			xLV = new int[MAXLV + 3];
			ne = new int[MAXLV];
			fne = new int[MAXLV];
		}
	}

	public void computeStatistics() {
		Iterator<Edge<GOeBurstNodeExtended>> iter = this.getEdges().iterator();

		while (iter.hasNext()) {
			Edge<GOeBurstNodeExtended> e = iter.next();

			stat.ne[ad.level(e) - 1]++;

			if (!e.visible()) {
				continue;
			}

			getInfo(e);
		}
	}

	public String getInfo(GOeBurstNodeExtended st) {
		String s = "";

		s +=   "# SLVs = " + st.getLV(0) + " ( " + mInstance.getSTxLV(st, 0) + " )";
		s += "\n# DLVs = " + st.getLV(1) + " ( " + mInstance.getSTxLV(st, 1) + " )";
		s += "\n# TLVs = " + st.getLV(2) + " ( " + mInstance.getSTxLV(st, 2) + " )";
		s += "\n# SAT  = " + st.getLV(3) + " ( " + mInstance.getSTxLV(st, 3) + " )";
		s += "\n# isolates = " + st.getFreq();
		s += "\n";

		return s;
	}

	public String getInfo(Edge<GOeBurstNodeExtended> e) {

		if (ad.level(e.getU(), e.getV()) != 1) {
			return "";
		}

		EdgeInfo info = eInfoMap.get(e);

		info = new EdgeInfo();
		eInfoMap.put(e, info);

		// Compute ties.
		info.info = "";
		// Find cut for 'e'.
		DisjointSet cut = new DisjointSet(maxStId);
		Iterator<Edge<GOeBurstNodeExtended>> niter = SLVedges.iterator();
		while (niter.hasNext()) {
			Edge<GOeBurstNodeExtended> f = niter.next();

			if (f.visible() && f != e && !cut.sameSet(f.getU().getUID(), f.getV().getUID())) {
				cut.unionSet(f.getU().getUID(), f.getV().getUID());
			}
		}

		// Find ties.
		int maxtb = 0;
		int nt = 0;
		niter = SLVedges.iterator();
		while (niter.hasNext()) {
			Edge<GOeBurstNodeExtended> f = niter.next();

			if (!f.visible() && ad.level(f) == ad.level(e)
				&& cut.findSet(f.getU().getUID()) != cut.findSet(f.getV().getUID())) {
				info.info += " + " + f.getU().getID() + " -- " + f.getV().getID() + " ";

				nt++;

				int tb = Math.abs(ad.compare(e, f)) - 1;

				maxtb = (tb > maxtb) ? tb : maxtb;

				if (tb / 2 == MAXLV + 1) {
					info.info += "(tiebreak at freq)\n";
				} else if (tb / 2 == MAXLV + 2) {
					info.info += "(tiebreak at id)\n";
				} else if (tb / 2 == MAXLV) {
					info.info += "(tiebreak at sat)\n";
				} else {
					int c = (1 + tb / 2);
					info.info += "(tiebreak at " + ((c == 1) ? 's' : ((c == 2) ? 'd' : 't')) + "lv)\n";
				}
			}
		}

		stat.fne[ad.level(e.getU(), e.getV()) - 1]++;

		if (nt == 0) {
			info.info += "0 ties\n";
			stat.withoutTies++;
			return info.info;
		}

		info.info += nt + " ties (highest ";
		if (maxtb / 2 == MAXLV + 1) {
			info.info += "tiebreak at freq)\n";
		} else if (maxtb / 2 == MAXLV + 2) {
			info.info += "tiebreak at id)\n";
		} else if (maxtb / 2 == MAXLV) {
			info.info += "tiebreak at sat)\n";
		} else {
			int c = (1 + maxtb / 2);
			info.info += "tiebreak at " + ((c == 1) ? 's' : ((c == 2) ? 'd' : 't')) + "lv)\n";
		}

		info.maxTie = maxtb / 2 + 1;
		//info.nbTies = nt;

		stat.xLV[info.maxTie - 1]++;

		return info.info;
	}

	public static String combinedInfo(Collection<GOeBurstClusterWithStats> cs) {
		String s = "Group(s):";
		int STs = 0;
		int isolates = 0;
		int ne = 0;
		int noties = 0;
		int[] e = new int[MAXLV];
		int nfe = 0;
		int[] fe = new int[MAXLV];
		int[] xLV = new int[MAXLV + 3];

		Iterator<GOeBurstClusterWithStats> iter = cs.iterator();
		while (iter.hasNext()) {
			GOeBurstClusterWithStats g = iter.next();

			s += " " + new Integer(g.getID()).toString();
			STs += g.getSTs().size();
			isolates += g.getIsolates();

			ne += g.getEdges().size();
			nfe += g.getVisibleEdges();

			noties += g.stat.withoutTies;

			for (int i = 0; i < MAXLV; i++) {
				fe[i] += g.stat.fne[i];
				e[i] += g.stat.ne[i];
			}

			for (int i = 0; i < MAXLV + 3; i++) {
				xLV[i] += g.stat.xLV[i];
			}
		}

		s += "\n# STs = " + STs
			+ "\n# isolates = " + isolates
			+ "\n# edges = " + ne + " [";
		for (int i = 0; i < MAXLV; i++) {
			s += " " + e[i];
		}
		s += "]\n# forest edges = " + nfe + " [";
		for (int i = 0; i < MAXLV; i++) {
			s += " " + fe[i];
		}
		s += "]\n# edges without ties = " + noties
			+ "\n# tiebreaks by SLV = " + xLV[0]
			+ "\n# tiebreaks by DLV = " + xLV[1]
			+ "\n# tiebreaks by TLV = " + xLV[2]
			+ //"\n# tiebreaks by SAT = " + xLV[3] +
			"\n# tiebreaks by FRQ = " + xLV[4]
			+ "\n# tiebreaks by ID  = " + xLV[5] + "\n";

		return s;
	}

	public static String dumpInfo(Collection<GOeBurstClusterWithStats> cs) {

		String s = "";
		int STs = 0;
		int isolates = 0;
		int ne = 0;
		int noties = 0;
		int[] e = new int[MAXLV];
		int nfe = 0;
		int[] fe = new int[MAXLV];
		int[] xLV = new int[MAXLV + 3];

		Iterator<GOeBurstClusterWithStats> iter = cs.iterator();
		while (iter.hasNext()) {
			GOeBurstClusterWithStats g = iter.next();

			STs += g.getSTs().size();
			isolates += g.getIsolates();

			ne += g.getEdges().size();
			nfe += g.getVisibleEdges();

			noties += g.stat.withoutTies;

			for (int i = 0; i < MAXLV; i++) {
				fe[i] += g.stat.fne[i];
				e[i] += g.stat.ne[i];
			}

			for (int i = 0; i < MAXLV + 3; i++) {
				xLV[i] += g.stat.xLV[i];
			}
		}

		s += "# groups = " + cs.size()
			+ "\n# edges = " + ne + " [";
		for (int i = 0; i < MAXLV; i++) {
			s += " " + e[i];
		}
		s += "]\n# forest edges = " + nfe + " [";
		for (int i = 0; i < MAXLV; i++) {
			s += " " + fe[i];
		}
		s += "]\n# edges without ties = " + noties
			+ "\n# tiebreaks by SLV = " + xLV[0]
			+ "\n# tiebreaks by DLV = " + xLV[1]
			+ "\n# tiebreaks by TLV = " + xLV[2]
			+ //"\n# tiebreaks by SAT = " + xLV[3] +
			"\n# tiebreaks by FRQ = " + xLV[4]
			+ "\n# tiebreaks by ID  = " + xLV[5] + "\n";

		return s;
	}
}
