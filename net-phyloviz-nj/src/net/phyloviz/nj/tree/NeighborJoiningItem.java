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
package net.phyloviz.nj.tree;

import net.phyloviz.algo.AbstractDistance;
import net.phyloviz.core.data.DataSet;
import net.phyloviz.core.util.NodeFactory;
import net.phyloviz.nj.json.NJToJSON;
import net.phyloviz.nj.ui.OutputPanel;
import net.phyloviz.nj.ui.NeighborJoiningNode;
import net.phyloviz.project.ProjectItem;
import org.openide.nodes.AbstractNode;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

public class NeighborJoiningItem implements NodeFactory, Lookup.Provider, ProjectItem{

    private DataSet ds;
    private AbstractDistance<NJLeafNode> ad;
    private OutputPanel op;
    private NJRoot root;
    private InstanceContent ic;
    private AbstractLookup lookup;

    public NeighborJoiningItem(NJRoot root, DataSet ds, AbstractDistance<NJLeafNode> ad, OutputPanel op) {
        this.root = root;
        this.ds = ds;
        this.op = op;
        this.ad = ad;
        
        ic = new InstanceContent();
        lookup = new AbstractLookup(ic);
    }
    public OutputPanel getPanel() {
        return op;
    }
    @Override
    public AbstractNode getNode() {
        return new NeighborJoiningNode(this);
    }
    @Override
    public String toString() {
        return "NeighborJoining (" + ad.toString() + ")";
    }
    public NJRoot getRoot() {
        return root;
    }    
    public DataSet getDataSet() {
        return ds;
    }
    public AbstractDistance getDistance() {
        return ad;
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
    public String getName() {
        return "nj";
    }
    @Override
    public String getMainName() {
        return getName();
    }

    @Override
    public String getOutput() {
        NJToJSON json = new NJToJSON(root);
        String output = json.saveToJSON();
        return output;
    }

}
