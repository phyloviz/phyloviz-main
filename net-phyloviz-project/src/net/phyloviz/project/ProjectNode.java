/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
