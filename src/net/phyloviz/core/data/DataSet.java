package net.phyloviz.core.data;

import net.phyloviz.core.explorer.DataSetNode;
import net.phyloviz.core.util.NodeFactory;
import org.openide.nodes.AbstractNode;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

public class DataSet implements Lookup.Provider, NodeFactory {

	private InstanceContent ic;
	private AbstractLookup lookup;

	private String name;
	private int popKey;

	public DataSet(String name) {
		ic = new InstanceContent();
		lookup = new AbstractLookup(ic);
		this.name = name;
		popKey = -1;
	}

	public int getPopKey() {
		return popKey;
	}

	public void setPopKey(int key) {
		Population pop = lookup.lookup(Population.class);
		if (pop != null)
			pop.setKey(key);
		popKey = key;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public Lookup getLookup() {
		return lookup;
	}

	public void add(Object o) {
		ic.add(o);
	}

	public void remove(Object o) {
		ic.remove(o);
	}

	@Override
	public AbstractNode getNode() {
		return new DataSetNode(this);
	}
}
