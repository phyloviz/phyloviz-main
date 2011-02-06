package net.phyloviz.goeburst;

import java.util.Collection;
import net.phyloviz.core.util.NodeFactory;
import net.phyloviz.goeburst.algorithm.AbstractDistance;
import net.phyloviz.goeburst.cluster.GOeBurstClusterWithStats;
import net.phyloviz.goeburst.ui.GOeBurstNode;
import net.phyloviz.goeburst.ui.OutputPanel;
import org.openide.nodes.AbstractNode;

public class GOeBurstResult implements NodeFactory {

	private Collection<GOeBurstClusterWithStats> clustering;
	private OutputPanel op;
	private AbstractDistance ad;
	private int level;

	public GOeBurstResult(Collection<GOeBurstClusterWithStats> clustering, AbstractDistance ad, int level, OutputPanel op) {
		this.clustering = clustering;
		this.op = op;
		this.level = level;
		this.ad = ad;
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

	public AbstractDistance getDistance() {
		return ad;
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
