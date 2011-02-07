package net.phyloviz.goeburst.algorithm;

import net.phyloviz.goeburst.AbstractDistance;
import java.util.Collection;

import net.phyloviz.core.data.AbstractProfile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.goeburst.cluster.Cluster;

public interface ClusteringMethod<E extends Cluster> {
	public abstract Collection<E> getClustering(TypingData<? extends AbstractProfile> td, AbstractDistance ad);
}
