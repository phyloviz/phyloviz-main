/*-
 * Copyright (c) 2016, PHYLOViZ Team <phyloviz@gmail.com>
 * All rights reserved.
 * 
 * This file is part of PHYLOViZ <http://www.phyloviz.net/>.
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
 */

package net.phyloviz.category.filter;

import java.util.List;
import java.util.TreeSet;
import net.phyloviz.core.data.DataModel;

public class CompositeFilter implements Filter{

	private Filter[] f;
	
	public CompositeFilter(TreeSet<String>[] array, DataModel dm){
		f = new SimpleFilter[array.length];
		for (int i = 0; i < array.length; i++) {
			if (array[i] != null && !array[i].isEmpty())
				f[i] = new SimpleFilter(array[i], i, dm.getDomainLabel(i));
		}
	}

	@Override
	public List<Category> filtering(List<Category> list) {
		for (int i = 0; i < f.length; i++){
			if(f[i] != null)
				list = f[i].filtering(list);
		}
		
		return list;
	}
}
