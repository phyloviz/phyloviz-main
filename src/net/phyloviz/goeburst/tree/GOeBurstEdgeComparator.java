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
