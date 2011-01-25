package net.phyloviz.goeburst;

import java.util.Collection;
import net.phyloviz.core.util.NodeFactory;
import net.phyloviz.goeburst.cluster.GOeBurstClusterWithStats;
import net.phyloviz.goeburst.ui.GOeBurstNode;
import net.phyloviz.goeburst.ui.OutputPanel;
import org.openide.nodes.AbstractNode;

public class GOeBurstResult implements NodeFactory {

	private Collection<GOeBurstClusterWithStats> clustering;
	private OutputPanel op;

	public GOeBurstResult(Collection<GOeBurstClusterWithStats> clustering, OutputPanel op) {
		this.clustering = clustering;
		this.op = op;
	}

	public OutputPanel getPanel() {
		return op;
	}

	public Collection<GOeBurstClusterWithStats> getClustering() {
		return clustering;
	}

	@Override
	public AbstractNode getNode() {
		return new GOeBurstNode(this);
	}

}
