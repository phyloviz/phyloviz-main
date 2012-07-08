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

package net.phyloviz.tview;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.regex.PatternSyntaxException;

import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import net.phyloviz.core.data.DataModel;

import org.openide.awt.StatusDisplayer;

public class TablePanel extends JTable {

	private static final long serialVersionUID = 1L;
	private TreeSet<Integer> clicable;
	TableRowSorter<TableModel> sorter;
	RowFilter<Integer, Integer> row = null;
	private TreeSet<String>[] filterSet;

	@SuppressWarnings("unchecked")
	public TablePanel(DataModel dm) {
		super();
		setModel(dm.tableModel());
		JTableHeader aux = this.getTableHeader();
		aux.setReorderingAllowed(false);
		aux.setDefaultRenderer(new MyDefaultTableHeaderCellRenderer(aux.getDefaultRenderer()));
		aux.addMouseListener(new MyMouse());
		setRowSelectionAllowed(true);
		setColumnSelectionAllowed(true);
		clicable = new TreeSet<Integer>();
		filterSet = new TreeSet[this.getColumnCount()];
		for (int i = 0; i < filterSet.length; i++) {
			filterSet[i] = new TreeSet<String>();
            }
		Comparator<String> cmp = new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				int size = o1.length() - o2.length();
				if (size != 0) {
					return size;
				}
				return o1.compareTo(o2);
			}
		};
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		sorter = new TableRowSorter<TableModel>(this.getModel());
		sorter.setComparator(0, cmp);
		setRowSorter(sorter);
                this.getModel().addTableModelListener(new TableModelListener(){

            @Override
                  public void tableChanged(TableModelEvent e) {
                       filterSet = new TreeSet[getModel().getColumnCount()];
                        for (int i = 0; i < filterSet.length; i++) {
                            filterSet[i] = new TreeSet<String>();
                        }
                  }
                });
	}

	public TreeSet<String>[] getFilterSet() {
		this.clearFilterSet();
		int colIdx[] = this.getSelectedColumns();
		int rowIdx[] = this.getSelectedRows();
		for (int i = 0; i < colIdx.length; i++) {
			for (int j = 0; j < rowIdx.length; j++) {
				filterSet[colIdx[i]].add(this.getValueAt(rowIdx[j], colIdx[i]).toString());
			}
		}

		return filterSet;
	}

	private void clearFilterSet() {
		for (int i = 0; i < filterSet.length; i++) {
			filterSet[i].clear();
		}
	}

	public void clearStructures() {
		this.clearFilterSet();
		clicable.clear();
	}

	public class MyDefaultTableHeaderCellRenderer extends DefaultTableCellRenderer {

		private TableCellRenderer render;

		public MyDefaultTableHeaderCellRenderer(TableCellRenderer render) {
			super();
			this.render = render;
			setHorizontalAlignment(CENTER);
			setHorizontalTextPosition(LEFT);
			setOpaque(true);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

			Component c = render.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			if (clicable.contains(column)) {
				c.setBackground(new Color(177, 204, 233));
			} else {
				c.setBackground(new Color(238, 238, 238));
			}

			setBorder(UIManager.getBorder("TableHeader.cellBorder"));

			return c;
		}
	}

	private class MyMouse extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {

			if (e.getButton() == MouseEvent.BUTTON3) {
				int j = getTableHeader().columnAtPoint(e.getPoint());

				if (clicable.contains(j)) {
					clicable.remove(j);

				} else {
					clicable.add(j);

				}

				getTableHeader().repaint();
			}
		}
	}

	public void reseting() {
		this.clearSelection();
		this.clearStructures();
		this.getTableHeader().updateUI();
		sorter.setRowFilter(null);
	}

	public void releasedKey(String text) {

		if (text.length() == 0) {
			sorter.setRowFilter(null);
		} else {
			try {
				int i = 0;
				int[] array = new int[clicable.size()];
				Iterator<Integer> ii = clicable.iterator();
				while (ii.hasNext())
					array[i++] = ii.next().intValue();
				sorter.setRowFilter(RowFilter.regexFilter(text, array));
			} catch (PatternSyntaxException pse) {
        			StatusDisplayer.getDefault().setStatusText("Error: Bad regex pattern: " + text);
			}
		}
	}

	public void selecting(TreeSet<String>[] otherView, boolean toReplace) {

		if (!toReplace) {
			if (clicable.isEmpty()) {
				this.addColumnSelectionInterval(0, this.getColumnCount() - 1);
			}
			Iterator<Integer> itt = clicable.iterator();
			while (itt.hasNext()) {
				int a = itt.next();
				this.addColumnSelectionInterval(a, a);
			}

			if (this.getSelectedRow() == -1) {
				int num = sorter.getViewRowCount();
				if (num != 0) {
					this.setRowSelectionInterval(0, num - 1);
				}
			}
		} else {
			this.clearSelection();
			filterSet = otherView;
			clicable.clear();
			ArrayList<Integer> columns = new ArrayList<Integer>();
			for (int i = 0; i < filterSet.length; i++) {
				if (!filterSet[i].isEmpty()) {
					columns.add(i);
					this.addColumnSelectionInterval(i, i);
				}
			}

			int nlines = this.getRowCount();
			boolean toadd;
			for (int i = 0; i < nlines; i++) {
				int j = 0;
				toadd = true;
				while (j < columns.size()) {
					if (!filterSet[columns.get(j)].contains(this.getValueAt(i, columns.get(j)).toString())) {
						toadd = false;
						break;
					}
					j++;
				}
				if (toadd) {
					this.addRowSelectionInterval(i, i);
				}
			}
		}
	}

	public TreeSet<String>[] viewing() {

		int colIdx[] = this.getSelectedColumns();
		int rowIdx[] = this.getSelectedRows();

		for (int i = 0; i < colIdx.length; i++) {
			for (int j = 0; j < rowIdx.length; j++) {
				if (this.getValueAt(rowIdx[j], colIdx[i]) != null )
					filterSet[colIdx[i]].add(this.getValueAt(rowIdx[j], colIdx[i]).toString());
			}
		}

		return filterSet;
	}
}
