/*-
 * Copyright (c) 2016, PHYLOViZ Team <phyloviz@gmail.com>
 * All rights reserved.
 * 
 * This file is part of PHYLOViZ <http://www.phyloviz.net/>.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.phyloviz.stats;

import java.beans.PropertyChangeEvent;
import javax.swing.SwingUtilities;
import net.phyloviz.core.data.DataModel;
import net.phyloviz.core.data.DataSet;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.HelpCtx;
import org.openide.util.actions.NodeAction;
import org.openide.windows.TopComponent;

public class StatisticsViewAction extends NodeAction {

	@Override
	protected void performAction(Node[] nodes) {

		DataModel dm = nodes[0].getLookup().lookup(DataModel.class);

		//If a viewer is already open, just give it focus
		for (TopComponent tc : TopComponent.getRegistry().getOpened()) {
			if (tc instanceof StatisticsPanel && tc.getLookup().lookup(DataModel.class) == dm) {
				tc.requestActive();
				return;
			}
		}
		//Nope, need a new viewer
		DataSet ds = nodes[0].getParentNode().getLookup().lookup(DataSet.class);
		StatisticsPanel sp = new StatisticsPanel(nodes[0].getParentNode().getDisplayName() + ": " + nodes[0].getDisplayName(), dm, ds);
		sp.open();
		sp.requestActive();

		nodes[0].addNodeListener(new LocalNodeListener(sp));
	}

	@Override
	protected boolean enable(Node[] nodes) {
		return nodes.length == 1;
	}

	@Override
	public String getName() {
		return "Statistics";
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

		private StatisticsPanel sp;

		LocalNodeListener(StatisticsPanel sp) {
			this.sp = sp;
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
					sp.close();
				}
			});
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
		}
	}
}
