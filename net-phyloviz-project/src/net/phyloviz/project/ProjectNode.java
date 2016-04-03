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

package net.phyloviz.project;

/**
 *
 * @author Marta Nascimento
 */
import java.awt.Image;
import javax.swing.Action;
import static net.phyloviz.project.PhylovizProjectLogicalView.CUSTOMER_ICON;
import net.phyloviz.project.action.CloseProjectAction;
import net.phyloviz.project.action.LoadDataSetAction;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

public final class ProjectNode extends FilterNode {

    final PhylovizProject project;
    private LoadDataSetAction loadAction;

    public ProjectNode(Node node, PhylovizProject project) throws DataObjectNotFoundException {
        super(node,
                NodeFactorySupport.createCompositeChildren(project, "Projects/PHYLOViZ/Nodes"),
                //new FilterNode.Children(node),
                new ProxyLookup(new Lookup[]{
                    Lookups.singleton(project),
                    node.getLookup()
                })
        );
        this.project = project;
    }

    @Override
    public Action[] getActions(boolean arg0) {
        return new Action[]{
            LoadDataSetActionGetInstance(),
            new CloseProjectAction(this), //CommonProjectActions.closeProjectAction()
        };
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage(CUSTOMER_ICON);
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public String getDisplayName() {
        return project.getProjectDirectory().getName();
    }

    private Action LoadDataSetActionGetInstance() {
        if (loadAction == null) {
            loadAction = new LoadDataSetAction();
        }
        return loadAction;
    }

}
