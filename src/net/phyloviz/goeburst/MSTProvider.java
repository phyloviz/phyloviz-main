package net.phyloviz.goeburst;

import net.phyloviz.algo.AlgorithmProvider;
import net.phyloviz.goeburst.ui.MSTWizardAction;
import org.openide.util.actions.NodeAction;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = AlgorithmProvider.class)
public class MSTProvider implements AlgorithmProvider {

	private static final String customName = "goeBURST Full MST";
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
			action = new MSTWizardAction();
		return action;
	}

}
