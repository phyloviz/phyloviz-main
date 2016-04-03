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

package net.phyloviz.upgma.run;

import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.upgma.HierarchicalClusteringMethod;
import net.phyloviz.upgma.UPGMAItem;
import net.phyloviz.upgma.ui.OutputPanel;
import net.phyloviz.upgma.algorithm.UPGMA;
import net.phyloviz.upgma.tree.UPGMALeafNode;
import net.phyloviz.upgma.tree.UPGMARoot;
import net.phyloviz.upgmanjcore.distance.ClusteringDistance;


public class UPGMARunner implements Runnable{
    
    private final OutputPanel op;
    private final ClusteringDistance<UPGMALeafNode> ad;
    private final TypingData<? extends Profile> td;
    private final HierarchicalClusteringMethod cm;

    public UPGMARunner(OutputPanel op, ClusteringDistance<UPGMALeafNode> ad, HierarchicalClusteringMethod cm, TypingData<? extends Profile> td) {
        this.op = op;
        this.ad = ad;
        this.td = td;
        this.cm = cm;
    }

    
    @Override
    public void run() {
        
        op.appendWithDate("Hierarchical Clustering - "+cm.toString()+" - (" +ad.toString()+  ") started\n computing distance matrix...\n\n");
        op.flush();
        
        UPGMA algorithm = new UPGMA(td, ad, cm, op);
        
        UPGMARoot root = algorithm.execute();
       
        op.appendWithDate( cm.toString() + " algorithm: done.\n");
        op.flush();
        
        td.add(new UPGMAItem(root, ad, cm, op));
    }
    
}
