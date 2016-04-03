/*-
 * Copyright (c) 2016, PHYLOViZ Team <phyloviz@gmail.com>
 * All rights reserved.
 * 
 * This file is part of PHYLOViZ <http://www.phyloviz.net/>.
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
 */

package net.phyloviz.synthdata.simul;

public class Profile implements Cloneable, Comparable<Profile> {

	public static int PROFILE_SIZE = 0;
	protected int[] iaAllelesIDs;

	public Profile() {
		iaAllelesIDs = new int[PROFILE_SIZE];
	}

	public Profile(int[] alleles) {
		iaAllelesIDs = alleles;
	}

	@Override
	public Profile clone() {
		Profile st = new Profile();
		System.arraycopy(this.iaAllelesIDs, 0, st.iaAllelesIDs, 0,
				st.iaAllelesIDs.length);
		return st;
	}

	public final int getAlleleIDatPos(int i) {
		return iaAllelesIDs[i];
	}

	public final void setAlleleAtPos(int i, int allele) {
		iaAllelesIDs[i] = allele;
	}

	public boolean isSLV(Profile pf) {
		return (getNdiff(pf) == 1);
	}

	public final int getNdiff(Profile st) {
		int diffs, i;

		for (diffs = i = 0; i < iaAllelesIDs.length; i++) {
			if (iaAllelesIDs[i] != st.iaAllelesIDs[i]) {
				diffs++;
			}
		}
		return diffs;
	}

	protected static void setProfileSize(int sz) {
		if (PROFILE_SIZE == 0 && sz > 0) {
			PROFILE_SIZE = sz;
		}
	}

	@Override
	public final int compareTo(Profile st) {
		int ret, i;

		for (ret = i = 0; ret == 0 && i < iaAllelesIDs.length; i++) {
			ret = iaAllelesIDs[i] - st.iaAllelesIDs[i];
		}

		return ret;
	}

	@Override
	public String toString() {
		String s = "";
		for (int i = 0; i < Profile.PROFILE_SIZE; i++) {
			s += "\t" + iaAllelesIDs[i];
		}
		return s;
	}
}
