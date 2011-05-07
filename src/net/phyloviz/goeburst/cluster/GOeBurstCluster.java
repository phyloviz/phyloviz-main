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

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;
import net.phyloviz.core.data.AbstractProfile;
import net.phyloviz.goeburst.AbstractDistance;
import net.phyloviz.algo.util.DisjointSet;

public class GOeBurstCluster extends Cluster {

	public static int MAXLV = 3;

	public static class STLV {

		public int[] lv = new int[MAXLV + 1];
	}
	//protected int id;
	protected int maxStId;
	protected TreeMap<Integer, STLV> lvMap;
	protected TreeMap<Integer, LinkedList<AbstractProfile>> slvList;
	protected TreeMap<Integer, LinkedList<AbstractProfile>> dlvList;
	protected AbstractDistance ad;

	public GOeBurstCluster(AbstractDistance ad) {
		super();
		lvMap = new TreeMap<Integer, STLV>();
		slvList = new TreeMap<Integer, LinkedList<AbstractProfile>>();
		dlvList = new TreeMap<Integer, LinkedList<AbstractProfile>>();
		this.ad = ad;
	}

	public Collection<AbstractProfile> getSLVs(AbstractProfile st) {
		return slvList.get(st.getUID());
	}

	public Collection<AbstractProfile> getDLVs(AbstractProfile st) {
		return dlvList.get(st.getUID());
	}

	@Override
	public boolean add(AbstractProfile st) {
		if (!super.add(st)) {
			return false;
		}

		maxStId = Math.max(maxStId, st.getUID());

		lvMap.put(st.getUID(), new STLV());

		slvList.put(st.getUID(), new LinkedList<AbstractProfile>());
		dlvList.put(st.getUID(), new LinkedList<AbstractProfile>());

		updateLVs(st);

		return true;
	}

	@Override
	public String toString() {
		return " " + new Integer(id).toString() + " ";
	}

	public void updateVisibleEdges() {

		// Kruskal's algorithm.
		Collections.sort(getEdges(), new GOeBurstEdgeComparator());
		DisjointSet s = new DisjointSet(maxStId);
		Iterator<Edge> iter = getEdges().iterator();
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
	}

	public void updateVisibleEdges(AbstractProfile root) {
		STLV data = lvMap.get(root.getUID());
		int[] orig = new int[MAXLV + 1];

		System.arraycopy(data.lv, 0, orig, 0, MAXLV + 1);

		for (int i = 0; i <= MAXLV; i++) {
			data.lv[i] = Integer.MAX_VALUE;
		}

		updateVisibleEdges();

		System.arraycopy(orig, 0, data.lv, 0, MAXLV + 1);
	}

	protected void updateLVs(AbstractProfile u) {

		GOeBurstCluster.STLV uLV = lvMap.get(u.getUID());
		LinkedList<AbstractProfile> uSLVs = slvList.get(u.getUID());
		LinkedList<AbstractProfile> uDLVs = dlvList.get(u.getUID());

		Iterator<AbstractProfile> vIter = getSTs().iterator();
		while (vIter.hasNext()) {
			AbstractProfile v = vIter.next();

			if (u.equals(v)) {
				continue;
			}

			GOeBurstCluster.STLV vLV = lvMap.get(v.getUID());

			int diff = ad.compute(u, v);

			if (diff == 1) {
				uSLVs.add(v);
				slvList.get(v.getUID()).add(u);
			}

			if (diff == 2) {
				uDLVs.add(v);
				dlvList.get(v.getUID()).add(u);
			}

			if (diff <= MAXLV) {
				uLV.lv[diff - 1]++;
				vLV.lv[diff - 1]++;
			} else {
				uLV.lv[MAXLV]++;
				vLV.lv[MAXLV]++;
			}
		}
	}

	protected class GOeBurstEdgeComparator implements Comparator<Edge> {

		@Override
		public int compare(Edge f, Edge e) {
			int ret = 0;
			int k = 0;

			ret = ad.compute(f.getU(), f.getV()) - ad.compute(e.getU(), e.getV());
			if (ret != 0) {
				return ret;
			}

			while (k < MAXLV) {
				ret = Math.max(
					lvMap.get(f.getU().getUID()).lv[k],
					lvMap.get(f.getV().getUID()).lv[k])
					- Math.max(
					lvMap.get(e.getU().getUID()).lv[k],
					lvMap.get(e.getV().getUID()).lv[k]);
				if (ret != 0) {
					break;
				}

				ret = Math.min(
					lvMap.get(f.getU().getUID()).lv[k],
					lvMap.get(f.getV().getUID()).lv[k])
					- Math.min(
					lvMap.get(e.getU().getUID()).lv[k],
					lvMap.get(e.getV().getUID()).lv[k]);
				if (ret != 0) {
					break;
				}

				k++;
			}

			/* ST frequency. */
			if (k >= MAXLV) {
				ret = Math.max(f.getU().getFreq(), f.getV().getFreq())
					- Math.max(e.getU().getFreq(), e.getV().getFreq());

				if (ret == 0) {
					ret = Math.min(f.getU().getFreq(), f.getV().getFreq())
						- Math.min(e.getU().getFreq(), e.getV().getFreq());
				}
			}

			/* Decreasing... */
			ret *= -1;

			/* Last chance... */

			Comparator<String> cmp = new Comparator<String>() {

				@Override
				public int compare(String o1, String o2) {
					int size = o1.length() - o2.length();
					if (size != 0) {
						return size;
					}
					return o1.compareTo(o2);
				}
			};


			if (ret == 0) {
				ret = cmp.compare(f.getU().getID(), f.getV().getID())
					- cmp.compare(e.getU().getID(), e.getV().getID());
			}

			//	ret = Math.min(f.getU().getUID(), f.getV().getUID())
			//	    - Math.min(e.getU().getUID(), e.getV().getUID());}
			if (ret == 0) {
				ret = cmp.compare(f.getU().getID(), f.getV().getID())
					- cmp.compare(e.getU().getID(), e.getV().getID());
			}
			//    ret = Math.max(f.getU().getUID(), f.getV().getUID())
			//	    - Math.max(e.getU().getUID(), e.getV().getUID());
			return ret;
		}

		public boolean equals(Edge f, Edge e) {
			return compare(f, e) == 0;
		}
	}
}
