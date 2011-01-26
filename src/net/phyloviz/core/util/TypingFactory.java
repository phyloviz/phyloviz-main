package net.phyloviz.core.util;

import java.io.IOException;
import java.io.Reader;
import net.phyloviz.core.data.AbstractProfile;
import net.phyloviz.core.data.TypingData;

public interface TypingFactory {

	public TypingData<? extends AbstractProfile> loadData(Reader r) throws IOException;
}
