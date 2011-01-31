package net.phyloviz.goeburst.algorithm;

import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;

public interface AbstractDistance {

	public int compute(Profile x, Profile y);

	public int maximum(TypingData td);
}
