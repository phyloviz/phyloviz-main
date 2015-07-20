package net.phyloviz.nj.algorithm.studier_keppler;

import net.phyloviz.upgmanjcore.AbstractClusteringMethod;
import net.phyloviz.nj.tree.NJLeafNode;
import net.phyloviz.upgmanjcore.ClusteringMethodProvider;
import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author aplf
 */
@ServiceProvider(service = ClusteringMethodProvider.class)
public class NJMethodProviderStudierKeppler implements ClusteringMethodProvider<NJLeafNode> {

    @Override
    public String toString() {
        return "Neighbor-Joining distance Studier and Keppler Criterion";
    }

    @Override
    public AbstractClusteringMethod<NJLeafNode> getMethod(TypingData<? extends Profile> td) {
        return new NJMethodStudierKeppler();
    }
}
