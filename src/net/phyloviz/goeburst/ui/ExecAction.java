package net.phyloviz.goeburst.ui;

import net.phyloviz.goeburst.run.GOeBurstRunner;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.NodeAction;

public class ExecAction extends NodeAction {

	@Override
	protected void performAction(Node[] nodes) {
		OutputPanel op = new OutputPanel();
		Runnable job = new GOeBurstRunner(nodes[0], op, 1);
		
		op.setName("Output - " + job.toString());
		op.open();
		op.requestActive();

		Thread thread = new Thread(job);
		thread.setDaemon(true);
		thread.start();
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
		return "Run goeBURST";
	}

	@Override
	public HelpCtx getHelpCtx() {
		return null;
	}
}
