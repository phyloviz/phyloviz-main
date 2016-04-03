/*-
 * Copyright (c) 2016, PHYLOViZ Team <phyloviz@gmail.com>
 * All rights reserved.
 * 
 * This file is part of PHYLOViZ <http://www.phyloviz.net/>.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
