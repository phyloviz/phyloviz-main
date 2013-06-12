/*-
 * Copyright (c) 2011, PHYLOViZ Team <phyloviz@gmail.com>
 * All rights reserved.
 * 
 * This file is part of PHYLOViZ <http://www.phyloviz.net>.
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
 * 
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole combination.
 * 
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent modules,
 * and to copy and distribute the resulting executable under terms of your
 * choice, provided that you also meet, for each linked independent module,
 * the terms and conditions of the license of that module.  An independent
 * module is a module which is not derived from or based on this library.
 * If you modify this library, you may extend this exception to your version
 * of the library, but you are not obligated to do so.  If you do not wish
 * to do so, delete this exception statement from your version.
 */

package net.phyloviz.core.explorer;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.Action;
import net.phyloviz.core.data.TypingData;
import org.openide.nodes.AbstractNode;
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
		super(new ExplorerChildren(td.getLookup()), new AbstractLookup(ic));
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
