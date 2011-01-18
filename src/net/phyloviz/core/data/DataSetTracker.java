package net.phyloviz.core.data;

import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = DataSetTracker.class)
public class DataSetTracker implements Lookup.Provider {

	private InstanceContent ic;
	private AbstractLookup lookup;

	public DataSetTracker() {
		ic = new InstanceContent();
		lookup = new AbstractLookup(ic);
	}

	@Override
	public Lookup getLookup() {
		return lookup;
	}

	public void add(DataSet ds) {
		ic.add(ds);
	}

	public void remove(DataSet ds) {
		ic.remove(ds);
	}
}
