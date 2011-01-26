package net.phyloviz.core.data;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * The Isolate class represents the sampled microorganisms of a bacterial
 * population. Values...anciliary data...
 * @since   PHILOViZ 1.0
 * @author A. P. Francisco
 */

public class Isolate {
	
	private ArrayList<String> values;

	protected Isolate() {
		values = new ArrayList<String>();
	}

       /*
        * @param
        * @throws NullPointerException if the specified array is null
        */
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

        /**
         *
         * @param value
         */
        protected void add(String value){
		values.add(value);
        }

	@Override
	public String toString() {
		return Arrays.toString(values.toArray());
	}
}
