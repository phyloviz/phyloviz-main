package net.phyloviz.nj.algorithm.saitou_nei;

import net.phyloviz.algo.AbstractClusteringMethod;
import net.phyloviz.algo.ClusteringMethodProvider;
import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.nj.tree.NJLeafNode;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author aplf
 */
@ServiceProvider(service = ClusteringMethodProvider.class)
public class NJMethodProviderSaitouNei implements ClusteringMethodProvider<NJLeafNode> {

    @Override
    public String toString() {
            return "Neighbor-Joining distance Saitou N. and Nei M. Criterion";
    }

    @Override
    public AbstractClusteringMethod<NJLeafNode> getMethod(TypingData<? extends Profile> td) {
        return new NJMethodSaitouNei();    
    }
}
