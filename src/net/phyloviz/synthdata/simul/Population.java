package net.phyloviz.synthdata.simul;

import java.util.ArrayList;
import java.util.TreeSet;

public class Population {
	private int[] indivIDs;
	private int[] indivPpos;
	private int[] indivSTids;
	private int[] indivSTpids;
	private int idx;
	private static int maxIndivID = 0;

	public Population() {
	}
	
	public Population(int sz) {
		super();
		idx = 0;
		indivIDs = new int[sz];
		indivPpos = new int[sz];
		indivSTids = new int[sz];
		indivSTpids = new int[sz];
	}

	public boolean add(int newSTid, int pos) {
		if (idx >= indivIDs.length)
			return false;

		maxIndivID++;
		indivIDs[idx] = maxIndivID;
		indivPpos[idx] = pos;
		indivSTids[idx] = newSTid; // This will change eventually
		indivSTpids[idx] = newSTid; // This will stay & tell the story
		idx++;

		return true;
	}

	public int size() {
		if (indivSTids != null)
			return indivSTids.length;
		return 0;
	}

	public int getId(int i) {
		return indivIDs[i];
	}

	public int getPpos(int i) {
		return indivPpos[i];
	}

	public int getSTid(int i) {
		return indivSTids[i];
	}

	public int getSTpid(int i) {
		return indivSTpids[i];
	}

	public void setId(int i, int id) {
		this.indivIDs[i] = id;
	}

	public void setPpos(int i, int pid) {
		this.indivPpos[i] = pid;
	}

	public void setSTid(int i, int stid) {
		this.indivSTids[i] = stid;
	}

	public void setSTpid(int i, int stpid) {
		this.indivSTpids[i] = stpid;
	}

	public TreeSet<Integer> getUniqueSTs() {
		TreeSet<Integer> tsUniqueSTs = new TreeSet<Integer>();
		for (int i = 0; i < indivSTids.length; i++) {
			tsUniqueSTs.add(indivSTids[i]);
		}
		return tsUniqueSTs;
	}

	public ArrayList<Integer> getSTs() {
		ArrayList<Integer> alSTs = new ArrayList<Integer>();
		for (int i = 0; i < indivSTids.length; i++) {
			alSTs.add(indivSTids[i]);
		}
		return alSTs;
	}

	public TreeSet<Integer> getParentPos() {
		TreeSet<Integer> tsPpos = new TreeSet<Integer>();
		for (int i = 0; i < indivPpos.length; i++) {
			tsPpos.add(indivPpos[i]);
		}
		return tsPpos;
	}
}
