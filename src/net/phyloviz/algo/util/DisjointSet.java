package net.phyloviz.algo.util;

public class DisjointSet {
	private int size;
	private int[] pi;
	private int[] rank;

	public DisjointSet(int n) {
		size = n + 1;
		pi = new int[size];
		rank = new int[size];

		for (int i = 0; i < size; i++) {
			rank[i] = 0;
			pi[i] = i;
		}
	}

	public int findSet(int i) {
		if (i < 0 || i >= size)
			return -1;
		for (; i != pi[i]; i = pi[i])
			pi[i] = pi[pi[i]];
		return i;
	}
		
	public boolean sameSet(int i, int j) {
		return findSet(i) == findSet(j);
	}

	public void unionSet(int i, int j) {
		if (i < 0 || j < 0 || i >= size || j >= size)
			return;
		int iRoot = findSet(i);
		int jRoot = findSet(j);
		if (iRoot == jRoot)
			return;
		if (rank[iRoot] > rank[jRoot])
			pi[jRoot] = iRoot;
		else if (rank[iRoot] < rank[jRoot])
			pi[iRoot] = jRoot;
		else {
			pi[iRoot] = jRoot;
			rank[jRoot] ++;
		}
	}
}
