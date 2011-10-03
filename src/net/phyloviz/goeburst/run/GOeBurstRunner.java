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
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.SwingUtilities;
import net.phyloviz.core.data.AbstractProfile;
import net.phyloviz.core.data.DataItem;
import net.phyloviz.core.data.DataModel;
import net.phyloviz.core.data.DataSet;
import net.phyloviz.core.data.Population;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.goeburst.GOeBurstResult;
import net.phyloviz.algo.AbstractDistance;
import net.phyloviz.core.data.Profile;
import net.phyloviz.goeburst.algorithm.GOeBurstWithStats;
import net.phyloviz.goeburst.cluster.Edge;
import net.phyloviz.goeburst.cluster.GOeBurstCluster;
import net.phyloviz.goeburst.cluster.GOeBurstClusterWithStats;
import net.phyloviz.goeburst.cluster.GOeBurstNodeExtended;
import net.phyloviz.goeburst.ui.OutputPanel;
import org.openide.nodes.Node;

public class GOeBurstRunner implements Runnable {

	private Node n;
	private DataSet ds;
	private Population pop;
	private OutputPanel op;
	private AbstractDistance<GOeBurstNodeExtended> ad;
	private HashMap<String, String> st2cl;
	private int level;
	static int times = 0;

	public GOeBurstRunner(Node n, OutputPanel op, int level, AbstractDistance<GOeBurstNodeExtended> ad) {
		this.n = n;
		this.op = op;
		this.level = level;
		this.ad = ad;
		ds = n.getParentNode().getLookup().lookup(DataSet.class);
		pop = ds.getLookup().lookup(Population.class);
	}

	@Override
	public void run() {

		op.appendWithDate("goeBURST started\ngoeBURST algorithm: computing groups...\n\n");
		op.flush();
		
		TypingData<? extends AbstractProfile> td = (TypingData<? extends AbstractProfile>) n.getLookup().lookup(TypingData.class);

		GOeBurstWithStats algorithm = new GOeBurstWithStats(level);
		Collection<GOeBurstClusterWithStats> groups = algorithm.getClustering(td, ad);

		// Integrate with isolate data, if it exists.
		if (pop != null) {
			op.appendWithDate("goeBURST algorithm: integrating data...\n\n");
			op.flush();

			st2cl = new HashMap<String, String>();
			Iterator<? extends GOeBurstCluster> igo = groups.iterator();
			while (igo.hasNext()) {
				GOeBurstCluster cluster = igo.next();
				String id = "" + cluster.getID();
				Iterator<? extends Profile> itr = cluster.getSTs().iterator();
				while (itr.hasNext()) {
					st2cl.put(itr.next().getID(), id);
				}
			}
			pop.addColumn("goeBURST[" + (times++) + "]", new DataModel.ColumnFiller() {

				@Override
				public String getValue(DataItem i) {
					return st2cl.get(i.get(ds.getPopKey()));
				}
			});

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					pop.tableModel().fireTableStructureChanged();
				}
			});
		}

		Iterator<GOeBurstClusterWithStats> ig = groups.iterator();
		op.append("#CC = " + groups.size() + "\n\n");
		op.flush();
		while (ig.hasNext()) {
			GOeBurstClusterWithStats g = ig.next();

			op.append("CC " + g.getID() + " has " + g.size() + " STs:\n");

			Iterator<? extends Profile> is = g.getSTs().iterator();
			while (is.hasNext()) {
				GOeBurstNodeExtended st = (GOeBurstNodeExtended) is.next();

				op.append("ST " + st.getID() + " (" + st.getFreq() + ") "
					+ st.getLV(0) + " " + st.getLV(1) + " " + st.getLV(2) + " " + st.getLV(3) + " ("
					+ algorithm.getSTxLV(st, 0) + " " + algorithm.getSTxLV(st, 1) + " " + algorithm.getSTxLV(st, 2) + " " + algorithm.getSTxLV(st, 3)
					+ ")" + (g.isFounder(st) ? " *" : "  ") + "\n");

			}

			ArrayList<Edge<GOeBurstNodeExtended>> elst = g.getEdges();
			if (g.size() >= 2) {
				op.append("\nCC " + g.getID() + " has " + (g.size() - 1) + "/" + elst.size() + " selected edges:\n");
			} else {
				op.append("\nCC " + g.getID() + " has " + (g.size() - 1) + "/" + elst.size() + " selected edges\n");
			}
			Iterator<Edge<GOeBurstNodeExtended>> ie = elst.iterator();
			while (ie.hasNext()) {
				Edge<GOeBurstNodeExtended> e = ie.next();
				if (e.visible()) {
					op.append(e.getU().getID() + " - " + e.getV().getID() + "\n");
				}
			}
			op.append("\n");
		}

		op.appendWithDate("goeBURST algorithm: computing statistics...\n");
		op.flush();
		Iterator<GOeBurstClusterWithStats> iter = groups.iterator();
		while (iter.hasNext()) {
			iter.next().computeStatistics();
		}
		op.appendWithDate("goeBURST algorithm: done.\n");
		op.flush();

		ds.add(new GOeBurstResult(groups, ad, level, op));
	}
}
