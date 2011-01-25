package net.phyloviz.goeburst.ui;

import net.phyloviz.goeburst.run.GOeBurstRunner;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.NodeAction;
import org.openide.windows.Mode;
import org.openide.windows.WindowManager;

public class ExecAction extends NodeAction {

	@Override
	protected void performAction(Node[] nodes) {
		OutputPanel op = new OutputPanel();
		op.setName("goeBURST Output");
		op.open();
		op.requestActive();

		Runnable job = new GOeBurstRunner(nodes[0], op, 1);
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
