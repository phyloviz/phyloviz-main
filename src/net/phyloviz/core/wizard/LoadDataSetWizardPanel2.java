package net.phyloviz.core.wizard;

import java.awt.Component;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.event.ChangeListener;
import net.phyloviz.core.data.AbstractProfile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.core.util.TypingFactory;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.awt.StatusDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;

public class LoadDataSetWizardPanel2 implements WizardDescriptor.ValidatingPanel {

	/**
	 * The visual component that displays this panel. If you need to access the
	 * component from this class, just use getComponent().
	 */
	private Component component;
	private TypingFactory tf;
	private TypingData<? extends AbstractProfile> td;

	// Get the visual component for the panel. In this template, the component
	// is kept separate. This can be more efficient: if the wizard is created
	// but never displayed, or not all panels are displayed, it is better to
	// create only those which really need to be visible.
	@Override
	public Component getComponent() {
		if (component == null) {
			component = new LoadDataSetVisualPanel2();
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
		((WizardDescriptor) settings).putProperty("WizardPanel_image", ImageUtilities.loadImage("net/phyloviz/core/TypingImage.png", true));
		tf = (TypingFactory) ((WizardDescriptor) settings).getProperty("typing");
	}

	@Override
	public void storeSettings(Object settings) {
		((WizardDescriptor) settings).putProperty("td_filename", ((LoadDataSetVisualPanel2) getComponent()).getFilename());
		((WizardDescriptor) settings).putProperty("typing_factory", tf);
		((WizardDescriptor) settings).putProperty("typing_data", td);
	}

	@Override
	public void validate() throws WizardValidationException {
		try {
        		StatusDisplayer.getDefault().setStatusText("Loading typing data...");
			td = tf.loadData(new FileReader(((LoadDataSetVisualPanel2) getComponent()).getFilename()));
			td.setDescription(tf.toString());

		} catch (FileNotFoundException ex) {
			throw new WizardValidationException(null, "File not found: " + ex.getMessage(), null);
		} catch (IOException ex) {
			throw new WizardValidationException(null, "Error loading file: " + ex.getMessage(), null);
		} catch (Exception ex) {
			throw new WizardValidationException(null, "Fatal error: " + ex.getMessage(), null);
		}
	}
}
