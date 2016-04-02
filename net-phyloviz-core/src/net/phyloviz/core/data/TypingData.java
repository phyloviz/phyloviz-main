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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;
import javax.swing.table.AbstractTableModel;
import net.phyloviz.core.explorer.TypingDataNode;
import net.phyloviz.core.util.NodeFactory;
import org.openide.nodes.AbstractNode;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 * The <code>TypingData</code> class represents the set of the
 * isolates profiles created by a microbial typing method.
 * Each typing data has a table view, i.e., in can be displayed as a two
 * dimensional table. 
 *
 * @param <T>
 * @since   PHILOViZ 1.0
 * @author PHYLOViZ Team &lt;phyloviz@gmail.com&gt;
 *
 */
public class TypingData<T extends AbstractProfile> implements DataModel, Lookup.Provider, NodeFactory {

	private InstanceContent ic;
	private AbstractLookup lookup;
	private String description;

	/**
	 * The list of the feature names corresponding to this kind of profiles.
	 * This names will be the headers of the columns in a table view of this
	 * typing data.
	 */
	private String[] headers;

	/**
	 * The array of sets of the feature data values.
	 * Each position of this array contains the corresponding set of the feature
	 * data values to a specific feature data name.
	 */
	private HashSet<String>[] domains;

	/**
	 *  The map that to each feature data name maps
	 *  an internal index.
	 */
	private HashMap<String, Integer> h2idx;

	/**
	 *  The list of the profiles that belong to this population.
	 */
	private ArrayList<T> collection;
	private TreeSet<T> unique;
	private HashSet<String> idset;
	
	/**
	 * A tabular model of this population.
	 * */
	private TableModel model;
	protected DataSaver saver;
	private boolean weightOk;
	private int weight;

	/**
	 * Constructs an empty typing data model, with <code>nColumns</code> features.
	 * @param nColumns
	 */
	@SuppressWarnings("unchecked")
	public TypingData(int nColumns) {

		ic = new InstanceContent();
		lookup = new AbstractLookup(ic);
		
		headers = new String[nColumns];
		domains = new HashSet[nColumns];
		for (int i = 0; i < nColumns; i++) {
			domains[i] = new HashSet<>();
		}
		h2idx = new HashMap<>();

		collection = new ArrayList<>();
		unique = new TreeSet<>();
		idset = new HashSet<>();

		weightOk = false;
	}

	/**
	 * Constructs an empty typing data model, with  <code>ha.lenght</code>
	 * features, named by the array <code>ha</code>.
	 * @param ha
	 */
	public TypingData(String[] ha) {
		this(ha.length);
		for (int i = 0; i < ha.length; i++)
			setHeader(i, ha[i]);
	}

	/**
	 * Returns the number of profiles in this typing data.
	 *
	 * @return the number of profiles of this typing data.
	 */
	@Override
	public int size() {
		return collection.size();
	}

	/**
	 * Sets the column <code> idx</code> of  this typing data model with a new name
	 * <code>header</code>.
	 *
	 * @param idx
	 * @param header the new name of the column <code>idx</code>.
	 * @thows ArrayIndexOutOfBoundException if the <code>idx</code> does not
	 * corresponds to an existent column.
	 */
	public final void setHeader(int idx, String header) {
		if (idx >= 0 && idx < headers.length) {
			headers[idx] = header;
			h2idx.put(header, idx);
		}
	}

	/**
	 * Obtains the list of the feature names headers or labels.
	 * This names are the headers of the columns in a table view of this
	 * typing data model.
	 *
	 * @return headers
	 */
	public Collection<String> getHeaders() {
		return Arrays.asList(headers);
	}

	/**
	 * Adds a new profile to this typing data, if there is not
	 * already an equal one. The new profile should have values for
	 * all the features considered in this typing data. Otherwise, it will not
	 * be added.
	 * @param profile the new profile to be added.
	 * @return the inserted profile.
	 * @throws Exception on unsuccess.
	 */
	public T addData(T profile) throws Exception {

		// Do we have values for all fields?
		if (profile.profileLength() != headers.length - 1) {
			throw new Exception("Incompatible length: "
				+ profile.getID() + " has length"
				+ profile.profileLength() + "(!="
				+ (headers.length - 1) + ")");
		}

		if (unique.contains(profile)) {
			throw new Exception("Duplicated profile: "
				+ profile.getID() + " aka "
				+ this.getEqual(profile).getID());
		}

		if (idset.contains(profile.getID())) {
			throw new Exception("Duplicated profile ID: "
				+ profile.getID());
		}

		domains[0].add(profile.getID());
		for (int i = 0; i < profile.profileLength(); i++) {
			domains[i + 1].add(profile.getValue(i));
		}

		collection.add(profile);
		unique.add(profile);
		idset.add(profile.getID());

		weightOk = false;

		return profile;
	}

	/**
	 * Returns the profile in this typing data equals to the given profile,
	 * or null if there is no such profile.
	 * @param profile the profile to match.
	 * @return the profile equals to the parameter, or null if there is no
	 * such element.
	 */
	public T getEqual(T profile) {
		T p = unique.ceiling(profile);

		if (p != null && p.equals(profile)) {
			return p;
		}

		return null;
	}

	/**
	 *  Sorts the profiles of this typing data according
	 *  to the order induced by the specified comparator.
	 *  @param c  the comparator to determine the order of the profiles.
	 *  A <code>null</code> value indicates that the elements' natural
	 * ordering should be used.
	 */
	public void sort(Comparator<T> c) {
		Collections.sort(collection, c);
	}

	/**
	 * Returns an iterator over the profiles in this typing data.
	 * There are no guarantees concerning the order in which the profiles
	 * are returned
	 * @return   an <code>Iterator</code>
	 * over the profiles in this typing data.
	 */
	@Override
	public Iterator<T> iterator() {
		return new DataIterator();
	}

	/**
	 * Returns a description of this typing data.
	 * @return  a description of this typing data.
	 *
	 */
	public String getDescription() {
		return description;
	}

	/**
	 *  Changes the description of this typing data.
	 * @param  desc the new description of this typing data.
	 */
	public void setDescription(String desc) {
		description = desc;
	}

	/**
	 * Returns a string representation of this typing data, which consists
	 * of its own description.
	 * @return  a string representation of this typing data.
	 */
	@Override
	public String toString() {
		return description;
	}

	/**
	 * Returns the label that describes the domain
	 * of the data values that belong to the profiles and
	 * are in the column idx of the table model of this typing data.
	 * @param idx  the idx column in the table model.
	 * @return the label that describes the domain of the data values that are
	 * on the column idx of the table model of this typing data.
	 */
	@Override
	public String getDomainLabel(int idx) {
		if (idx < 0 || idx >= headers.length) {
			return null;
		}

		return headers[idx];
	}

	@Override
	/**
	 * Returns the set of data values that belong to the profiles and
	 * are in the column idx of the table model inherent to this typing data.
	 * @param idx  the idx column in the table model inherent to this typing data.
	 * @return the set of data values that belong to the profiles that are
	 * on the column idx of the table model inherent to this typing data.
	 */
	public HashSet<String> getDomain(int idx) {
		if (idx < 0 || idx > domains.length) {
			return null;
		}

		return domains[idx];
	}

	/**
	 * Allows to explore the population in the context of an explorer viewer.
	 * @return  Returns the population as a node in the explorer view.
	 * @see AbstractNode
	 */
	@Override
	public AbstractNode getNode() {
		return new TypingDataNode(this);
	}

	/**
	 *  Returns the lookup inherited to this typing data.
	 * @see Lookup
	 * @return the lookup inherited to this typing data.
	 */
	@Override
	public Lookup getLookup() {
		return lookup;
	}

	/**
	 * Registers the object <code>o</code> with this lookup.
	 * @see  org.openide.util.Lookup.Provider.
	 * @param o  the object to be registered.
	 */
	public void add(Object o) {
		ic.add(o);
	}

	/**
	 * Unregisters the object <code>o</code> with this lookup.
	 * @see  org.openide.util.Lookup.Provider.
	 * @param o  the object to be registered.
	 */
	public void remove(Object o) {
		ic.remove(o);
	}

	/**
	 * Returns the key column of this typing data model.
	 *
	 * @return the index of the key column for this typing data model.
	 */
	@Override
	public int getKey() {
		return 0;
	}

	/**
	 * Returns a collection with all the profiles of this data model.
	 * @return a collection with all the profiles of this data model.
	 */
	@Override
	public Collection<? extends DataItem> getItems() {
		return new ArrayList<>(collection);
	}

	/**
	 * Returns the sum of the weights of all the profiles of this typing data.
	 * @return the sum of the weights of all the profiles of this typing data.
	 */
	@Override
	public synchronized int weight() {

		if (!weightOk) {
			weight = 0;
			Iterator<T> i = collection.iterator();
			while (i.hasNext()) {
				weight += i.next().weight();
			}
		}

		return weight;
	}

	@Override
	public boolean addColumn(String domainLabel, ColumnFiller cf) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	private class DataIterator implements Iterator<T> {

		private final Iterator<T> i;
		private T last;
		private int idx;

		public DataIterator() {
			i = collection.iterator();
			last = null;
			idx = 0;
		}

		@Override
		public boolean hasNext() {
			return i.hasNext();
		}

		@Override
		public T next() {
			last = i.next();
			idx++;
			return last;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Not supported!");
		}

		public String getID() {
			if (last == null) {
				return null;
			}

			return last.getID();
		}

		public String getValue(int idx) {
			if (last == null) {
				return null;
			}

			return last.getValue(idx);
		}

		public String getValue(String column) {
			return getValue(h2idx.get(column) - 1);
		}

		public int index() {
			return idx;
		}
	}

	/**
	 *  Returns a table model for this typing data model.
	 * @return a table model for this typing data model.
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
			return headers.length;
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
