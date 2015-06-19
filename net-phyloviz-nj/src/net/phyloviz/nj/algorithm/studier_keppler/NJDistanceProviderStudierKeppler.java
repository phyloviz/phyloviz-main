package net.phyloviz.nj.algorithm.studier_keppler;

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
public class NJDistanceProviderStudierKeppler implements DistanceProvider<NJLeafNode> {

	@Override
	public AbstractDistance<NJLeafNode> getDistance(TypingData<? extends Profile> td) {
		return new NJDistanceStudierKeppler();
	}

	@Override
	public String toString() {
		return "Neighbor-Joining distance Studier and Keppler Criterion";
	}
 
	@Override
	public AbstractDistance<NJLeafNode> getDistance(TypingData<? extends Profile> td, int maxLevel) {
		return new NJDistanceStudierKeppler();
	}
}
