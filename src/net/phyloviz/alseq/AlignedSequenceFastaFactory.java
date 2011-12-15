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

package net.phyloviz.alseq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.phyloviz.core.data.AbstractProfile;
import net.phyloviz.core.data.Isolate;
import net.phyloviz.core.data.Population;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.core.util.TypingFactory;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = TypingFactory.class)
public class AlignedSequenceFastaFactory implements TypingFactory {
	
	private static final String customName = "Aligned Sequences (FASTA)";

	@Override
	public String toString() {
		return customName;
	}

	@Override
	public TypingData<? extends AbstractProfile> loadData(Reader r)  throws IOException {

		BufferedReader in = new BufferedReader(r);
		int uid = 0;
		boolean error = false;

		TypingData<AlignedSequenceFasta> td = null;

		String s = in.readLine();
		while (s != null && s.charAt(0) == '>') {

			String[] STvec = new String[2];
			String header = s;
			Pattern pat = Pattern.compile("id[|=].+?[|;]");
			Matcher m = pat.matcher(s);
			Pattern pat2 = Pattern.compile("> ?.{1,15}");
			Matcher m2 = pat2.matcher(s);
			if (m.find()) {
				STvec[0] = s.substring(m.start()+3, m.end()-1);
			} else if (m2.matches()) {
				if (s.charAt(1) == ' ')
					STvec[0] = s.substring(2);
				else
					STvec[0] = s.substring(1);
			} else {
				STvec[0] = "#" + String.valueOf(uid + 1);
			}	
			STvec[1] = "";
			s = in.readLine();
			StringBuilder sb = new StringBuilder();
			while (s != null && (s.equals("") || s.charAt(0) != '>')) {
				sb.append(s);
				s = in.readLine();	
			}
			STvec[1] = sb.toString();
		
			if (STvec[1] == null || STvec[1].equals(""))
				continue;

			// Get headers and initialize this instance.
			if (td == null) {
				int len = STvec[1].length();
				String headers[] = new String[len + 1];
				headers[0] = "ID";
				for (int i = 0; i < len; i++)
					headers[i + 1] = "s[" + i + "]";
				td = new TypingData<AlignedSequenceFasta>(headers);
				//continue; // We may need to comment this given the FASTA format...
			}
			
			AlignedSequenceFasta profile = new AlignedSequenceFasta(uid++, STvec);
			profile.setHeader(header);

			AlignedSequenceFasta oldProfile = td.getEqual(profile);
			if (oldProfile != null) {
				oldProfile.incFreq();
				if (!profile.getID().equals(oldProfile.getID())) {
					Logger.getLogger(AlignedSequenceFastaFactory.class.getName()).log(Level.WARNING,
						"Duplicated profile: {0} aka {1} (frequency updated)", 
						new Object[]{profile.getID(), oldProfile.getID()});
					error = true;
				}
			} else {
				try {
					td.addData(profile);
				} catch(Exception e) {
					Logger.getLogger(AlignedSequenceFastaFactory.class.getName()).log(Level.WARNING,
						e.getLocalizedMessage());
					error = true;
				}
			}
		} 
		in.close();

		if (error) {
			String failMsg = "Some profiles may have been discarded! Check the log (View->Log).";
			DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(failMsg));
		}
		
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
		return AlignedSequenceFastaFactory.class.getResource("FastaDescription.html");
	}

	@Override
	public URL getFormatDescription() {
		return AlignedSequenceFastaFactory.class.getResource("FastaFormatDescription.html");
	}
}
