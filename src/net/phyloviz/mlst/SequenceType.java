package net.phyloviz.mlst;

import net.phyloviz.core.data.AbstractProfile;

public class SequenceType extends AbstractProfile implements Comparable<SequenceType> {

	private String[] alleles;
	private int freq;

	public SequenceType(String[] profile) {
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

	public int getFreq() {
		return freq;
	}

	public void incFreq() {
		freq ++;
	}

	// Returns the number of locus variants.
	public int diff(SequenceType st) {
		int diffs = 0;

		for (int i = 0; i < alleles.length; i++)
			if (alleles[i].compareTo(st.alleles[i])!=0)
				diffs ++;

		return diffs;
	}

	@Override
	public int compareTo(SequenceType st) {
		int ret, i;

		for (ret = i = 0; ret == 0 && i < alleles.length; i++)
			ret = alleles[i].compareTo(st.alleles[i]);

		return ret;
	}

	public boolean equals(SequenceType st) {
		return this.compareTo(st) == 0;
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
