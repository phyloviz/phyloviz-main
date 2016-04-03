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

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

public class Archive {

	protected TreeMap<Integer, Profile> stID2Profile;
	protected TreeMap<Profile, Integer> profile2stID;

	private int maxSTid;
	private int[] maxAlleleID;
	private ArrayList<Population> popg;
	private int lpGens;

	public Archive(int lpGens) {
		stID2Profile = new TreeMap<Integer, Profile>();
		profile2stID = new TreeMap<Profile, Integer>();

		this.lpGens = lpGens;
		maxAlleleID = new int[Profile.PROFILE_SIZE];
		for (int i = 0; i < Profile.PROFILE_SIZE; i++) {
			maxAlleleID[i] = 0;
		}

		maxSTid = 0;
		popg = new ArrayList<Population>();
	}

	public Profile getProfile(int id) {
		return (Profile) stID2Profile.get(id);
	}

	public Profile getCopyOfSTid(int stID) {
		return ((Profile) stID2Profile.get(stID)).clone();
	}

	public int getNewAllele(int k) {
		maxAlleleID[k]++;
		return maxAlleleID[k];
	}

	public final int getSTid(Profile p) {
		Integer id = profile2stID.get(p);
		return (id == null) ? 0 : id.intValue();
	}

	public int addProfile(Profile st) {
		int stID = getSTid(st);
		if (stID != 0)
			return stID;

		// Add a new profile.
		maxSTid++;
		stID = maxSTid;
		stID2Profile.put(stID, st);
		profile2stID.put(st, stID);

		return stID;
	}

	public Population getGeneration(int i) {
		return popg.get(i);
	}

	public void addPopulation(Population pop) {
		popg.add(pop);
		// Memory optimization: discard old (> lpGens) populations
		if (lpGens > 0) {
			int size = popg.size() - 1;
			if (size > lpGens)
				popg.set(size - lpGens, new Population());
		}
	}

	public Population getPopulation(int i) {
		return popg.get(i);
	}

	public Population getLastPopulation() {
		return popg.get(popg.size() - 1);
	}
}
