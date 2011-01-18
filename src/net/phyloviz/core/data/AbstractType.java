package net.phyloviz.core.data;

public abstract class AbstractType {

	protected String id;

	public String getID() {
		return id;
	}

	public abstract String getValue(int idx);

	public abstract int profileLength();
}
