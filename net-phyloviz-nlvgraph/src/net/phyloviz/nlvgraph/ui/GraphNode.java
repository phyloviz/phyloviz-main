package net.phyloviz.nlvgraph.ui;

import java.awt.Image;
import java.util.Collection;
import javax.swing.Action;
import net.phyloviz.nlvgraph.Result;
import org.openide.actions.PropertiesAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.Utilities;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

public class GraphNode extends AbstractNode {

	public GraphNode(Result g) {
		super(Children.LEAF, Lookups.singleton(g));
	        setDisplayName(g.toString());
	}

	@Override
	public Image getIcon (int type) {
		return ImageUtilities.loadImage("net/phyloviz/nlvgraph/nLVGraphIcon.png");
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

		Collection<? extends Action> a4p = Utilities.actionsForPath("/Actions/PHYLOViZ/nLVGraph");

		@SuppressWarnings("SuspiciousToArrayCall")
		Action[] actions = a4p.toArray(new Action[a4p.size() + 1]);
		actions[a4p.size()] = SystemAction.get(PropertiesAction.class);

		return actions;
	}
	
}
