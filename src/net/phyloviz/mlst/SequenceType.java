package net.phyloviz.mlst;

import net.phyloviz.core.data.AbstractProfile;

public class SequenceType extends AbstractProfile implements Comparable<SequenceType> {

	private String[] alleles;
	private int freq;

	public SequenceType(int uid, String[] profile) {
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
