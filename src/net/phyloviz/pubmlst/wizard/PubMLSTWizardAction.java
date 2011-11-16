package net.phyloviz.pubmlst.wizard;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import javax.swing.JComponent;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import org.openide.util.HelpCtx;

public final class PubMLSTWizardAction implements ActionListener {

	private WizardDescriptor.Panel[] panels;

	@SuppressWarnings("unchecked")
	@Override
	public void actionPerformed(ActionEvent e) {
		WizardDescriptor wizardDescriptor = new WizardDescriptor((Panel<WizardDescriptor>[]) getPanels());
		// {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
		wizardDescriptor.setTitleFormat(new MessageFormat("{0}"));
		wizardDescriptor.setTitle(org.openide.util.NbBundle.getMessage(PubMLSTWizardAction.class, "PubMLSTWizardAction.title"));
		wizardDescriptor.putProperty("WizardPanel_contentBackgroundColor", Color.WHITE);
		wizardDescriptor.putProperty("WizardPanel_imageAligment", "center");
		Dialog dialog = DialogDisplayer.getDefault().createDialog(wizardDescriptor);
		dialog.setVisible(true);
		dialog.toFront();
		boolean cancelled = wizardDescriptor.getValue() != WizardDescriptor.FINISH_OPTION;
		if (!cancelled) {
			// do something
		}
	}

	/**
	 * Initialize panels representing individual wizard's steps and sets
	 * various properties for them influencing wizard appearance.
	 */
	private WizardDescriptor.Panel[] getPanels() {
		if (panels == null) {
			panels = new WizardDescriptor.Panel[]{
				new PubMLSTWizardPanel1(),
				new PubMLSTWizardPanel2(),
				new PubMLSTWizardPanel3()
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

	public String getName() {
		return "Start Sample Wizard";
	}

	public String iconResource() {
		return null;
	}

	public HelpCtx getHelpCtx() {
		return HelpCtx.DEFAULT_HELP;
	}

	protected boolean asynchronous() {
		return false;
	}
}
