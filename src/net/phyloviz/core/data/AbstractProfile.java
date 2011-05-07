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

/*
 * @(#)AbstractProfile.java 27/01/11
 *
 * Copyright 2011 Phyloviz. All rights reserved.
 * Use is subject to license terms.
 */
package net.phyloviz.core.data;

/**
 * This class provides a skeletal implementation of the <tt>Profile</tt>
 * interface, to minimize the effort required to implement this interface.
 *
 * @since   PHILOViZ 1.0
 * @author A. P. Francisco
 */
public abstract class AbstractProfile implements Profile {

	/**
	 * The internal value  of the identifier this typing profile.
	 */
	protected int uid;

	/**
	 * The identifier of this typing profile.
	 */
	protected String id;

	@Override
	public int getUID() {
		return uid;
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public String get(int idx) {
		if (idx < 0 || idx >= length()) {
			return null;
		}

		if (idx == 0) {
			return getID();
		} else {
			return getValue(idx - 1);
		}
	}

	@Override
	public int length() {
		return profileLength() + 1;
	}

	@Override
	public int weight() {
		return getFreq();
	}
}
