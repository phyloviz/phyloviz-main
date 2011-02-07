package net.phyloviz.goeburst.tree;

import java.util.Collection;
import java.util.Iterator;
import net.phyloviz.algo.tree.Node;
import net.phyloviz.goeburst.AbstractDistance;
import net.phyloviz.core.data.Profile;

public class GOeBurstTreeNode implements Node {

	private Profile p;
	private int[] lv;

	public GOeBurstTreeNode(Profile p) {
		this.p = p;
	}

	@Override
	public Profile getProfile() {
		return p;
	}

	public void updateLVs(Collection<GOeBurstTreeNode> nlst, AbstractDistance ad, int maxLen) {

		lv = new int[maxLen];

		Iterator<GOeBurstTreeNode> vIter = nlst.iterator();
		while (vIter.hasNext()) {
			Profile v = vIter.next().getProfile();

			if (p.equals(v))
				continue;

			lv[ad.compute(p, v) - 1] ++;
		}
	}

	public int getLV(int idx) {

		if (idx < 0 || idx >= lv.length)
			return -1;

		return lv[idx];
	}

	public int diffLV(GOeBurstTreeNode v) {
		int ret, i;

		for (ret = i = 0; ret == 0 && i < lv.length; i++)
			ret = getLV(i) - v.getLV(i);

		return Integer.signum(ret)*i;
	}
}
