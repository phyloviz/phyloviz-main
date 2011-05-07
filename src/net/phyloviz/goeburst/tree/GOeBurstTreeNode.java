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
