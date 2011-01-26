package net.phyloviz.gtview.ui;

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.NodeAction;

public class ViewAction extends NodeAction {

	@Override
	protected void performAction(Node[] nodes) {
	}

	@Override
	protected boolean enable(Node[] nodes) {
		return nodes.length == 1;
	}

	@Override
	protected boolean asynchronous() {
		return false;
	}

	@Override
	public String getName() {
		return "View";
	}

	@Override
	public HelpCtx getHelpCtx() {
		return null;
	}
}
