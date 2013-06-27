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

package net.phyloviz.goeburst.run;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import net.phyloviz.algo.Edge;
import net.phyloviz.core.data.DataSet;
import net.phyloviz.core.data.Population;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.algo.AbstractDistance;
import net.phyloviz.algo.tree.MSTAlgorithm;
import net.phyloviz.core.data.Profile;
import net.phyloviz.goeburst.tree.GOeBurstMSTResult;
import net.phyloviz.goeburst.tree.GOeBurstNode;
import net.phyloviz.goeburst.ui.OutputPanel;
import org.openide.nodes.Node;

public class MSTRunner implements Runnable {

	private Node n;
	private OutputPanel op;
	private AbstractDistance<GOeBurstNode> ad;
	static int times = 0;

	public MSTRunner(Node n, OutputPanel op, AbstractDistance<GOeBurstNode> ad) {
		this.n = n;
		this.op = op;
		this.ad = ad;

	}

	@Override
	public void run() {

		op.appendWithDate("MST algorithm has started\nMST algorithm: computing nodes...\n");
		op.flush();

		DataSet ds = n.getParentNode().getLookup().lookup(DataSet.class);
		Population pop = ds.getLookup().lookup(Population.class);
		TypingData<? extends Profile> td = (TypingData<? extends Profile>) n.getLookup().lookup(TypingData.class);

		ArrayList<GOeBurstNode> nlst = new ArrayList<GOeBurstNode>();

		Iterator<? extends Profile> ip = td.iterator();
		while (ip.hasNext())
			nlst.add(new GOeBurstNode(ip.next()));

		op.appendWithDate("\nMST algorithm: computing LVs...\n");
		op.flush();

		int maxLV = ad.maxLevel();

		Iterator<GOeBurstNode> in = nlst.iterator();
		while (in.hasNext())
			in.next().updateLVs(nlst, ad, maxLV);

		op.appendWithDate("\nMST algorithm: sorting nodes...\n");
		op.flush();

		Collections.sort(nlst, ad.getProfileComparator());

		op.appendWithDate("\nMST algorithm: computing tree edges...\n");
		op.flush();

		Collection<Edge<GOeBurstNode>> tree = null;
		if (nlst.size() > 2) {
			MSTAlgorithm<GOeBurstNode> algorithm = new MSTAlgorithm<GOeBurstNode>(nlst, ad.getEdgeComparator());
			tree = algorithm.getTree();
		}

		op.appendWithDate("\nMST algorithm: printing edges...\n");
		op.flush();

		Iterator<Edge<GOeBurstNode>> ei = tree.iterator();
		while (ei.hasNext()) {
			Edge<GOeBurstNode> e = ei.next();

			int diff = ad.level(e);

			op.append(e.getU().getID() + " -- " + e.getV().getID() + " (level: " + diff);
			String einfo = ad.info(e);
			if (einfo != null)
				op.append("; " + ad.info(e) + ")\n");
			else
				op.append(")\n");
		}

		op.appendWithDate("MST algorithm: done.\n");
		op.flush();

		td.add(new GOeBurstMSTResult(ds, tree, ad, op));
	}
}
