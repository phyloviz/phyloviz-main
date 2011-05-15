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

package net.phyloviz.mlva;

import java.util.Arrays;
import net.phyloviz.core.data.AbstractProfile;

public class MLVAType extends AbstractProfile implements Comparable<MLVAType> {

	private String[] alleles;
	private int freq;

	public MLVAType(int uid, String[] profile) {
		this.uid = uid;
		this.id = profile[0];
		this.freq = 1;
		this.alleles = new String[profile.length - 1];
		System.arraycopy(profile, 1, alleles, 0, profile.length - 1);
	}

	@Override
	public String getValue(int idx) {
		return alleles[idx];
	}

	@Override
	public int profileLength() {
		return alleles.length;
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
	public int compareTo(MLVAType st) {
		int ret, i;

		for (ret = i = 0; ret == 0 && i < alleles.length; i++)
			ret = alleles[i].compareTo(st.alleles[i]);

		return ret;
	}

	@Override
	public boolean equals(Object st) {
		if (st instanceof MLVAType)
			return this.compareTo((MLVAType) st) == 0;

		return false;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 37 * hash + Arrays.deepHashCode(this.alleles);
		return hash;
	}

	@Override
	public String toString() {
		String s;
		int i;

		s = "ST: " + id + " Profile:";
		for (i = 0; i < alleles.length; i++)
			s += " " + alleles[i];

		return s;
	}
}
