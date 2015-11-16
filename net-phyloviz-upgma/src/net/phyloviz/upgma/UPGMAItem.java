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
package net.phyloviz.upgma;

import net.phyloviz.upgmanjcore.AbstractClusteringMethod;
import net.phyloviz.algo.AbstractDistance;
import net.phyloviz.core.util.NodeFactory;
import net.phyloviz.project.ProjectItem;
import net.phyloviz.upgma.json.UPGMAToJSON;
import net.phyloviz.upgma.tree.UPGMALeafNode;
import net.phyloviz.upgma.tree.UPGMARoot;
import net.phyloviz.upgma.ui.OutputPanel;
import net.phyloviz.upgma.ui.UPGMANode;
import net.phyloviz.upgmanjcore.distance.ClusteringDistance;
import net.phyloviz.upgmanjcore.visualization.PersistentVisualization;
import org.openide.nodes.AbstractNode;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

public class UPGMAItem implements NodeFactory, Lookup.Provider, ProjectItem {

    private final OutputPanel op;
    private final ClusteringDistance<UPGMALeafNode> ad;
    private final UPGMARoot root;
    private final InstanceContent ic;
    private final AbstractLookup lookup;
    private final AbstractClusteringMethod cm;
    private PersistentVisualization cp;

    public UPGMAItem(UPGMARoot root, ClusteringDistance<UPGMALeafNode> ad, AbstractClusteringMethod cm, OutputPanel op) {
        this.op = op;
        this.ad = ad;
        this.cm = cm;
        this.root = root;

        ic = new InstanceContent();
        lookup = new AbstractLookup(ic);
    }

    public OutputPanel getPanel() {
        return op;
    }

    public UPGMARoot getRoot() {
        return root;
    }

    public AbstractDistance getDistance() {
        return ad;
    }

    @Override
    public AbstractNode getNode() {
        return new UPGMANode(this);
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    public void add(Object o) {
        ic.add(o);
    }

    public void remove(Object o) {
        ic.remove(o);
    }

    @Override
    public String toString() {
        return "Hierarchical Clustering  - " + cm.toString() + " - (" + ad.toString() + ")";
    }

    @Override
    public String getMethodProviderName() {
        return cm.toString().split(" ")[0].toLowerCase();
    }

    @Override
    public String getFactoryName() {
        return "UPGMAItemFactory";
    }

    @Override
    public String getOutput() {
        UPGMAToJSON json = new UPGMAToJSON(root);
        String output = json.saveToJSON();
        return output;
    }

    @Override
    public void addPersistentVisualization(PersistentVisualization cp) {
        this.cp = cp;
    }

    @Override
    public PersistentVisualization getPersistentVisualization() {
        return this.cp;
    }

    @Override
    public String getDistanceProvider() {
        return ad.getClass().getCanonicalName();
    }

    @Override
    public String getAlgorithmLevel(){
        return "0";
    }

}
