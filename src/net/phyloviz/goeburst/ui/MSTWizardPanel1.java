package net.phyloviz.goeburst.ui;

import java.awt.Component;
import javax.swing.event.ChangeListener;
import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.goeburst.AbstractDistance;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;

public class MSTWizardPanel1 implements WizardDescriptor.ValidatingPanel {

	/**
	 * The visual component that displays this panel. If you need to access the
	 * component from this class, just use getComponent().
	 */
	private MSTVisualPanel1 component;

	private Node node;

	public MSTWizardPanel1(Node node) {
		super();
		this.node = node;
	}

	@Override
	public Component getComponent() {
		if (component == null) {
			TypingData<? extends Profile> td = node.getLookup().lookup(TypingData.class);
			component = new MSTVisualPanel1(td);
		}
		return component;
	}

	@Override
	public HelpCtx getHelp() {
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

	@Override
	public void readSettings(Object settings) {
		((WizardDescriptor) settings).putProperty("WizardPanel_image", ImageUtilities.loadImage("net/phyloviz/goeburst/GOeBurstMSTImage.png", true));
	}

	@Override
	public void storeSettings(Object settings) {
		((WizardDescriptor) settings).putProperty("distance", ((MSTVisualPanel1) getComponent()).getDistance());
	}

	@Override
	public void validate() throws WizardValidationException {
		AbstractDistance ad = component.getDistance();

		if (ad == null)
			throw new WizardValidationException(null, "Invalid distance", null);
	}
}
