package net.phyloviz.nj.algorithm.saitou_nei;

import net.phyloviz.nj.tree.NJLeafNode;
import net.phyloviz.algo.AbstractDistance;
import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.nj.AgglomerativeClusteringMethod;
import net.phyloviz.nj.NJClusteringMethod;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author aplf
 */
@ServiceProvider(service = NJClusteringMethod.class)
public class NJDistanceProviderSaitouNei implements NJClusteringMethod<NJLeafNode> {

    @Override
    public String toString() {
            return "Neighbor-Joining distance Saitou N. and Nei M. Criterion";
    }

    @Override
    public AgglomerativeClusteringMethod<NJLeafNode> getMethod() {
        return new NJDistanceSaitouNei();
    }
}
