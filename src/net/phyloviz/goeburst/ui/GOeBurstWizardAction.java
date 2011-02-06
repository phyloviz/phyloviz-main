package net.phyloviz.goeburst.ui;

import java.awt.Component;
import java.awt.Dialog;
import java.beans.PropertyChangeEvent;
import java.text.MessageFormat;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.goeburst.algorithm.AbstractDistance;
import net.phyloviz.goeburst.run.GOeBurstRunner;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.HelpCtx;
import org.openide.util.actions.NodeAction;

public final class GOeBurstWizardAction extends NodeAction {

	private WizardDescriptor.Panel[] panels;

	@SuppressWarnings("unchecked")
	@Override
	protected void performAction(Node[] nodes) {

		WizardDescriptor wizardDescriptor = new WizardDescriptor(getPanels(nodes[0]));
		// {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
		wizardDescriptor.setTitleFormat(new MessageFormat("{0}"));
		wizardDescriptor.setTitle("goeBURST Configuration");
		wizardDescriptor.putProperty("node", nodes[0]);
	
		Dialog dialog = DialogDisplayer.getDefault().createDialog(wizardDescriptor);
		dialog.setVisible(true);
		dialog.toFront();
		boolean cancelled = wizardDescriptor.getValue() != WizardDescriptor.FINISH_OPTION;
		if (!cancelled) {

			// do something
			AbstractDistance ad = (AbstractDistance) wizardDescriptor.getProperty("distance");
			int level = (Integer) wizardDescriptor.getProperty("level");

			// Let us find the safe max. Note that we want to avoid complete graphs.
			TypingData<? extends Profile> td = nodes[0].getLookup().lookup(TypingData.class);
			int safeMax = ad.maximum(td) - 1;

			if (level > safeMax) {
				JOptionPane.showMessageDialog(null, "Invalid level for this dataset!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			OutputPanel op = new OutputPanel();
			Runnable job = new GOeBurstRunner(nodes[0], op, level, ad);

			op.setName(nodes[0].getParentNode().getDisplayName() + ": goeBURST Output");
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
					new GOeBurstWizardPanel1(node),
					new GOeBurstWizardPanel2(node)
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
		return "Run goeBURST";
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
