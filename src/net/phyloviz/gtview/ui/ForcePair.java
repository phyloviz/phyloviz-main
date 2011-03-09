package net.phyloviz.gtview.ui;

public class ForcePair {

	public float value;
	public String name;

	public ForcePair(String name, float i) {
		value = i;
		this.name = name;
	}

	@Override
	public String toString() {
		return name + ":" + value;
	}

	public static ForcePair valueOf(String s) {
		int k = s.indexOf(':');
		String i = s.substring(0, k);
		float j = Float.parseFloat(s.substring(k + 1));
		ForcePair p = new ForcePair(i, j);
		return p;
	}
}
