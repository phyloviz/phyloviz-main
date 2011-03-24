package net.phyloviz.core.explorer;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.Action;
import net.phyloviz.core.data.TypingData;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.Utilities;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

public class TypingDataNode extends AbstractNode {

	public TypingDataNode(TypingData td) {
		this(td, new InstanceContent());
	        setDisplayName(td.toString());
	}

	private TypingDataNode(TypingData td, InstanceContent ic) {
		super(Children.LEAF, new AbstractLookup(ic));
		ic.add(td);
		ic.add(td.getSaver());
	}


	@Override
	public Image getIcon (int type) {
		return ImageUtilities.loadImage("net/phyloviz/core/TypingIcon.png");
	}
	
	@Override
	public Image getOpenedIcon (int type) {
		return getIcon(type);
	}

	@Override
	public boolean canDestroy() {
		return false;
	}

	@Override
	public Action getPreferredAction() {
		return getActions(true)[0];
	}

	@Override
	public Action[] getActions(boolean context) {

		Collection<Action> a4p = new ArrayList<Action>();
		a4p.addAll(Utilities.actionsForPath("/Actions/PHYLOViZ/DataModel"));
		a4p.addAll(Utilities.actionsForPath("/Actions/PHYLOViZ/Algorithm"));

		Action[] actions = a4p.toArray(new Action[a4p.size()]);
		//actions[a4p.size()] = SystemAction.get(PropertiesAction.class);

		return actions;
	}
}
