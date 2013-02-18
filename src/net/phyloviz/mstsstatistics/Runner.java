/*-
 * Copyright (c) 2013, PHYLOViZ Team <phyloviz@gmail.com>
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
package net.phyloviz.mstsstatistics;

import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import net.phyloviz.algo.Edge;
import net.phyloviz.algo.util.DisjointSet;
import net.phyloviz.goeburst.GOeBurstResult;
import net.phyloviz.goeburst.cluster.GOeBurstClusterWithStats;
import net.phyloviz.goeburst.cluster.GOeBurstNodeExtended;
import org.apache.commons.lang3.ArrayUtils;
import org.openide.nodes.Node;

public class Runner implements Runnable {

	private GOeBurstResult gr;

	public Runner(GOeBurstResult gr) {
		this.gr = gr;
	}

	@Override
	public void run() {

		gr.getPanel().appendWithDate("\nMST Statistics started...\n");
		gr.getPanel().flush();

		List<EdgeMST> edgesList = new ArrayList<EdgeMST>();
		Collection<GOeBurstClusterWithStats> groups = gr.getClustering();
		Iterator<GOeBurstClusterWithStats> gIter = groups.iterator();

		while (gIter.hasNext()) {

			GOeBurstClusterWithStats g = gIter.next();
			ArrayList<net.phyloviz.goeburst.cluster.Edge<GOeBurstNodeExtended>> gEdges = g.getEdges();
			Iterator<net.phyloviz.goeburst.cluster.Edge<GOeBurstNodeExtended>> geIter = gEdges.iterator();
			while (geIter.hasNext()) {
				Edge<GOeBurstNodeExtended> e = geIter.next();
				int level = gr.getDistance().level(e);
				EdgeMST ne = new EdgeMST(e.getU(), e.getV(), level);
				edgesList.add(ne);
			}
			if (edgesList.isEmpty()) {
				continue;
			}

			double nmsts = calcNumberMSTs(edgesList);

			gr.getPanel().append("CC " + g.getID() + " has " + Math.round(nmsts) + " MSTs\nEdge stats:\n");
			Iterator<EdgeMST> ei = edgesList.iterator();
			while (ei.hasNext()) {
				EdgeMST e = ei.next();
				gr.getPanel().append(e.getSourceNode().getID() + " - " + e.getDestNode().getID()
					+ ", level: " + e.getLevel() + ", freq: " + Math.round(e.getNmsts() * 10000) / 100.0 + "%\n");
			}
			gr.getPanel().append("\n");
			gr.getPanel().flush();

			edgesList.clear();
		}
	}

	private static void calcEdgesNMSTs(List edgesList, int prev, int now, int[] map, int[] mapaux, ArrayList[] calcDet, SparseDoubleMatrix2D matrix, double[] calcNMSTs) {
		for (int i = prev; i < now; i++) {
			EdgeMST e = (EdgeMST) edgesList.get(i);
			e.setNmsts(map, mapaux, calcDet, matrix, calcNMSTs);
		}


	}

	private static int findMaxId(List edgesList) {
		int id = 0;
		Iterator<EdgeMST> eIter = edgesList.iterator();
		while (eIter.hasNext()) {
			EdgeMST e = (EdgeMST) eIter.next();
			id = Math.max(id, e.getSource());
			id = Math.max(id, e.getDest());
		}


		return id;
	}

	private static double calcNumberMSTs(List edgesList) {

		Collections.sort(edgesList);

		double nmsts = 1;

		int mapid;

		//Passo 1 - Descobrir o MaxId Inicial
		int maxid = findMaxId(edgesList);
		SparseDoubleMatrix2D matrix = new SparseDoubleMatrix2D(maxid + 1, maxid + 1);
		matrix.assign(0);

		DisjointSet ds = new DisjointSet(maxid);

		int[] map = new int[maxid + 1];
		for (int i = 0; i <= maxid; i++) {
			map[i] = i;
		}

		int[] mapaux = new int[maxid + 1];
		for (int i = 0; i <= maxid; i++) {
			mapaux[i] = -1;
		}


		//Passo 2 - Varrer arcos do nivel L, preenchendo a matriz
		int level = 1;
		Iterator<EdgeMST> eIter = edgesList.iterator();

		EdgeMST e = eIter.next();


		ArrayList<Integer> vaux = new ArrayList<Integer>(maxid + 1);

		int prev = 0;
		int now = 0;

		while (true) {
			if (e != null && e.getLevel() == level) {
				int u = e.getSource();
				int v = e.getDest();
				if (!vaux.contains(u)) {
					vaux.add(u);
				}
				if (!vaux.contains(v)) {
					vaux.add(v);
				}
				//Preenchimento da Matriz
				int s = map[u];
				int d = map[v];

				matrix.setQuick(s, d, matrix.getQuick(s, d) - 1);
				matrix.setQuick(d, s, matrix.getQuick(d, s) - 1);
				matrix.setQuick(s, s, matrix.getQuick(s, s) + 1);
				matrix.setQuick(d, d, matrix.getQuick(d, d) + 1);

				if (!ds.sameSet(u, v)) {
					ds.unionSet(u, v);
				}

				now++;

				try {
					e = eIter.next();
				} catch (NoSuchElementException ex) {
					e = null;
				}

			} else {
				mapid = 0;
				for (int i = 0; i <= maxid; i++) {
					int setid = ds.findSet(i);
					if (mapaux[setid] == -1) {
						mapaux[setid] = mapid;
						mapaux[i] = mapid;
						mapid++;
					} else {
						mapaux[i] = mapaux[setid];
					}
				}


				ArrayList[] calcDet = new ArrayList[mapid];
				for (int i = 0; i < calcDet.length; i++) {
					calcDet[i] = new ArrayList<Integer>();
				}


				for (int i = 0; i < vaux.size(); i++) {
					if (!calcDet[mapaux[vaux.get(i)]].contains(map[vaux.get(i)])) {
						calcDet[mapaux[vaux.get(i)]].add(map[vaux.get(i)]);
					}
				}

				double[] calcNMstsDet = new double[mapid];
				for (int i = 0; i < calcDet.length; i++) {
					int[] vgraph = new int[calcDet[i].size()];
					ArrayList<Integer> graph = (ArrayList<Integer>) calcDet[i];
					Iterator<Integer> gIter = graph.iterator();
					int index = 0;
					while (gIter.hasNext()) {
						vgraph[index++] = gIter.next();
					}
					Algebra a = new Algebra();
					double det = a.det(matrix.viewSelection(ArrayUtils.subarray(vgraph, 0, vgraph.length - 1), ArrayUtils.subarray(vgraph, 0, vgraph.length - 1)));

					if (det == 0) {
						det = 1;
					}
					if (index > 0) {
						calcNMstsDet[mapaux[vgraph[0]]] = det;
					}
					nmsts = nmsts * det;
				}

				calcEdgesNMSTs(edgesList, prev, now, map, mapaux, calcDet, matrix, calcNMstsDet);
				prev = now;
				if (e == null) {
					break;
				}

				matrix = new SparseDoubleMatrix2D(mapid, mapid);
				matrix.assign(0);
				map = mapaux;
				mapaux = new int[maxid + 1];
				for (int i = 0; i <= maxid; i++) {
					mapaux[i] = -1;
				}
				//Passa para o nível seguinte
				level++;
				vaux = new ArrayList<Integer>(mapid);
			}
		}



		return nmsts;
	}
}
