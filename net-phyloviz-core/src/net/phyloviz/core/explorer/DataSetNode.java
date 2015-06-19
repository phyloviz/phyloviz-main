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
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import javax.swing.Action;
import net.phyloviz.core.data.DataSet;
import net.phyloviz.core.data.DataSetTracker;
import org.openide.actions.DeleteAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

public class DataSetNode extends AbstractNode {

        private DataSet ds;
    
	public DataSetNode(DataSet ds) {
		super(new ExplorerChildren(ds.getLookup()), Lookups.singleton(ds));
		setDisplayName(ds.toString());
                
                this.ds = ds;
	}

	@Override
	public Image getIcon(int type) {
		return ImageUtilities.loadImage("net/phyloviz/core/DataSetIcon.png");
	}

	@Override
	public Image getOpenedIcon(int type) {
		return getIcon(type);
	}

	@Override
	public boolean canDestroy() {
		return true;
	}

	@Override
	public void destroy() throws IOException {
		Lookup.getDefault().lookup(DataSetTracker.class).remove(this.getLookup().lookup(DataSet.class));

		Enumeration<Node> e = this.getChildren().nodes();
		while (e.hasMoreElements())
			e.nextElement().destroy();

		super.destroy();
	}

	@Override
	public Action[] getActions(boolean context) {

            Collection<? extends Action> a4p = Utilities.actionsForPath("Actions/PHYLOViZ/DataSet");                        
            
            Action[] actions = a4p.toArray(new Action[a4p.size() + 1]);
            actions[a4p.size()] = SystemAction.get(DeleteAction.class);
            
            return actions;
/*		Action[] actions = null;
		try {
			actions = new Action[]{(Action) DataObject.find(
					FileUtil.getConfigFile("Actions/Edit/org-openide-actions-DeleteAction.instance")).getLookup().lookup(InstanceCookie.class).instanceCreate()};
		} catch (IOException ex) {
			Exceptions.printStackTrace(ex);
		} catch (ClassNotFoundException ex) {
			Exceptions.printStackTrace(ex);
		}
		return actions;
*/
	}
}
