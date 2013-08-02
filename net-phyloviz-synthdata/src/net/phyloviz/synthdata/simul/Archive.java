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
