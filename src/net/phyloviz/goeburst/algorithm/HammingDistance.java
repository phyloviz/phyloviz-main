package net.phyloviz.goeburst.algorithm;

import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
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
		return "eBURST Distance";
	}

	@Override
	public int maximum(TypingData td) {
		// -1 because of the id...
		return td.getHeaders().size() - 1;
	}
}
