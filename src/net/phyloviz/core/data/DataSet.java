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

public class DataSet implements Lookup.Provider, NodeFactory {

	private InstanceContent ic;
	private AbstractLookup lookup;

	private String name;
	private int popKey;

	public DataSet(String name) {
		ic = new InstanceContent();
		lookup = new AbstractLookup(ic);
		this.name = name;
		popKey = -1;
	}

	public int getPopKey() {
		return popKey;
	}

	public void setPopKey(int key) {
		Population pop = lookup.lookup(Population.class);
		if (pop != null)
			pop.setKey(key);
		popKey = key;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public Lookup getLookup() {
		return lookup;
	}

	public void add(Object o) {
		ic.add(o);
	}

	public void remove(Object o) {
		ic.remove(o);
	}

	@Override
	public AbstractNode getNode() {
		return new DataSetNode(this);
	}
}
