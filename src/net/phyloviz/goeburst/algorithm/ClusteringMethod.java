package net.phyloviz.goeburst.algorithm;

import java.util.Collection;

import net.phyloviz.core.data.AbstractProfile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.goeburst.cluster.Cluster;

public interface ClusteringMethod<E extends Cluster> {
	public abstract Collection<E> getClustering(TypingData<? extends AbstractProfile> td);
}
