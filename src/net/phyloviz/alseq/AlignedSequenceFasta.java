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

import net.phyloviz.core.data.AbstractProfile;

public class AlignedSequenceFasta extends AbstractProfile implements Comparable<AlignedSequenceFasta> {

	private String header;
	private String sequence;
	private int freq;

	public AlignedSequenceFasta(int uid, String[] profile) {
		this.uid = uid;
		this.id = profile[0];
		this.freq = 1;
		this.sequence = profile[1];
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getHeader() {
		return header;
	}
	
	@Override
	public String getValue(int idx) {
		return String.valueOf(sequence.charAt(idx));
	}

	@Override
	public int profileLength() {
		return sequence.length();
	}

	@Override
	public int getFreq() {
		return freq;
	}

	@Override
	public void setFreq(int freq) {
		this.freq = freq;
	}

	public void incFreq() {
		freq ++;
	}

	@Override
	public int compareTo(AlignedSequenceFasta st) {
		return sequence.compareTo(st.sequence);
	}

	@Override
	public boolean equals(Object st) {
		if (st instanceof AlignedSequenceFasta)
			return this.compareTo((AlignedSequenceFasta) st) == 0;

		return false;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 29 * hash + sequence.hashCode();
		return hash;
	}

	@Override
	public String toString() {
		return "Seq: " + id + " Header: " + header + "\nSequence: " + sequence;
	}
}
