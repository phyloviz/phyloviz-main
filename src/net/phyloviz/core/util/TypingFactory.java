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

package net.phyloviz.core.util;

import java.io.IOException;
import java.io.Reader;
import net.phyloviz.core.data.AbstractProfile;
import net.phyloviz.core.data.Population;
import net.phyloviz.core.data.TypingData;

/**
 *  This interface is defined for implementing the concept of a factory for
 *  typing data of profiles (factory pattern). The classes that implement this interface
 *  will be able to create typing data for a list of profiles.
 *
 * @author PHYLOViZ Team &lt;phyloviz@gmail.com&gt;
 */
public interface TypingFactory {

	/**
	 * Returns a new typing data, obtained from reading files
	 * with the description of the profiles.
	 * @param r The reader of descriptions of the profiles.
	 * @return a new typing data, obtained from <code>r</code>.
	 * @throws IOException if an I/O error occurs.
	 */
	public TypingData<? extends AbstractProfile> loadData(Reader r) throws IOException;

	/**
	 * This method updates a typing data instance with the data given by a population.
	 * Each isolate from the considered population may be related to a
	 * profile of the considered typing data, if they match a specific identificator.
	 * Thus, the typing data is updated according to the isolates of the population.
	 * @param td the typing data to be updated.
	 * @param pop the population to be considered for update.
	 * @param key an index of a column in the table view of the population,
	 * where are the matching identificators.
	 * @return an updated version of the typing data.
	 * @throws IndexOutOfBoundsException if the index <code>key</code> is
	 * not valid in the table view of the population.
	 */
	public TypingData<? extends AbstractProfile> integrateData(TypingData<? extends AbstractProfile> td, Population pop, int key);
}
