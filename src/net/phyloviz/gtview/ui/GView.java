package net.phyloviz.gtview.ui;

import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JPanel;
import prefuse.Visualization;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.util.ui.JForcePanel;

public abstract class GView extends JPanel {

	public abstract JComponent getDisplay();

	public abstract Visualization getVisualization();

	public abstract boolean getLinearSize();

	public abstract void setLinearSize(boolean status);

	public abstract void setHighQuality(boolean status);

	public abstract JForcePanel getForcePanel();

	public abstract ForceDirectedLayout getForceLayout();

        public abstract ArrayList<ForcePair> getForces();

	public abstract void showGroupPanel(boolean status);

	public abstract void showInfoPanel(boolean status);
}
