package net.phyloviz.goeburst;

import net.phyloviz.algo.AlgorithmProvider;
import net.phyloviz.goeburst.ui.ExecAction;
import org.openide.util.actions.NodeAction;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = AlgorithmProvider.class)
public class GOeBurstProvider implements AlgorithmProvider {

	private static final String customName = "goeBURST";
	private NodeAction action;

	@Override
	public String toString() {
		return customName;
	}

	public boolean asynchronous() {
		return false;
	}

	@Override
	public NodeAction getAction() {
		if (action == null)
			action = new ExecAction();
		return action;
	}
}
