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
