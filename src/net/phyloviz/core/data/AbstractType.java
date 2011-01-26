package net.phyloviz.core.data;

public abstract class AbstractType {

	protected int uid;
	protected String id;

	public int getUID() {
		return uid;
	}

	public String getID() {
		return id;
	}

	public abstract String getValue(int idx);

	public abstract int profileLength();

	public abstract int getFreq();
	
	public abstract void setFreq(int freq);
}
