package net.phyloviz.nj.ui;

import java.awt.Image;
import java.util.Collection;
import javax.swing.Action;
import net.phyloviz.core.explorer.ExplorerChildren;
import net.phyloviz.nj.tree.NeighborJoiningItem;
import org.openide.nodes.AbstractNode;
import org.openide.util.ImageUtilities;
import org.openide.util.Utilities;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

public class NeighborJoiningNode extends AbstractNode{

        NeighborJoiningItem result;
    
        public NeighborJoiningNode(NeighborJoiningItem g, InstanceContent ic) {
            super(new ExplorerChildren(g.getLookup()), new AbstractLookup(ic));
            ic.add(g);
            setDisplayName(g.toString());
            result = g;
        }
	public NeighborJoiningNode(NeighborJoiningItem g) {
            this(g, new InstanceContent());
            setDisplayName(g.toString());
	}

	@Override
	public Image getIcon (int type) {
		return ImageUtilities.loadImage("net/phyloviz/nj/Neighbor-JoiningIcon.png");
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

		Collection<? extends Action> a4p = Utilities.actionsForPath("/Actions/PHYLOViZ/NeighborJoining");

		Action[] actions = a4p.toArray(new Action[a4p.size()]);

		return actions;
	}
}
