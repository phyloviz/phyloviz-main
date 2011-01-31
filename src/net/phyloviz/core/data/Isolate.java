/**
 * @(#)Isolate.java 27/01/11
 *
 * Copyright 2011 Phyloviz. All rights reserved.
 * Use is subject to license terms.
 */
package net.phyloviz.core.data;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The <code> Isolate </code> class represents the sampled microorganisms
 * of bacterial populations.  The name of isolate comes from the process of
 * isolating each sampled microorganism from the population. Each isolate has
 * ancillary data, such the epidemiological and demographic
 * information, important information for its characterization.
 *
 * @since   PHILOViZ 1.0
 * @author A. P. Francisco
 */
public class Isolate implements DataItem {

	/**
	 * The data structure into which the ancillary data of the isolate is stored.
	 * The capacity of the ArrayList depends on the initialization of the isolate.
	 */
	private ArrayList<String> values;

	/**
	 * Constructs an isolate with no ancillary data.
	 */
	protected Isolate() {
		values = new ArrayList<String>();
	}

	/**
	 * Constructs an isolate with a list that contains the
	 * ancillary data specified
	 * in the array, by the same order.
	 *
	 * @param val the ancillary data of the isolate.
	 * @throws NullPointerException if the specified array is null.
	 */
	protected Isolate(String[] val) {
		values = new ArrayList<String>(Arrays.asList(val));
	}

	/**
	 * Returns the data at the specified position in the ancillary data list of
	 * this isolate.
	 *
	 * @param  idx index of the data to return.
	 * @return the data at the specified position in the ancillary data list of
	 * this isolate.
	 * @throws IndexOutOfBoundsException - if the idx argument is negative
	 * or not less than the size of the ancillary data list.
	 */
	@Override
	public String get(int idx) {
		if (idx < 0 || idx >= length())
                	return null;

		return values.get(idx);
	}

	/**
	 * Replaces the data at the specified position in the ancillary data list of
	 * this isolate.
	 *
	 * @param idx index of the data to replace
	 * @param value data to be stored at the specified position.
	 * @throws IndexOutOfBoundsException  - if the idx argument is negative
	 * or not less than the size of the ancillary data list.
	 */
	protected void set(int idx, String value) {
		if (idx >= 0 && idx < values.size()) {
			values.set(idx, value);
		}
	}

	/**
	 * Appends the specified data to the end of the ancillary data list of
	 * this isolate.
	 * @param value data to be appended to the ancillary data list of
	 * this isolate.
	 *
	 */
	protected void add(String value) {
		values.add(value);
	}

	/**
	 * Returns a string representation of this isolate.  The string
	 * representation consists of the list of the ancillary data of this isolate
	 * in the added order, enclosed in square brackets
	 * (<tt>"[]"</tt>).  Adjacent elements are separated by the characters
	 * <tt>", "</tt> (comma and space).
	 *
	 * @return a string representation of this isolate
	 */
	@Override
	public String toString() {
		return Arrays.toString(values.toArray());
	}

	@Override
	public String getID() {
		// We do not know anything about it.
		return null;
	}

	@Override
	public int length() {
		return values.size();
	}

	@Override
	public int weight() {
		return 1;
	}
}
