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

import java.util.Collection;
import net.phyloviz.algo.Edge;
import net.phyloviz.core.data.DataSet;
import net.phyloviz.core.util.NodeFactory;
import net.phyloviz.algo.AbstractDistance;
import net.phyloviz.goeburst.Result;
import net.phyloviz.goeburst.ui.GOeBurstMSTNode;
import net.phyloviz.goeburst.ui.OutputPanel;
import org.openide.nodes.AbstractNode;

public class GOeBurstMSTResult implements NodeFactory, Result {

	private DataSet ds;
	private Collection<Edge<GOeBurstNode>> edges;
	private AbstractDistance<GOeBurstNode> ad;
	private OutputPanel op;

	public GOeBurstMSTResult(DataSet ds, Collection<Edge<GOeBurstNode>> edges, AbstractDistance<GOeBurstNode> ad, OutputPanel op) {
		this.ds = ds;
		this.edges = edges;
		this.op = op;
		this.ad = ad;
	}

	@Override
	public OutputPanel getPanel() {
		return op;
	}

	public Collection<Edge<GOeBurstNode>> getEdges() {
		return edges;
	}

	public AbstractDistance<GOeBurstNode> getDistance() {
		return ad;
	}

	@Override
	public AbstractNode getNode() {
		return new GOeBurstMSTNode(this);
	}

	@Override
	public String toString() {
		return "goeBURST Full MST";
	}

	public DataSet getDataSet() {
		return ds;
	}
}
