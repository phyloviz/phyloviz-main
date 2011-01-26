package net.phyloviz.goeburst.cluster;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;
import net.phyloviz.core.data.AbstractType;
import net.phyloviz.goeburst.algorithm.DisjointSet;
import net.phyloviz.goeburst.algorithm.HammingDistance;

public class GOeBurstCluster extends Cluster {

	public static int MAXLV = 3;

	public static class STLV {

		public int[] lv = new int[MAXLV + 1];
	}
	//protected int id;
	protected int maxStId;
	protected TreeMap<Integer, STLV> lvMap;
	protected TreeMap<Integer, LinkedList<AbstractType>> slvList;
	protected TreeMap<Integer, LinkedList<AbstractType>> dlvList;

	public GOeBurstCluster() {
		super();
		lvMap = new TreeMap<Integer, STLV>();
		slvList = new TreeMap<Integer, LinkedList<AbstractType>>();
		dlvList = new TreeMap<Integer, LinkedList<AbstractType>>();
	}

	public Collection<AbstractType> getSLVs(AbstractType st) {
		return slvList.get(st.getUID());
	}

	public Collection<AbstractType> getDLVs(AbstractType st) {
		return dlvList.get(st.getUID());
	}

	@Override
	public boolean add(AbstractType st) {
		if (!super.add(st)) {
			return false;
		}

		maxStId = Math.max(maxStId, st.getUID());

		lvMap.put(st.getUID(), new STLV());

		slvList.put(st.getUID(), new LinkedList<AbstractType>());
		dlvList.put(st.getUID(), new LinkedList<AbstractType>());

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

	public void updateVisibleEdges(AbstractType root) {
		STLV data = lvMap.get(root.getUID());
		int[] orig = new int[MAXLV + 1];

		System.arraycopy(data.lv, 0, orig, 0, MAXLV + 1);

		for (int i = 0; i <= MAXLV; i++) {
			data.lv[i] = Integer.MAX_VALUE;
		}

		updateVisibleEdges();

		System.arraycopy(orig, 0, data.lv, 0, MAXLV + 1);
	}

	protected void updateLVs(AbstractType u) {

		GOeBurstCluster.STLV uLV = lvMap.get(u.getUID());
		LinkedList<AbstractType> uSLVs = slvList.get(u.getUID());
		LinkedList<AbstractType> uDLVs = dlvList.get(u.getUID());

		Iterator<AbstractType> vIter = getSTs().iterator();
		while (vIter.hasNext()) {
			AbstractType v = vIter.next();

			if (u.equals(v)) {
				continue;
			}

			GOeBurstCluster.STLV vLV = lvMap.get(v.getUID());

			int diff = HammingDistance.compute(u, v);

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

			ret = HammingDistance.compute(f.getU(), f.getV()) - HammingDistance.compute(e.getU(), e.getV());
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