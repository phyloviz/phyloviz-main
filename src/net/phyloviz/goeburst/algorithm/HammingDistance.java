package net.phyloviz.goeburst.algorithm;

import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.goeburst.cluster.GOeBurstCluster;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = AbstractDistance.class)
public class HammingDistance implements AbstractDistance {

	@Override
	public int compute(Profile x, Profile y) {
		int diffs = 0;

		for (int i = 0; i < x.profileLength(); i++)
			if (x.getValue(i).compareTo(y.getValue(i)) != 0)
				diffs ++;

		return diffs;
	}

	@Override
	public String toString() {
		return "Hamming Distance";
	}

	@Override
	public int maximum(TypingData td) {
		// -2 because of the id and to avoid complete graphs...
		int plen = td.getHeaders().size() - 2;
		return Math.min(plen, GOeBurstCluster.MAXLV);
	}
}
