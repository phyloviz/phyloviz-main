package net.phyloviz.nj.algorithm.saitou_nei;

import net.phyloviz.nj.tree.NJLeafNode;
import net.phyloviz.algo.AbstractDistance;
import net.phyloviz.algo.DistanceProvider;
import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author aplf
 */
@ServiceProvider(service = DistanceProvider.class)
public class NJDistanceProviderSaitouNei implements DistanceProvider<NJLeafNode> {

	@Override
	public AbstractDistance<NJLeafNode> getDistance(TypingData<? extends Profile> td) {
		return new NJDistanceSaitouNei();
	}

	@Override
	public String toString() {
		return "Neighbor-Joining distance Saitou N. and Nei M. Criterion";
	}
 
	@Override
	public AbstractDistance<NJLeafNode> getDistance(TypingData<? extends Profile> td, int maxLevel) {
		return new NJDistanceSaitouNei();
	}
}
