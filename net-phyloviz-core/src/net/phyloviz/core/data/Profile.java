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

/**
 * This interface describes the minimal requirements for an isolate
 * profile created by a microbial typing method.  An isolate represents a sampled
 * microorganism of a bacterial population. The application of a typing
 * method to an isolate gives a characterization at the strain level of the isolate,
 * i.e., an isolate profile.
 * Thus, each profile consists of the list of features that characterizes an
 * isolate with respect to a specific typing method.
 * @since   PHILOViZ 1.0
 * @author PHYLOViZ Team &lt;phyloviz@gmail.com&gt;
 *
 */
public interface Profile extends DataItem {

	/**
	 * Returns an <tt>int</tt>  that represents the internal identifier of this typing
	 * profile.
	 *
	 * @return the internal value of the identifier of this typing profile.
	 *
	 */
	public int getUID();

	/**
	 * Returns a <tt>String</tt>  that represents the identifier of this typing
	 * profile.
	 *
	 * @return the identifier of this typing profile.
	 */
	@Override
	public String getID();

	/**
	 * Returns the number of features of this profile.
	 * If contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
	 * <tt>Integer.MAX_VALUE</tt>.
	 *
	 * @return the number of elements in this profile.
	 */
	public int profileLength();

	/**
	 * Returns  the value of a feature of this
	 * profile as a string that is at the specified index. An index ranges from
	 * <code>0</code> to <code>profileLenght() - 1</code>.
	 * The first description corresponds to the feature that
	 * is at index <code>0</code>, the next at index <code>1</code>,
	 * and so on, as for array indexing.
	 *
	 *
	 * @param      idx the index of the description of a feature.
	 * @return     the <code>String</code>  representation
	 *             of the feature at the specified index of the list of features .
	 *             The first <code>String</code> is at index <code>0</code>.
	 * @exception  IndexOutOfBoundsException  if the <code>idx</code>
	 *             argument is negative or not less than the <code>profileLenght()</code>
	 * this profile.
	 */
	public String getValue(int idx);

	/**
	 * Returns the frequency of this typing
	 * profile among all the isolates.
	 *
	 * @return the frequency of this typing profile.
	 *
	 */
	public int getFreq();

	/**
	 * This method updates the frequency of this typing
	 * profile.
	 *
	 * @param freq the new frequency of this typing profile.
	 * @void updates the frequency of this typing profile.
	 *
	 */
	public void setFreq(int freq);
}
