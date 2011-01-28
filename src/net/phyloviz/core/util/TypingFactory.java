package net.phyloviz.core.util;

import java.io.IOException;
import java.io.Reader;
import net.phyloviz.core.data.AbstractProfile;
import net.phyloviz.core.data.Population;
import net.phyloviz.core.data.TypingData;

public interface TypingFactory {

	public TypingData<? extends AbstractProfile> loadData(Reader r) throws IOException;

	public TypingData<? extends AbstractProfile> integrateData(TypingData<? extends AbstractProfile> td, Population pop, int key);
}
