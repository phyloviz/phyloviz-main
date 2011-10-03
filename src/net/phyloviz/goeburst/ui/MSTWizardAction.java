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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.goeburst.ui;

import java.awt.Component;
import java.awt.Dialog;
import java.beans.PropertyChangeEvent;
import java.text.MessageFormat;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import net.phyloviz.algo.AbstractDistance;
import net.phyloviz.goeburst.run.MSTRunner;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.HelpCtx;
import org.openide.util.actions.NodeAction;

public final class MSTWizardAction extends NodeAction {

	private WizardDescriptor.Panel[] panels;

	@SuppressWarnings("unchecked")
	@Override
	public void performAction(Node[] nodes) {
		
		WizardDescriptor wizardDescriptor = new WizardDescriptor(getPanels(nodes[0]));
		// {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
		wizardDescriptor.setTitleFormat(new MessageFormat("{0}"));
		wizardDescriptor.setTitle("MST Algorithm Configuration");
		wizardDescriptor.putProperty("node", nodes[0]);

		Dialog dialog = DialogDisplayer.getDefault().createDialog(wizardDescriptor);
		dialog.setVisible(true);
		dialog.toFront();
		boolean cancelled = wizardDescriptor.getValue() != WizardDescriptor.FINISH_OPTION;
		if (!cancelled) {

			// do something
			AbstractDistance ad = (AbstractDistance) wizardDescriptor.getProperty("distance");

			OutputPanel op = new OutputPanel(nodes[0].getParentNode().getDisplayName() + ": goeBURST Full MST Output");
			Runnable job = new MSTRunner(nodes[0], op, ad);

			op.open();
			op.requestActive();

			Thread thread = new Thread(job);
			thread.setDaemon(true);
			thread.start();

			nodes[0].addNodeListener(new LocalNodeListener(op));
		}
	}

	/**
	 * Initialize panels representing individual wizard's steps and sets
	 * various properties for them influencing wizard appearance.
	 */
	private WizardDescriptor.Panel[] getPanels(Node node) {
		if (panels == null) {
			panels = new WizardDescriptor.Panel[]{
					new MSTWizardPanel1(node)
				};
			String[] steps = new String[panels.length];
			for (int i = 0; i < panels.length; i++) {
				Component c = panels[i].getComponent();
				// Default step name to component name of panel. Mainly useful
				// for getting the name of the target chooser to appear in the
				// list of steps.
				steps[i] = c.getName();
				if (c instanceof JComponent) { // assume Swing components
					JComponent jc = (JComponent) c;
					// Sets step number of a component
					// TODO if using org.openide.dialogs >= 7.8, can use WizardDescriptor.PROP_*:
					jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i));
					// Sets steps names for a panel
					jc.putClientProperty("WizardPanel_contentData", steps);
					// Turn on subtitle creation on each step
					jc.putClientProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
					// Show steps on the left side with the image on the background
					jc.putClientProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
					// Turn on numbering of all steps
					jc.putClientProperty("WizardPanel_contentNumbered", Boolean.TRUE);
				}
			}
		}
		return panels;
	}

	@Override
	public String getName() {
		return "goeBURST Full MST";
	}

	@Override
	public String iconResource() {
		return null;
	}

	@Override
	public HelpCtx getHelpCtx() {
		return HelpCtx.DEFAULT_HELP;
	}

	@Override
	protected boolean asynchronous() {
		return false;
	}

	@Override
	protected boolean enable(Node[] nodes) {
		return nodes.length == 1;
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
