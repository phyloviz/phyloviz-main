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
	private int level;

	public GOeBurstResult(Collection<GOeBurstClusterWithStats> clustering, int level, OutputPanel op) {
		this.clustering = clustering;
		this.op = op;
		this.level = level;

	}

	public OutputPanel getPanel() {
		return op;
	}

	public Collection<GOeBurstClusterWithStats> getClustering() {
		return clustering;
	}

	public int getLevel() {
		return level;
	}

	@Override
	public AbstractNode getNode() {
		return new GOeBurstNode(this);
	}

	@Override
	public String toString() {
		return "goeBURST (Level " + level + ")";
	}
}
