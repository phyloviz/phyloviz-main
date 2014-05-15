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

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import cern.colt.matrix.linalg.LUDecomposition;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.NoSuchElementException;
import net.phyloviz.goeburst.cluster.Edge;
import net.phyloviz.algo.util.DisjointSet;
import net.phyloviz.goeburst.GOeBurstResult;
import net.phyloviz.goeburst.cluster.GOeBurstClusterWithStats;
import net.phyloviz.goeburst.cluster.GOeBurstNodeExtended;
import org.apache.commons.lang3.ArrayUtils;

public class Runner implements Runnable {

    private GOeBurstResult gr;
    private static Map<Integer, Double> result;

    public Runner(GOeBurstResult gr) {
        this.gr = gr;
        this.result = new HashMap<Integer,Double>();
    }

    @Override
    public void run() {

        gr.getPanel().appendWithDate("MST Statistics started...\n");
        gr.getPanel().flush();

        System.out.println("MST Statistics started... \n");

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
                EdgeMST ne = new EdgeMST(e.getU(), e.getV(), level, e.visible());
                edgesList.add(ne);
            }
            if (edgesList.isEmpty()) {
                continue;
            }

            gr.getPanel().appendWithDate("Processing CC " + g.getID() + "\n");
            double nmsts = calcNumberMSTs(edgesList, gr);

            /*		DecimalFormat df = new DecimalFormat("0.######E0");
             DecimalFormat pf = new DecimalFormat("#.##");*/
//			if (nmsts < Double.POSITIVE_INFINITY) {
            gr.getPanel().append("CC " + g.getID() + " has 10^ " + nmsts + " MSTs\nEdge stats:\n");

            Iterator<EdgeMST> ei = edgesList.iterator();
            while (ei.hasNext()) {
                EdgeMST e = ei.next();
                if (e.isVisible()) {
                    gr.getPanel().append(e.getSourceNode().getID() + " - " + e.getDestNode().getID()
                        + ", level: " + e.getLevel() + ", freq: " + (Math.pow(10, e.getNmsts()) * 100.0)
                        + "% (" + e.getNmsts() + ")\n");
                    gr.getPanel().flush();
                    
                    String source = String.valueOf(e.getSourceNode().getID());
                    String dest = String.valueOf(e.getDestNode().getID());
                    String edge = source+dest;
                    result.put(Integer.valueOf(edge), e.getNmsts());

                }

            }
            /*	} else {
             gr.getPanel().append("CC " + g.getID() + " has more than 1E100 MSTs\nSkipping edge stats.\n");	
             }*/

            gr.getPanel().append("\n");
            gr.getPanel().flush();

            edgesList.clear();
        }
        gr.getPanel().appendWithDate("MST Statistics done.\n");
        gr.getPanel().flush();
        
        gr.setEdgestats(result);
    }

    private void calcEdgesNMSTs(List edgesList, int prev, int now, int[] map, int[] mapaux, ArrayList[] calcDet, SparseDoubleMatrix2D matrix, double[] calcNMSTs) {
        int size = now-prev;
        gr.getPanel().appendWithDate("Calculating Edge Stats: \n");
        for (int i = prev; i < now; i++) {
            EdgeMST e = (EdgeMST) edgesList.get(i);
            if (e.isVisible()) {
                gr.getPanel().appendWithDate(Integer.toString(i-prev+1) + "/" + Integer.toString(size) + "\n");
                gr.getPanel().flush();
                e.setNmsts(map, mapaux, calcDet, matrix, calcNMSTs);
                
            }

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

    private double calcNumberMSTs(List edgesList, GOeBurstResult sgr) {

        Collections.sort(edgesList);

        double nmsts = 0;

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
        Iterator<EdgeMST> eIter = edgesList.iterator();

        EdgeMST e = eIter.next();
        int level = (e != null) ? e.getLevel() : 1;

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

            } else if (prev != now) {
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

                sgr.getPanel().appendWithDate("Level " + level + "\n");

                double[] calcNMstsDet = new double[mapid];
                for (int i = 0; i < calcDet.length; i++) {

                    if (!calcDet[i].isEmpty()) {

                        int[] vgraph = new int[calcDet[i].size()];
                        ArrayList<Integer> graph = (ArrayList<Integer>) calcDet[i];
                        Iterator<Integer> gIter = graph.iterator();
                        int index = 0;
                        while (gIter.hasNext()) {
                            vgraph[index++] = gIter.next();
                        }

                        double det = 0.0;
                        LUDecomposition tempMax = new LUDecomposition((matrix.viewSelection(ArrayUtils.subarray(vgraph, 0, vgraph.length - 1),
                            ArrayUtils.subarray(vgraph, 0, vgraph.length - 1))));
                        DoubleMatrix2D max = tempMax.getU();
                        System.out.println(max.rows());
                        for (int k = 0; k < max.rows(); k++) {
                            det += Math.log10(Math.abs(max.get(k, k)));
                        }

                        if (det == 0) {
                            det = 1;
                        }

                        sgr.getPanel().append("View: " + Arrays.toString(vgraph) + "\n");
                        sgr.getPanel().append("Det: " + det + "\n");
                        sgr.getPanel().flush();

                        calcNMstsDet[i] = det;
                        nmsts = nmsts + det;

                    }
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
            } else { // Se prev==now a lista é vazia e passamos para o nível seguinte
                level++;
            }
        }

        return nmsts;
    }
}
