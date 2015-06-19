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

import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ServiceProvider;

/**
 * The instances of this class tracks all DataSet instances.
 *
 * @since   PHILOViZ 1.0
 * @author PHYLOViZ Team &lt;phyloviz@gmail.com&gt;
 */
@ServiceProvider(service = DataSetTracker.class)
public class DataSetTracker implements Lookup.Provider {

	private InstanceContent ic;
	private AbstractLookup lookup;

	public DataSetTracker() {
		ic = new InstanceContent();
		lookup = new AbstractLookup(ic);
	}

	/** Returns a lookup associated with the object.
	 * @return  fully initialized lookup instance provided by this object.
	 * @override  <code>getLookUp</code> in class <code>AbstractLookUp</code>.
	 */
	@Override
	public Lookup getLookup() {
		return lookup;
	}

	/**
	 *  Registers the object within this lookup.
	 * @param o the object to register within this lookup.
	 */
	public void add(DataSet ds) {
    ic.add(ds);
	}

	/**
	 * Unregisters the object within this lookup.
	 * @param o the object to be unregistered.
	 */
	public void remove(DataSet ds) {
		ic.remove(ds);
	}
}
