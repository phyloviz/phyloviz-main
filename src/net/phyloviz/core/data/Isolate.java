package net.phyloviz.core.data;

import java.util.ArrayList;
import java.util.Arrays;

public class Isolate {
	
	private ArrayList<String> values;

	protected Isolate() {
		values = new ArrayList<String>();
	}

	protected Isolate(String[] val) {
		values = new ArrayList<String>(Arrays.asList(val));
	}
	
	public String get(int idx) {
		return values.get(idx);
	}

	protected void set(int idx, String value) {
		if (idx >= 0 && idx < values.size())
			values.set(idx, value);
	}

        protected void add(String value){
		values.add(value);
        }

	@Override
	public String toString() {
		return Arrays.toString(values.toArray());
	}
}
