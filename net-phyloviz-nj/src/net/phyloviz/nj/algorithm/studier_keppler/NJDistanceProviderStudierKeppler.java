package net.phyloviz.nj.algorithm.studier_keppler;

import net.phyloviz.algo.AbstractClusteringMethod;
import net.phyloviz.nj.tree.NJLeafNode;
import net.phyloviz.algo.AbstractDistance;
import net.phyloviz.algo.ClusteringMethodProvider;
import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.nj.AgglomerativeClusteringMethod;
import net.phyloviz.nj.NJClusteringMethod;
import net.phyloviz.nj.algorithm.NJ;
import net.phyloviz.nj.ui.OutputPanel;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author aplf
 */
@ServiceProvider(service = NJClusteringMethod.class)
public class NJDistanceProviderStudierKeppler implements NJClusteringMethod<NJLeafNode> {

    @Override
    public String toString() {
        return "Neighbor-Joining distance Studier and Keppler Criterion";
    }

    @Override
    public AgglomerativeClusteringMethod<NJLeafNode> getMethod() {
        return new NJDistanceStudierKeppler();
    }
}
