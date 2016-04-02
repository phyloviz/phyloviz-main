/*-
 * Copyright (c) 2011, PHYLOViZ Team <phyloviz@gmail.com>
 * All rights reserved.
 * 
 * This file is part of PHYLOViZ <http://www.phyloviz.net>.
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
 * 
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole combination.
 * 
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent modules,
 * and to copy and distribute the resulting executable under terms of your
 * choice, provided that you also meet, for each linked independent module,
 * the terms and conditions of the license of that module.  An independent
 * module is a module which is not derived from or based on this library.
 * If you modify this library, you may extend this exception to your version
 * of the library, but you are not obligated to do so.  If you do not wish
 * to do so, delete this exception statement from your version.
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
 * @author PHYLOViZ Team &lt;phyloviz@gmail.com&gt;
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
	public Isolate() {
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
	public Isolate(String[] val) {
		values = new ArrayList<String>(Arrays.asList(val));
	}

	/**
	 * Returns the data at the specified position in the ancillary data list of
	 * this isolate.
	 *
	 * @param  idx index of the data to return.
	 * @return the data at the specified position in the ancillary data list of
	 * this isolate.
	 * @throws IndexOutOfBoundsException  if the argument <code>idx</code> is negative
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
	 * @throws IndexOutOfBoundsException   if the argument <code>idx</code> is negative
	 * or not less than the size of the ancillary data list.
	 */
	public void set(int idx, String value) {
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

        /**
	 * Returns the number of features (including the ID) of this isolate.
	 * If contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
	 * <tt>Integer.MAX_VALUE</tt>.
	 *
	 * @return the number of elements (including the ID) for this isolate.
	 */
	@Override
	public int length() {
		return values.size();
	}
	
        /**
	 * Returns the frequency of this isolate, which is 1.
	 *
	 * @return the frequency the isolate,  which is 1.
	 *
	 */
	@Override
	public int weight() {
		return 1;
	}
}
