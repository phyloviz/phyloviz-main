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
