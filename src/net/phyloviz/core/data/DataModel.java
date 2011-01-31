package net.phyloviz.core.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.table.AbstractTableModel;

public interface DataModel {

	public AbstractTableModel tableModel();

	public String getDomainLabel(int i);

	public HashSet<String> getDomain(int i);

	/**
	 * Returns the key column of this data model. Note that it returns
	 * <tt>-1</tt> whenever we should use instead the item ID.
	 *
	 * @return the index of the key column for this model, or <tt>-1</tt>.
	 */
	public int getKey();

	public Collection<? extends DataItem> getItems();

	public Iterator<? extends DataItem> iterator();

	public int size();

	public int weight();
}
