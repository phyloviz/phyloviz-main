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

/**
 * The <code> DataModel </code> interface specifies the methods for a
 * tabular representation of the data. Each row of the
 * table is a <code>DataItem</code>, i.e., a vector of data values that caracterize the
 * item.
 *
 *
 * @since   PHILOViZ 1.0
 * @author PHYLOViZ Team &lt;phyloviz@gmail.com&gt;
 *
 */
public interface DataModel {

    
	/**
         * Returns a table model for the data model.
         * @return a table model for the data model.
         * 
         */
	public AbstractTableModel tableModel();

        /**
         * Returns the label that describes the domain
         * of the data values in the column <code>idx</code> of the table model.
         * @param idx  the <code>idx</code> column in the table model.
         * @return the label that describes the domain of the data values that are
         * on the column <code>idx</code> of the table model.
         */
	public String getDomainLabel(int idx);

       /**
        * Returns the set of  all data  values   that appear in
        * the column  <code>idx</code> of the table model for some data item.
        * @param idx  the <code>idx</code> column in the table model.
        * @return the set of data values that appear in the column <code>idx</code> in the
        * table model.
        */
	public HashSet<String> getDomain(int idx);

	/**
	 * Returns the key column of this data model.
	 *
	 * @return the index of the key column for this model.
	 */
	public int getKey();

	/**
	 * Returns a collection with all the data items of this data model.
	 *
	 *
	 * @return a <code>Collection</code> with all the data items of this data model.
	 */
	public Collection<? extends DataItem> getItems();

	/**
         *  Returns an iterator over the data items.
         * @return an <code>Iterator</code> over the data items.
         */
	public Iterator<? extends DataItem> iterator();

        /**
         *  Returns the number of data items in this data model. If this
         *  data model contains more than
         *  Integer.MAX_VALUE data items, returns Integer.MAX_VALUE. 
         *  @return  the number of data items in this data model.
         */ 
	public int size();

       /**
        *
	* Returns the sum of the weights of all the data items of this model.
	* @return the sum of the weights of all the data items of this model.
        */
	public int weight();

	public DataSaver getSaver();

	/**
         * Adds a new column to this population model with name
         * <code>header</code> and values obtained thought <code>cf</code>,
         * if there is not already a column with the same name.
         *
         * @param header the name of the column to add.
         * @param cf  the filler for the column to add.
         * @return <code>true</code> if this population model
         * did not already contain a column with  the specified name.
         */
	public boolean addColumn(String domainLabel, ColumnFiller cf);

	/**
         * This interface represents a column filler for this table model.
         * Within a column, the
         * <code>getValue</code> method obtains the value of this column for the
         * corresponding isolate.
         */
	public interface ColumnFiller {
		public String getValue(DataItem i);
	}
}
