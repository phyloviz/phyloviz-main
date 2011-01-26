package net.phyloviz.goeburst.algorithm;

import net.phyloviz.core.data.AbstractProfile;

public class HammingDistance {

	public static int compute(AbstractProfile x, AbstractProfile y) {
		int diffs = 0;

		for (int i = 0; i < x.profileLength(); i++)
			if (x.getValue(i).compareTo(y.getValue(i)) != 0)
				diffs ++;

		return diffs;
	}
}
