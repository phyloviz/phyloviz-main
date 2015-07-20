package net.phyloviz.nj;

import net.phyloviz.algo.AlgorithmProvider;
import net.phyloviz.nj.ui.NeighborJoiningWizardAction;
import org.openide.util.actions.NodeAction;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = AlgorithmProvider.class)
public class NeighborJoiningProvider implements AlgorithmProvider {

	private static final String customName = "Neighbor-Joining";
	private NodeAction action;

	@Override
	public String toString() {
		return customName;
	}

	public boolean asynchronous() {
		return false;
	}

        /**
         * 
         * @return 
         */
	@Override
	public NodeAction getAction() {
		if (action == null)
			action = new NeighborJoiningWizardAction();
		return action;
	}
}
