/*-
 * Copyright (c) 2016, PHYLOViZ Team <phyloviz@gmail.com>
 * All rights reserved.
 * 
 * This file is part of PHYLOViZ <http://www.phyloviz.net/>.
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
 */

package net.phyloviz.nj.run;

import net.phyloviz.nj.tree.NJLeafNode;
import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.nj.AgglomerativeClusteringMethod;
import net.phyloviz.nj.tree.NeighborJoiningItem;
import net.phyloviz.nj.tree.NJRoot;
import net.phyloviz.nj.algorithm.NJ;
import net.phyloviz.upgmanjcore.distance.ClusteringDistance;
import net.phyloviz.nj.ui.OutputPanel;

/**
 *
 * @author Adriano
 */
public class NeighborJoiningRunner implements Runnable{
    
    private final OutputPanel op;
    private final ClusteringDistance<NJLeafNode> ad;
    private final AgglomerativeClusteringMethod cm;
    private final TypingData<? extends Profile> td;

    public NeighborJoiningRunner(OutputPanel op, ClusteringDistance<NJLeafNode> ad, AgglomerativeClusteringMethod cm, TypingData<? extends Profile> td) {
        this.op = op;
        this.ad = ad;
        this.td = td;
        this.cm = cm;
    }

    @Override
    public void run() {
        op.appendWithDate("Neighbor-Joining has started\nNJ algorithm: computing nodes...\n");
        op.flush();

        op.appendWithDate("\nNJ algorithm: computing diferences...\n");
        op.flush();
        
        NJ matrix = cm.getClusteringMethod(td, ad, op);
        NJRoot root = matrix.generateTree();
        op.flush();

        op.appendWithDate("\nNJ algorithm: finished!\n");
        op.flush();

        td.add(new NeighborJoiningItem(root, ad, cm, op));
    }
}
