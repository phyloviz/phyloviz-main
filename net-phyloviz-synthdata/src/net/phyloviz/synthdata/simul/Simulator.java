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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Random;

public class Simulator {

	private int popSize;
	private int generations;
	private int lpGens;
	private double recRate;
	private double mutRate;
	private Random rand;
	private Archive archive;
	private Population population;

	public Simulator(int generations, int lpGens, int popSize,
			int sz, int mut, int rec, int randSeed) {
		this.popSize = popSize;
		this.generations = generations;
		this.lpGens = lpGens;
		this.recRate = rec / (2.0 * popSize);
		this.mutRate = mut / (2.0 * popSize);
		this.rand = new Random(randSeed);

		Profile.setProfileSize(sz);
		archive = new Archive(lpGens);

		population = new Population(popSize);
		int[] alleles = new int[Profile.PROFILE_SIZE];
		for (int i = 0; i < Profile.PROFILE_SIZE; i++) {
			alleles[i] = archive.getNewAllele(i);
		}

		int stID = archive.addProfile(new Profile(alleles));
		for (int i = 0; i < popSize; i++) {
			population.add(stID, 0);
		}
	}

	public boolean runOneStep() {
		if (generations <= 0) {
			return false;
		}

		// New population.
		Population newPopulation = new Population(popSize);
		for (int i = 0; i < popSize; i++) {
			int k = rand.nextInt(popSize);
			// Stores the position k of the father ;)
			newPopulation.add(population.getSTid(k), k);
		}

		for (int i = 0; i < popSize; i++) {
			Profile p = archive.getCopyOfSTid(newPopulation.getSTid(i));
			for (int k = 0; k < Profile.PROFILE_SIZE; k++) {
				// Recombination.
				if (rand.nextFloat() < recRate) {
					int donorST = population.getSTid(rand.nextInt(popSize));
					p.setAlleleAtPos(k, archive.getProfile(donorST).getAlleleIDatPos(k));
				}
				// Mutation.
				if (rand.nextFloat() < mutRate) {
					p.setAlleleAtPos(k, archive.getNewAllele(k));
				}
			}

			int stID = archive.addProfile(p);
			newPopulation.setSTid(i, stID);
		}

		archive.addPopulation(newPopulation);
		population = newPopulation;
		return true;
	}

	public String writeLastPopGenerations() {
		String s = "ST";
		for (int i = 1; i <= Profile.PROFILE_SIZE; i++) {
			s += "\tlocus" + i;
		}
		for (int i = generations - lpGens; i < generations; i++) {
			s += dumpGeneration(i);
		}
		return s;
	}

	private String dumpGeneration(int g) {
		String s = "";
		Population pop = archive.getGeneration(g);
		for (int i = 0; i < popSize; i++) {
			int stID = pop.getSTid(i);
			s += "\n" + stID + archive.getProfile(stID);
		}
		return s;
	}
}
