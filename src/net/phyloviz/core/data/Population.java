package net.phyloviz.core.data;

import net.phyloviz.core.util.DataModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.table.AbstractTableModel;
import net.phyloviz.core.explorer.PopulationNode;
import net.phyloviz.core.util.NodeFactory;
import org.openide.nodes.AbstractNode;

public class Population implements DataModel, NodeFactory {

	private ArrayList<String> headers;
	private ArrayList<HashSet<String>> domains;
	private HashMap<String, Integer> h2idx;

	private ArrayList<Isolate> collection;

	private TableModel model;

	public Population() {
		headers = new ArrayList<String>();
		domains = new ArrayList<HashSet<String>>();
		h2idx = new HashMap<String, Integer>();
		collection = new ArrayList<Isolate>();
	}

	public boolean addColumn(String header) {
		if (h2idx.containsKey(header))
			return false;

		h2idx.put(header, headers.size());
		headers.add(header);
		domains.add(new HashSet<String>());

		Iterator<Isolate> i = collection.iterator();
		while (i.hasNext())
			i.next().add(null);

		return true;
	}

	public boolean addIsolate(String[] values) {

		// Do we have values for all fields?
		if (values.length != headers.size())
			return false;

		Isolate isolate = new Isolate();

		for (int i = 0; i < values.length; i++) {
			isolate.add(values[i]);
			domains.get(i).add(values[i]);
		}

		collection.add(isolate);

		return true;
	}

	public int size() {
		return collection.size();
	}

	public void sort(Comparator<Isolate> c) {
		Collections.sort(collection, c);
	}

	public PopulationIterator iterator() {
		return new PopulationIterator();
	}

	@Override
	public HashSet<String> getDomain(int idx) {
		if (idx < 0 || idx > domains.size())
			return null;

		return domains.get(idx);
	}

	@Override
	public AbstractNode getNode() {
		return new PopulationNode(this);
	}

	@Override
	public String toString() {
		return "Isolate Data";
	}

	public class PopulationIterator implements Iterator<Isolate> {

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
			idx ++;
			return last;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Not supported!");
		}

		public String get(int idx) {
			if (last == null)
				return null;

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
			return headers.size();
		}

		@Override
		public String getColumnName(int idx) {
			if (idx < 0 || idx >= headers.size())
				return null;

			return headers.get(idx);
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

			if (columnIndex < 0 || columnIndex >= headers.size())
				return null;

			return collection.get(rowIndex).get(columnIndex);
		}

	}
}
