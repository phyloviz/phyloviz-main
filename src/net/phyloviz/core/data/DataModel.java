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

	public DataSaver getSaver();

	public boolean addColumn(String domainLabel, ColumnFiller cf);

	public interface ColumnFiller {

		public String getValue(DataItem i);
	}
}
