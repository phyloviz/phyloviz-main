/* Fri Nov 12 15:38:55 UTC 2010
 * Created by aplf@
 */

package net.phyloviz.core.data;

import net.phyloviz.core.util.DataModel;
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

public class TypingData<T extends AbstractProfile> implements DataModel, Lookup.Provider, NodeFactory {

	private InstanceContent ic;
	private AbstractLookup lookup;

	private String description;
	private String[] headers;
	private HashSet<String>[] domains;
	private HashMap<String, Integer> h2idx;

	private ArrayList<T> collection;
	private TreeSet<T> unique;

	private TableModel model;

	public TypingData(int nColumns) {
		headers = new String[nColumns];
		domains = new HashSet[nColumns];
		for (int i = 0; i < nColumns; i++)
			domains[i] = new HashSet<String>();
		h2idx = new HashMap<String, Integer>();

		collection = new ArrayList<T>();
		unique = new TreeSet<T>();
	}

	public TypingData(String[] ha) {
		this(ha.length);
		System.arraycopy(ha, 0, headers, 0, ha.length);
	}

	public int size() {
		return collection.size();
	}

	public void setHeader(int idx, String header) {
		if (idx >= 0 && idx < headers.length)
			headers[idx] = header;
	}

	public Collection<String> getHeaders() {
		return Arrays.asList(headers);
	}

	public boolean addData(T profile) {

		// Do we have values for all fields?
		if (profile.profileLength() != headers.length - 1)
			return false;

		if (! unique.add(profile))
			return false;

		domains[0].add(profile.getID());
		for (int i = 0; i < profile.profileLength(); i++)
			domains[i+1].add(profile.getValue(i));

		collection.add(profile);

		return true;
	}

	public T getEqual(T profile) {
		T p = unique.ceiling(profile);

		if (p.equals(profile))
			return p;

		return null;
	}

	public void sort(Comparator<T> c) {
		Collections.sort(collection, c);
	}

	public DataIterator iterator() {
		return new DataIterator();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String desc) {
		description = desc;
	}

	@Override
	public HashSet<String> getDomain(int idx) {
		if (idx < 0 || idx > domains.length)
			return null;

		return domains[idx];
	}

	@Override
	public AbstractNode getNode() {
		return new TypingDataNode(this);
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

	public class DataIterator implements Iterator<T> {

		private Iterator<T> i;
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
			idx ++;
			return last;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Not supported!");
		}

		public String getID() {
			if (last == null)
				return null;
			
			return last.getID();
		}

		public String getValue(int idx) {
			if (last == null)
				return null;

			return last.getValue(idx);
		}

		public String getValue(String column) {
			return getValue(h2idx.get(column) - 1);
		}

		public int index() {
			return idx;
		}
	}

	@Override
	public String toString() {
		return description;
	}

	@Override
	public TableModel tableModel() {
		if (model == null)
			model = new TableModel();

		return model;
	}

	public class TableModel extends AbstractTableModel {

		@Override
		public int getRowCount() {
			return collection.size();
		}

		@Override
		public int getColumnCount() {
			return headers.length;
		}

		@Override
		public String getColumnName(int idx) {
			if (idx < 0 || idx >= headers.length)
				return null;

			return headers[idx];
		}

		@Override
		public int findColumn(String name) {
			if (h2idx.containsKey(name))
				return h2idx.get(name);

			return -1;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (rowIndex < 0 || rowIndex >= collection.size())
				return null;

			if (columnIndex < 0 || columnIndex >= headers.length)
				return null;

			if (columnIndex == 0)
				return collection.get(rowIndex).getID();
			else
				return collection.get(rowIndex).getValue(columnIndex - 1);
		}
	}
}
