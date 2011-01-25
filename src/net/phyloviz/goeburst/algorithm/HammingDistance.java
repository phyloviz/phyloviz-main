package net.phyloviz.goeburst.algorithm;

import net.phyloviz.core.data.AbstractType;

public class HammingDistance {

	public static int compute(AbstractType x, AbstractType y) {
		int diffs = 0;

		for (int i = 0; i < x.profileLength(); i++)
			if (x.getValue(i).compareTo(y.getValue(i)) != 0)
				diffs ++;

		return diffs;
	}
}
