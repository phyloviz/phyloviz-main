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

import net.phyloviz.core.explorer.DataSetNode;
import net.phyloviz.core.util.NodeFactory;
import org.openide.nodes.AbstractNode;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *  The DataSet class represents a set that contains population data and related
 *  typing data. An instance of this class can be explored in the context of
 * an explorer viewer.
 *
 * @author PHYLOViZ Team &lt;phyloviz@gmail.com&gt;
 */
public class DataSet implements Lookup.Provider, NodeFactory {

	private InstanceContent ic;
	private AbstractLookup lookup;
	private String name;
	private int popKey;

	/**
	 * Constructs a new data designed by <code>name</code>
	 * @param name the name of the data set.
	 */
	public DataSet(String name) {
		ic = new InstanceContent();
		lookup = new AbstractLookup(ic);
		this.name = name;
		popKey = -1;
	}

	/**
	 * Returns the key column of the tabular model for the population of this data set.
	 *
	 * @return the index of the key column of the tabular model for the population of this data set.
	 */
	public int getPopKey() {
		return popKey;
	}

	/**
	 * Changes the key column of the tabular model for the population of this data set.
	 * @param key   the new key column for the tabular model of the population of this data set.
	 */
	public void setPopKey(int key) {
		Population pop = lookup.lookup(Population.class);
		if (pop != null) {
			pop.setKey(key);
		}
		popKey = key;
	}

	/**
	 * Returns a string representation of this data set.
	 * @overrides <code>toString</code> in class <code>Object</code>
	 * @return a string representation of this data set.
	 */
	@Override
	public String toString() {
		return name;
	}

	/** Returns lookup associated with the object.
	 * @return  fully initialized lookup instance provided by this object.
	 * @override  <code>getLookUp</code> in class <code>AbstractLookUp</code>.
	 */
	@Override
	public Lookup getLookup() {
		return lookup;
	}

	/**
	 *  Registers the object with this lookup.
	 * @param o the object to register with this lookup.
	 */
	public void add(Object o) {
		ic.add(o);
	}

	/**
	 * Unregisters the object with this lookup.
	 * @param o the object to be unregistered.
	 */
	public void remove(Object o) {
		ic.remove(o);
	}

	/**
	 * Allows to explore the population in the context of an explorer viewer.
	 * @return  returns the population as a node in the explorer view.
	 * @see AbstractNode
	 */
	@Override
	public AbstractNode getNode() {
		return new DataSetNode(this);
	}
}
