package net.phyloviz.core.explorer;

import net.phyloviz.core.util.NodeFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

public class ExplorerChildren extends Children.Keys<NodeFactory> {

	private Lookup.Result<NodeFactory> result;

	public ExplorerChildren(Lookup lookup) {
		super();
		result = lookup.lookupResult(NodeFactory.class);
		result.addLookupListener(new LookupListener() {

			@Override
			public void resultChanged(LookupEvent le) {
				refreshKeys();
			}
		});
	}

	@Override
	public void addNotify() {
		refreshKeys();
	}

	@Override
	protected Node[] createNodes(NodeFactory t) {
		return new Node[] { t.getNode() };
	}

	private void refreshKeys() {
		setKeys(result.allInstances());
	}
}
