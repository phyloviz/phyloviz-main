/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.project.action;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.Action;
import net.phyloviz.core.data.DataSet;
import net.phyloviz.core.data.DataSetTracker;
import net.phyloviz.project.ProjectNode;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

public final class CloseProjectAction extends AbstractAction {

    private final ProjectNode _project;
    
    public CloseProjectAction(ProjectNode project) {
        putValue(Action.NAME, "Close");
        _project = project;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        Lookup lk = Lookup.getDefault().lookup(DataSetTracker.class).getLookup();
        
        Collection<? extends DataSet> data = lk.lookupAll(DataSet.class);
        String projName = _project.getDisplayName();
        for (DataSet ds : data) {
            if(ds.toString().equals(projName)){
                try{ 
                    ds.getNode().destroy(); 
                    break;
                }
                catch(IOException ioe){ Exceptions.printStackTrace(ioe); }
            }
        }
        CommonProjectActions.closeProjectAction().actionPerformed(ae);
    }
}
