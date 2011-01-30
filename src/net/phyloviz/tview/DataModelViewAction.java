package net.phyloviz.tview;

import java.beans.PropertyChangeEvent;
import javax.swing.SwingUtilities;
import net.phyloviz.core.data.DataModel;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.HelpCtx;
import org.openide.util.actions.NodeAction;
//import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
//import org.openide.windows.WindowManager;

public class DataModelViewAction extends NodeAction {

	@Override
	protected void performAction(Node[] nodes) {

		DataModel dm = nodes[0].getLookup().lookup(DataModel.class);

		//If a viewer is already open, just give it focus
		for (TopComponent tc : TopComponent.getRegistry().getOpened()) {
			if (tc instanceof TViewPanel && tc.getLookup().lookup(DataModel.class) == dm) {
				tc.requestActive();
				return;
			}
		}
		//Nope, need a new viewer
		TViewPanel tvp = new TViewPanel(nodes[0].getParentNode().getDisplayName() + ": " + nodes[0].getDisplayName(), dm);
		//Mode mode = WindowManager.getDefault().findMode("rightSlidingSide");
		//if (mode != null)
		//	mode.dockInto(tvp);
		tvp.open();
		tvp.requestActive();

		nodes[0].addNodeListener(new LocalNodeListener(tvp));
	}

	@Override
	protected boolean enable(Node[] nodes) {
		return nodes.length == 1;
	}

	@Override
	public String getName() {
		return "View";
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

		private TViewPanel tvp;

		LocalNodeListener(TViewPanel tvp) {
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
