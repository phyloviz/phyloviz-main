package net.phyloviz.goeburst.ui;

import java.beans.PropertyChangeEvent;
import javax.swing.SwingUtilities;
import net.phyloviz.goeburst.GOeBurstResult;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.HelpCtx;
import org.openide.util.actions.NodeAction;

public class OutputViewAction extends NodeAction {

	@Override
	protected void performAction(Node[] nodes) {

		GOeBurstResult gr = nodes[0].getLookup().lookup(GOeBurstResult.class);
		if (! gr.getPanel().isOpened())
			gr.getPanel().open();
		gr.getPanel().requestActive();
	}

	@Override
	protected boolean enable(Node[] nodes) {
		return nodes.length == 1;
	}

	@Override
	public String getName() {
		return "Output";
	}

	@Override
	public HelpCtx getHelpCtx() {
		return null;
	}

	@Override
	protected boolean asynchronous() {
		return false;
	}

	private class LocalNodeListener implements NodeListener {

		private OutputPanel op;

		LocalNodeListener(OutputPanel op) {
			this.op = op;
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
					op.close();
				}
			});
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
		}
	}
}
