package net.phyloviz.loadmlst.wizard;

import java.awt.Component;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import javax.swing.event.ChangeListener;
import net.phyloviz.alseq.AlignedSequenceFastaFactory;
import net.phyloviz.core.data.AbstractProfile;
import net.phyloviz.core.data.DataSet;
import net.phyloviz.core.data.DataSetTracker;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.core.util.TypingFactory;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.awt.StatusDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;

public class LoadMLSTWizardPanel3 implements WizardDescriptor.ValidatingPanel {

	/**
	 * The visual component that displays this panel. If you need to access the
	 * component from this class, just use getComponent().
	 */
	private Component component;
	private String dataSetName;
	private int iDBIndex;
	private TypingData<? extends AbstractProfile> td;
	private TypingFactory tf;
	private DataSet ds;

	// Get the visual component for the panel. In this template, the component
	// is kept separate. This can be more efficient: if the wizard is created
	// but never displayed, or not all panels are displayed, it is better to
	// create only those which really need to be visible.
	@Override
	public Component getComponent() {
		if (component == null) {
			component = new LoadMLSTVisualPanel3();
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

	@Override
	public void readSettings(Object settings) {
		((WizardDescriptor) settings).putProperty("WizardPanel_image", ImageUtilities.loadImage("net/phyloviz/loadmlst/LoadMLST-Sequence.png", true));
		td = (TypingData<AbstractProfile>) ((WizardDescriptor) settings).getProperty("typing_data");
		tf = (TypingFactory) ((WizardDescriptor) settings).getProperty("typing_factory");
		dataSetName = (String) ((WizardDescriptor) settings).getProperty("name");
		iDBIndex = (Integer) ((WizardDescriptor) settings).getProperty("dbIndex");
		((LoadMLSTVisualPanel3) getComponent()).initDynamicComponents(iDBIndex);
	}

	@Override
	public void storeSettings(Object settings) {
	}

	@Override
	public void validate() throws WizardValidationException {
		ds = new DataSet(dataSetName);

		LoadMLSTVisualPanel3 vp3 = (LoadMLSTVisualPanel3) getComponent();
		if (vp3.isSeqDataSelected()) {
			ArrayList<String> alLoci = vp3.getLoci();
			if (vp3.hasSeqDataComplete() && alLoci != null) {
				for (int i = 0; i < alLoci.size(); i++) {
					try {
						Reader reader = vp3.getSequence(i);
						if (reader == null) {
							reader = new FileReader(vp3.getFilename(i));
						}
						
						TypingData<? extends AbstractProfile> seqData = new AlignedSequenceFastaFactory().loadData(reader);
						seqData.setDescription(alLoci.get(i) + " locus sequence");
						ds.add(seqData);
						// TODO integrate data
					} catch (IOException e) {
					}
				}
			} else {
				throw new WizardValidationException(null, "Sequence data not completely loaded yet!", null);
			}
		}

		ds.add(td);
		Lookup.getDefault().lookup(DataSetTracker.class).add(ds);
		StatusDisplayer.getDefault().setStatusText("Dataset loaded.");
	}
}
