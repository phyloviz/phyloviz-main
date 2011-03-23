package net.phyloviz.goeburst.tree;

import java.util.Collection;
import net.phyloviz.algo.tree.Edge;
import net.phyloviz.core.data.DataSet;
import net.phyloviz.core.util.NodeFactory;
import net.phyloviz.goeburst.AbstractDistance;
import net.phyloviz.goeburst.Result;
import net.phyloviz.goeburst.ui.GOeBurstMSTNode;
import net.phyloviz.goeburst.ui.OutputPanel;
import org.openide.nodes.AbstractNode;

public class GOeBurstMSTResult implements NodeFactory, Result {

	private DataSet ds;
	private Collection<Edge> edges;
	private AbstractDistance ad;
	private OutputPanel op;

	public GOeBurstMSTResult(DataSet ds, Collection<Edge> edges, AbstractDistance ad, OutputPanel op) {
		this.ds = ds;
		this.edges = edges;
		this.op = op;
		this.ad = ad;
	}

	@Override
	public OutputPanel getPanel() {
		return op;
	}

	public Collection<Edge> getEdges() {
		return edges;
	}

	public AbstractDistance getDistance() {
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
