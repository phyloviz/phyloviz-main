package net.phyloviz.loadmlst.wizard;

import java.awt.Component;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;

public class LoadMLSTWizardPanel1 implements WizardDescriptor.ValidatingPanel {

	/**
	 * The visual component that displays this panel. If you need to access the
	 * component from this class, just use getComponent().
	 */
	private LoadMLSTVisualPanel1 component;

	// Get the visual component for the panel. In this template, the component
	// is kept separate. This can be more efficient: if the wizard is created
	// but never displayed, or not all panels are displayed, it is better to
	// create only those which really need to be visible.
	@Override
	public Component getComponent() {
		if (component == null) {
			component = new LoadMLSTVisualPanel1();
		}
		return component;
	}

	@Override
	public HelpCtx getHelp() {
		// Show no Help button for this panel:
		return HelpCtx.DEFAULT_HELP;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public final void addChangeListener(ChangeListener l) {
	}

	@Override
	public final void removeChangeListener(ChangeListener l) {
	}

	// You can use a settings object to keep track of state. Normally the
	// settings object will be the WizardDescriptor, so you can use
	// WizardDescriptor.getProperty & putProperty to store information entered
	// by the user.
	@Override
	public void readSettings(Object settings) {
		((WizardDescriptor) settings).putProperty("WizardPanel_image", ImageUtilities.loadImage("net/phyloviz/loadmlst/LoadMLST-logo.png", true));
	}

	@Override
	public void storeSettings(Object settings) {
		((WizardDescriptor) settings).putProperty("name", ((LoadMLSTVisualPanel1) getComponent()).getDatasetName());
		((WizardDescriptor) settings).putProperty("dbIndex", ((LoadMLSTVisualPanel1) getComponent()).getSelectedIndex());
		((WizardDescriptor) settings).putProperty("dbName", ((LoadMLSTVisualPanel1) getComponent()).getSelectedName());
	}

	@Override
	public void validate() throws WizardValidationException {
		String name = component.getDatasetName();
		int index = component.getSelectedIndex();

		if (name.trim().equals(""))
			throw new WizardValidationException(null, "Invalid name", null);

		if (index < 0)
			throw new WizardValidationException(null, "Invalid database", null);
	}
}
