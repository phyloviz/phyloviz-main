package net.phyloviz.core.util;

import java.util.HashSet;
import javax.swing.table.AbstractTableModel;

public interface DataModel {

	public AbstractTableModel tableModel();

	public HashSet<String> getDomain(int i);
}
