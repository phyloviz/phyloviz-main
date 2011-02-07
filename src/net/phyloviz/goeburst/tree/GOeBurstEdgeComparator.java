package net.phyloviz.goeburst.tree;

import java.util.Comparator;
import net.phyloviz.algo.tree.Edge;
import net.phyloviz.goeburst.AbstractDistance;

public class GOeBurstEdgeComparator implements Comparator<Edge> {

	private AbstractDistance ad;
	private int maxLV;

	public GOeBurstEdgeComparator(AbstractDistance ad, int maxLV) {
		super();
		this.ad = ad;
		this.maxLV = maxLV;
	}

	@Override
	public int compare(Edge f, Edge e) {

			int ret = 0;
			int k = 0;

			ret = ad.compute(f.getU().getProfile(), f.getV().getProfile())
				- ad.compute(e.getU().getProfile(), e.getV().getProfile());

			if (ret != 0) {
				return ret;
			}

			while (k < maxLV) {

				ret = Math.max(
					((GOeBurstTreeNode) f.getU()).getLV(k),
					((GOeBurstTreeNode) f.getV()).getLV(k))
					- Math.max(
					((GOeBurstTreeNode) e.getU()).getLV(k),
					((GOeBurstTreeNode) e.getV()).getLV(k));
				if (ret != 0) {
					break;
				}

				ret = Math.min(
					((GOeBurstTreeNode) f.getU()).getLV(k),
					((GOeBurstTreeNode) f.getV()).getLV(k))
					- Math.min(
					((GOeBurstTreeNode) f.getU()).getLV(k),
					((GOeBurstTreeNode) f.getV()).getLV(k));

				if (ret != 0) {
					break;
				}

				k++;
			}

			/* ST frequency. */
			if (k >= maxLV) {
				ret = Math.max(f.getU().getProfile().getFreq(), f.getV().getProfile().getFreq())
					- Math.max(e.getU().getProfile().getFreq(), e.getV().getProfile().getFreq());

				if (ret == 0) {
					ret = Math.min(f.getU().getProfile().getFreq(), f.getV().getProfile().getFreq())
						- Math.min(e.getU().getProfile().getFreq(), e.getV().getProfile().getFreq());
				}
			}

			/* Decreasing... */
			ret *= -1;
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

			/* Last chance... */
			if (ret == 0) {
				ret = cmp.compare(f.getU().getProfile().getID(), f.getV().getProfile().getID())
					- cmp.compare(e.getU().getProfile().getID(), e.getV().getProfile().getID());
			}

			return ret;
	}
}
