package net.phyloviz.goeburst.ui;

import java.beans.PropertyChangeEvent;
import javax.swing.SwingUtilities;
import net.phyloviz.goeburst.run.GOeBurstRunner;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
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

		nodes[0].addNodeListener(new LocalNodeListener(op));
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

	private class LocalNodeListener implements NodeListener {

		private OutputPanel tvp;

		LocalNodeListener(OutputPanel tvp) {
			this.tvp = tvp;
		}

		@Override
		public void childrenAdded(NodeMemberEvent nme) {
		}

		@Override
		public void childrenRemoved(NodeMemberEvent nme) {
		}

		@Override
		public void childrenReordered(NodeReorderEvent nre) {
		}

		@Override
		public void nodeDestroyed(NodeEvent ne) {

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					tvp.close();
				}
			});
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
		}
	}

}
