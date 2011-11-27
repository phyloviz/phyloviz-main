package net.phyloviz.synthdata.wizard;

import java.awt.Component;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;

public class GenDatasetWizardPanel2 implements WizardDescriptor.ValidatingPanel {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private GenDatasetVisualPanel2 component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public Component getComponent() {
	   if (component == null) {
		  component = new GenDatasetVisualPanel2();
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
	   ((WizardDescriptor) settings).putProperty("WizardPanel_image", ImageUtilities.loadImage("net/phyloviz/synthdata/GenDataset-logo.png", true));
	   ((GenDatasetVisualPanel2) getComponent()).initSeed();
    }

    @Override
    public void storeSettings(Object settings) {
	   ((WizardDescriptor) settings).putProperty("gensize", ((GenDatasetVisualPanel2) getComponent()).getGenSize());
	   ((WizardDescriptor) settings).putProperty("gensampled", ((GenDatasetVisualPanel2) getComponent()).getGenSampled());
	   ((WizardDescriptor) settings).putProperty("popsize", ((GenDatasetVisualPanel2) getComponent()).getPopSize());
	   ((WizardDescriptor) settings).putProperty("pfsize", ((GenDatasetVisualPanel2) getComponent()).getProfileSize());
	   ((WizardDescriptor) settings).putProperty("mut", ((GenDatasetVisualPanel2) getComponent()).getMutRate());
	   ((WizardDescriptor) settings).putProperty("rec", ((GenDatasetVisualPanel2) getComponent()).getRecRate());
	   ((WizardDescriptor) settings).putProperty("seed", ((GenDatasetVisualPanel2) getComponent()).getRandomSeed());
    }

    @Override
    public void validate() throws WizardValidationException {
	   int igensize = validateIsNumber(component.getGenSize(), 1, "Generations total #");
	   int igensampled = validateIsNumber(component.getGenSampled(), 1, "Generations sampled #");
	   int ipopsize = validateIsNumber(component.getPopSize(), 1, "Population size");
	   validateIsNumber(component.getProfileSize(), 1, "Profile size");
	   int imut = validateIsNumber(component.getMutRate(), 0, "Rate of mutation");
	   int irec = validateIsNumber(component.getRecRate(), 0, "Rate of recombination");
	   int iseed = validateIsNumber(component.getRandomSeed(), 1, "Random seed");

	   if (igensampled > igensize) {
		  throw new WizardValidationException(null, "Total generations must be bigger than sampled generations", null);
	   }
	   if (iseed <= 0 || iseed > Integer.MAX_VALUE) {
		  throw new WizardValidationException(null, "Random seed must be between [1, " + (Integer.MAX_VALUE / 100) + "]", null);
	   }
	   if (imut > (2 * ipopsize) || irec > (2 * ipopsize)) {
		  throw new WizardValidationException(null, "Mutation/Recombination cannot exceed 2x Population size", null);
	   }
    }

    private int validateIsNumber(String field, int min, String desc) throws WizardValidationException {
	   int ifield = 0, max = Integer.MAX_VALUE;
	   try {
		  ifield = Integer.parseInt(field);

		  if (ifield < min || ifield > max) {
			 throw new WizardValidationException(null, desc + " must be between [" + min + "," + max + "]", null);
		  }
	   } catch (NumberFormatException e) {
		  throw new WizardValidationException(null, desc + " is not a number", null);
	   }
	   return ifield;
    }
}
