package net.phyloviz.core.explorer;

import java.awt.Image;
import java.io.IOException;
import java.util.Enumeration;
import javax.swing.Action;
import net.phyloviz.core.data.DataSet;
import net.phyloviz.core.data.DataSetTracker;
import org.openide.actions.DeleteAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

public class DataSetNode extends AbstractNode {

	public DataSetNode(DataSet ds) {
		super(new ExplorerChildren(ds.getLookup()), Lookups.singleton(ds));
		setDisplayName(ds.toString());
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

		return new SystemAction[] {
			SystemAction.get(DeleteAction.class),
			//SystemAction.get(PropertiesAction.class)
		};

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
