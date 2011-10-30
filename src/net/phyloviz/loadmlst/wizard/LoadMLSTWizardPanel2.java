package net.phyloviz.loadmlst.wizard;

import java.awt.Component;
import java.io.IOException;
import javax.swing.event.ChangeListener;
import net.phyloviz.core.data.AbstractProfile;
import net.phyloviz.core.data.DataSet;
import net.phyloviz.core.data.DataSetTracker;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.mlst.MLSTypingFactory;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.awt.StatusDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;

public class LoadMLSTWizardPanel2 implements WizardDescriptor.ValidatingPanel {

	/**
	 * The visual component that displays this panel. If you need to access the
	 * component from this class, just use getComponent().
	 */
	private int iDBIndex;
	private String sDBName;
	private String dataSetName;
	private Component component;
	private MLSTypingFactory tf;
	private TypingData<? extends AbstractProfile> td;
	private DataSet ds;

	// Get the visual component for the panel. In this template, the component
	// is kept separate. This can be more efficient: if the wizard is created
	// but never displayed, or not all panels are displayed, it is better to
	// create only those which really need to be visible.
	@Override
	public Component getComponent() {
		if (component == null) {
			component = new LoadMLSTVisualPanel2();
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
		iDBIndex = (Integer) ((WizardDescriptor) settings).getProperty("dbIndex");
		sDBName = (String) ((WizardDescriptor) settings).getProperty("dbName");
		dataSetName = (String) ((WizardDescriptor) settings).getProperty("name");
		((LoadMLSTVisualPanel2) getComponent()).setDatabase(iDBIndex, sDBName);
		tf = new MLSTypingFactory();
	}

	@Override
	public void storeSettings(Object settings) {
		((WizardDescriptor) settings).putProperty("typing_factory", tf);
		((WizardDescriptor) settings).putProperty("typing_data", td);
	}

	@Override
	public void validate() throws WizardValidationException {
//		if (tf == null) {
//			throw new WizardValidationException(null, "Could not load MSLT plugin", null);
//		}
//
//		if (((LoadMLSTVisualPanel2) getComponent()).isEmpty()) {
//			throw new WizardValidationException(null, "No typing data completely loaded yet!", null);
//		}
// TODO uncomment this
//		try {
//			ds = new DataSet(dataSetName);
//			td = tf.loadData(((LoadMLSTVisualPanel2) getComponent()).getTypingData());
//			td.setDescription(tf.toString());
//
//			ds.add(td);
//			Lookup.getDefault().lookup(DataSetTracker.class).add(ds);
//			StatusDisplayer.getDefault().setStatusText("Dataset loaded.");
//		} catch (IOException ex) {
//			throw new WizardValidationException(null, "Fatal error: " + ex.getMessage(), null);
//		}
	}
}
