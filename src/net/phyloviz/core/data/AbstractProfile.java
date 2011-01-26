/**
 *
 */

package net.phyloviz.core.data;

public abstract class AbstractProfile implements Profile {

	protected int uid;
	protected String id;

	@Override
	public int getUID() {
		return uid;
	}

	@Override
	public String getID() {
		return id;
	}
}
