package net.phyloviz.mlst;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.phyloviz.core.data.AbstractProfile;
import net.phyloviz.core.data.Isolate;
import net.phyloviz.core.data.Population;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.core.util.TypingFactory;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = TypingFactory.class)
public class MLSTypingFactory implements TypingFactory {
	
	private static final String customName = "Multi-Locus Sequence Typing";

	@Override
	public String toString() {
		return customName;
	}

	@Override
	public TypingData<? extends AbstractProfile> loadData(Reader r)  throws IOException {

		BufferedReader in = new BufferedReader(r);
		int uid = 0;

		TypingData<SequenceType> td = null;

		while (in.ready()) {
			String[] lineFields = in.readLine().split("[ ,\t]+", 0);

			// Get headers and initialize this instance.
			if (td == null) {
				td = new TypingData<SequenceType>(lineFields);
				continue;
			}

			String[] STvec = new String[lineFields.length];
			System.arraycopy(lineFields, 0, STvec, 0, lineFields.length);

			SequenceType profile = new SequenceType(uid++, STvec);

			if (! td.addData(profile)) {
				SequenceType oldProfile = td.getEqual(profile);
				if (oldProfile != null && !profile.getID().equals(oldProfile.getID())) {

					oldProfile.incFreq();

					Logger.getLogger(MLSTypingFactory.class.getName()).log(Level.WARNING,
						"{0} aka {1}\n", new Object[]{profile.getID(), oldProfile.getID()});
				}
			}
		}
		in.close();

		return td;
	}

	@Override
	public TypingData<? extends AbstractProfile> integrateData(TypingData<? extends AbstractProfile> td, Population pop, int key) {

		TreeMap<String, Integer> st2freq = new TreeMap<String, Integer>();

		Iterator<Isolate> ii = pop.iterator();
		while (ii.hasNext()) {
			String sid = ii.next().get(key);
			Integer freq = st2freq.get(sid);
			st2freq.put(sid, (freq == null) ? 1 : (freq.intValue() + 1));
		}

		Iterator<? extends AbstractProfile> ip = td.iterator();
		while(ip.hasNext()) {
			AbstractProfile ap = ip.next();
			Integer freq = st2freq.get(ap.getID());
			ap.setFreq((freq == null) ? 0 : freq.intValue());
		}

		return td;
	}
}
