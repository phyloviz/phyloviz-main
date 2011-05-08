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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.table.AbstractTableModel;
import net.phyloviz.core.explorer.PopulationNode;
import net.phyloviz.core.util.NodeFactory;
import org.openide.nodes.AbstractNode;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 * The <code> Population </code> class represents the set of the
 * isolates of a bacterial population.
 * The isolates of a population can be displayed as a two dimensional table, where each
 * row corresponds to a different isolate and each column to a specific feature of the isolate.
 * A population instance can be explored in the context of an explorer viewer.
 *
 *
 * @since   PHILOViZ 1.0
 * @author PHYLOViZ Team &lt;phyloviz@gmail.com&gt;
 */
public class Population implements DataModel, Lookup.Provider, NodeFactory {

	private InstanceContent ic;
	private AbstractLookup lookup;

	/**
	 * The set of the ancillary data names.
	 */
	private ArrayList<String> headers;

	/**
	 * The list of sets of ancillary data values.
	 * Each position of this list contains the corresponding set of ancillary
	 * data values to a specific ancillary data name.
	 */
	private ArrayList<HashSet<String>> domains;

	/**
	 *  The map that to each ancillary data name maps
	 *  an internal index.
	 */
	private HashMap<String, Integer> h2idx;

	/**
	 *  The list of the isolates that belong to this population.
	 */
	private ArrayList<Isolate> collection;

	/**
	 * A tabular model of this population.
	 * */
	private TableModel model;

	private DataSaver saver;
	private int key;

	/**
	 * Constructs an empty population.
	 */
	public Population() {
		ic = new InstanceContent();
		lookup = new AbstractLookup(ic);
		headers = new ArrayList<String>();
		domains = new ArrayList<HashSet<String>>();
		h2idx = new HashMap<String, Integer>();
		collection = new ArrayList<Isolate>();
	}

	/**
	 * Adds an isolate to this population with <code>values</code> as its
	 * ancillary data.
	 *
	 * @param values the ancillary data values of an isolate.
	 * @return <code>true</code> if <code>values</code> has the expected
	 * length for this population.
	 */
	public boolean addIsolate(String[] values) {

		// Do we have values for all fields?
		if (values.length != headers.size()) {
			return false;
		}

		Isolate isolate = new Isolate();

		for (int i = 0; i < values.length; i++) {
			isolate.add(values[i]);
			domains.get(i).add(values[i]);
		}

		collection.add(isolate);

		return true;
	}

	/**
	 * Returns the number of isolates in this population.
	 *
	 * @return the number of isolates of this population
	 */
	@Override
	public int size() {
		return collection.size();
	}

	/**
	 * Sorts the isolates from this population according to the order
	 * induced by the specified comparator. All isolates in the population
	 * must be mutually
	 * comparable using the specified comparator
	 * (that is, c.compare(e1, e2) must not throw a
	 * <code>ClassCastException</code> for any elements e1 and e2 in the
	 * population).
	 *
	 * @param c  the comparator to determine the order of the list. A
	 * null value indicates that the elements' natural ordering should
	 * be used.
	 */
	public void sort(Comparator<Isolate> c) {
		Collections.sort(collection, c);
	}

	/**
	 * Returns an iterator over the elements in this population.
	 * There are no guarantees concerning the order in which the elements
	 * are returned
	 * @return  an <code>Iterator</code>
	 * over the elements in this population.
	 */
	@Override
	public Iterator<Isolate> iterator() {
		return new PopulationIterator();
	}

	/**
	 * Returns a string representation of this population.
	 * The string representation express de kind of elements of this
	 * population, i.e., isolates.
	 * @overrides <code>toString</code> in class <code>Object</code>
	 * @return a string representation of this population.
	 */
	@Override
	public String toString() {
		return "Isolate Data";
	}

	// LookUp Methods

	/** Returns the lookup associated with the object.
	 * @return  fully initialized lookup instance provided by this object.
	 * @override  <code>getLookUp</code> in class <code>AbstractLookUp</code>.
	 */
	@Override
	public Lookup getLookup() {
		return lookup;
	}

	/**
	 * Registers the object o within this lookup.
	 * @see - org.openide.util.Lookup.Provider.
	 * @param o  the object to be registered.
	 */
	public void add(Object o) {
		ic.add(o);
	}

	/**
	 * Unregisters the object <code>o</code> within this lookup.
	 * @param  o the object to be registered..
	 */
	public void remove(Object o) {
		ic.remove(o);
	}

	/**
	 * Allows to explore this population in the context of an explorer viewer.
	 * @return - Returns this population as a node in the explorer view.
	 */
	@Override
	public AbstractNode getNode() {
		return new PopulationNode(this);
	}

	private class PopulationIterator implements Iterator<Isolate> {

		private Iterator<Isolate> i;
		private Isolate last;
		private int idx;

		public PopulationIterator() {
			i = collection.iterator();
			last = null;
			idx = 0;
		}

		@Override
		public boolean hasNext() {
			return i.hasNext();
		}

		@Override
		public Isolate next() {
			last = i.next();
			idx++;
			return last;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Not supported!");
		}

		public String get(int idx) {
			if (last == null) {
				return null;
			}

			return last.get(idx);
		}

		public void set(int idx, String value) {
			if (last != null) {
				domains.get(idx).add(value);
				last.set(idx, value);
			}
		}

		public String get(String column) {
			return get(h2idx.get(column));
		}

		public void set(String column, String value) {
			set(h2idx.get(column), value);
		}

		public int index() {
			return idx;
		}
	}

	//Data Model Methods

	/**
	 * Returns the key column of this population model.
	 *
	 * @return the index of the key column for this population model.
	 */
	@Override
	public int getKey() {
		return key;
	}

	/**
	 * Changes the key column of this population model.
	 * @param key   the new key column for this population model.
	 */
	public void setKey(int key) {
		this.key = key;
	}

	/**
	 * Adds a new column to this population model with name
	 * <code>header</code> and values obtaineds thought <code>cf</code>,
	 * if there is not already a column with the same name.
	 *
	 * @param header the name of the column to add.
	 * @param cf  the values filler for the column to add.
	 * @return <code>true</code> if this population model
	 * did not already contain a column with  the specified name.
	 */
	@Override
	public boolean addColumn(String header, ColumnFiller cf) {
		if (h2idx.containsKey(header)) {
			return false;
		}

		h2idx.put(header, headers.size());
		headers.add(header);
		HashSet<String> dom = new HashSet<String>();
		domains.add(dom);

		Iterator<Isolate> i = collection.iterator();
		if (cf != null) {
			while (i.hasNext()) {
				Isolate iso = i.next();
				String val = cf.getValue(iso);
				dom.add(val);
				iso.add(val);
			}
		} else {
			while (i.hasNext()) {
				i.next().add(null);
			}
		}

		return true;
	}

	/**
	 * Returns the label that describes the domain
	 * of the data values that
	 * are in the column idx of the table model of this population.
	 * @param idx the idx column in the table model.
	 * @return the label that describes the domain of the data values that are
	 * on the column idx of the table model of this population. It returns null
	 * if <code>idx</code> is not a valid index.
	 */
	@Override
	public String getDomainLabel(int idx) {
		if (idx < 0 || idx >= headers.size()) {
			return null;
		}

		return headers.get(idx);
	}

	/**
	 * Returns a collection with all the isolates of this population.
	 * @return a <code> Collection</code> with all the isolates of this population.
	 */
	@Override
	public Collection<? extends DataItem> getItems() {
		return new ArrayList<Isolate>(collection);
	}

	/**
	 * Returns the sum of the weights of all the isolates of this population.
	 * @return the sum of the weights of all the isolates of this population.
	 */
	@Override
	public int weight() {
		return size();
	}

	/**
	 * Returns the set of data values that belong to the isolates and
	 * are in the column idx of the table model inherent to this population.
	 * @param idx  the idx column in the table model inherent to this population.
	 * @return the set of data values that belong to the isolates that are
	 * on the column idx of the table model inherent to this population.
	 */
	@Override
	public HashSet<String> getDomain(int idx) {
		if (idx < 0 || idx > domains.size()) {
			return null;
		}

		return domains.get(idx);
	}

	/**
	 *  Returns a table model for this population.
	 * @return a table model for this population.
	 */
	@Override
	public AbstractTableModel tableModel() {
		if (model == null) {
			model = new TableModel();
		}

		return model;
	}

	@Override
	public DataSaver getSaver() {
		if (saver == null) {
			saver = new DataSaver(this);
		}

		return saver;
	}

	private class TableModel extends AbstractTableModel {

		@Override
		public int getRowCount() {
			return size();
		}

		@Override
		public int getColumnCount() {
			return headers.size();
		}

		@Override
		public String getColumnName(int idx) {
			return getDomainLabel(idx);
		}

		@Override
		public int findColumn(String name) {
			if (h2idx.containsKey(name)) {
				return h2idx.get(name);
			}

			return -1;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (rowIndex < 0 || rowIndex >= collection.size()) {
				return null;
			}

			return collection.get(rowIndex).get(columnIndex);
		}
	}
}
