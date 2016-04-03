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
