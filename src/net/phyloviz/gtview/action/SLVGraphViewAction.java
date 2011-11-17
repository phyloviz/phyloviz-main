/*-
 * Copyright (c) 2011, PHYLOViZ Team <phyloviz@gmail.com>
 * All rights reserved.
 * 
 * This file is part of PHYLOViZ <http://www.phyloviz.net>.
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
 * 
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole combination.
 * 
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent modules,
 * and to copy and distribute the resulting executable under terms of your
 * choice, provided that you also meet, for each linked independent module,
 * the terms and conditions of the license of that module.  An independent
 * module is a module which is not derived from or based on this library.
 * If you modify this library, you may extend this exception to your version
 * of the library, but you are not obligated to do so.  If you do not wish
 * to do so, delete this exception statement from your version.
 */

package net.phyloviz.gtview.action;

import java.beans.PropertyChangeEvent;
import javax.swing.SwingUtilities;
import net.phyloviz.core.data.DataSet;
import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.goeburst.GOeBurstResult;
import net.phyloviz.gtview.ui.SLVGraphGTPanel;
import org.openide.awt.StatusDisplayer;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.HelpCtx;
import org.openide.util.actions.NodeAction;
import org.openide.windows.TopComponent;

public class SLVGraphViewAction extends NodeAction {

	@Override
	protected void performAction(Node[] nodes) {
		GOeBurstResult gr = nodes[0].getLookup().lookup(GOeBurstResult.class);
		
		//If a viewer is already open, just give it focus
		for (TopComponent tc : TopComponent.getRegistry().getOpened()) {
			if (tc instanceof SLVGraphGTPanel && tc.getLookup().lookup(GOeBurstResult.class) == gr) {
				tc.requestActive();
				return;
			}
		}

		TypingData<? extends Profile> td = nodes[0].getParentNode().getLookup().lookup(TypingData.class);

		//Nope, need a new viewer
        	StatusDisplayer.getDefault().setStatusText("Starting display...");
		SLVGraphGTPanel tvp = new SLVGraphGTPanel(nodes[0].getParentNode().getDisplayName() + ": " + nodes[0].getDisplayName(), gr, td);
		tvp.open();
		tvp.requestActive();

		nodes[0].addNodeListener(new LocalNodeListener(tvp));
	
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
		return "View SLV Graph";
	}

	@Override
	public HelpCtx getHelpCtx() {
		return null;
	}

	private class LocalNodeListener implements NodeListener {

		private SLVGraphGTPanel tvp;

		LocalNodeListener(SLVGraphGTPanel tvp) {
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
