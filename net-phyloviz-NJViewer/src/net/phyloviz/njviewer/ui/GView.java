package net.phyloviz.njviewer.ui;

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
	
	public abstract void setLevelLabel(boolean status);
    
        public abstract void setEdgePercentageLabel(boolean status);

	public abstract boolean showLabel();

	public abstract void setShowLabel(boolean status);

	public abstract void setHighQuality(boolean status);

	public abstract JForcePanel getForcePanel();

	public abstract ForceDirectedLayout getForceLayout();

        public abstract ArrayList<ForcePair> getForces();

	public abstract void showInfoPanel();

	public abstract void closeInfoPanel();

	public abstract InfoPanel getInfoPanel();
}
