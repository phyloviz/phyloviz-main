package net.phyloviz.nj.ui;

import java.awt.Component;
import java.awt.Dialog;
import java.beans.PropertyChangeEvent;
import java.text.MessageFormat;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import net.phyloviz.algo.AbstractDistance;
import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.nj.AgglomerativeClusteringMethod;
import net.phyloviz.nj.run.NeighborJoiningRunner;
import org.openide.*;
import org.openide.WizardDescriptor;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.HelpCtx;
import org.openide.util.actions.NodeAction;

public class NeighborJoiningWizardAction extends NodeAction {

        private WizardDescriptor.Panel[] panels;
    
	@Override
	protected void performAction(Node[] nodes) {
            WizardDescriptor wizardDescriptor = new WizardDescriptor(getPanels(nodes[0]));
            // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
            wizardDescriptor.setTitleFormat(new MessageFormat("{0}"));
            wizardDescriptor.setTitle("Neighbor-Joining Algorithm Configuration");
            wizardDescriptor.putProperty("node", nodes[0]);

            Dialog dialog = DialogDisplayer.getDefault().createDialog(wizardDescriptor);
            dialog.setVisible(true);
            dialog.toFront();
            boolean cancelled = wizardDescriptor.getValue() != WizardDescriptor.FINISH_OPTION;
            if (!cancelled) {
                // do something
                AbstractDistance ad = (AbstractDistance) wizardDescriptor.getProperty("distance");
		AgglomerativeClusteringMethod cm = (AgglomerativeClusteringMethod) wizardDescriptor.getProperty("method");

                TypingData<? extends Profile> td = nodes[0].getLookup().lookup(TypingData.class);
                
                if (ad.configurable()) {
                    ad.configure();
                }

                OutputPanel op = new OutputPanel(nodes[0].getParentNode().getDisplayName() + ": Neighbor-Joining (" + ad.toString() + ") Output");
                Runnable job = new NeighborJoiningRunner(op, ad, cm, td);

                op.open();
                op.requestActive();

                Thread thread = new Thread(job);
                thread.setDaemon(true);
                thread.start();

                nodes[0].addNodeListener(new LocalNodeListener(op));
            }

            panels = null;
	}
        
    /**
     * Initialize panels representing individual wizard's steps and sets
     * various properties for them influencing wizard appearance.
     */
    private WizardDescriptor.Panel[] getPanels(Node node) {
            //TO REMOVE
        if (panels == null) {
            panels = new WizardDescriptor.Panel[]{
                    new NeighborJoiningWizardPanel1(node),
                    new NeighborJoiningWizardPanel2(node)
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
    protected boolean enable(Node[] nodes) {
        return nodes.length == 1;
    }

    @Override
    public String getName() {
        return "Neighbor-Joining";
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }

    private class LocalNodeListener implements NodeListener {

		private final OutputPanel tvp;

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
