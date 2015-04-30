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

import java.util.Collections;
import java.util.Iterator;
import net.phyloviz.algo.AbstractDistance;
import net.phyloviz.algo.util.DisjointSet;

public class GOeBurstCluster extends Cluster<GOeBurstNodeExtended> {

	public static int MAXLV = 3;
	protected int maxStId;
	protected AbstractDistance<GOeBurstNodeExtended> ad;

	public GOeBurstCluster(AbstractDistance<GOeBurstNodeExtended> ad) {
		super();
		this.ad = ad;
	}

	@Override
	public boolean add(GOeBurstNodeExtended st) {
		if (!super.add(st)) {
			return false;
		}

		maxStId = Math.max(maxStId, st.getUID());
		st.updateLVsExtended(this.getSTs(), ad, MAXLV);

		return true;
	}

	@Override
	public String toString() {
		return " " + Integer.toString(id) + " ";
	}

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
	}

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
}
