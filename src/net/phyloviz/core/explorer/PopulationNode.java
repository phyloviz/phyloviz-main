package net.phyloviz.core.explorer;

import java.awt.Image;
import net.phyloviz.core.data.Population;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.Lookups;

public class PopulationNode extends AbstractNode {

	public PopulationNode(Population pop) {
		super(Children.LEAF, Lookups.singleton(pop));
	        setDisplayName(pop.toString());
	}

	@Override
	public Image getIcon (int type) {
		return ImageUtilities.loadImage("net/phyloviz/core/PopulationIcon.png");
	}
	
	@Override
	public Image getOpenedIcon (int type) {
		return getIcon(type);
	}
}
