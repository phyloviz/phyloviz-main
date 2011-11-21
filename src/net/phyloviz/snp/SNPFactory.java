/*-
 * Copyright (c) 2011, PHYLOViZ Team <phyloviz@gmail.com>
 * All rights reserved.
 * 
 * This file is part of PHYLOViZ <http://www.phyloviz.net>.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole combination.
 * 
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent modules,
 * and to copy and distribute the resulting executable under terms of your
 * choice, provided that you also meet, for each linked independent module,
 * the terms and conditions of the license of that module.  An independent
 * module is a module which is not derived from or based on this library.
 * If you modify this library, you may extend this exception to your version
 * of the library, but you are not obligated to do so.  If you do not wish
 * to do so, delete this exception statement from your version.
 */

package net.phyloviz.snp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
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
public class SNPFactory implements TypingFactory {
	
	private static final String customName = "Single-Nucleotide Polymorphism (SNP)";

	@Override
	public String toString() {
		return customName;
	}

	@Override
	public TypingData<? extends AbstractProfile> loadData(Reader r)  throws IOException {

		BufferedReader in = new BufferedReader(r);
		int uid = 0;

		TypingData<SNP> td = null;

		while (in.ready()) {
			String s = in.readLine();
			if (s == null)
				break;

			String[] lineFields = s.split("[ ,\t]+", 0);

			// Get headers and initialize this instance.
			if (td == null) {
				td = new TypingData<SNP>(lineFields);
				continue;
			}

			String[] STvec = new String[lineFields.length];
			System.arraycopy(lineFields, 0, STvec, 0, lineFields.length);

			SNP profile = new SNP(uid++, STvec);

			SNP oldProfile = td.getEqual(profile);
			if (oldProfile != null) {
				oldProfile.incFreq();
				if (!profile.getID().equals(oldProfile.getID())) {
					Logger.getLogger(SNPFactory.class.getName()).log(Level.WARNING,
						"Duplicated profile: {0} aka {1} (frequency updated)", new Object[]{profile.getID(), oldProfile.getID()});
				}
			} else {
				try {
					td.addData(profile);
				} catch(Exception e) {
					Logger.getLogger(SNPFactory.class.getName()).log(Level.WARNING,
						e.getLocalizedMessage());
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

	@Override
	public URL getDescription() {
		return SNPFactory.class.getResource("Description.html");
	}

	@Override
	public URL getFormatDescription() {
		return SNPFactory.class.getResource("FormatDescription.html");
	}
}
