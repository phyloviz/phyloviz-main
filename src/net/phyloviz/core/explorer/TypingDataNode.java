package net.phyloviz.core.explorer;

import java.awt.Image;
import net.phyloviz.core.data.TypingData;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.Lookups;

public class TypingDataNode extends AbstractNode {

	public TypingDataNode(TypingData td) {
		super(Children.LEAF, Lookups.singleton(td));
	        setDisplayName(td.toString());
	}

	@Override
	public Image getIcon (int type) {
		return ImageUtilities.loadImage("net/phyloviz/core/TypingIcon.png");
	}
	
	@Override
	public Image getOpenedIcon (int type) {
		return getIcon(type);
	}
}
